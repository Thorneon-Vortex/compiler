package ast.statementNodes;
import ast.Expression;
import ast.Statement;


// ast/IfStmt.java
public class IfStmt extends Statement {
    public final Expression condition;
    public final Statement thenBranch;
    public final Statement elseBranch; // 可以为 null
    public IfStmt(int lineNum, Expression condition, Statement thenBranch, Statement elseBranch) {
        super(lineNum);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}