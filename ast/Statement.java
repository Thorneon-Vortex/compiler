package ast;

// ast/Statement.java
// 所有语句节点的抽象基类
public abstract class Statement extends Node {
    public Statement(int lineNum) { super(lineNum); }
}