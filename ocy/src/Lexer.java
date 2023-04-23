import java.util.ArrayList;
import java.util.List;

public class Lexer {

    private final String text;
    private final Position pos = new Position(-1, 0, -1);
    private String currentChar;
    private final LexerReturn res = new LexerReturn();

    public Lexer(String text) {
        this.text = text;
    }

    private void advance() {
        this.pos.advance(this.currentChar);
        if(this.pos.getIdx() < this.text.length()) {
            this.currentChar = "" + this.text.charAt(this.pos.getIdx());
        } else {
            this.currentChar = null;
        }
    }

    private Token makeNumber() {
        String numStr = "";
        int dotCount = 0;
        Position posStart = this.pos.copy();

        while(this.currentChar != null && (Constants.DIGITS.contains(this.currentChar) || this.currentChar.equals("."))) {
            if(this.currentChar.equals(".")) {
                if(dotCount == 1) {
                    break;
                }
                dotCount++;
                numStr = "%s.".formatted(numStr);
            } else {
                numStr = "%s%s".formatted(numStr, this.currentChar);
            }
            this.advance();
        }

        Token tok = new Token(null, numStr, posStart.copy(), this.pos.copy());
        if(dotCount == 0) {
            tok.setType(Constants.TT_INT);
        } else {
            tok.setType(Constants.TT_FLOAT);
        }
        return tok;
    }

    private Token makeIdentifier() {
        String idStr = "";
        Position posStart = this.pos.copy();

        while(this.currentChar != null && Constants.LETTERSDIGITS.contains(this.currentChar)) {
            idStr = "%s%s".formatted(idStr, this.currentChar);
            this.advance();
        }

        Token tok = new Token(null, idStr, posStart.copy(), this.pos.copy());

        if(Constants.KEYWORDS.contains(idStr)) {
            tok.setType(Constants.TT_KEYWORD);
        } else {
            tok.setType(Constants.TT_IDENTIFIER);
        }

        return tok;
    }

    private Token makeString() {
        String str = "";
        Position posStart = this.pos.copy();
        boolean escapeChar = false;
        this.advance();

        while(this.currentChar != null && (!this.currentChar.equals("\"") || escapeChar)) {
            if(escapeChar) {
                if(this.currentChar.equals("t")) {
                    str = "%s\t".formatted(str);
                } else if (this.currentChar.equals("n")) {
                    str = "%s\n".formatted(str);
                } else {
                    str = "%s%s".formatted(str, this.currentChar);
                }
                escapeChar = false;
            } else {
                if(this.currentChar.equals("\\")) {
                    escapeChar = true;
                } else {
                    str += this.currentChar;
                }
            }
            this.advance();
        }

        if(this.currentChar == null) {
            this.res.setError(new ExpectedCharError("Expected '\"'", posStart.copy(), this.pos.copy(), null));
            return null;
        }
        this.advance();
        return new Token(Constants.TT_STRING, str, posStart.copy(), this.pos.copy());
    }

    private Token makeOp(String op, String ope, String text) {
        Position posStart = this.pos.copy();
        this.advance();

        if(this.currentChar != null && this.currentChar.equals("=")) {
            this.advance();
            return new Token(ope, text+"=", posStart.copy(), this.pos.copy());
        }
        return new Token(op, text, posStart.copy(), null);
    }

    private Token makeMul() {
        Position posStart = this.pos.copy();
        this.advance();

        if(this.currentChar != null && this.currentChar.equals("*")) {
            this.advance();
            return new Token(Constants.TT_POW, "**", posStart.copy(), this.pos.copy());
        } else if (this.currentChar != null && this.currentChar.equals("=")) {
            this.advance();
            return new Token(Constants.TT_MULE, "*=", posStart.copy(), this.pos.copy());
        }
        return new Token(Constants.TT_MUL, "*", posStart.copy(), null);
    }

    private Token makeNotEquals() {
        Position posStart = this.pos.copy();
        this.advance();

        if(this.currentChar != null && this.currentChar.equals("=")) {
            this.advance();
            return new Token(Constants.TT_NE, "!=", posStart.copy(), this.pos.copy());
        }

        this.res.setError(new ExpectedCharError("Expected '=' after '!'", posStart.copy(), this.pos.copy(), null));
        return null;
    }

