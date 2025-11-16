package lox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private static final Map<String, TokenType> keywords = Map.ofEntries(
        Map.entry("and", TokenType.And),
        Map.entry("class", TokenType.Class),
        Map.entry("else", TokenType.Else),
        Map.entry("false", TokenType.False),
        Map.entry("for", TokenType.For),
        Map.entry("fun", TokenType.Fun),
        Map.entry("if", TokenType.If),
        Map.entry("nil", TokenType.Nil),
        Map.entry("or", TokenType.Or),
        Map.entry("print", TokenType.Print),
        Map.entry("return", TokenType.Return),
        Map.entry("super", TokenType.Super),
        Map.entry("this", TokenType.This),
        Map.entry("true", TokenType.True),
        Map.entry("var", TokenType.Var),
        Map.entry("while", TokenType.While)
    );

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        char ret =  source.charAt(current);
        current++;
        return ret;
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void scanToken() {
        char ch = advance();
        switch (ch) {
            case '(' ->  {
                addToken(TokenType.LeftParen);
            }
            case ')' ->  {
                addToken(TokenType.RightParen);
            }
            case '{' ->  {
                addToken(TokenType.LeftBrace);
            }
            case '}' ->  {
                addToken(TokenType.RightBrace);
            }
            case ',' ->  {
                addToken(TokenType.Comma);
            }
            case '.' ->  {
                addToken(TokenType.Dot);
            }
            case '-' ->  {
                addToken(TokenType.Minus);
            }
            case '+' ->  {
                addToken(TokenType.Plus);
            }
            case ';' ->  {
                addToken(TokenType.Semicolon);
            }
            case '*' ->  {
                addToken(TokenType.Star);
            }
            case '!' -> {
                addToken(match('=') ? TokenType.BangEqual : TokenType.Bang);
            }
            case '=' -> {
                addToken(match('=') ? TokenType.EqualEqual : TokenType.Equal);
            }
            case '<' -> {
                addToken(match('=') ? TokenType.LessEqual : TokenType.Less);
            }
            case '>' -> {
                addToken(match('=') ? TokenType.GreaterEqual : TokenType.Greater);
            }
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if (match('*')) {
                    char next = peek();
                    int nest_cnt = 1;
                    WHILE:
                    while (true) {
                        switch (next) {
                            case '\0' -> {
                                Lox.error(line, "Unterminated comment.");
                                return;
                            }
                            case '\n' -> {
                                advance();
                                line++;
                            }
                            case '*' -> {
                                advance();
                                if (match('/')) {
                                    nest_cnt--;
                                    if (nest_cnt == 0) {
                                        break WHILE;
                                    }
                                }
                            }
                            case '/' -> {
                                advance();
                                if (match('*')) {
                                    nest_cnt++;
                                }
                            }
                            default -> {
                                advance();
                            }
                        }
                        next = peek();
                    }
                } else {
                    addToken(TokenType.Slash);
                }
            }
            case ' ',  '\r',  '\t' -> {}
            case '\n' -> {
                line++;
            }
            case '"' -> {
                string();
            }
            default ->  {
                if (isDigit(ch)) {
                    number();
                } else if (isAlpha(ch)) {
                    identifier();
                } else {
                    Lox.error(line, String.format("Unexpected character `%c`.", ch));
                }
            }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.Identifier;
        }
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        addToken(TokenType.Number, Double.valueOf(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Untermitated string.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.String, value);
    }

    private boolean match(char expected) {
        if (isAtEnd()) {
            return false;
        }
        if (source.charAt(current) != expected) {
            return false;
        }
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
    }

    private boolean isAlphaNumeric(char ch) {
        return isAlpha(ch) || isDigit(ch);
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
