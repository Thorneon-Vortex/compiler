package ast.statementNodes;
import ast.Expression;
import ast.Statement;

import java.util.List;

// ast/ForStmt.java
//这里的forStmt表示的是一整个for语句
public class ForStmt extends Statement {
    // ForStmt → LVal '=' Exp
    // 将for循环的三个部分抽象出来
    public final List<AssignStmt> init;      // for的第一部分, 可以为 null
    public final Expression condition; // for的第二部分, 可以为 null
    public final List<AssignStmt> update;    // for的第三部分, 可以为 null
    public final Statement body;      // 循环体
    
    public ForStmt(int lineNum, List<AssignStmt> init, Expression condition, List<AssignStmt> update, Statement body) {
        super(lineNum);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }
}