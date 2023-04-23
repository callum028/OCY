import java.util.ArrayList;
import java.util.List;

public class Node {
    private final Token tok;
    private Position posStart;
    private Position posEnd;

    public Node(Token tok) {
        this.tok = tok;
        if(this.tok != null) {
            this.posStart = this.tok.getPosStart();
            this.posEnd = this.tok.getPosEnd();
        }
    }

    public String repr() {
        if(this.tok == null) {
            return "";
        }
        return this.tok.repr();
    }

    public Token getTok() {
        return tok;
    }

    public Position getPosStart() {
        return posStart;
    }

    public void setPosStart(Position posStart) {
        this.posStart = posStart;
    }

    public Position getPosEnd() {
        return posEnd;
    }

    public void setPosEnd(Position posEnd) {
        this.posEnd = posEnd;
    }
    public List<Node> getElementNodes() { return null; }
    public Token getVarNameTok() {
        return null;
    }
    public Node getValueNode() {
        return null;
    }
    public Token getOpTok() {
        return null;
    }
    public Node getLeftNode() {return null;}
    public Node getRightNode() {return null;}
    public Node getNode() {return null;}
    public ArrayList<ArrayList<Node>> getCases() {return null;}
    public Node getElseCase() {return null;}
    public Node getConditionNode() {return null;}
    public Node getBodyNode() {return null;}
    public Node getNodeToCall() {return null;}
    public ArrayList<Node> getArgNodes() {
        return null;
    }
    public ArrayList<Token> getArgNameToks() {
        return null;
    }
    public Node getStartValueNode() {
        return null;
    }

    public Node getEndValueNode() {
        return null;
    }

    public Node getStepValueNode() {
        return null;
    }
}

class NumberNode extends Node {
    public NumberNode(Token tok) {
        super(tok);
    }
}

class StringNode extends Node {
    public StringNode(Token tok) {
        super(tok);
    }
}

class ListNode extends Node {
    private final List<Node> elementNodes;

    public ListNode(List<Node> elementNodes, Position posStart, Position posEnd) {
        super(null);
        this.elementNodes = elementNodes;
        this.setPosStart(posStart);
        this.setPosEnd(posEnd);
    }

    public List<Node> getElementNodes() {
        return elementNodes;
    }

    public String repr() {
        String temp = "[";
        for(Node el: this.elementNodes) {
            temp = "%s%s".formatted(temp, el.repr());
        }
        temp += "]";
        return temp;
    }
}

class BinOpNode extends Node {
    private final Node leftNode;
    private final Token opTok;
    private final Node rightNode;

