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

import java.util.*;

public class LexedProgram
{
    private Set<LexicalChar> allChars = new LinkedHashSet<>();
    private char             last     = '\0';

    public LexedProgram(String program)
    {
        List<Character> exploded = explode(program);

        while(exploded.size() > 0)
        {
            char current = exploded.get(0);



            exploded.remove(0);
        }
    }

    int line, offset, whitespace;

    private void add(char chr)
    {
        if(chr == '\n')
        {
            line ++;
            offset = 1;
            whitespace = 0;
        }

        if(chr == last)
        {
        }

        allChars.add(new LexicalChar(chr, line, offset, whitespace));
    }

    private List<Character> explode(String program)
    {
        List<Character> list = new ArrayList<>();

        for(char chr : program.toCharArray())
            list.add(chr);

        return list;
    }
}