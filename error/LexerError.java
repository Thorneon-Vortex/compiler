package error;

/**
 * 词法分析错误类
 * 用于记录词法分析过程中遇到的错误
 */
public class LexerError extends CompilerError {
    public LexerError(int lineNum, String errorType) {
        super(lineNum, errorType);
    }
}
