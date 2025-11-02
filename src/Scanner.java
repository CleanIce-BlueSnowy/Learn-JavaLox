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
        Map.entry("and", TokenType.AND),
        Map.entry("class", TokenType.CLASS),
        Map.entry("else", TokenType.ELSE),
        Map.entry("false", TokenType.FALSE),
        Map.entry("for", TokenType.FOR),
        Map.entry("fun", TokenType.FUN),
        Map.entry("if", TokenType.IF),
        Map.entry("nil", TokenType.NIL),
        Map.entry("or", TokenType.OR),
        Map.entry("print", TokenType.PRINT),
        Map.entry("return", TokenType.RETURN),
        Map.entry("super", TokenType.SUPER),
        Map.entry("this", TokenType.THIS),
        Map.entry("true", TokenType.TRUE),
        Map.entry("var", TokenType.VAR),
        Map.entry("while", TokenType.WHILE)
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
                addToken(TokenType.LEFT_PAREN);
            }
            case ')' ->  {
                addToken(TokenType.RIGHT_PAREN);
            }
            case '{' ->  {
                addToken(TokenType.LEFT_BRACE);
            }
            case '}' ->  {
                addToken(TokenType.RIGHT_BRACE);
            }
            case ',' ->  {
                addToken(TokenType.COMMA);
            }
            case '.' ->  {
                addToken(TokenType.DOT);
            }
            case '-' ->  {
                addToken(TokenType.MINUS);
            }
            case '+' ->  {
                addToken(TokenType.PLUS);
            }
            case ';' ->  {
                addToken(TokenType.SEMICOLON);
            }
            case '*' ->  {
                addToken(TokenType.STAR);
            }
            case '!' -> {
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
            }
            case '=' -> {
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            }
            case '<' -> {
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            }
            case '>' -> {
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
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
                    addToken(TokenType.SLASH);
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
            type = TokenType.IDENTIFIER;
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

        addToken(TokenType.NUMBER, Double.valueOf(source.substring(start, current)));
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
        addToken(TokenType.STRING, value);
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
