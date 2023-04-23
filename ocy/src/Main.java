import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    private static final SymbolTable globalSymbolTable = new SymbolTable(null);

    public static String run(String text) {
        Lexer lex = new Lexer(text);
        LexerReturn res = lex.makeTokens();
        if(res.getError() != null) {
            return res.getError().asString();
        }

        Parser parser = new Parser(res.getResult());
        ParseResult ast = parser.parse();
        if(ast.getError() != null) {
            return ast.getError().asString();
        }


        Interpreter interpreter = new Interpreter();
        Context context = new Context("<Program>", null, null);
        context.setSymbolTable(globalSymbolTable);
        RTResult result = interpreter.visit(ast.getNode(), context);

        if(result.getError() != null) {
            return result.getError().asString();
        }
        if(result.getValue() != null && result.getOutput())  {
            return result.getValue().repr();
        }
        return null;
    }

    public static void terminal() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("\u001B[37mOCY >> ");
            String text = scanner.nextLine();
            if(text.equals("")) {
                break;
            }

            String result = run(text);
            if(result != null) {
                System.out.println(result);
            }
        }
    }

    public static void main(String[] args) {
        initSymbolTable();
        if(args.length == 0) {
            terminal();
        } else {
            Scanner scn;
            try {
                scn = new Scanner(new File(args[0]));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("\n%s was not found".formatted(args[0]));
            }

            while (scn.hasNextLine()) {
                String result = run(scn.nextLine());
                if (result != null) {
                    System.out.println(result);
                }
            }
        }
    }

    public static void initSymbolTable() {
        globalSymbolTable.set("null", new Null());
        globalSymbolTable.set("true", Boolean.trueValue);
        globalSymbolTable.set("false", Boolean.falseValue);
        globalSymbolTable.set("print", new BuiltInFunctionPrint());
        globalSymbolTable.set("input", new BuiltInFunctionInput());
        globalSymbolTable.set("str", new BuiltInFunctionStr());
        globalSymbolTable.set("int", new BuiltInFunctionInt());
        globalSymbolTable.set("type", new BuiltInFunctionType());
    }
}

//TODO
// Alter the for loop syntax in the parser to java version
// make more built-in functions
// alter comparisons in value to be more type inclusive
// fix integer cap and limit

//BUILD COMMANDS
// package: jar cfe ocy.jar Main *.class
// run: java -jar ocy.jar [filename]

// REECE
// balls (rtw)