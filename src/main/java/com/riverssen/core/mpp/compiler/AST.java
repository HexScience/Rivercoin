package com.riverssen.core.mpp.compiler;

import com.riverssen.core.mpp.runtime.Class_;
import com.riverssen.core.mpp.runtime.Method;
import com.riverssen.core.rvm.Opcode;
import com.riverssen.core.rvm.opcodes.FunctionOpcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class AST
{
    private List<Opcode> opcodes;

    private HashMap<String, Class_> classes = new HashMap<>();
    private HashMap<String, Method> methods = new HashMap<>();

    private Stack<String>           globalStack = new Stack<>();
    private HashMap<Integer, String>globalMemry = new HashMap<>();
    private int                     globalIndex = 0;

    public AST(ParsedProgram program)
    {
        Token root = program.getTokens();
        this.opcodes = new ArrayList<>();
    }

    private void enterRoot(Token root)
    {
        List<Token> classes = new ArrayList<>();

        for(Token token : classes)
            if(token.getType() == Token.Type.CLASS_DECLARATION)
                classes.add(token);

        for(Token clss : classes)
            enterClass(clss);
    }

    private void enterClass(Token clasz)
    {
//        Opcode rootCode = new ClassOpcode(null);
        Class_ clss = new Class_(clasz, this);
        FunctionOpcode functionOpcode = new FunctionOpcode();
    }

    protected int push(String name)
    {
        globalStack.push(name);
        return globalStack.size() - 1;
    }

    protected int stackSize()
    {
        return globalStack.size();
    }

    protected int newObject(String name)
    {
        return globalIndex++;
    }

    public List<Opcode> getOpcodes()
    {
        return opcodes;
    }
}
