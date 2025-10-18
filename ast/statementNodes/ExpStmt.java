package ast.statementNodes;
import ast.Expression;
import ast.Statement;


// ast/ExpStmt.java (用于 [Exp]; 或空语句 ;)
public class ExpStmt extends Statement {
    public final Expression expression; // 可以为 null，表示空语句
    public ExpStmt(int lineNum, Expression expression) {
        super(lineNum);
        this.expression = expression;
    }
}