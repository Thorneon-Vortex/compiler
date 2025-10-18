package ast.expressionNodes;// ast/LVal.java
import ast.Expression;
import frontend.Token;
import java.util.List;

/**
 * 对应文法: LVal → Ident ['[' Exp ']']
 */
//lineNum是LVal所在行数
public class LVal extends Expression {
    public final Token ident;
    public final Expression exp; // 数组访问的索引，可以为空

    public LVal(Token ident, Expression exp) {
        super(ident.lineNum());
        this.ident = ident;
        this.exp = exp;
    }
}