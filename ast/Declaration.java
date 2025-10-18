package ast;

// ast/Declaration.java
// 所有声明节点的抽象基类 (用于顶层结构)
public abstract class Declaration extends Statement {
    public Declaration(int lineNum) { super(lineNum); }
}