    public BinOpNode(Node leftNode, Token opTok, Node rightNode) {
        super(null);
        this.leftNode = leftNode;
        this.opTok = opTok;
        this.rightNode = rightNode;

        this.setPosStart(this.leftNode.getPosStart().copy());
        this.setPosEnd(this.rightNode.getPosEnd().copy());
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public Token getOpTok() {
        return opTok;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public String repr() {
        return "(%s, %s, %s)".formatted(this.leftNode.repr(), this.opTok.repr(), this.rightNode.repr());
    }
}

class UnaryOpNode extends Node {
    private final Token opTok;
    private final Node node;

    public UnaryOpNode(Token opTok, Node node) {
        super(null);
        this.opTok = opTok;
        this.node = node;

        this.setPosStart(this.opTok.getPosStart().copy());
        this.setPosEnd(this.node.getPosEnd().copy());
    }

    public Token getOpTok() {
        return opTok;
    }

    public Node getNode() {
        return node;
    }

    public String repr() {
        return "(%s, %s)".formatted(this.opTok.repr(), this.node.repr());
    }
}

class VarAccessNode extends Node {
    private final Token varNameTok;

    public VarAccessNode(Token varNameTok) {
        super(null);
        this.varNameTok = varNameTok;

        this.setPosStart(this.varNameTok.getPosStart().copy());
        this.setPosEnd(this.varNameTok.getPosEnd().copy());
    }

    public Token getVarNameTok() {
        return varNameTok;
    }

    public String repr() {
        return "ACCESS: %s".formatted(this.varNameTok.getValue());
    }
}

class VarAssignNode extends Node {
    private final Token varNameTok;
    private final Node valueNode;
    private final Token opTok;

    public VarAssignNode(Token varNameTok, Node valueNode, Token opTok) {
        super(null);
        this.varNameTok = varNameTok;
        this.valueNode = valueNode;
        this.opTok = opTok;

        this.setPosStart(this.varNameTok.getPosStart().copy());
        this.setPosEnd(this.valueNode.getPosEnd().copy());
    }

    public Token getVarNameTok() {
        return varNameTok;
    }

    public Node getValueNode() {
        return valueNode;
    }

    public Token getOpTok() {
        return opTok;
    }

    public String repr() {
        return "ASSIGN: %s, VALUE: %s".formatted(this.varNameTok.getValue(), this.valueNode.repr());
    }
}

class IfNode extends Node {
    private final ArrayList<ArrayList<Node>> cases;
    private final Node elseCase;

    public IfNode(ArrayList<ArrayList<Node>> cases, Node elseCase) {
        super(null);
        this.cases = cases;
        this.elseCase = elseCase;
        this.setPosStart(this.cases.get(0).get(0).getPosStart().copy());
        if(this.elseCase == null) {
            this.setPosEnd(this.cases.get(this.cases.size()-1).get(0).getPosEnd().copy());
        } else {
            this.setPosEnd(this.elseCase.getPosEnd().copy());
        }
    }

    public ArrayList<ArrayList<Node>> getCases() {
        return cases;
    }

    public Node getElseCase() {
        return elseCase;
    }

    public String repr() {
        return "IF NODE";
    }
}

class ForNode extends Node {
    private final Token varNameTok;
    private final Node startValueNode;
    private final Node endValueNode;
    private final Node stepValueNode;
    private final Node bodyNode;

    public ForNode(Token varNameTok, Node startValueNode, Node endValueNode, Node stepValueNode, Node bodyNode) {
        super(null);
        this.varNameTok = varNameTok;
        this.startValueNode = startValueNode;
        this.endValueNode = endValueNode;
        this.stepValueNode = stepValueNode;
        this.bodyNode = bodyNode;

        this.setPosStart(this.varNameTok.getPosStart().copy());
        this.setPosEnd(this.bodyNode.getPosEnd().copy());
    }

    public Token getVarNameTok() {
        return varNameTok;
    }

    public Node getStartValueNode() {
        return startValueNode;
    }

    public Node getEndValueNode() {
        return endValueNode;
    }

    public Node getStepValueNode() {
        return stepValueNode;
    }

    public Node getBodyNode() {
        return bodyNode;
    }

    public String repr() {
        String text = "FOR NODE, START: %s, END: %s, STEP: ".formatted(this.startValueNode.repr(), this.endValueNode.repr());
        if(this.stepValueNode != null) {
            text += this.stepValueNode.repr();
        } else {
            text += "1";
        }
        return text;
    }
}

class WhileNode extends Node {
    private final Node conditionNode;
    private final Node bodyNode;

    public WhileNode(Node conditionNode, Node bodyNode) {
        super(null);
        this.conditionNode = conditionNode;
        this.bodyNode = bodyNode;
        this.setPosStart(this.conditionNode.getPosStart().copy());
        this.setPosEnd(this.bodyNode.getPosEnd().copy());
    }

    public Node getConditionNode() {
        return conditionNode;
    }

    public Node getBodyNode() {
        return bodyNode;
    }

    public String repr() {
        return "WHILE NODE, CONDITION: %s".formatted(this.conditionNode.repr());
    }
}

class FuncDefNode extends Node {
    private final Token varNameTok;
    private final ArrayList<Token> argNameToks;
    private final Node bodyNode;

    public FuncDefNode(Token varNameTok, ArrayList<Token> argNameToks, Node bodyNode) {
        super(null);
        this.varNameTok = varNameTok;
        this.argNameToks = argNameToks;
        this.bodyNode = bodyNode;

        if(this.varNameTok != null) {
            this.setPosStart(this.varNameTok.getPosStart().copy());
        } else if(this.argNameToks.size() > 0) {
            this.setPosStart(this.argNameToks.get(0).getPosStart().copy());
        } else {
            this.setPosStart(this.bodyNode.getPosStart().copy());
        }
        this.setPosEnd(this.bodyNode.getPosEnd().copy());
    }

    public Token getVarNameTok() {
        return varNameTok;
    }

    public ArrayList<Token> getArgNameToks() {
        return argNameToks;
    }

    public Node getBodyNode() {
        return bodyNode;
    }

    public String repr() {
        return "FUNC DEF: %s ,PARAMETERS: %s".formatted(this.varNameTok.getValue(), this.argNameToks.size());
    }
}

class CallNode extends Node {
    private final Node nodeToCall;
    private final ArrayList<Node> argNodes;

    public CallNode(Node nodeToCall, ArrayList<Node> argNodes) {
        super(null);
        this.nodeToCall = nodeToCall;
        this.argNodes = argNodes;
        this.setPosStart(this.nodeToCall.getPosStart().copy());

        if(this.argNodes.size() > 0) {
            this.setPosEnd(this.argNodes.get(this.argNodes.size()-1).getPosEnd().copy());
        } else {
            this.setPosEnd(this.nodeToCall.getPosEnd().copy());
        }
    }

    public Node getNodeToCall() {
        return nodeToCall;
    }

    public ArrayList<Node> getArgNodes() {
        return argNodes;
    }

    public String repr() {
        return "FUNC CALL, PARAMETERS: %s".formatted(this.argNodes.size());
    }
}