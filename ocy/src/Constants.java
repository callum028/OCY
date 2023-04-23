import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String TT_INT = "INT";
    public static final String TT_FLOAT = "FLOAT";
    public static final String TT_STRING = "STRING";
    public static final String TT_PLUS = "PLUS";
    public static final String TT_PLUSE = "PLUSE";
    public static final String TT_MINUS = "MINUS";
    public static final String TT_MINUSE = "MINUSE";
    public static final String TT_MUL = "MUL";
    public static final String TT_MULE = "MULE";
    public static final String TT_DIV = "DIV";
    public static final String TT_DIVE = "DIVE";
    public static final String TT_POW = "POW";
    public static final String TT_LPAREN = "LPAREN";
    public static final String TT_RPAREN = "RPAREN";
    public static final String TT_LSQUARE = "LSQUARE";
    public static final String TT_RSQUARE = "RSQUARE";
    public static final String TT_IDENTIFIER = "IDENTIFIER";
    public static final String TT_KEYWORD = "KEYWORD";
    public static final String TT_EE = "EE";
    public static final String TT_NE = "NE";
    public static final String TT_LT = "LT";
    public static final String TT_GT = "GT";
    public static final String TT_LTE = "LTE";
    public static final String TT_GTE = "GTE";
    public static final String TT_EQ = "EQ";
    public static final String TT_COMMA = "COMMA";
    public static final String TT_ARROW = "ARROW";
    public static final String TT_EOF = "EOF";
    
    public static final String DIGITS = "1234567890";
    public static final String LETTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String LETTERSDIGITS = LETTERS + DIGITS + "_";

    private static final String[] temp = {
            "and",
            "or",
            "not",
            "if",
            "then",
            "elif",
            "else",
            "for",
            "to",
            "step",
            "while",
            "func"
    };

    public static final List<String> KEYWORDS = new ArrayList<>(Arrays.asList(temp));

    public static final Int nullValue = new Int(0);
    public static final Boolean trueValue = new Boolean(true);
    public static final Boolean falseValue = new Boolean(false);


    private Constants() {}
}
