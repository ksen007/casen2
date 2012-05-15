package edu.berkeley.cs.lexer;

import edu.berkeley.cs.builtin.objects.CObject;
import edu.berkeley.cs.builtin.objects.preprocessor.*;
import edu.berkeley.cs.parser.SymbolTable;

import java.io.IOException;
import java.io.Reader;

public class StandardLexer implements Lexer {
    static final private int END_OF_FILE = -1;

    private int lineNo = 1;
    private int columnNo = 1;
    private PeekReader in;

    public StandardLexer(Reader in) throws IOException {
        this.in = new PeekReader(in, 2);
    }

    private int lookAhead(int i) {
        return in.peek(i);
    }

    private int read() {
        try {
            int c = in.read();;
            if (c == '\n') {
                lineNo++;
                columnNo = 0;
            }
            columnNo++;
            return c;
        } catch (IOException e) {
            throw new LexerException(e.getMessage(), lineNo, columnNo);
        }
    }

    private void close() {
        try {
            in.close();
        } catch (IOException e) {
        }
    }

    private int next() {
        read();
        return lookAhead(1);
    }

    private char match(char c) {
        int input = read();
        if (input != c) {
            String inputChar = (input != END_OF_FILE) ? "" + (char) input : "END_OF_FILE";
            throw new LexerException("Expected '" + c + "' but got '" + inputChar + "'", lineNo, columnNo);
        }
        return c;
    }

    private String match(String str) {
        for (int i = 0; i < str.length(); i++) {
            match(str.charAt(i));
        }
        return str;
    }

    private CObject createToken(char c, boolean isSpace) {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        match(c);
        return new SymbolToken(pos, isSpace,SymbolTable.getInstance().getId("" + c));
    }

    private CObject createToken(String str, boolean isSpace) {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        match(str);
        return new SymbolToken(pos, isSpace, SymbolTable.getInstance().getId(str));
    }

    public CObject getNextToken() {
        int character = lookAhead(1);
        boolean isSpace = false;
        while (character == ' ' || character == '\t' ||
                character == '\r') {
            isSpace = true;
            character = next();
        }
        switch (character) {
            case END_OF_FILE: {
                close();
                return SymbolToken.end;
            }
            case '"': {
                return matchStringLiteral((char) character,isSpace);
            }
            default: {
                if ((character >= '0' && character <= '9')) {
                    return matchNumber(isSpace);
                } else if (character=='@') {
                    character = next();
                    return matchIdentifier(true,isSpace);
                } else {
                    return matchIdentifier(false,isSpace);
                }
            }
        }
    }

    private int matchDigits(StringBuilder sb) {
        int character = lookAhead(1);
        int count = 0;
        while (character >= '0' && character <= '9') {
            sb.append((char) character);
            character = next();
            count++;
        }
        if (count == 0) {
            throw new LexerException("Unexpected '" + ((char) character) + "' character", lineNo, columnNo);
        }
        return count;
    }

    private void matchDecimalNumber(StringBuilder sb) {
        int character = lookAhead(1);
        if (character >= '0' && character <= '9') {
            matchDigits(sb);
            character = lookAhead(1);
        }
        if (character == '.') {
            sb.append('.');
            character = next();
            matchDigits(sb);
            character = lookAhead(1);
        }
        if (character == 'e' || character == 'E') {
            sb.append('e');
            character = next();
            if (character == '-' || character == '+') {
                sb.append(character);
                character = next();
            }
            matchDigits(sb);
        }
    }

    private int matchOctalDigits(StringBuilder sb) {
        int character = lookAhead(1);
        int count = 0;
        while (character >= '0' && character <= '7') {
            sb.append((char) character);
            character = next();
            count++;
        }
        if (count == 0) {
            throw new LexerException("Unexpected '" + ((char) character) + "' character", lineNo, columnNo);
        }
        return count;
    }

