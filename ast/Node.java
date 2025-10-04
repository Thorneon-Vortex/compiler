package ast;

// 所有AST节点的基类
public abstract class Node {
    // 可以包含行号等公共信息
    private int lineNum;

    public Node(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return lineNum;
    }
}