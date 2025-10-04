package error;

public class SyntaxError extends CompilerError {
    public SyntaxError(int lineNum, String errorCode) {
        super(lineNum, errorCode);
    }
}
