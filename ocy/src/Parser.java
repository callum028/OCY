import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Parser {
    private final List<Token> tokens;
    private int tokIdx = 0;
    private Token currentTok;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        if(this.tokens.size() > 0) {
            this.currentTok = tokens.get(tokIdx);
        }
    }

    private Token advance() {
        this.tokIdx++;
        if(this.tokIdx < this.tokens.size()) {
            this.currentTok = this.tokens.get(this.tokIdx);
        }
        return this.currentTok;
    }

    private Token decrement() {
        this.tokIdx--;
        this.currentTok = this.tokens.get(this.tokIdx);
        return this.currentTok;
    }

    public ParseResult parse() {
        ParseResult res = this.expression();
        if(res.getError() == null && !this.currentTok.getType().equals(Constants.TT_EOF)) {
            return res.failure(new InvalidSyntaxError("Unexpected Character '%s'".formatted(this.currentTok.getValue()), this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }
        return res;
    }

    private ParseResult expression() {
        ParseResult res = new ParseResult();

        if(this.currentTok.getType().equals(Constants.TT_IDENTIFIER)) {
            Token varName = this.currentTok;
            res.registerAdvancement();
            this.advance();

            if(this.currentTok.getType().equals(Constants.TT_EQ)) {
                res.registerAdvancement();
                this.advance();
                Node expr = res.register(this.expression());
                if(res.getError() != null) {
                    return res;
                }
                return res.success(new VarAssignNode(varName, expr, new Token(Constants.TT_EQ, null, varName.getPosEnd().copy(), null)));
            } else {
                List<String> ops = Arrays.asList(Constants.TT_PLUSE, Constants.TT_MINUSE, Constants.TT_MULE, Constants.TT_DIVE);
                if (ops.contains(this.currentTok.getType())) {
                    Token type = this.currentTok;
                    res.registerAdvancement();
                    this.advance();
                    Node expr = res.register(this.expression());
                    if (res.getError() != null) {
                        return res;
                    }

                    return res.success(new VarAssignNode(varName, expr, type));
                } else {
                    res.registerDecrement();
                    this.decrement();
                }
            }
        }

        List<String> keywords = Arrays.asList("and", "or");
        Fun func = this::compExpr;
        return this.binOp(func, keywords, null, null);
    }

    private ParseResult binOp(Fun func, List<String> keywords, List<String> ops, Fun func2) {
        ParseResult res = new ParseResult();
        Node left = res.register(func.function());

        if(func2 == null) {
            func2 = func;
        }

        if(ops == null) {
            ops = new ArrayList<>();
        }

        if(res.getError() != null) {
            return res;
        }


        while(ops.contains(this.currentTok.getType()) || this.helper(keywords)) {
            Token opTok = this.currentTok;
            res.registerAdvancement();
            this.advance();
            Node right = res.register(func2.function());
            if(res.getError() != null) {
                return res;
            }

            left = new BinOpNode(left, opTok, right);
        }

        return res.success(left);
    }

    private ParseResult compExpr() {
        ParseResult res = new ParseResult();
        if(this.currentTok.matches(Constants.TT_KEYWORD, "not")) {
            Token opTok = this.currentTok;
            res.registerAdvancement();
            this.advance();

            Node node = res.register(this.compExpr());
            if(res.getError() != null) {
                return res;
            }
            return res.success(new UnaryOpNode(opTok, node));
        }

        List<String> ops = Arrays.asList(Constants.TT_EE, Constants.TT_NE, Constants.TT_LTE, Constants.TT_LT, Constants.TT_GTE, Constants.TT_GT);
        Fun func = this::arithExpr;
        Node node = res.register(this.binOp(func, null, ops, null));

        if(res.getError() != null) {
            return res;
        }
        return res.success(node);
    }

    private ParseResult arithExpr() {
        List<String> ops = Arrays.asList(Constants.TT_PLUS, Constants.TT_MINUS);
        Fun func = this::term;
        return this.binOp(func, null, ops, null);
    }

    private ParseResult term() {
        List<String> ops = Arrays.asList(Constants.TT_MUL, Constants.TT_DIV);
        Fun func = this::factor;
        return this.binOp(func, null, ops, null);
    }

    private ParseResult factor() {
        ParseResult res = new ParseResult();
        Token tok = this.currentTok;
        if(tok.getType().equals(Constants.TT_PLUS) || tok.getType().equals(Constants.TT_MINUS)) {
            res.registerAdvancement();
            this.advance();
            Node fact = res.register(this.factor());
            if(res.getError() != null) {
                return res;
            }
            return res.success(new UnaryOpNode(tok, fact));
        }
        return this.power();
    }

    private ParseResult power() {
        List<String> ops = List.of(Constants.TT_POW);
        Fun func = this::call;
        Fun func2 = this::factor;
        return this.binOp(func, null, ops, func2);
    }

    private ParseResult call() {
        ParseResult res = new ParseResult();
        Node at = res.register(this.atom());
        if(res.getError() != null) {
            return res;
        }

        if(this.currentTok.getType().equals(Constants.TT_LPAREN)) {
            res.registerAdvancement();
            this.advance();
            ArrayList<Node> argNodes = new ArrayList<>();

            if(this.currentTok.getType().equals(Constants.TT_RPAREN)) {
                res.registerAdvancement();
                this.advance();
            } else {
                argNodes.add(res.register(this.expression()));
                if(res.getError() != null) {
                    return res.failure(new InvalidSyntaxError("Expected an identifier or a complete logical or mathematical expression", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
                }

                while(Objects.equals(this.currentTok.getType(), Constants.TT_COMMA)) {
                    res.registerAdvancement();
                    this.advance();
                    argNodes.add(res.register(this.expression()));
                    if(res.getError() != null) {
                        return res;
                    }
                }

                if(!Objects.equals(this.currentTok.getType(), Constants.TT_RPAREN)) {
                    return res.failure(new InvalidSyntaxError("Expected ',' or ')'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
                }

                res.registerAdvancement();
                this.advance();
            }
            return res.success(new CallNode(at, argNodes));
        }
        return res.success(at);
    }

    private ParseResult atom() {
        ParseResult res = new ParseResult();
        Token tok = this.currentTok;
        String type = tok.getType();

        if(type.equals(Constants.TT_INT) || type.equals(Constants.TT_FLOAT)) {
            res.registerAdvancement();
            this.advance();
            return res.success(new NumberNode(tok));
        } else if(type.equals(Constants.TT_STRING)) {
            res.registerAdvancement();
            this.advance();
            return res.success(new StringNode(tok));
        } else if(type.equals(Constants.TT_IDENTIFIER)) {
            res.registerAdvancement();
            this.advance();
            return res.success(new VarAccessNode(tok));
        } else if(type.equals(Constants.TT_LPAREN)) {
            res.registerAdvancement();
            this.advance();
            Node expr = res.register(this.expression());
            if(res.getError() != null) {
                return res;
            }

            if(this.currentTok.getType().equals(Constants.TT_RPAREN)) {
                res.registerAdvancement();
                this.advance();
                return res.success(expr);
            } else {
                return res.failure(new InvalidSyntaxError("Expected ')'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
            }
        } else if(type.equals(Constants.TT_LSQUARE)) {
            Node listExpr = res.register(this.listExpr());
            if(res.getError() != null) {
                return res;
            }
            return res.success(listExpr);
        } else if(tok.matches(Constants.TT_KEYWORD, "if")) {
            Node ifExpr = res.register(this.ifExpr());
            if(res.getError() != null) {
                return res;
            }
            return res.success(ifExpr);
        } else if(tok.matches(Constants.TT_KEYWORD, "for")) {
            Node forExpr = res.register(this.forExpr());
            if(res.getError() != null) {
                return res;
            }
            return res.success(forExpr);
        } else if(tok.matches(Constants.TT_KEYWORD, "while")) {
            Node whileExpr = res.register(this.whileExpr());
            if(res.getError() != null) {
                return res;
            }
            return res.success(whileExpr);
        } else if(tok.matches(Constants.TT_KEYWORD, "func")) {
            Node funcDef = res.register(this.funcDef());
            if(res.getError() != null) {
                return res;
            }
            return res.success(funcDef);
        }

        String details;
        if(!this.currentTok.getValue().equals("")) {
            details = "Unexpected Token '%s'".formatted(this.currentTok.getValue());
        } else {
            details = "Unexpected end of input";
        }
        return res.failure(new InvalidSyntaxError(details, tok.getPosStart(), tok.getPosEnd()));
    }

    private ParseResult listExpr() {
        ParseResult res = new ParseResult();
        ArrayList<Node> elementNodes = new ArrayList<>();
        Position posStart = this.currentTok.getPosStart().copy();

        if(!this.currentTok.getType().equals(Constants.TT_LSQUARE)) {
            return res.failure(new InvalidSyntaxError("Expected '['", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        if(this.currentTok.getType().equals(Constants.TT_RSQUARE)) {
            res.registerAdvancement();
            this.advance();
        } else {
            elementNodes.add(res.register(this.expression()));
            if(res.getError() != null) {
                return res.failure(new InvalidSyntaxError("Expected an identifier or a complete logical or mathematical expression", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
            }

            while(this.currentTok.getType().equals(Constants.TT_COMMA)) {
                res.registerAdvancement();
                this.advance();

                elementNodes.add(res.register(this.expression()));
                if(res.getError() != null) {
                    return res;
                }
            }

            if(!this.currentTok.getType().equals(Constants.TT_RSQUARE)) {
                return res.failure(new InvalidSyntaxError("Expected ',' or ']'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
            }

            res.registerAdvancement();
            this.advance();
        }
        return res.success(new ListNode(elementNodes, posStart, this.currentTok.getPosEnd().copy()));
    }

    private ParseResult ifExpr() {
        ParseResult res = new ParseResult();
        ArrayList<ArrayList<Node>> cases = new ArrayList<>();
        Node elseCase = null;

        if(!this.currentTok.matches(Constants.TT_KEYWORD, "if")) {
            return res.failure(new InvalidSyntaxError("Expected 'if'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        Node condition = res.register(this.expression());
        if(res.getError() != null) {
            return res;
        }

        if(!this.currentTok.matches(Constants.TT_KEYWORD, "then")) {
            return res.failure(new InvalidSyntaxError("Expected 'then'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        Node expr = res.register(this.expression());
        if(res.getError() != null) {
            return res;
        }

        cases.add(new ArrayList<>(Arrays.asList(condition, expr)));

        while(this.currentTok.matches(Constants.TT_KEYWORD, "elif")) {
            res.registerAdvancement();
            this.advance();

            condition = res.register(this.expression());
            if(res.getError() != null) {
                return res;
            }

            if(!this.currentTok.matches(Constants.TT_KEYWORD, "then")) {
                return res.failure(new InvalidSyntaxError("Expected 'then'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
            }

            res.registerAdvancement();
            this.advance();

            expr = res.register(this.expression());
            if(res.getError() != null) {
                return res;
            }
            cases.add(new ArrayList<>(Arrays.asList(condition, expr)));
        }

        if(this.currentTok.matches(Constants.TT_KEYWORD, "else")) {
            res.registerAdvancement();
            this.advance();
            elseCase = res.register(this.expression());
            if(res.getError() != null) {
                return res;
            }
        }

        return res.success(new IfNode(cases, elseCase));
    }

    private ParseResult forExpr() {
        ParseResult res = new ParseResult();
        Node stepValue = null;

        if(!this.currentTok.matches(Constants.TT_KEYWORD, "for")) {
            return res.failure(new InvalidSyntaxError("Expected 'for'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        if(!this.currentTok.getType().equals(Constants.TT_IDENTIFIER)) {
            return res.failure(new InvalidSyntaxError("Expected an identifier", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        Token varName = this.currentTok;
        res.registerAdvancement();
        this.advance();

        if(!this.currentTok.getType().equals(Constants.TT_EQ)) {
            return res.failure(new InvalidSyntaxError("Expected '='", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        Node startValue = res.register(this.expression());
        if(res.getError() != null) {
            return res;
        }

        if(!this.currentTok.matches(Constants.TT_KEYWORD, "to")) {
            return res.failure(new InvalidSyntaxError("Expected 'to'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        Node endValue = res.register(this.expression());
        if(res.getError() != null) {
            return res;
        }

        if(this.currentTok.matches(Constants.TT_KEYWORD, "step")) {
            res.registerAdvancement();
            this.advance();

            stepValue = res.register(this.expression());
            if(res.getError() != null) {
                return res;
            }
        }

        if(!this.currentTok.matches(Constants.TT_KEYWORD, "then")) {
            return res.failure(new InvalidSyntaxError("Expected 'then'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        Node body = res.register(this.expression());
        if(res.getError() != null) {
            return res;
        }

        return res.success(new ForNode(varName, startValue, endValue, stepValue, body));
    }

    private ParseResult whileExpr() {
        ParseResult res = new ParseResult();

        if(!this.currentTok.matches(Constants.TT_KEYWORD, "while")) {
            return res.failure(new InvalidSyntaxError("Expected 'while'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        Node condition = res.register(this.expression());
        if(res.getError() != null) {
            return res;
        }

        if(!this.currentTok.matches(Constants.TT_KEYWORD, "then")) {
            return res.failure(new InvalidSyntaxError("Expected 'then'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        Node body = res.register(this.expression());
        if(res.getError() != null) {
            return res;
        }

        return res.success(new WhileNode(condition, body));
    }

    private ParseResult funcDef() {
        ParseResult res = new ParseResult();
        Token varNameTok = null;

        if(!this.currentTok.matches(Constants.TT_KEYWORD, "func")) {
            return res.failure(new InvalidSyntaxError("Expected 'func'", this.advance().getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        if(this.currentTok.getType().equals(Constants.TT_IDENTIFIER)) {
            varNameTok = this.currentTok;
            res.registerAdvancement();
            this.advance();

            if(!this.currentTok.getType().equals(Constants.TT_LPAREN)) {
                return res.failure(new InvalidSyntaxError("Expected '('", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
            }
        } else {
            if(!this.currentTok.getType().equals(Constants.TT_LPAREN)) {
                return res.failure(new InvalidSyntaxError("Expected '('", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
            }
        }

        res.registerAdvancement();
        this.advance();

        ArrayList<Token> argNameToks = new ArrayList<>();
        if(this.currentTok.getType().equals(Constants.TT_IDENTIFIER)) {
            argNameToks.add(this.currentTok);
            res.registerAdvancement();
            this.advance();

            while(this.currentTok.getType().equals(Constants.TT_COMMA)) {
                res.registerAdvancement();
                this.advance();

                if(!this.currentTok.getType().equals(Constants.TT_IDENTIFIER)) {
                    return res.failure(new InvalidSyntaxError("Expected and identifier", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
                }

                argNameToks.add(this.currentTok);
                res.registerAdvancement();
                this.advance();
            }

            if(!this.currentTok.getType().equals(Constants.TT_RPAREN)) {
                return res.failure(new InvalidSyntaxError("Expected ',' or ')'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
            }
        } else {
            if(!this.currentTok.getType().equals(Constants.TT_RPAREN)) {
                return res.failure(new InvalidSyntaxError("Unexpected Token '%s'".formatted(this.currentTok.getValue()), this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
            }
        }

        res.registerAdvancement();
        this.advance();

        if(!this.currentTok.getType().equals(Constants.TT_ARROW)) {
            return res.failure(new InvalidSyntaxError("Expected '=>'", this.currentTok.getPosStart(), this.currentTok.getPosEnd()));
        }

        res.registerAdvancement();
        this.advance();

        Node nodeToReturn = res.register(this.expression());
        if(res.getError() != null) {
            return res;
        }

        return res.success(new FuncDefNode(varNameTok, argNameToks, nodeToReturn));
    }

    private boolean helper(List<String> ops) {
        if(ops == null) {
            return false;
        }
        return this.currentTok.getType().equals(Constants.TT_KEYWORD) && ops.contains(this.currentTok.getValue());
    }
}

@FunctionalInterface
interface Fun {
    ParseResult function();
}

class ParseResult {
    private Node node;
    private Error error;
    private int advanceCount = 0;

    public Node getNode() {
        return node;
    }

    public ParseResult setResult(Node result) {
        this.node = result;
        return this;
    }

    public Error getError() {
        return error;
    }

    public ParseResult setError(Error error) {
        this.error = error;
        return this;
    }

    public int getAdvanceCount() {
        return advanceCount;
    }

    public void registerAdvancement() {
        advanceCount++;
    }

    public void registerDecrement() {
        advanceCount--;
    }

    public Node register(ParseResult res) {
        if(res != null) {
            if(res.getError() != null) {
                this.error = res.getError();
            }
            return res.getNode();
        }
        return null;
    }

    public ParseResult success(Node node) {
        this.node = node;
        return this;
    }

    public ParseResult failure(Error error) {
        this.error = error;
        return this;
    }
}
