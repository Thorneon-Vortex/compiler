package frontend;

/**
 * 词法分析错误类
 * 用于记录词法分析过程中遇到的错误
 */
public class LexerError {
    private int lineNum;        // 错误所在行号
    private String errorType;   // 错误类型（如 "a" 表示非法符号）
    
    /**
     * 构造函数
     * @param lineNum 错误所在行号
     * @param errorType 错误类型
     */
    public LexerError(int lineNum, String errorType) {
        this.lineNum = lineNum;
        this.errorType = errorType;
    }
    
    /**
     * 获取错误行号
     * @return 错误行号
     */
    public int getLineNum() {
        return lineNum;
    }
    
    /**
     * 获取错误类型
     * @return 错误类型
     */
    public String getErrorType() {
        return errorType;
    }
    
    /**
     * 设置错误行号
     * @param lineNum 错误行号
     */
    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }
    
    /**
     * 设置错误类型
     * @param errorType 错误类型
     */
    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
    
    @Override
    public String toString() {
        return lineNum + " " + errorType;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LexerError that = (LexerError) obj;
        return lineNum == that.lineNum && errorType.equals(that.errorType);
    }
    
    @Override
    public int hashCode() {
        return lineNum * 31 + errorType.hashCode();
    }
}
