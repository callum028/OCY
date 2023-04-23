import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.*;

public class Value {
    private Position posStart;
    private Position posEnd;
    private Context context;
    private String value = "";
    private String type_ = "";

    public Value() {
        this.setPos(null, null);
        this.setContext(null);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType_(String type_) {
        this.type_ = type_;
    }

    public void setPos(Position posStart, Position posEnd) {
        this.posStart = posStart;
        this.posEnd = posEnd;
    }

    public Value setContext(Context context) {
        this.context = context;
        return this;
    }

    public Position getPosStart() {
        return posStart;
    }

    public Position getPosEnd() {
        return posEnd;
    }

    public Context getContext() {
        return context;
    }

    public String getValue() {
        return value;
    }

    public String getType_() {
        return type_;
    }

    public List<Value> getElements() { return null; }

    public String getName() { return null; }

    public ValueReturn addedTo(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn subbedBy(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn multedBy(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn divedBy(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn powedBy(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn getComparisonEq(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn getComparisonNe(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn getComparisonLt(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn getComparisonGt(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn getComparisonLte(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn getComparisonGte(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn andedBy(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn oredBy(Value other) {
        return new ValueReturn(this.illegalOperation(other, ""));
    }

    public ValueReturn notted() {
        return new ValueReturn(this.illegalOperation(this, ""));
    }

    public RTResult execute(List<Value> args) {
        RTResult err = new RTResult();
        return err.failure(this.illegalOperation(null, ""));
    }
    
    public boolean isTrue() {
        return false;
    }
    
    public Value remake() {
        return null;
    }
    
    public Error illegalOperation(Value other, String op) {
        if(other == null) {
            other = this;
        }
        
        return new RunTimeError("Illegal operation %s between types %s and %s".formatted(op, this.type_, other.type_), other.posStart, other.posEnd, this.context);
    }

    public String repr() {
        return "";
    }
}

class Int extends Value {
    public Int(int value) {
        super();
        this.setValue(Integer.toString(value));
        this.setType_("Integer");
    }

    public ValueReturn addedTo(Value other) {
        if(other instanceof Int) {
            Int temp = new Int(Integer.parseInt(this.getValue()) + Integer.parseInt(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        } else if(other instanceof Float) {
            Float temp = new Float(Integer.parseInt(this.getValue()) + Double.parseDouble(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "+"));
    }

    public ValueReturn subbedBy(Value other) {
        if(other instanceof Int) {
            Int temp = new Int(Integer.parseInt(this.getValue()) - Integer.parseInt(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        } else if(other instanceof Float) {
            Float temp = new Float(Integer.parseInt(this.getValue()) - Double.parseDouble(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "-"));
    }

    public ValueReturn multedBy(Value other) {
        if(other instanceof Int) {
            Int temp = new Int(Integer.parseInt(this.getValue()) * Integer.parseInt(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        } else if(other instanceof Float) {
            Float temp = new Float(Integer.parseInt(this.getValue()) * Double.parseDouble(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "*"));
    }

    public ValueReturn divedBy(Value other) {
        if(other instanceof Int || other instanceof Float) {
            if(Double.parseDouble(other.getValue()) == 0) {
                return new ValueReturn(new RunTimeError("Division by 0", other.getPosStart(), other.getPosEnd(), this.getContext()));
            }

            Float temp = new Float(Double.parseDouble(this.getValue()) / Double.parseDouble(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "/"));
    }

    public ValueReturn powedBy(Value other) {
        if(other instanceof Int) {
            Int temp = new Int((int) pow(Integer.parseInt(this.getValue()), Integer.parseInt(other.getValue())));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        } else if(other instanceof Float) {
            Float temp = new Float(pow(Double.parseDouble(this.getValue()), Double.parseDouble(other.getValue())));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "**"));
    }

    public ValueReturn getComparisonEq(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) == Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, "=="));
    }

    public ValueReturn getComparisonNe(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) != Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, "!="));
    }

    public ValueReturn getComparisonLt(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) < Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, "<"));
    }

    public ValueReturn getComparisonGt(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) > Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, ">"));
    }

    public ValueReturn getComparisonLte(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) <= Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, "<="));
    }

    public ValueReturn getComparisonGte(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) >= Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, ">="));
    }

    public ValueReturn andedBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() && other.isTrue()));
    }

    public ValueReturn oredBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() || other.isTrue()));
    }

    public ValueReturn notted() {
        return new ValueReturn(new Boolean(!this.isTrue()));
    }

    public boolean isTrue() {
        return Double.parseDouble(this.getValue()) > 0;
    }

    public Int remake() {
        Int copy = new Int(Integer.parseInt(this.getValue()));
        copy.setPos(this.getPosStart(), this.getPosEnd());
        copy.setContext(this.getContext());
        return copy;
    }

    public String repr() {
        return this.getValue();
    }
}
class Float extends Value {
    public Float(double value) {
        super();
        this.setValue(Double.toString(value));
        this.setType_("Float");
    }

