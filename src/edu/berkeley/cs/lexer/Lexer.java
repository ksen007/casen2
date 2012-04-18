package edu.berkeley.cs.lexer;

/**
 * Copyright (c) 2006-2011,
 * Koushik Sen    <ksen@cs.berkeley.edu>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * <p/>
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * <p/>
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * 3. The names of the contributors may not be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
import edu.berkeley.cs.builtin.objects.*;
import edu.berkeley.cs.builtin.objects.preprocessor.*;
import edu.berkeley.cs.parser.SymbolTable;

import java.io.IOException;
import java.io.Reader;

/**
 * The <a href="http://en.wikipedia.org/wiki/Lexical_analysis#Scanner">lexer</a>
 * is used to read characters and identify tokens and pass them to the parser
 *
 * @author <a href="mailto:grom@zeminvaders.net">Cameron Zemek</a>
 */
public class Lexer {
    static final private int END_OF_FILE = -1;

    private int lineNo = 1;
    private int columnNo = 1;
    private PeekReader in;

    public Lexer(Reader in) throws IOException {
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
            //System.out.println("c = " + c);
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

    private Token createToken(char c) {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        match(c);
        return new SymbolToken(pos, SymbolTable.getInstance().getId("" + c));
    }

    private Token createToken(String str) {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        match(str);
        return new SymbolToken(pos, SymbolTable.getInstance().getId(str));
    }

    public Token getNextToken() {
        int character = lookAhead(1);
        // Skip whitespace
        while (character == ' ' || character == '\t' ||
                character == '\r') {
            character = next();
        }
//        while(character=='#') {
//            matchLineComment();
//            character = lookAhead(1);
//        }
//        while (character == ' ' || character == '\t' ||
//                character == '\r' || character == '\n') {
//            character = next();
//        }
        switch (character) {
            case END_OF_FILE: {
                close();
                return null;
            }
            case '\n': {
                SourcePosition pos = new SourcePosition(lineNo, columnNo);
                match((char)character);
                return new NewLineToken(pos);

            }
            case '(':
            case ')':
            case '{':
            case '}':
            case '[':
            case ']':
            case ';':
            {
                return createToken((char)character);
            }
            case '"': {
                return matchStringLiteral((char) character);
            }
            default: {
                if ((character >= '0' && character <= '9')) {
                    return matchNumber();
                } else if (character=='@') {
                    character = next();
                    return matchIdentifier(true);
                } else {
                    return matchIdentifier(false);
                }
            }
        }
    }

    private void matchLineComment() {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        match("#");
        StringBuilder sb = new StringBuilder();
        int character = lookAhead(1);
        while (character != '\r' && character != '\n' && character != END_OF_FILE) {
            sb.append((char) character);
            character = next();
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
        // IntegerPart
        if (character >= '0' && character <= '9') {
            matchDigits(sb);
            character = lookAhead(1);
        }
        // FractionPart
        if (character == '.') {
            sb.append('.');
            character = next();
            matchDigits(sb);
            character = lookAhead(1);
        }
        // Exponent
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

    private Token matchNumber() {
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
        /*
         * Check that another number does not immediately follow as this means
         * we have an invalid number. For example, the input 12.34.5 after the
         * above code finishes leaves us with 12.34 matched. Without this
         * check .5 will then be matched separately as another valid number.
         */
        int character = lookAhead(1);
        if (character == '.' || (character >= '0' && character <= '9')) {
            throw new LexerException("Unexpected '" + ((char) character) + "' character", lineNo, columnNo);
        }
        String number = sb.toString();
        if (number.contains(".")) {
            return new DoubleToken(pos,Double.parseDouble(number));
        } else {
            return new LongToken(pos,Long.parseLong(number));
        }
    }

    /**
     * An identifier is either a keyword, function, or variable
     *
     * @return Token
     */
    private Token matchIdentifier(boolean isMeta) {
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
        } else {
            while (!( (character >= 'a' && character <= 'z') ||
                    (character >= 'A' && character <= 'Z') ||
                    (character >= '0' && character <= '9') ||
                    character == '_' ||
                    character == '(' ||
                    character == ')' ||
                    character == '[' ||
                    character == ']' ||
                    character == '{' ||
                    character == '}' ||
                    character == ' ' || character == '\t' ||
                    character == '\r' || character == '\n' || character == END_OF_FILE)){
                sb.append((char) character);
                character = next();
            }

        }
        String word = sb.toString();
        //System.out.println("Word:" + word);
        if (isMeta) {
            return new MetaToken(pos,SymbolTable.getInstance().getId(word));
        } else {
            if (word.equals("true")) {
                return new BooleanToken(pos,true);
            } else if (word.equals("false")) {
                return new BooleanToken(pos,false);
            } else if(word.equals("null")){
                return new NullToken(pos);
            }
            return new SymbolToken(pos,SymbolTable.getInstance().getId(word));
        }
    }

    private Token matchStringLiteral(char quote) {
        SourcePosition pos = new SourcePosition(lineNo, columnNo);
        match(quote);
        StringBuilder sb = new StringBuilder();
        int character = lookAhead(1);
        while (character != quote && character != END_OF_FILE) {
            sb.append((char) character);
            character = next();
        }
        match(quote);
        return new StringToken(pos, sb.toString());
    }
}

