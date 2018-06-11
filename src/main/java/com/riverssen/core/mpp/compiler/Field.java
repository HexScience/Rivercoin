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

package com.riverssen.core.mpp.compiler;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class Field extends Container implements Serializable
{
    private String          fieldName;
    private String          fieldType;
    private Set<Modifier>   fieldModifiers;
    private Token           defaultValue;
    private int             offset;

    public Field(String name, String type)
    {
        this(name, type, null);
    }

    public Field(String name, String type, Collection<Modifier> modifiers)
    {
        this.fieldName = name;
        this.fieldType = type;
        this.fieldModifiers = new LinkedHashSet<>();

        if (modifiers != null) this.fieldModifiers.addAll(modifiers);
    }

    public Field(Token tok)
    {
        this.fieldName = tok.getTokens().get(1).toString();
        this.fieldType = tok.getTokens().get(0).toString();

        this.fieldModifiers = new LinkedHashSet<>();
        this.fieldModifiers.addAll(tok.getModifiers());

        if (tok.getType().equals(Token.Type.FULL_DECLARATION)) this.defaultValue = tok.getTokens().get(2);
    }

    public Field addModifier(Modifier modifier)
    {
        this.fieldModifiers.add(modifier);
        return this;
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode();
    }

    protected void setOffset(int offset)
    {
        this.offset = offset;
    }

    public int getOffset()
    {
        return this.offset;
    }

    public String getName()
    {
        return this.fieldName;
    }

    public String getType()
    {
        return this.fieldType;
    }

    public Collection<Modifier> getModifiers()
    {
        return this.fieldModifiers;
    }
}