    public ValueReturn addedTo(Value other) {
        if(other instanceof Int || other instanceof Float) {
            Float temp = new Float(Double.parseDouble(this.getValue()) + Double.parseDouble(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "+"));
    }

    public ValueReturn subbedBy(Value other) {
        if(other instanceof Int || other instanceof Float) {
            Float temp = new Float(Double.parseDouble(this.getValue()) - Double.parseDouble(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "-"));
    }

    public ValueReturn multedBy(Value other) {
        if(other instanceof Int || other instanceof Float) {
            Float temp = new Float(Double.parseDouble(this.getValue()) * Double.parseDouble(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "*"));
    }

    public ValueReturn divedBy(Value other) {
        if(other instanceof Int || other instanceof Float) {
            if(Double.parseDouble(other.getValue()) == 0) {
                return new ValueReturn(new RunTimeError("Division by 0", other.getPosStart(), other.getPosEnd(), this.getContext()));
            }
            Float temp = new Float(Double.parseDouble(this.getValue()) / Double.parseDouble(other.getValue()));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "/"));
    }

    public ValueReturn powedBy(Value other) {
        if(other instanceof Int || other instanceof Float) {
            Float temp = new Float(pow(Double.parseDouble(this.getValue()), Double.parseDouble(other.getValue())));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        }
        return new ValueReturn(this.illegalOperation(other, "**"));
    }

    public ValueReturn getComparisonEq(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) == Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, "=="));
    }

    public ValueReturn getComparisonNe(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) != Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, "!="));
    }

    public ValueReturn getComparisonLt(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) < Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, "<"));
    }

    public ValueReturn getComparisonGt(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) > Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, ">"));
    }

    public ValueReturn getComparisonLte(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) <= Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, "<="));
    }

    public ValueReturn getComparisonGte(Value other) {
        if(other instanceof Int || other instanceof Float) {
            return new ValueReturn(new Boolean(Double.parseDouble(this.getValue()) >= Double.parseDouble(other.getValue())));
        }
        return new ValueReturn(this.illegalOperation(other, ">="));
    }

    public ValueReturn andedBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() && other.isTrue()));
    }

    public ValueReturn oredBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() || other.isTrue()));
    }

    public ValueReturn notted() {
        return new ValueReturn(new Boolean(!this.isTrue()));
    }

    public boolean isTrue() {
        return Double.parseDouble(this.getValue()) > 0;
    }

    public Float remake() {
        Float copy = new Float(Double.parseDouble(this.getValue()));
        copy.setPos(this.getPosStart(), this.getPosEnd());
        copy.setContext(this.getContext());
        return copy;
    }

    public String repr() {
        return this.getValue();
    }
}

class Str extends Value {
    public Str(String value) {
        super();
        this.setValue(value);
        this.setType_("String");
    }

    public ValueReturn addedTo(Value other) {
        if(other instanceof Str) {
            Str temp = new Str(this.getValue() + other.getValue());
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        } else {
            return new ValueReturn(this.illegalOperation(other, "+"));
        }
    }

    public ValueReturn multedBy(Value other) {
        if(other instanceof Int) {
            Str temp = new Str(this.getValue().repeat(Integer.parseInt(other.getValue())));
            temp.setContext(this.getContext());
            return new ValueReturn(temp);
        } else {
            return new ValueReturn(this.illegalOperation(other, "*"));
        }
    }

