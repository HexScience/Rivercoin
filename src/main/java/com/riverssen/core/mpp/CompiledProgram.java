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

package com.riverssen.core.mpp;

import com.riverssen.core.mpp.compiler.ParsedProgram;
import com.riverssen.core.mpp.compiler.Token;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CompiledProgram
{
    private Executable executable;

    public CompiledProgram(ParsedProgram parsedProgram)
    {
        Token root = parsedProgram.getRoot();
        executable = new Executable();

        simulate(root);
    }

    private void simulate(Token root)
    {
        Set<Token> methods_ = new LinkedHashSet<>();
        Set<Token> structs_ = new LinkedHashSet<>();
        Map<String, Integer> sizeof_ = new HashMap();
        Map<String, Integer> tablef_ = new HashMap<>();

        for (Token token : root.getTokens())
        {
            if (token.getType().equals(Token.Type.METHOD_DECLARATION))
            {
                tablef_.put(token.)
                methods_.add(token);
            }
            else if (token.getType().equals(Token.Type.CLASS_DECLARATION))
                structs_.add(token);
        }
    }

    public boolean spit(File file)
            throws IOException
    {
        FileOutputStream io = new FileOutputStream(file);

        for (Byte byt : executable.op_codes)
            io.write(Byte.toUnsignedInt(byt.byteValue()));

        io.flush();

        return true;
    }
}
