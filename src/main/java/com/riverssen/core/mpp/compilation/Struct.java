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

package com.riverssen.core.mpp.compilation;

import com.riverssen.core.mpp.compiler.Token;

import java.util.LinkedHashSet;
import java.util.Set;

public class Struct
{
    public static long NULL = 0;

    private String      __typename__;
    private long        __typesize__;
    private Set<Field>  __fields__;
    private Set<Method> __methods__;

    public Struct(Token token)
    {
        this.__fields__ = new LinkedHashSet<>();
        this.__methods__= new LinkedHashSet<>();
        this.__typesize__   = 0;
        this.__typename__ = token.getTokens().get(0).toString();
        Set<String> fields = new LinkedHashSet<>();

        for (Token t : token.getTokens().get(1).getTokens())
        {
            switch (t.getType())
            {
                case EMPTY_DECLARATION:
                    if (fields.contains(t.getTokens().get(1).toString()))
                    {
                        System.err.println("field __" + t.getTokens().get(1).toString() + "__ already exists in __" + __typename__ + "__.");
                        System.exit(0);
                    }
                    __fields__.add(new Field(t));
                    fields.add(t.getTokens().get(1).toString());
                    break;
                case FULL_DECLARATION:
                    if (fields.contains(t.getTokens().get(1).toString()))
                    {
                        System.err.println("field __" + t.getTokens().get(1).toString() + "__ already exists in __" + __typename__ + "__.");
                        System.exit(0);
                    }
                    __fields__.add(new Field(t));
                    fields.add(t.getTokens().get(1).toString());
                    break;
                case METHOD_DECLARATION:
                    Method method = new Method(t);

                    if (__methods__.contains(method))
                    {
                        System.err.println("method __" + t.getTokens().get(0).toString() + "__ already exists in __" + __typename__ + "__.");
                        System.exit(0);
                    }

                    __methods__.add(method);
                    break;
                case CLASS_DECLARATION:
                    System.err.println("class declaration not allowed inside a class __" + __typename__ + "__.");
                    default:
                        System.exit(0);
            }
        }
    }
}
