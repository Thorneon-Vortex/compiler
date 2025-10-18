package ast.statementNodes;


import ast.Expression;
import ast.Statement;

// ast/ReturnStmt.java
public class ReturnStmt extends Statement {
    public final Expression returnValue; // 可以为 null
    public ReturnStmt(int lineNum, Expression returnValue) {
        super(lineNum);
        this.returnValue = returnValue;
    }
}