package ast;

import java.util.List;

public class CompUnitNode extends Node {
    private List<Node> declsAndFuncs; // 简化的结构，存放所有声明和函数定义

    public CompUnitNode(int lineNum, List<Node> content) {
        super(lineNum);
        this.declsAndFuncs = content;
    }
}