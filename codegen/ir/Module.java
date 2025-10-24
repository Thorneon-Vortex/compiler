package codegen.ir;
import codegen.ir.values.Function;

import java.util.ArrayList;
import java.util.List;

public class Module {
    private String name;
    private final List<Function> functions = new ArrayList<>();
    // private final List<GlobalVariable> globals = new ArrayList<>();

    public Module(String name) { this.name = name; }
    
    public String getName() { return name; }
    
    public void addFunction(Function func) { functions.add(func); }
    public List<Function> getFunctions() { return functions; }
}