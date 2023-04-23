public class Token {
    private String type;
    private String value;
    private Position posStart;
    private Position posEnd;
    public String getType() {
        return type;
    }

    public void setType(String type_) {
        this.type = type_;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String val) {
        this.value = val;
    }

    public Position getPosStart() {
        return posStart;
    }

    public void setPosStart(Position posStart_) {
        this.posStart = posStart_;

        if(this.posEnd == null) {
            this.posEnd = this.posStart.copy();
            this.posEnd.advance(null);
        }
    }

    public Position getPosEnd() {
        return this.posEnd;
    }

    public void setPosEnd(Position posEnd_) {
        this.posEnd = posEnd_;
    }

    public boolean matches(String type_, String val) {
        return type.equals(type_) && value.equals(val);
    }

    public String repr() {
        if(!value.equals("")) {
            return type + ":" + value;
        } else {
            return type;
        }
    }

    public Token(String type_, String value, Position posStart, Position posEnd) {
        this.type = type_;
        this.value = value;
        this.posStart = posStart;
        this.posEnd = posEnd;

        if(this.posEnd == null) {
            this.posEnd = this.posStart.copy();
            this.posEnd.advance(null);
        }
    }

    public Token() {
        return;
    }
}
