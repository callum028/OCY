import java.util.ArrayList;
import java.util.List;

public class Interpreter {
    public InterpreterMethod method = this::noVisitMethod;

    public RTResult noVisitMethod(Node node, Context context) {
        RTResult res = new RTResult();
        return res.failure(null);
    }

    public RTResult visit(Node node, Context context) {
        if(node instanceof NumberNode) {
            this.method = this::visitNumberNode;
        } else if(node instanceof StringNode) {
            this.method = this::visitStringNode;
        } else if(node instanceof ListNode) {
            this.method = this::visitListNode;
        }
        else if(node instanceof BinOpNode) {
            this.method = this::visitBinOpNode;
        }
        else if(node instanceof UnaryOpNode) {
            this.method = this::visitUnaryOpNode;
        }
        else if(node instanceof VarAccessNode) {
            this.method = this::visitVarAccessNode;
        }
        else if(node instanceof VarAssignNode) {
            this.method = this::visitVarAssignNode;
        }
        else if(node instanceof IfNode) {
            this.method = this::visitIfNode;
        }
        else if(node instanceof ForNode) {
            this.method = this::visitForNode;
        }
        else if(node instanceof WhileNode) {
            this.method = this::visitWhileNode;
        }
        else if(node instanceof FuncDefNode) {
            this.method = this::visitFuncDefNode;
        }
        else if(node instanceof CallNode) {
            this.method = this::visitCallNode;
        }

        try {
            return this.method.execute(node, context);
        }  catch(StackOverflowError e) {
            return new RTResult().failure(new StackOverFlowError(node.getPosStart(), node.getPosEnd(), context));
        }

    }

    public RTResult visitNumberNode(Node node, Context context) {
        Value num;
        if(Double.parseDouble(node.getTok().getValue()) % 1 == 0) {
            num = new Int(Integer.parseInt(node.getTok().getValue()));
        } else {
            num = new Float(Double.parseDouble(node.getTok().getValue()));
        }

        num.setContext(context);
        num.setPos(node.getPosStart(), node.getPosEnd());
        return new RTResult().success(num);
    }

    public RTResult visitStringNode(Node node, Context context) {
        Str string = new Str(node.getTok().getValue());
        string.setContext(context);
        string.setPos(node.getPosStart(), node.getPosEnd());
        return new RTResult().success(string);
    }

    public RTResult visitListNode(Node node, Context context) {
        RTResult res = new RTResult();
        List<Value> elements = new ArrayList<>();

        for(Node elementNode : node.getElementNodes()) {
            elements.add(res.register(this.visit(elementNode, context)));
            if(res.getError() != null) {
                return res;
            }
        }

        List_ list = new List_(elements);
        list.setContext(context);
        list.setPos(node.getPosStart(), node.getPosEnd());
        return new RTResult().success(list);
    }

    public RTResult visitVarAccessNode(Node node, Context context) {
        RTResult res = new RTResult();
        String varName = node.getVarNameTok().getValue();
        if(!context.getSymbolTable().exists(varName)) {
            return res.failure(new RunTimeError("%s is not defined".formatted(varName), node.getPosStart(), node.getPosEnd(), context));
        }

        Value value = context.getSymbolTable().get(varName);
        value = value.remake();
        value.setContext(context);
        value.setPos(node.getPosStart(), node.getPosEnd());
        return res.success(value);
    }

    public RTResult visitVarAssignNode(Node node, Context context) {
        RTResult res = new RTResult();
        String varName = node.getVarNameTok().getValue();
        Value value = res.register(this.visit(node.getValueNode(), context));
        if(res.getError() != null) {
            return res;
        }

        if(node.getOpTok().getType().equals(Constants.TT_EQ)) {
            context.getSymbolTable().set(varName, value);
            return res.success(value);
        } else {
            if(!context.getSymbolTable().exists(varName)) {
                return res.failure(new RunTimeError("%s is not defined".formatted(varName), node.getPosStart(), node.getPosEnd(), context));
            }

            Value previous = context.getSymbolTable().get(varName);
            Value newValue = res.register(this.visit(node.getValueNode(), context));
            if(res.getError() != null) {
                return res;
            }

            switch (node.getOpTok().getType()) {
                case Constants.TT_PLUSE -> newValue = previous.addedTo(newValue).getValue();
                case Constants.TT_MINUSE -> newValue = previous.subbedBy(newValue).getValue();
                case Constants.TT_MULE -> newValue = previous.multedBy(newValue).getValue();
                case Constants.TT_DIVE -> newValue = previous.divedBy(newValue).getValue();
            }

            if(res.getError() != null) {
                return res;
            }

            context.getSymbolTable().set(varName, newValue);
            return res.success(newValue);
        }
    }

