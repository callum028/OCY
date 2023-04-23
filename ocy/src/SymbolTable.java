import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, Value> symbols = new HashMap<>();
    private SymbolTable parent = null;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public Value get(String name) {
        Value value = this.symbols.getOrDefault(name, null);

        if(value == null && this.parent != null) {
            return this.parent.get(name);
        }

        return value;
    }

    public boolean exists(String key) {
        if(this.symbols.containsKey(key)) {
            return true;
        }

        if(this.parent != null) {
            return this.parent.exists(key);
        }
        return false;
    }

    public void set(String name, Value value) {
        this.symbols.put(name, value);
    }

    public void remove(String name) {
        this.symbols.remove(name);
    }


}
