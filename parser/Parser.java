package parser;

import ast.Node; // 之后会创建更多Node
import error.SyntaxError;
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
        if (tokens.isEmpty()) {
            throw new IllegalArgumentException("Token list cannot be empty");
        }
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
        //i：缺少;
        //j：缺少)
        //k，缺少]
        if (peek() == expectedType) {
            consume();
        } else {
            // 错误处理：缺失了期望的token
            // 错误行号通常记录在前一个token的位置
            int errorLine = currentPos > 0 ? tokens.get(currentPos - 1).lineNum() : currentToken.lineNum();
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
    // CompUnit → {Decl} {FuncDef} MainFuncDef
    private Node parseCompUnit() {
        // 循环分析全局声明和函数定义
        while (peek() != TokenType.EOF) {
            if (peek() == TokenType.INTTK && peek(1) == TokenType.MAINTK) {
                // 遇到主函数，跳出循环
                break;
            }
            if (peek() == TokenType.CONSTTK || peek() == TokenType.STATICTK || 
                (peek() == TokenType.INTTK && peek(2) != TokenType.LPARENT)) {
                // 声明
                parseDecl();
            } else if ((peek() == TokenType.VOIDTK || peek() == TokenType.INTTK) && 
                       peek(1) == TokenType.IDENFR && peek(2) == TokenType.LPARENT) {
                // 函数定义
                parseFuncDef();
            } else {
                // 无法识别的结构，跳过当前token避免死循环
                nextToken();
            }
        }
        
        // 分析主函数
        if (peek() == TokenType.INTTK && peek(1) == TokenType.MAINTK) {
            parseMainFuncDef();
        }

        printSyntaxComponent("CompUnit");
        return null; // TODO: 返回CompUnitNode
    }

    private Node parseFuncDef() {
        parseFuncType();
        consume();//indent
        consume();//(
        parseFuncFParams();
        consume(TokenType.RPARENT, "j");
        parseBlock();
        printSyntaxComponent("FuncDef");
        return null;
    }

    private Node parseBlock() {
        consume();
        while (peek() != TokenType.RBRACE) {
            parseBlockItem();
        }
        consume();
        printSyntaxComponent("Block");
        return null;
    }

    private Node parseBlockItem() {
//        if (peek() == TokenType.IDENFR) {
//            parseStmt();
//        } else {
//            parseDecl();
//        }
        if (TokenType.isStmt(peek())) {
            parseStmt();
        } else {
            int i = 0;
            boolean flag = false;
            while (true) {
                TokenType type = peek(i);
                if (type == TokenType.EOF) {
                    //细节，这也算没读到int，程序虽然缺少；
                    //但不在这里处理，之后调用parseStmt()处理
                    break;
                }
                if (peek(i) == TokenType.INTTK) {
                    flag = true;
                    break;
                }
                if (peek(i) == TokenType.SEMICN) {
                    break;
                }
                i++;
            }
            if (flag) {
                parseDecl();
            } else {
                parseStmt();
            }
        }
        //题目要求，不需要输出
        return null;
    }

    private Node parseFuncFParams() {
        if (peek() != TokenType.RPARENT) {
            parseFuncFParam();
            while (peek() == TokenType.COMMA) {
                consume(); // 消费 ','
                parseFuncFParam();
            }
        }
        printSyntaxComponent("FuncFParams");
        return null;
    }

    private Node parseFuncFParam() {
        parseBType();
        consume(); // Ident
        if (peek() == TokenType.LBRACK) {
            consume(); // '['
            consume(TokenType.RBRACK, "k"); // ']'
        }
        printSyntaxComponent("FuncFParam");
        return null;
    }

    private Node parseFuncType() {
        if (peek() == TokenType.VOIDTK) {
            consume();
        } else if (peek() == TokenType.INTTK) {
            consume();
        }
        printSyntaxComponent("FuncType");
        return null;
    }

    private Node parseMainFuncDef() {
        consume();//int
        consume();//main
        consume();//(
        consume(TokenType.RPARENT, "j");//)
        parseBlock();
        printSyntaxComponent("MainFuncDef");
        return null;
    }

    // Decl → ConstDecl | VarDecl
    private Node parseDecl() {
        if (peek() == TokenType.CONSTTK) {
            parseConstDecl();
        } else {
            parseVarDecl();
        }
        // 注意: <Decl> 不要求输出
        return null;
    }

    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
    private Node parseConstDecl() {
        consume(TokenType.CONSTTK, "expect reserved word const"); // 假设有个错误码，实际上不可能出现错误。
        parseBType();
        parseConstDef();
        while (peek() == TokenType.COMMA) {
            consume(); // 消费 ','
            parseConstDef();
        }
        // 错误检查 i: 缺少分号
        consume(TokenType.SEMICN, "i");
        // <ConstDecl> 不要求输出，但其子节点会输出
        return null;
    }

    private Node parseConstDef() {
        consume(); // Ident
        if (peek() == TokenType.LBRACK) {
            consume(); // '['
            parseConstExp();
            consume(TokenType.RBRACK, "k"); // ']'
        }
        consume(); // '='
        parseConstInitVal();
        printSyntaxComponent("ConstDef");
        return null;
    }

    private Node parseConstInitVal() {
        //ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}'
        if (peek() == TokenType.LBRACE) {
            //消耗{
            consume();
            //一维数组初值
            if (peek() != TokenType.RBRACE) {
                parseConstExp();
                while (peek() == TokenType.COMMA) {
                    consume(); // ','
                    parseConstExp();
                }
            }
            //吃掉}
            consume();
        } else {
            parseConstExp();
        }
        printSyntaxComponent("ConstInitVal");
        return null;
    }

    private Node parseConstExp() {
        parseAddExp();
        printSyntaxComponent("ConstExp");
        return null;
    }

    // BType → 'int'
    private Node parseBType() {
        consume(TokenType.INTTK, "expect int");
        // <BType> 不要求输出
        return null;
    }

    // VarDecl → [ 'static' ] BType VarDef { ',' VarDef } ';'
    private Node parseVarDecl() {
        if (peek() == TokenType.STATICTK) {
            consume();
        }
        parseBType();
        parseVarDef();
        while (peek() == TokenType.COMMA) {
            consume();
            parseVarDef();
        }
        consume(TokenType.SEMICN, "i");
        printSyntaxComponent("VarDecl");
        return null; // TODO: 返回 VarDeclNode
    }

    // VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
    private Node parseVarDef() {
        consume(TokenType.IDENFR, "ERROR_CODE"); // Ident
        while (peek() == TokenType.LBRACK) {
            consume(); // '['
            parseConstExp();
            consume(TokenType.RBRACK, "k"); // ']' 错误检查 k
        }
        if (peek() == TokenType.ASSIGN) {
            consume(); // '='
            parseInitVal();
        }
        printSyntaxComponent("VarDef");
        return null; // TODO: 返回 VarDefNode
    }

    private Node parseInitVal() {
        if (peek() == TokenType.LBRACE) {
            consume(); // '{'
            if (peek() != TokenType.RBRACE) {
                parseExp();
                while (peek() == TokenType.COMMA) {
                    consume(); // ','
                    parseExp();
                }
            }
            consume(); // '}'
        } else {
            parseExp();
        }
        printSyntaxComponent("InitVal");
        return null;
    }

    // Stmt → LVal '=' Exp ';' | [Exp] ';' | Block | ...
    private Node parseStmt() {
        switch (peek()) {
            case LBRACE: // Block
                return parseBlock();
            case IFTK:
                // 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
                consume(); // if
                consume(); // '('
                parseCond();
                consume(TokenType.RPARENT, "j"); // ')' 错误检查 j
                parseStmt();
                if (peek() == TokenType.ELSETK) {
                    consume(); // else
                    parseStmt();
                }
                break;
            case FORTK:
                // 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
                consume(); // for
                consume(); // '('
                if (peek() != TokenType.SEMICN) {
                    parseForStmt();
                }
                consume(TokenType.SEMICN, "i"); // ';'
                if (peek() != TokenType.SEMICN) {
                    parseCond();
                }
                consume(TokenType.SEMICN, "i"); // ';'
                if (peek() != TokenType.RPARENT) {
                    parseForStmt();
                }
                consume(TokenType.RPARENT, "j"); // ')'
                parseStmt();
                break;
            case BREAKTK:
                consume(); // break
                consume(TokenType.SEMICN, "i"); // 错误检查 i
                break;
            case CONTINUETK:
                consume(); // break or continue
                consume(TokenType.SEMICN, "i"); // 错误检查 i
                break;
            case RETURNTK:
                consume(); // return
                // [Exp]
                if (peek() != TokenType.SEMICN) {
                    parseExp();
                }
                consume(TokenType.SEMICN, "i"); // 错误检查 i
                break;
            case PRINTFTK:
                // 'printf''('StringConst {','Exp}')'';'
                consume(); // printf
                consume(); // '('
                consume(); // StringConst
                while (peek() == TokenType.COMMA) {
                    consume(); // ','
                    parseExp();
                }
                consume(TokenType.RPARENT, "j"); // ')' 错误检查 j
                consume(TokenType.SEMICN, "i"); // ';' 错误检查 i
                break;
            case SEMICN: // [Exp] ';' -> 空语句
                consume();
                break;
            default:
                // LVal '=' Exp ';'  或  Exp ';'
                // 这里需要预读来区分。如果 Exp 后是 ASSIGN，则是赋值语句。
                // 这是一个难点，需要更复杂的预读，或者在解析Exp后判断。
                // 一个简化策略是：先尝试按Exp解析，如果解析完后是'='，则确定为赋值语句。
//                Node exp = parseExp(); // parseExp内部会解析LVal
//                if (peek() == TokenType.ASSIGN) {
//                    // 这是 LVal = Exp;
//                    // TODO: 需要回溯或调整 parseExp 的结构来获取 LVal
//                    // ...
//                }
//                consume(TokenType.SEMICN, "i"); // 错误检查 i
//                break;
                //预读，看看是先出现=还是;
                int i = 0;
                boolean flag = false;
                while (true) {
                    if (peek(i) == TokenType.EOF) {
                        break;
                    }
                    if (peek(i) == TokenType.ASSIGN) {
                        flag = true;
                        break;
                    }
                    if (peek(i) == TokenType.SEMICN) {
                        break;
                    }
                    i++;
                }
                if (flag) {
                    // 这是 LVal = Exp;
                    parseLVal();
                    consume(); // '='
                    parseExp();
                    consume(TokenType.SEMICN, "i"); // ';'
                } else {
                    // 这是 Exp;
                    if (peek() != TokenType.SEMICN && peek() != TokenType.EOF) {
                        parseExp();
                    }
                    consume(TokenType.SEMICN, "i"); // ';' 错误检查 i
                }
        }

        printSyntaxComponent("Stmt");
        return null; // TODO: 返回 StmtNode
    }

    //ForStmt → LVal '=' Exp { ',' LVal '=' Exp }
    private Node parseForStmt() {
        parseLVal();
        consume(); // '='
        parseExp();
        while (peek() == TokenType.COMMA) {
            consume(); // ','
            parseLVal();
            consume(); // '='
            parseExp();
        }
        printSyntaxComponent("ForStmt");
        return null;
    }

    private Node parseCond() {
        parseLOrExp();
        printSyntaxComponent("Cond");
        return null;
    }

    // LOrExp → LAndExp | LOrExp '||' LAndExp
    // 消除左递归: LAndExp { '||' LAndExp }
    private Node parseLOrExp() {
        parseLAndExp();
        while (peek() == TokenType.OR || peek() == TokenType.ERROROR) {
            consume();
            parseLAndExp();
        }
        printSyntaxComponent("LOrExp");
        return null;
    }

    //LAndExp → EqExp | LAndExp '&&' EqExp
    //消除左递归: EqExp { '&&' EqExp }
    private Node parseLAndExp() {
        parseEqExp();
        //注意这里认为&也是逻辑与，这里不处理。避免错误雪崩
        while (peek() == TokenType.AND || peek() == TokenType.ERRORAND) {
            consume();
            parseEqExp();
        }
        printSyntaxComponent("LAndExp");
        return null;
    }

    //EqExp → RelExp | EqExp ('==' | '!=') RelExp
    //消除左递归: RelExp { ('==' | '!=') RelExp }
    private Node parseEqExp() {
        parseRelExp();
        while (peek() == TokenType.EQL || peek() == TokenType.NEQ) {
            consume();
            parseRelExp();
        }
        printSyntaxComponent("EqExp");
        return null;
    }

    //RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    //消除左递归: AddExp { ('<' | '>' | '<=' | '>=') AddExp }
    private Node parseRelExp() {
        parseAddExp();
        while (peek() == TokenType.LSS || peek() == TokenType.GRE || peek() == TokenType.LEQ || peek() == TokenType.GEQ) {
            consume();
            parseAddExp();
        }
        printSyntaxComponent("RelExp");
        return null;
    }

    // Exp → AddExp
    private Node parseExp() {
        parseAddExp();
        printSyntaxComponent("Exp");
        return null;
    }

    // AddExp → MulExp { ('+' | '−') MulExp }
    // 注意：文法是左递归的，需要改写成非左递归形式进行分析
    private Node parseAddExp() {
        parseMulExp();
        while (peek() == TokenType.PLUS || peek() == TokenType.MINU) {
            consume();
            parseMulExp();
        }
        printSyntaxComponent("AddExp");
        return null;
    }

    //MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    private Node parseMulExp() {
        parseUnaryExp();
        while (peek() == TokenType.MULT || peek() == TokenType.DIV || peek() == TokenType.MOD) {
            consume();
            parseUnaryExp();
        }
        printSyntaxComponent("MulExp");
        return null;
    }

    //UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp // j
    private Node parseUnaryExp() {
        if (peek() == TokenType.IDENFR && peek(1) == TokenType.LPARENT) {
            consume(); // Ident
            consume(); // '('
            if (peek() != TokenType.RPARENT) {
                parseFuncRParams();
            }
            consume(TokenType.RPARENT, "j"); // ')'
        } else if (peek() == TokenType.PLUS || peek() == TokenType.MINU || peek() == TokenType.NOT) {
            parseUnaryOp();
            parseUnaryExp();
        } else {
            parsePrimaryExp();
        }
        printSyntaxComponent("UnaryExp");
        return null;
    }

    private Node parseFuncRParams() {
        parseExp();
        while (peek() == TokenType.COMMA) {
            consume(); // ','
            parseExp();
        }
        printSyntaxComponent("FuncRParams");
        return null;
    }

    // PrimaryExp → '(' Exp ')' | LVal | Number // j
    private Node parsePrimaryExp() {
        if (peek() == TokenType.LPARENT) {
            consume();
            parseExp();
            consume(TokenType.RPARENT, "j");
        } else if (peek() == TokenType.INTCON) {
            parseNumber();
        } else {
            parseLVal();
        }
        printSyntaxComponent("PrimaryExp");
        return null;
    }

    private Node parseLVal() {
        consume(); // Ident
        while (peek() == TokenType.LBRACK) {
            consume(); // '['
            parseExp();
            consume(TokenType.RBRACK, "k"); // ']'
        }
        printSyntaxComponent("LVal");
        return null;
    }

    private Node parseNumber() {
        consume(); // IntConst
        printSyntaxComponent("Number");
        return null;
    }

    //UnaryOp → '+' | '−' | '!' 注：'!'仅出现在条件表达式中
    private Node parseUnaryOp() {
        consume(); // '+' | '-' | '!'
        printSyntaxComponent("UnaryOp");
        return null;
    }

}