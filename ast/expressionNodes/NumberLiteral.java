package ast.expressionNodes;// ast/NumberLiteral.java
import ast.Expression;
import frontend.Token;

/**
 * 对应文法: Number → IntConst
 */
public class NumberLiteral extends Expression {
    public final Token number;

    public NumberLiteral(Token number) {
        super(number.lineNum());
        this.number = number;
    }

    public int getValue() {
        return Integer.parseInt(number.value());
    }
}