package frontend;

/**
 * 词法分析器的单词类型枚举类
 * 定义了编程语言中各种token的类别码
 */
public enum TokenType {
    // 标识符和常量 (Identifiers and Literals)
    IDENFR("IDENFR"),           // 标识符 Ident
    INTCON("INTCON"),           // 整型常量 IntConst
    STRCON("STRCON"),           // 字符串常量 StringConst
    
    // 关键字 (Keywords)
    CONSTTK("CONSTTK"),         // const
    INTTK("INTTK"),             // int
    STATICTK("STATICTK"),       // static
    BREAKTK("BREAKTK"),         // break
    CONTINUETK("CONTINUETK"),   // continue
    IFTK("IFTK"),               // if
    MAINTK("MAINTK"),           // main
    ELSETK("ELSETK"),           // else
    FORTK("FORTK"),             // for
    RETURNTK("RETURNTK"),       // return
    VOIDTK("VOIDTK"),           // void
    PRINTFTK("PRINTFTK"),       // printf
    
    // 运算符 (Operators)
    NOT("!"),                   // !
    AND("&&"),                  // &&
    ERRORAND("&"),              // &
    ERROROR("|"),               // |
    OR("||"),                   // ||
    PLUS("+"),                  // +
    MINU("-"),                  // -
    MULT("*"),                  // *
    DIV("/"),                   // /
    MOD("%"),                   // %
    LSS("<"),                   // <
    LEQ("<="),                  // <=
    GRE(">"),                   // >
    GEQ(">="),                  // >=
    EQL("=="),                  // ==
    NEQ("!="),                  // !=
    ASSIGN("="),                // =
    
    // 分界符 (Delimiters)
    SEMICN(";"),                // ;
    COMMA(","),                 // ,
    LPARENT("("),               // (
    RPARENT(")"),               // )
    LBRACK("["),                // [
    RBRACK("]"),                // ]
    LBRACE("{"),                // {
    RBRACE("}"),                // }
    
    // 特殊符号
    EOF("EOF"),                 // 文件结束符
    ERROR("ERROR");             // 错误token
    
    private final String value;
    
    /**
     * 构造函数
     * @param value token的字符串值
     */
    TokenType(String value) {
        this.value = value;
    }
    
    /**
     * 获取token的字符串值
     * @return token的字符串值
     */
    public String getValue() {
        return value;
    }
    
    /**
     * 判断给定字符串是否为关键字
     * @param word 待检查的字符串
     * @return 如果是关键字返回对应的TokenType，否则返回null
     */
    public static TokenType getKeywordType(String word) {
        switch (word) {
            case "const": return CONSTTK;
            case "int": return INTTK;
            case "static": return STATICTK;
            case "break": return BREAKTK;
            case "continue": return CONTINUETK;
            case "if": return IFTK;
            case "main": return MAINTK;
            case "else": return ELSETK;
            case "for": return FORTK;
            case "return": return RETURNTK;
            case "void": return VOIDTK;
            case "printf": return PRINTFTK;
            default: return null;
        }
    }

    public static boolean isStmt(TokenType type) {
        return type == IFTK || type == FORTK || type == BREAKTK
                || type == CONTINUETK || type == RETURNTK || type == PRINTFTK
                || type == IDENFR;
    }
    
    /**
     * 判断是否为关键字
     * @return 如果是关键字返回true，否则返回false
     */
    public boolean isKeyword() {
        return this == CONSTTK || this == INTTK || this == STATICTK || this == BREAKTK ||
               this == CONTINUETK || this == IFTK || this == MAINTK || this == ELSETK ||
               this == FORTK || this == RETURNTK || this == VOIDTK || this == PRINTFTK;
    }
    
    /**
     * 判断是否为运算符
     * @return 如果是运算符返回true，否则返回false
     */
    public boolean isOperator() {
        return this == PLUS || this == MINU || this == MULT || this == DIV ||
               this == MOD || this == LSS || this == LEQ || this == GRE ||
               this == GEQ || this == EQL || this == NEQ || this == ASSIGN ||
               this == AND || this == OR || this == NOT;
    }
    
    /**
     * 判断是否为分界符
     * @return 如果是分界符返回true，否则返回false
     */
    public boolean isDelimiter() {
        return this == SEMICN || this == COMMA || this == LPARENT || this == RPARENT ||
               this == LBRACK || this == RBRACK || this == LBRACE || this == RBRACE;
    }
    
    @Override
    public String toString() {
        return name() + "(" + value + ")";
    }
}
