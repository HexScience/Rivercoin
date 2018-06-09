package com.riverssen.core.mpp.compiler;

import java.util.ArrayList;
import java.util.List;

public class AST
{
    public AST(ParsedProgram program)
    {
        Token root = program.getTokens();
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

    }
}
