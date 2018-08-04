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

public class MainCompiler
{
    public static void main(String args[])
    {
        final String[] commands = {
                "---commands---",
                "-c <path to main-class> --compiles and exports it to class.o file--",
                "-t <path to main-class> --creates human-readable abstract syntax tree and exports it to class.t file---",
                "-compile <path to main-class>",
                "-tree <path to main-class>",
                "---arguments---",
                "--v_x --appended at end of command to define language version (e.g --v_1_0a--"
        };

        System.out.println("hi");

        if (args == null && args.length == 0)
        {
            System.err.println("no arguments...");
            for (String string : commands)
                System.out.println(string);

            System.exit(0);
        }

        if (args.length > 0)
        {
            System.out.println(args.length);
        }
    }
}
