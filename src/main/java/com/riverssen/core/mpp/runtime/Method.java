/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core.mpp.runtime;

import com.riverssen.core.mpp.compiler.Token;
import com.riverssen.core.rvm.Opcode;
import com.riverssen.core.rvm.opcodes.FunctionOpcode;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Method
{
    private String      name;
    private Set<Field>  arguments;
    private String      returnType;
    private Opcode      opcode[];
    private Opcode      method;
    private int         offset;
    private Token       body;

    public Method(Token token)
    {
        this.name       = token.getTokens().get(0).toString();
        this.arguments  = new LinkedHashSet<>();
        for(Token tok : token.getTokens().get(1).getTokens())
            this.arguments.add(new Field(tok));

        this.method     = new FunctionOpcode(token.getTokens().get(3));
        this.body       = token.getTokens().get(3);
    }

    public Method(String name)
    {
        this.name       = name;
    }

    public Method(String name, Opcode opcode[])
    {
        this.name   = name;
        this.opcode = opcode;
    }

    protected   void    setOffset(int offset)
    {
        this.offset = offset;
    }

    public      int     getOffset()
    {
        return this.offset;
    }

    public String getName()
    {
        return name;
    }

    public String getReturnType()
    {
        return returnType;
    }

    HashMap<String, Object> objectHashMap = new HashMap<>();

    protected Object getObject(String name, Object self)
    {
        if(objectHashMap.containsKey(name))
            return objectHashMap.get(name);
        else return self.getFieldByName(name);
    }

    protected Object proceduralAccess(Token token, Object self)
    {
        if(token.getTokens().get(0).getType().equals(Token.Type.METHOD_CALL))
            self = self.callMethod(token.getTokens().get(0).toString());
        else if(self != null && self.getFieldByName(token.getTokens().get(0).toString()) != null)
            self = self.getFieldByName(token.getTokens().get(0).toString());
        else self = objectHashMap.get(token.getTokens().get(0).toString());

        if(token.getTokens().get(1).getType().equals(Token.Type.PROCEDURAL_ACCESS))
            return proceduralAccess(token.getTokens().get(1), self);
        else return self.getFieldByName(token.getTokens().get(1).toString());
    }

    protected Object call(Object self, Object ...args) throws RuntimeException
    {
        objectHashMap.clear();
        objectHashMap.put("this", self);

        int i = 0;

        for (Field field : arguments)
            objectHashMap.put(field.getName(), args[i++]);

        /** pass this as parameter **/
        for(Token token : body.getTokens())
        {
            switch (token.getType())
            {
                case INITIALIZATION:
                        Token obj = token.getTokens().get(0);
                        Token val = token.getTokens().get(1).getTokens().get(0);

                        Object value = null;
                        if(val.getType().equals(Token.Type.INPUT))
                            value = Object.fromInput(val);
                        else if(val.getType().equals(Token.Type.PROCEDURAL_ACCESS))
                        {
                            value = proceduralAccess(val, self);
                        }

                        if(objectHashMap.containsKey(obj))
                            objectHashMap.put(obj.toString(), value);
                        else self.setField(obj.toString(), value);
                    break;
            }
        }
        return null;
    }
}
