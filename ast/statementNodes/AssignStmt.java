package ast.statementNodes;
import ast.Expression;
import ast.Statement;
import ast.expressionNodes.LVal;

public class AssignStmt extends Statement {
    public final LVal lval;
    public final Expression value;
    public AssignStmt(LVal lval, Expression value) {
        super(lval.getLineNum());
        this.lval = lval;
        this.value = value;
    }
}