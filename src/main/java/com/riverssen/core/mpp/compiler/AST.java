package com.riverssen.core.mpp.compiler;

import com.riverssen.core.mpp.exceptions.CompileException;
import com.riverssen.core.mpp.runtime.Class;
import com.riverssen.core.mpp.runtime.Method;
import com.riverssen.core.rvm.Opcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class AST
{
    private List<Opcode> opcodes;

    private HashMap<String, Class>  classes = new HashMap<>();
    private HashMap<String, Method> methods = new HashMap<>();
    private HashMap<String, Integer>sizes   = new HashMap<>();

    private Stack<String>           globalStack = new Stack<>();
    private HashMap<Integer, String>globalMemry = new HashMap<>();
    private int                     globalIndex = 0;

    public AST(ParsedProgram program) throws CompileException
    {
        Token root = program.getTokens();
        this.opcodes = new ArrayList<>();
        enterRoot(root);
    }

    private void enterRoot(Token root) throws CompileException
    {
        List<Token> classes = new ArrayList<>();

        for(Token token : root.getTokens())
            if(token.getType() == Token.Type.CLASS_DECLARATION)
                classes.add(token);

        for(Token clss : classes)
            enterClass(clss);
    }

    private void enterClass(Token clasz) throws CompileException
    {
//        Opcode rootCode = new ClassOpcode(null);
        Class clss = new Class(clasz);
        if(classes.containsKey(clss.getName())) throw new CompileException("Class already exists", clasz);
        classes.put(clss.getName(), clss);
    }

    private List<Short> enterMethodBody(Token body)
    {
        List<Short> opcodes = new ArrayList<>();

        for(Token token : body.getTokens())
        {
            switch (token.getType())
            {
                case IF:
                        Token condition = token.getTokens().get(0);
                        opcodes.add(com.riverssen.core.mpp.Opcode.IF);
                        opcodes.addAll(enterMethodBody(condition));
                    break;
                case WHILE:
                    break;
                case FOR:
                    break;
                case EMPTY_DECLARATION:
                    break;
                case FULL_DECLARATION:
                    break;
                case PROCEDURAL_ACCESS:
                    break;
            }
        }

        return opcodes;
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

    public int sizeof(String type)
    {
        return sizes.get(type);
    }
}