    public ValueReturn andedBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() && other.isTrue()));
    }

    public ValueReturn oredBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() || other.isTrue()));
    }

    public ValueReturn notted() {
        return new ValueReturn(new Boolean(!this.isTrue()));
    }

    public boolean isTrue() {
        return !this.getValue().isEmpty();
    }

    public Str remake() {
        Str copy = new Str(this.getValue());
        copy.setPos(this.getPosStart(), this.getPosEnd());
        copy.setContext(this.getContext());
        return copy;
    }

    public String repr() {
        return "%s".formatted(this.getValue());
    }
}

class Boolean extends Value {
    public static Boolean trueValue = new Boolean(true);
    public static Boolean falseValue = new Boolean(false);
    public Boolean(boolean value) {
        super();
        this.setValue(value ? "true" : "false");
        this.setType_("Boolean");
    }

    public ValueReturn getComparisonEq(Value other) {
        return new ValueReturn(new Boolean(this.getValue().equals(other.getValue())));
    }

    public ValueReturn getComparisonNe(Value other) {
        return new ValueReturn(new Boolean(!this.getValue().equals(other.getValue())));
    }

    public ValueReturn andedBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() && other.isTrue()));
    }

    public ValueReturn oredBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() || other.isTrue()));
    }

    public ValueReturn notted() {
        return new ValueReturn(new Boolean(!this.getValue().equals("true")));
    }

    public boolean isTrue() {
        return this.getValue().equals("true");
    }

    public Boolean remake() {
        Boolean copy = new Boolean(this.getValue().equals("true"));
        copy.setPos(this.getPosStart(), this.getPosEnd());
        copy.setContext(this.getContext());
        return copy;
    }

    public String repr() {
        return this.getValue();
    }
}

class Null extends Value {
    public Null() {
        super();
        this.setValue("null");
        this.setType_("null");
    }

    public ValueReturn getComparisonEq(Value other) {
        if(other instanceof Null) {
            return new ValueReturn(new Boolean(true));
        }
        return new ValueReturn(new Boolean(false));
    }

    public ValueReturn getComparisonNe(Value other) {
        if(other instanceof Null) {
            return new ValueReturn(new Boolean(false));
        }
        return new ValueReturn(new Boolean(true));
    }

    public Null remake() {
        Null copy = new Null();
        copy.setPos(this.getPosStart(), this.getPosEnd());
        copy.setContext(this.getContext());
        return copy;
    }
}

class List_ extends Value {
    private final List<Value> elements;
    public List_(List<Value> elements) {
        super();
        this.elements = elements;
    }

    public List<Value> getElements() {
        return elements;
    }

    private void push(Value element) {
        this.elements.add(element);
    }

    private boolean remove(Integer index) {
        try {
            this.elements.remove(index);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private void concat(List<Value> other) {
        this.elements.addAll(other);
    }

    public ValueReturn addedTo(Value other) {
        List_ newList = this.remake();
        newList.push(other);
        return new ValueReturn(newList);
    }

    public ValueReturn subbedBy(Value other) {
        if(other instanceof Int) {
            List_ newList = this.remake();
            boolean result = newList.remove(Integer.parseInt(other.getValue()));
            if(result) {
                return new ValueReturn(newList);
            }
            return new ValueReturn(new RunTimeError("Index %s is out of range".formatted(other.getValue()), other.getPosStart(), other.getPosEnd(), this.getContext()));
        } else {
            return new ValueReturn(this.illegalOperation(other, "-"));
        }
    }

    public ValueReturn multedBy(Value other) {
        if(other instanceof List_) {
            List_ newList = this.remake();
            newList.concat(other.getElements());
            return new ValueReturn(newList);
        } else {
            return new ValueReturn(this.illegalOperation(other, "*"));
        }
    }

    public ValueReturn divedBy(Value other) {
        if(other instanceof Int) {
            try {
                Value el = this.elements.get(Integer.parseInt(other.getValue()));
                return new ValueReturn(el);
            } catch (Exception e){
                return new ValueReturn(new RunTimeError("Index %s out of range".formatted(other.getValue()), other.getPosStart(), other.getPosEnd(), this.getContext()));
            }
        } else {
            return new ValueReturn(this.illegalOperation(other, "/"));
        }
    }

    public ValueReturn andedBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() && other.isTrue()));
    }

    public ValueReturn oredBy(Value other) {
        return new ValueReturn(new Boolean(this.isTrue() || other.isTrue()));
    }

    public ValueReturn notted() {
        return new ValueReturn(new Boolean(!this.isTrue()));
    }

    public boolean isTrue() {
        return this.elements.size() != 0;
    }

    public List_ remake() {
        List<Value> els = new ArrayList<>(this.elements);
        List_ copy = new List_(els);
        copy.setPos(this.getPosStart(), this.getPosEnd());
        copy.setContext(this.getContext());
        return copy;
    }

    public String repr() {
        String text = "[";
        for(Value el : this.elements) {
            text = "%s%s, ".formatted(text, el.repr());
        }
        if(text.length() != 0) {
            text = text.substring(0, text.length() - 2);
        }
        text += "]";
        return text;
    }
}