    private Token makeEquals() {
        Position posStart = this.pos.copy();
        this.advance();

        if(this.currentChar != null && this.currentChar.equals("=")) {
            this.advance();
            return new Token(Constants.TT_EE, "==", posStart.copy(), this.pos.copy());
        } else if(this.currentChar != null && this.currentChar.equals(">")) {
            this.advance();
            return new Token(Constants.TT_ARROW, "=>", posStart.copy(), this.pos.copy());
        }

        return new Token(Constants.TT_EQ, "=", posStart.copy(), null);
    }

    private Token makeLessThan() {
        Position posStart = this.pos.copy();
        this.advance();

        if(this.currentChar != null && this.currentChar.equals("=")) {
            this.advance();
            return new Token(Constants.TT_LTE, "<=", posStart.copy(), this.pos.copy());
        }
        return new Token(Constants.TT_LT, "<", posStart.copy(), null);
    }

    private Token makeGreaterThan() {
        Position posStart = this.pos.copy();
        this.advance();

        if(this.currentChar != null && this.currentChar.equals("=")) {
            this.advance();
            return new Token(Constants.TT_GTE, ">=", posStart.copy(), this.pos.copy());
        }
        return new Token(Constants.TT_GT, ">", posStart.copy(), null);
    }

    public LexerReturn makeTokens() {
        this.advance();
        List<Token> tokens = new ArrayList<>();

        while(this.currentChar != null) {
            if(" \t".contains(this.currentChar)) {
                this.advance();
            } else if(Constants.DIGITS.contains(this.currentChar)) {
                tokens.add(this.makeNumber());
            } else if(Constants.LETTERS.contains(this.currentChar)) {
                tokens.add(this.makeIdentifier());
            } else if(this.currentChar.equals("\"")) {
                Token result = this.makeString();
                if(result != null) {
                    tokens.add(result);
                }
            } else if(this.currentChar.equals("+")) {
                tokens.add(this.makeOp(Constants.TT_PLUS, Constants.TT_PLUSE, "+"));
            } else if(this.currentChar.equals("-")) {
                tokens.add(this.makeOp(Constants.TT_MINUS, Constants.TT_MINUSE, "-"));
            } else if(this.currentChar.equals("/")) {
                tokens.add(this.makeOp(Constants.TT_DIV, Constants.TT_DIVE, "/"));
            } else if(this.currentChar.equals("*")) {
                tokens.add(this.makeMul());
            } else if(this.currentChar.equals("(")) {
                tokens.add(new Token(Constants.TT_LPAREN, "(", this.pos.copy(), null));
                this.advance();
            } else if(this.currentChar.equals(")")) {
                tokens.add(new Token(Constants.TT_RPAREN, ")", this.pos.copy(), null));
                this.advance();
            } else if(this.currentChar.equals("[")) {
                tokens.add(new Token(Constants.TT_LSQUARE, "[", this.pos.copy(), null));
                this.advance();
            } else if(this.currentChar.equals("]")) {
                tokens.add(new Token(Constants.TT_RSQUARE, "]", this.pos.copy(), null));
                this.advance();
            } else if(this.currentChar.equals(",")) {
                tokens.add(new Token(Constants.TT_COMMA, ",", this.pos.copy(), null));
                this.advance();
            } else if(this.currentChar.equals("=")) {
                tokens.add(this.makeEquals());
            } else if(this.currentChar.equals("<")) {
                tokens.add(this.makeLessThan());
            } else if(this.currentChar.equals(">")) {
                tokens.add(this.makeGreaterThan());
            } else if(this.currentChar.equals("!")) {
                Token result = this.makeNotEquals();
                if(result != null) {
                    tokens.add(result);
                }
            } else {
                Position posStart = this.pos.copy();
                String character = this.currentChar;
                this.advance();
                res.setError(new IllegalCharError("'%s'".formatted(character), posStart.copy(), this.pos.copy()));
            }

            if(res.getError() != null) {
                return res;
            }
        }

        Token tok = new Token(Constants.TT_EOF, "", this.pos.copy(), null);
        tokens.add(tok);

        res.setResult(tokens);
        return res;
    }
}

class LexerReturn {
    private List<Token> result;
    private Error error;

    public List<Token> getResult() {
        return result;
    }

    public void setResult(List<Token> result) {
        this.result = result;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
