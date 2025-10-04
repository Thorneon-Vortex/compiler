package parser;

public class SyntaxError {
    private int lineNum;
    private String errorType;

    public SyntaxError(int lineNum, String errorType) {
        this.lineNum = lineNum;
        this.errorType = errorType;
    }

    public int getLineNum() {
        return lineNum;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setLineNum(int lineNum) {
        this.lineNum = lineNum;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    @Override
    public String toString() {
        return lineNum + " " + errorType;
    }
}
