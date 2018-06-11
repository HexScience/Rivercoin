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

import com.riverssen.core.mpp.Opcode;
import com.riverssen.core.mpp.compiler.OpcodeWriter;
import com.riverssen.core.mpp.compiler.Token;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

public class Class implements Serializable
{
    private static final Set<Class> const_parents = new LinkedHashSet<>();
    static {
        Method toString = new ToStringMethod();
    }

    private String          name;
    private Set<Class>      parents;
    private Set<Field>      fields;
    private Set<Method>     methods;
    private int             size;

    public Class(Token token)
    {
        this.name       = token.getTokens().get(0).toString();
        this.parents    = new LinkedHashSet<>();
        this.fields     = new LinkedHashSet<>();
        this.methods    = new LinkedHashSet<>();

        for(Token tok : token.getTokens().get(1).getTokens())
            if(tok.getType().equals(Token.Type.EMPTY_DECLARATION) || tok.getType().equals(Token.Type.FULL_DECLARATION))
                fields.add(new Field(tok));
            else if(tok.getType().equals(Token.Type.METHOD_DECLARATION))
                methods.add(new Method(tok));

        for(Field field : fields)
            field.setOffset(this.size ++);

        for(Method method : methods)
            method.setOffset(this.size ++);
    }

    protected Class(String name)
    {
        this.name       = name;
        this.parents    = new LinkedHashSet<>();
        this.fields     = new LinkedHashSet<>();
        this.methods    = new LinkedHashSet<>();
    }

    public void accessStaticField(String name, OpcodeWriter writer) throws IOException
    {
        for(Field field : fields)
            if (field.getName().equals(name))
            {
                writer.write(Opcode.LOAD);
                writer.getStream().writeInt(field.getOffset());
            }
    }

    public int getFieldByName(String name)
    {
        for(Field field : fields)
            if (field.getName().equals(name)) return field.getOffset();

        return 0;
    }

    public Method getMethoddByName(String name)
    {
        for(Method method : methods)
            if (method.getName().equals(name)) return method;

        return null;
    }

    public Class addMethod(Method method)
    {
        this.methods.add(method);
        return this;
    }

    public Class addField(Field field)
    {
        this.fields.add(field);
        return this;
    }

    public Object newInstance(Object...args) throws RuntimeException
    {
        return new Object(this, args);
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        String string = "---" + name + "---\n";

        for (Field field : fields)
            string += "\t" + field.getName() + " " + field.getType() + "\n";

        return string;
    }

    public Set<Field> getFields()
    {
        return fields;
    }

    public Object newInstanceBlank()
    {
        return new Object(this);
    }
}