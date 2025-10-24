package codegen.ir.types;

public class PointerType extends Type {
    private final Type elementType; // 指针指向的元素的类型

    public PointerType(Type elementType) { this.elementType = elementType; }
    
    public Type getElementType() { return elementType; }

    // 例如: new PointerType(new IntegerType(32)) -> "i32*"
    @Override
    public String toString() { return elementType.toString() + "*"; }
}