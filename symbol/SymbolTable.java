package symbol;

import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class SymbolTable {
    private Stack<Map<String, Symbol>> scopeStack;
    private Stack<Integer> scopeIdStack; // 作用域序号栈
    private int nextScopeId = 1;

    // 用于最终输出
    private List<Symbol> allSymbolsInOrder;

    public SymbolTable() {
        this.scopeStack = new Stack<>();
        this.scopeIdStack = new Stack<>();
        this.allSymbolsInOrder = new ArrayList<>();
    }

    // 进入一个新的作用域
    public void enterScope() {
        scopeStack.push(new HashMap<>());
        scopeIdStack.push(nextScopeId++);
    }

    // 退出当前作用域
    public void exitScope() {
        scopeStack.pop();
        scopeIdStack.pop();
    }

    // 在当前作用域添加一个符号
    // 返回 true 如果成功, false 如果重定义 (错误b)
    public boolean addSymbol(Symbol symbol) {
        Map<String, Symbol> currentScope = scopeStack.peek();
        if (currentScope.containsKey(symbol.getName())) {
            return false; // 重定义
        }
        symbol.setScopeId(scopeIdStack.peek());
        currentScope.put(symbol.getName(), symbol);
        allSymbolsInOrder.add(symbol);
        return true;
    }

    // 查找一个符号 (从当前作用域向外层找)
    // 找不到返回 null (错误c)
    public Symbol lookupSymbol(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            if (scopeStack.get(i).containsKey(name)) {
                return scopeStack.get(i).get(name);
            }
        }
        return null;
    }
    
    // 获取最终的符号列表用于输出
    public List<Symbol> getAllSymbols() {
        return allSymbolsInOrder;
    }

    public int getCurrentScopeId() {
        return scopeIdStack.peek();
    }
}