class BaseFunction extends Value {
    private String name;

    public BaseFunction(String name) {
        super();
        this.name = name != null ? name : "<anonymous>";
    }

    public String getName() {
        return this.name;
    }

    public Context generateNewContext() {
        Context newContext = new Context(this.getName(), this.getContext(), this.getPosStart());
        newContext.setSymbolTable(new SymbolTable(newContext.getParent().getSymbolTable()));
        return newContext;
    }

    public RTResult checkArgs(List<String> argNames, List<Value> args) {
        RTResult res = new RTResult();

        if(args.size() != argNames.size()) {
            return res.failure(new RunTimeError("Expected %s but got %s arguments passed into %s".formatted(argNames.size(), args.size(), this.getName()), this.getPosStart(), this.getPosEnd(), this.getContext()));
        }

        return res.success(null);
    }

    public void populateArgs(List<String> argNames, List<Value> args, Context execCtx) {
        for(int i = 0; i < args.size(); i++) {
            String argName = argNames.get(i);
            Value argValue = args.get(i);
            argValue.setContext(execCtx);
            execCtx.getSymbolTable().set(argName, argValue);
        }
    }

    public RTResult checkAndPopulateArgs(List<String> argNames, List<Value> args, Context execCtx) {
        RTResult res = new RTResult();
        res.register(this.checkArgs(argNames, args));
        if(res.getError() != null) {
            return res;
        }
        this.populateArgs(argNames, args, execCtx);
        return res.success(null);
    }
}

class Function extends BaseFunction {
    private Node bodyNode;
    private List<String> argNames;

    public Function(String name, Node bodyNode, List<String> argNames) {
        super(name);
        this.setValue("<Function %s>".formatted(name));
        this.setType_("Function");
        this.bodyNode = bodyNode;
        this.argNames = argNames;
    }

    public RTResult execute(List<Value> args) {
        RTResult res = new RTResult();
        Interpreter interpreter = new Interpreter();
        Context execCtx = this.generateNewContext();

        this.checkAndPopulateArgs(this.argNames, args, execCtx);
        RTResult temp = interpreter.visit(this.bodyNode, execCtx);

        Value value = res.register(temp);
        if(res.getError() != null) {
            return res;
        }
        return res.success(value);
    }

    public Function remake() {
        Function copy = new Function(this.getName(), this.bodyNode, this.argNames);
        copy.setContext(this.getContext());
        copy.setPos(this.getPosStart(), this.getPosEnd());
        return copy;
    }

    public String repr() {
        return "<Function %s>".formatted(this.getName());
    }
}

class BuiltInFunction extends BaseFunction {
    List<String> argNames = new ArrayList<String>();
    public BuiltInFunction(String name, List<String> argNames) {
        super(name);
        this.setValue("<BuiltInFunction %s>".formatted(name));
        this.setType_("BuiltInFunction");
        this.argNames = argNames;
    }

    public RTResult execute(List<Value> args) {
        RTResult res = new RTResult();
        Context execCtx = this.generateNewContext();
        res.register(this.checkAndPopulateArgs(this.argNames, args, execCtx));
        if(res.getError() != null) {
            return res;
        }

        Value returnValue = res.register(this.method(execCtx));
        if(res.getError() != null) {
            return res;
        }
        return res.success(returnValue);

    }

