package ast.statementNodes;// ast/PrintfStmt.java
import ast.Expression;
import ast.Statement;
import frontend.Token;
import java.util.List;
public class PrintfStmt extends Statement {
    public final Token formatString;
    public final List<Expression> args;
    public PrintfStmt(Token formatString, List<Expression> args) {
        super(formatString.lineNum());
        this.formatString = formatString;
        this.args = args;
    }
}