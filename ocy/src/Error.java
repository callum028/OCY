public class Error {
    private final String errorName;
    private final String details;
    private final Position posStart;
    private final Position posEnd;
    private final Context context;

    public Error(String errorName, String details, Position posStart, Position posEnd, Context context) {
        this.errorName = errorName;
        this.details = details;
        this.posStart = posStart;
        this.posEnd = posEnd;
        this.context = context;
    }

    public String asString() {
        String result;
        if(this.context != null) {
            result = this.generateTraceback();
            result += "%s: %s".formatted(this.errorName, this.details);
        } else {
            result = "\u001B[31m%s on line %s: %s".formatted(this.errorName, this.posStart.getLn() + 1, this.details);
        }
        return result;
    }

    public String generateTraceback() {
        String result = "";
        Position pos = this.posStart.copy();
        Context ctx = this.context;

        while(ctx != null) {
            result = "  File __main__, line %s in %s\n".formatted(pos.getLn()+1, ctx.getName());
            pos = ctx.getEntryPos();
            ctx = ctx.getParent();
        }
        return "\u001B[31mTraceback (most recent call last):\n" + result;
    }
}

class IllegalCharError extends Error {
    public IllegalCharError(String details, Position posStart, Position posEnd) {
        super("Illegal Character Error", details, posStart, posEnd, null);
    }
}

class InvalidSyntaxError extends Error {
    public InvalidSyntaxError(String details, Position posStart, Position posEnd) {
        super("Invalid Syntax Error", details, posStart, posEnd, null);
    }
}

class RunTimeError extends Error {
    public RunTimeError(String details, Position posStart, Position posEnd, Context context) {
        super("Run Time Error", details, posStart, posEnd, context);
    }
}

class ExpectedCharError extends Error {
    public ExpectedCharError(String details, Position posStart, Position posEnd, Context context) {
        super("Expected Character Error", details, posStart, posEnd, context);
    }
}

class StackOverFlowError extends Error {
    public StackOverFlowError(Position posStart, Position posEnd, Context context) {
        super("Stack Overflow Error", "the stack is full", posStart, posEnd, context);
    }
}

class ValueError extends Error {
    public ValueError(String details, Position posStart, Position posEnd, Context context) {
        super("Value Error", details, posStart, posEnd, context);
    }
}
