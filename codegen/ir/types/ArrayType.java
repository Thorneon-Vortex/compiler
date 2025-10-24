package codegen.ir.types;

public class ArrayType extends Type {
    private final int size;
    private final Type elementType;

    public ArrayType(int size, Type elementType) {
        this.size = size;
        this.elementType = elementType;
    }

    public int getSize() { return size; }
    public Type getElementType() { return elementType; }

    // 例如: new ArrayType(10, new IntegerType(32)) -> "[10 x i32]"
    @Override
    public String toString() { return "[" + size + " x " + elementType.toString() + "]"; }
}