    public RTResult method(Context execCtx) {
        return null;
    }

    public BuiltInFunction remake() {
        BuiltInFunction copy = new BuiltInFunction(this.getName(), new ArrayList<String>());
        copy.setContext(this.getContext());
        copy.setPos(this.getPosStart(), this.getPosEnd());
        return copy;
    }

    public String repr() {
        return "<BuiltInFunction %s>".formatted(this.getName());
    }
}

class BuiltInFunctionPrint extends BuiltInFunction {
    public BuiltInFunctionPrint() {
        super("print", new ArrayList<>(List.of("value")));
    }

    public RTResult method(Context execCtx) {
        RTResult res = new RTResult();
        Value value = execCtx.getSymbolTable().get("value");
        System.out.println(value.repr());
        return res.success(new Null());
    }

    public BuiltInFunctionPrint remake() {
        BuiltInFunctionPrint copy = new BuiltInFunctionPrint();
        copy.setContext(this.getContext());
        copy.setPos(this.getPosStart(), this.getPosEnd());
        return copy;
    }
}

class BuiltInFunctionInput extends BuiltInFunction {
    public BuiltInFunctionInput() {
        super("input", new ArrayList<>(List.of("value")));
    }

    public RTResult method(Context execCtx) {
        RTResult res = new RTResult();
        Value value = execCtx.getSymbolTable().get("value");
        Scanner scanner = new Scanner(System.in);
        System.out.print(value.repr());
        String text = scanner.nextLine();
        return res.success(new Str(text));
    }

    public BuiltInFunctionInput remake() {
        BuiltInFunctionInput copy = new BuiltInFunctionInput();
        copy.setContext(this.getContext());
        copy.setPos(this.getPosStart(), this.getPosEnd());
        return copy;
    }
}

class BuiltInFunctionStr extends BuiltInFunction {
    public BuiltInFunctionStr() {
        super("str", new ArrayList<>(List.of("value")));
    }

    public RTResult method(Context execCtx) {
        RTResult res = new RTResult();
        Value value = execCtx.getSymbolTable().get("value");
        return res.success(new Str(value.getValue()));
    }

    public BuiltInFunctionStr remake() {
        BuiltInFunctionStr copy = new BuiltInFunctionStr();
        copy.setContext(this.getContext());
        copy.setPos(this.getPosStart(), this.getPosEnd());
        return copy;
    }
}

class BuiltInFunctionInt extends BuiltInFunction {
    public BuiltInFunctionInt() {
        super("str", new ArrayList<>(List.of("value")));
    }

    public RTResult method(Context execCtx) {
        RTResult res = new RTResult();
        Value value = execCtx.getSymbolTable().get("value");
        try {
            return res.success(new Int(Integer.parseInt(value.getValue())));
        } catch (NumberFormatException e){
            return res.failure(new ValueError("%s %s cannot be converted to Integer".formatted(value.getType_(), value.repr()), this.getPosStart(), this.getPosEnd(), this.getContext()));
        }

    }

    public BuiltInFunctionInt remake() {
        BuiltInFunctionInt copy = new BuiltInFunctionInt();
        copy.setContext(this.getContext());
        copy.setPos(this.getPosStart(), this.getPosEnd());
        return copy;
    }
}

class BuiltInFunctionType extends BuiltInFunction {
    public BuiltInFunctionType() {
        super("str", new ArrayList<>(List.of("value")));
    }

    public RTResult method(Context execCtx) {
        RTResult res = new RTResult();
        Value value = execCtx.getSymbolTable().get("value");
        return res.success(new Str(value.getType_()));
    }

    public BuiltInFunctionType remake() {
        BuiltInFunctionType copy = new BuiltInFunctionType();
        copy.setContext(this.getContext());
        copy.setPos(this.getPosStart(), this.getPosEnd());
        return copy;
    }
}

class ValueReturn {
    private Value value = null;
    private Error error = null;

    public ValueReturn(Value value) {
        this.value = value;
    }

    public ValueReturn(Error error) {
        this.error = error;
    }

    public Value getValue() {
        return value;
    }

    public Error getError() {
        return error;
    }
}