    public RTResult visitBinOpNode(Node node, Context context) {
        RTResult res = new RTResult();
        Value left = res.register(this.visit(node.getLeftNode(), context));
        if(res.getError() != null) {
            return res;
        }

        Value right = res.register(this.visit(node.getRightNode(), context));
        if(res.getError() != null) {
            return res;
        }

        ValueReturn result = null;

        switch (node.getOpTok().getType()) {
            case Constants.TT_PLUS -> result = left.addedTo(right);
            case Constants.TT_MINUS -> result = left.subbedBy(right);
            case Constants.TT_MUL -> result = left.multedBy(right);
            case Constants.TT_DIV -> result = left.divedBy(right);
            case Constants.TT_POW -> result = left.powedBy(right);
            case Constants.TT_EE -> result = left.getComparisonEq(right);
            case Constants.TT_NE -> result = left.getComparisonNe(right);
            case Constants.TT_LT -> result = left.getComparisonLt(right);
            case Constants.TT_GT -> result = left.getComparisonGt(right);
            case Constants.TT_LTE -> result = left.getComparisonLte(right);
            case Constants.TT_GTE -> result = left.getComparisonGte(right);
        }

        if(node.getOpTok().matches(Constants.TT_KEYWORD, "and")) {
            result = left.andedBy(right);
        } else if(node.getOpTok().matches(Constants.TT_KEYWORD, "or")) {
            result = left.oredBy(right);
        }

        if(result == null) {
            return res.failure(new RunTimeError("Unexpected operation", left.getPosStart(), right.getPosEnd(), context));
        }

        if(result.getError() != null) {
            return res.failure(result.getError());
        }

        result.getValue().setPos(node.getPosStart(), node.getPosEnd());
        return res.success(result.getValue());
    }

    public RTResult visitUnaryOpNode(Node node, Context context) {
        RTResult res = new RTResult();
        Value number = res.register(this.visit(node.getNode(), context));
        if(res.getError() != null) {
            return res;
        }

        ValueReturn result = null;
        if(node.getOpTok().getType().equals(Constants.TT_MINUS)) {
            result = number.multedBy(new Int(-1));
        } else if(node.getOpTok().getType().equals(Constants.TT_PLUS)) {
            result = number.multedBy(new Int(1));
        } else if(node.getOpTok().matches(Constants.TT_KEYWORD, "not")) {
            result = number.notted();
        }

        if(result == null) {
            return res.failure(new RunTimeError("Unexpected operation", node.getPosStart(), node.getPosEnd(), context));
        }

        if(result.getError() != null) {
            return res.failure(result.getError());
        }

        result.getValue().setPos(node.getPosStart(), node.getPosEnd());
        return res.success(result.getValue());
    }

    public RTResult visitIfNode(Node node, Context context) {
        RTResult res = new RTResult();
        for(int i=0; i<node.getCases().size(); i++) {
            Node condition = node.getCases().get(i).get(0);
            Node expr = node.getCases().get(i).get(1);

            Value conditionValue = res.register(this.visit(condition, context));
            if(res.getError() != null) {
                return res;
            }

            if(conditionValue.isTrue()) {
                Value exprValue = res.register(this.visit(expr, context));
                if(res.getError() != null) {
                    return res;
                }
                return res.success(exprValue);
            }
        }

        if(node.getElseCase() != null) {
            Value elseValue = res.register(this.visit(node.getElseCase(), context));
            if(res.getError() != null) {
                return res;
            }
            return res.success(elseValue);
        }

        return res.success(null);
    }

