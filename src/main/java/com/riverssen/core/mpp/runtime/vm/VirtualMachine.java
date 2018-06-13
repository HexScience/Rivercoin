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

package com.riverssen.core.mpp.runtime.vm;

import com.riverssen.core.headers.ContextI;
import com.riverssen.core.mpp.compiler.OpcodeParser;
import com.riverssen.core.mpp.runtime.vm.memory.VirtualMemory;
import com.riverssen.core.mpp.runtime.vm.startupdisk.VirtualStorageSpace;
import com.riverssen.core.mpp.Opcode;

public class VirtualMachine
{
    private VirtualMemory           memory;
    private Opcode                  program[];
    private VirtualStorageSpace     vss;

    public VirtualMachine(byte program[], ContextI contextI)
    {
        this(new OpcodeParser(program).getOpcode(), contextI);
    }

    public VirtualMachine(Opcode program[], ContextI contextI)
    {
        this.program    = program;
        this.memory     = new VirtualMemory();
        this.vss        = new VirtualStorageSpace(contextI.getConfig().getVSSSize(), contextI.getConfig().getVSSDirectory());
    }

    public VirtualMemory getMemory()
    {
        return memory;
    }

    public void execute(String... args)
    {
        for(Opcode opcode : program)
            opcode.execute(this);
    }

    public VirtualStorageSpace getStorage()
    {
        return vss;
    }
}