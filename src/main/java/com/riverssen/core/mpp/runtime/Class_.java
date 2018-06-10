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

import com.riverssen.core.mpp.compiler.AST;
import com.riverssen.core.mpp.compiler.Token;
import com.riverssen.core.mpp.runtime.vm.VirtualMachine;

import java.util.LinkedHashSet;
import java.util.Set;

public class Class_
{
    private static final Set<Class_> const_parents = new LinkedHashSet<>();
    static {
        Method toString = new ToStringMethod();

//        Class_ origin_class = new Class_("class").addMethod(toString);
//        const_parents.add(origin_class);
    }

    private String          name;
    private Set<Class_>     parents;
    private Set<Field>      fields;
    private Set<Method>     methods;
    private int             size;

    public Class_(Token token, AST context)
    {
        this.name       = token.getTokens().get(0).toString();
        this.parents    = new LinkedHashSet<>();
        this.fields     = new LinkedHashSet<>();
        this.methods    = new LinkedHashSet<>();

        for(Token tok : token.getTokens().get(1).getTokens())
            if(tok.getType().equals(Token.Type.EMPTY_DECLARATION) || tok.getType().equals(Token.Type.FULL_DECLARATION))
                fields.add(new Field(tok));
            else if(tok.getType().equals(Token.Type.METHOD_DECLARATION))
                methods.add(new Method(tok, context));

        for(Field field : fields)
            field.setOffset(this.size ++);

        for(Method method : methods)
            method.setOffset(this.size ++);
    }

    public int getFieldByName(String name)
    {
        for(Field field : fields)
            if (field.getName().equals(name)) return field.getOffset();

        return 0;
    }

    public int getMethoddByName(String name)
    {
        for(Method method : methods)
            if (method.getName().equals(name)) return method.getOffset();

        return 0;
    }

    public Class_ addMethod(Method method)
    {
        this.methods.add(method);
        return this;
    }

    public void newInstance(AST context)
    {
    }

    protected void calculate(AST context)
    {
    }
}