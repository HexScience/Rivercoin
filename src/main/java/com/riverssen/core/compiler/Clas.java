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

package com.riverssen.core.compiler;

import java.util.LinkedHashSet;
import java.util.Set;

public class Clas
{
    private static final Set<Clas> const_parents = new LinkedHashSet<>();
    static {
        Method toString = new ToStringMethod();

        Clas origin_class = new Clas("class").addMethod(toString);
        const_parents.add(origin_class);
    }

    private String          name;
    private Set<Clas>       parents;
    private Set<Field>      fields;
    private Set<Method>     methods;

    public Clas(String name)
    {
        this.name = name;
        this.parents = new LinkedHashSet<>();
        this.parents.addAll(const_parents);
    }

    public Clas addMethod(Method method)
    {
        this.methods.add(method);
        return this;
    }
}