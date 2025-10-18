package ast.expressionNodes;// ast/BinaryExp.java
import ast.Expression;
import frontend.Token;

/**
 * 对应文法: MulExp, AddExp, RelExp, EqExp, LAndExp, LOrExp
 * 如: a + b, a > b, a && b
 */
public class BinaryExp extends Expression {
    public final Expression left;
    public final Token op; // +, -, *, /, %, <, >, <=, >=, ==, !=, &&, ||
    public final Expression right;

    public BinaryExp(Expression left, Token op, Expression right) {
        super(op.lineNum());
        this.left = left;
        this.op = op;
        this.right = right;
    }
}