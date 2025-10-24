package codegen.ir.values;

import codegen.ir.types.Type;

// 函数参数也是一个Value
public class Argument extends Value {
    public Argument(Type type, String name) {
        super(type, name);
    }
}