package codegen.ir.types;

import java.util.List;
import java.util.ArrayList;

public class FunctionType extends Type {
    private final Type returnType;
    private final List<Type> paramTypes;

    public FunctionType(Type returnType, List<Type> paramTypes) {
        this.returnType = returnType;
        this.paramTypes = new ArrayList<>(paramTypes);
    }

    public FunctionType(Type returnType) {
        this.returnType = returnType;
        this.paramTypes = new ArrayList<>();
    }

    public Type getReturnType() { return returnType; }
    public List<Type> getParamTypes() { return paramTypes; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(returnType.toString()).append(" (");
        for (int i = 0; i < paramTypes.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(paramTypes.get(i).toString());
        }
        sb.append(")");
        return sb.toString();
    }
}
