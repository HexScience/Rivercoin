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
    private Set<LexicalToken> allChars = new LinkedHashSet<>();
    private LexicalToken      curtoken = null;

    private char             last     = '\0';

    public LexedProgram(String program)
    {
        List<Character> exploded = explode(program);

        while(exploded.size() > 0)
        {
            char current = exploded.get(0);
            add(current);
            exploded.remove(0);
        }
    }

    int line = 1, offset = 1, whitespace = 0;

    private void wrap()
    {
        allChars.add(curtoken);
    }

    private boolean add(char chr)
    {
        if(chr == '\n')
        {
            line ++;
            offset = 1;
            whitespace = 0;
            wrap();
        } else if (chr == ' ')
        {
            whitespace ++;
            wrap();
        }
        else if (chr == '\t')
        {
            whitespace += 4;
            wrap();
        }
        else
        {
            if(curtoken == null)
                curtoken = new LexicalToken(chr, line, offset, whitespace);

            /** check char is a separator **/
            final char separators[] = {'.','=','+','-','\'','"',',','<','>','?',';',':','!','\\','/',
            '[',']','{','}','(',')','*','&','^','%','$','#','@'};

            boolean isSeparator = false;

            boolean escaped     = last == '\\';

            boolean string      = curtoken.toString().startsWith("\"") || curtoken.toString().startsWith("'");

            for(char s : separators) if(chr == s) isSeparator = true;

            if(isSeparator && !escaped)
            {
               wrap();

               if(chr == '\'' || chr == '"')
               {
                    if(curtoken.toString().startsWith("\"") || curtoken.toString().startsWith("'"))
                    {
                        curtoken.append(chr);
                        curtoken = null;
                    } else
                        curtoken = new LexicalToken(chr, line, offset, whitespace);
               } else {
                    curtoken = new LexicalToken(chr, line, offset, whitespace);
                    allChars.add(curtoken);
               }

               curtoken = null;
            } else curtoken.append(chr);
        }

            return false;
    }

    private List<Character> explode(String program)
    {
        List<Character> list = new ArrayList<>();

        for(char chr : program.toCharArray())
            list.add(chr);

        return list;
    }

    public Set<LexicalToken> getTokens()
    {
        return allChars;
    }
}