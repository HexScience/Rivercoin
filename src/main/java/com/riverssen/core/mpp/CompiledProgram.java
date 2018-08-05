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
import com.riverssen.core.utils.Tuple;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class CompiledProgram
{
    private Executable executable;

    public CompiledProgram(ParsedProgram parsedProgram)
    {
        Token root = parsedProgram.getRoot();
        executable = new Executable();

        simulate(root);
    }

    /** for none contract type compilation **/
    private void simulate(Token root)
    {
        Set<Token>              methods_ = new LinkedHashSet<>();
        Set<Token>              structs_ = new LinkedHashSet<>();
        Map<String, Integer>    sizeof_ = new HashMap();
        Map<String, Integer>    tablef_ = new HashMap<>();

        int funcs_  = 0;
        int _stack_ = 0;

        Map<String, Tuple<Integer, Object>> map = new HashMap();

        class _struct_{
            Token   _this_;
            String  _typename_;
            int     _typesize_;
            int     _stackidx_;
            int     _memryidx_;

            _struct_(Token token)
            {
                this._this_ = token;
            }

            Token find_method(String name, _struct_ ...args)
            {
                for (Token token : _this_.getTokens().get(2).getTokens())
                    if (token.getType().equals(Token.Type.METHOD_DECLARATION))
                    {
                    }

                    String args_ = "";

                    for (_struct_ arg : args)
                        args_ += (arg._typename_ + " ");

                    System.err.println("compilation error: no function '" + name + "' with args '" + args_.substring(0, args_.length() - 1) + "'.");

                return null;
            }

            void _empty_constructor_()
            {
            }

            void _argtd_constructor_(_struct_ ...args)
            {
            }

            void _new_(_struct_ ...args)
            {
                /** push a reference to pointer **/
                executable.add(instructions.push_a);
                /** make the size of the pointer the same as _typesize_ **/
                executable.addAll(executable.convertInt(_typesize_));

                if (args == null || args.length == 0)
                    _empty_constructor_();
                else _argtd_constructor_(args);
            }

            void _simulate_method_(_struct_ ...methods)
            {
            }

            void _simulate_fetch_()
            {
                executable.add(instructions.memory_load);
                executable.add(executable.convertInt(_memryidx_));
            }

            void simulate()
            {
            }
        }

        _struct_ __struct__ = new _struct_(root);

        for (Token token : root.getTokens())
        {
            if (token.getType().equals(Token.Type.METHOD_DECLARATION))
            {
                tablef_.put(token.toString(), methods_.size());
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
