package ast.expressionNodes;// ast/FuncCall.java
import ast.Expression;
import frontend.Token;
import java.util.List;

/**
 * 对应文法: UnaryExp → Ident '(' [FuncRParams] ')'
 */
public class FuncCall extends Expression {
    public final Token ident;
    public final List<Expression> args; // 实参列表

    public FuncCall(Token ident, List<Expression> args) {
        super(ident.lineNum());
        this.ident = ident;
        this.args = args;
    }
}