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

import com.riverssen.core.mpp.compiler.Class;
import com.riverssen.core.mpp.compiler.Message;
import com.riverssen.core.mpp.compiler.Token;
import com.riverssen.core.mpp.exceptions.RuntimeException;
import com.riverssen.core.security.CompressedAddress;

import java.util.HashMap;
import java.util.Map;

public class RuntimeInterpreter
{
    private Map<String, Class>  classes;
    private Map<String, Object> objects;

    public RuntimeInterpreter(Token root, CompressedAddress address) throws RuntimeException
    {
        classes = new HashMap<>();
        objects = new HashMap<>();

        objects.put("msg", new Message().newInstance(new Object() {
            @Override
            public java.lang.Object asJavaObject()
            {
                return address;
            }
        }, new Object() {
            @Override
            public java.lang.Object asJavaObject()
            {
                return address.toPublicKey();
            }
        }, new Object() {
            @Override
            public java.lang.Object asJavaObject()
            {
                return address.toPublicKey().getAddress();
            }
        }));

        for(Token token : root.getTokens())
            if(token.getType().equals(Token.Type.CLASS_DECLARATION))
            {
                Class clss = new Class(token);
                if(classes.containsKey(clss.getName())) throw new RuntimeException("Class '" + clss.getName() + "' already defined.");

                classes.put(clss.getName(), clss);
                objects.put(clss.getName(), clss.newInstanceBlank());
            }
    }

    public Class getClass(String cla55)
    {
        return classes.get(cla55);
    }

    public Object getObject(String name)
    {
        return objects.get(name);
    }

    public void instantiate(String clss, Object ...args)
    {
        objects.put(clss, getClass(clss).newInstance(args));
    }
}
