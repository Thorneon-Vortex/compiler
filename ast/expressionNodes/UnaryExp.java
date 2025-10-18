package ast.expressionNodes;// ast/UnaryExp.java
import ast.Expression;
import frontend.Token;

/**
 * 对应文法: UnaryExp → UnaryOp UnaryExp
 * 如: -a, !flag
 */
public class UnaryExp extends Expression {
    public final Token op; // +, -, !
    public final Expression operand;

    public UnaryExp(Token op, Expression operand) {
        super(op.lineNum());
        this.op = op;
        this.operand = operand;
    }
}