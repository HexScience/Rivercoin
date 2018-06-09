package com.riverssen.core.mpp.compiler;

import com.riverssen.core.mpp.runtime.Class_;
import com.riverssen.core.rvm.Opcode;
import com.riverssen.core.rvm.opcodes.FunctionOpcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AST
{
    private List<Opcode> opcodes;

    private HashMap<String, Class_> classes = new HashMap<>();

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
        Class_ clss = new Class_(clasz);
        FunctionOpcode functionOpcode = new FunctionOpcode();
    }
}