    public RTResult visitForNode(Node node, Context context) {
        RTResult res = new RTResult();
        List<Value> elements = new ArrayList<>();

        Value startValue = res.register(this.visit(node.getStartValueNode(), context));
        if(res.getError() != null) {
            return res;
        }

        Value endValue = res.register(this.visit(node.getEndValueNode(), context));
        if(res.getError() != null) {
            return res;
        }

        Value stepValue = res.register(this.visit(node.getStepValueNode(), context));
        if(res.getError() != null) {
            return res;
        }

        int i = Integer.parseInt(startValue.getValue());
        ForCondition condition;
        if(Integer.parseInt(stepValue.getValue()) >= 0) {
            condition = (x, end) -> x < end;
        } else {
            condition = (x, end) -> x > end;
        }

        if(context.getSymbolTable().exists(node.getVarNameTok().getValue())) {
            return res.failure(new RunTimeError("Variable %s already exists and cannot be used in the for loop".formatted(node.getVarNameTok().getValue()), node.getVarNameTok().getPosStart(), node.getVarNameTok().getPosEnd(), context));
        }

        while(condition.execute(i, Integer.parseInt(endValue.getValue()))) {
            context.getSymbolTable().set(node.getVarNameTok().getValue(), new Int(i));
            i += Integer.parseInt(stepValue.getValue());

            elements.add(res.register(this.visit(node.getBodyNode(), context)));
            if(res.getError() != null) {
                return res;
            }
        }

        context.getSymbolTable().remove(node.getVarNameTok().getValue());

        List_ list = new List_(elements);
        list.setContext(context);
        list.setPos(node.getPosStart(), node.getPosEnd());
        return res.success(list);
    }

    public RTResult visitWhileNode(Node node, Context context) {
        RTResult res = new RTResult();
        List<Value> elements = new ArrayList<>();

        while(true) {
            Value condition = res.register(this.visit(node.getConditionNode(), context));
            if(res.getError() != null) {
                return res;
            }

            if(!condition.isTrue()) {
                break;
            }

            elements.add(res.register(this.visit(node.getBodyNode(), context)));
            if(res.getError() != null) {
                return res;
            }
        }

        List_ list = new List_(elements);
        list.setContext(context);
        list.setPos(node.getPosStart(), node.getPosEnd());
        return res.success(list);
    }

    public RTResult visitFuncDefNode(Node node, Context context) {
        RTResult res = new RTResult();
        String funcName = null;

        if(node.getVarNameTok() != null) {
            funcName = node.getVarNameTok().getValue();
        }

        Node bodyNode = node.getBodyNode();
        List<String> argNames = new ArrayList<>();

        for(Token argName : node.getArgNameToks()) {
            argNames.add(argName.getValue());
        }

        Function funcValue = new Function(funcName, bodyNode, argNames);
        funcValue.setContext(context);
        funcValue.setPos(node.getPosStart(), node.getPosEnd());

        if(node.getVarNameTok() != null) {
            context.getSymbolTable().set(funcName, funcValue);
        }

        return res.success(funcValue);
    }

    public RTResult visitCallNode(Node node, Context context) {
        RTResult res = new RTResult();
        List<Value> args = new ArrayList<>();

        Value valueToCall = res.register(this.visit(node.getNodeToCall(), context));
        if(res.getError() != null) {
            return res;
        }

        valueToCall = valueToCall.remake();
        valueToCall.setPos(node.getPosStart(), node.getPosEnd());

        for(Node argNode : node.getArgNodes()) {
            args.add(res.register(this.visit(argNode, context)));
            if(res.getError() != null) {
                return res;
            }
        }

        RTResult temp = valueToCall.execute(args);

        Value returnValue = res.register(temp);
        if(res.getError() != null) {
            return res;
        }

        returnValue = returnValue.remake();
        returnValue.setContext(context);
        returnValue.setPos(node.getPosStart(), node.getPosEnd());
        return res.success(returnValue);
    }
}

@FunctionalInterface
interface InterpreterMethod {
    RTResult execute(Node node, Context context);
}

@FunctionalInterface
interface ForCondition {
    boolean execute(int x, int end);
}
