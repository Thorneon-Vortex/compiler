package error;

public class SemanticError extends CompilerError{
    public SemanticError(int lineNum, String errorCode) {
        super(lineNum, errorCode);
    }
}