    private int matchHexDigits(StringBuilder sb) {
        int character = lookAhead(1);
        int count = 0;
        while ( (character >= '0' && character <= '9') ||
                (character >= 'a' && character <= 'f') ||
                (character >= 'A' && character <= 'F')) {
            sb.append((char) character);
            character = next();
            count++;
        }
        if (count == 0) {
            throw new LexerException("Unexpected '" + ((char) character) + "' character", lineNo, columnNo);
        }
        return count;
    }

    private int matchBinaryDigits(StringBuilder sb) {
        int character = lookAhead(1);
        int count = 0;
        while (character == '0' || character == '1') {
            sb.append((char) character);
            character = next();
            count++;
        }
        if (count == 0) {
            throw new LexerException("Unexpected '" + ((char) character) + "' character", lineNo, columnNo);
        }
        return count;
    }

    private CObject matchNumber(boolean isSpace) {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        StringBuilder sb = new StringBuilder();
        int digit = lookAhead(1);
        char secondDigit = (char) lookAhead(2);

        boolean isFloat = false;
        if (digit == '0' && (secondDigit == 'o' || secondDigit == 'O')) {
            sb.append(match('0'));
            sb.append(match(secondDigit));
            matchOctalDigits(sb);
        } else if (digit == '0' && (secondDigit == 'x' || secondDigit == 'X')) {
            sb.append(match('0'));
            sb.append(match(secondDigit));
            matchHexDigits(sb);
        } else if (digit == '0' && (secondDigit == 'b' || secondDigit == 'B')) {
            sb.append(match('0'));
            sb.append(match(secondDigit));
            matchBinaryDigits(sb);
        } else {
            matchDecimalNumber(sb);
        }
        int character = lookAhead(1);
        if (character == '.' || (character >= '0' && character <= '9')) {
            throw new LexerException("Unexpected '" + ((char) character) + "' character", lineNo, columnNo);
        }
        String number = sb.toString();
        if (number.contains(".")) {
            return new DoubleToken(pos,isSpace,Double.parseDouble(number));
        } else {
            return new LongToken(pos,isSpace,Long.parseLong(number));
        }
    }

    private CObject matchIdentifier(boolean isMeta, boolean isSpace) {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        StringBuilder sb = new StringBuilder();
        int character = lookAhead(1);
        if ((character >= 'A' && character <= 'Z') ||
                (character >= 'a' && character <= 'z') ||
                character == '_') {
            while ((character >= 'a' && character <= 'z') ||
                    (character >= 'A' && character <= 'Z') ||
                    (character >= '0' && character <= '9') ||
                    character == '_') {
                sb.append((char) character);
                character = next();
            }
        } else if (!(character == ' ' || character == '\t'
                || character == '\r' || character == END_OF_FILE)){
            if (isMeta && character == '\n') {
                sb.append('@');
            } else {
                if(!isMeta && character=='=') {
                    sb.append((char) character);
                    character = next();
                    if (character == '=') {
                        sb.append((char) character);
                        next();
                    }
                } else {
                    sb.append((char) character);
                    next();
                }
            }
        } else if (isMeta) {
            sb.append('@');
        }
        String word = sb.toString();
        if (isMeta) {
            return new MetaToken(pos,isSpace,SymbolTable.getInstance().getId(word));
        } else {
            if (word.equals("true")) {
                return new BooleanToken(pos,isSpace,true);
            } else if (word.equals("false")) {
                return new BooleanToken(pos,isSpace,false);
            } else if(word.equals("null")){
                return new NullToken(pos,isSpace);
            }
            return new SymbolToken(pos,isSpace,SymbolTable.getInstance().getId(word));
        }
    }

    private CObject matchStringLiteral(char quote, boolean isSpace) {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        match(quote);
        StringBuilder sb = new StringBuilder();
        int character = lookAhead(1);
        while (character != quote && character != END_OF_FILE) {
            sb.append((char) character);
            character = next();
        }
        match(quote);
        return new StringToken(pos, isSpace, sb.toString());
    }
}

