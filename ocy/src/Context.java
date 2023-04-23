public class Context {
    private final String name;
    private final Context parent;
    private final Position entryPos;
    private SymbolTable symbolTable = null;

    public Context(String name, Context parent, Position entryPos) {
        this.name = name;
        this.parent = parent;
        this.entryPos = entryPos;
    }

    public String getName() {
        return name;
    }

    public Context getParent() {
        return parent;
    }

    public Position getEntryPos() {
        return entryPos;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
}
