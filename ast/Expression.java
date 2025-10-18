package ast;

// ast/Expression.java
// 所有表达式节点的抽象基类
public abstract class Expression extends Node {
    public Expression(int lineNum) { super(lineNum); }
}