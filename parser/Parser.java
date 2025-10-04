package parser;

import ast.Node; // 之后会创建更多Node
import frontend.Token;
import frontend.TokenType;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int currentPos = 0; // 指向当前token的索引
    private Token currentToken; // 当前token的快照，方便访问

    private final PrintWriter outputWriter; // 用于输出到 parser.txt
    private final List<SyntaxError> errors = new ArrayList<>();

    // 构造函数
    public Parser(List<Token> tokens, PrintWriter outputWriter) {
        this.tokens = tokens;
        this.outputWriter = outputWriter;
        // 初始化第一个token
        this.currentToken = tokens.get(0);
    }

    public List<SyntaxError> getErrors() {
        return errors;
    }

    // ----- Token流处理辅助方法 -----

    // 前进一个token
    private void nextToken() {
        if (currentPos < tokens.size() - 1) {
            currentPos++;
            currentToken = tokens.get(currentPos);
        }
    }

    // 查看当前token类型
    private TokenType peek() {
        return currentToken.type();
    }

    // 查看未来第k个token的类型（不移动指针）
    private TokenType peek(int k) {
        if (currentPos + k < tokens.size()) {
            return tokens.get(currentPos + k).type();
        }
        return TokenType.EOF; // 超出范围则返回EOF
    }

    // 消费当前token并输出
    private void consume() {
        // 输出当前token信息
        outputWriter.println(currentToken.type().name() + " " + currentToken.value());
        nextToken();
    }

    // 匹配并消费一个期望的token，否则记录错误
    private void consume(TokenType expectedType, String errorCode) {
        if (peek() == expectedType) {
            consume();
        } else {
            // 错误处理：缺失了期望的token
            // 错误行号通常记录在前一个token的位置
            int errorLine = tokens.get(currentPos - 1).lineNum();
            errors.add(new SyntaxError(errorLine, errorCode));
        }
    }

    // ----- 语法成分输出辅助方法 -----
    private void printSyntaxComponent(String name) {
        outputWriter.println("<" + name + ">");
    }

    // ----- 错误记录辅助方法 -----
    private void addError(String errorCode) {
        // 错误行号记录在当前token的位置
        errors.add(new SyntaxError(currentToken.lineNum(), errorCode));
    }


    // ----- 递归下降分析方法（主入口）-----
    public Node parse() {
        return parseCompUnit();
    }

    // 接下来我们将在这里填充所有的 parseXXX() 方法
    // ...
    private Node parseCompUnit(){
        return null;
    }
}