package com.riverssen.core.mpp.compiler.opcodes;

import com.riverssen.core.mpp.runtime.vm.VirtualMachine;
import com.riverssen.core.mpp.Opcode;

import java.util.List;

public class ClassOpcode implements Opcode
{
    private List<Opcode> opcodes;

    public ClassOpcode(List<Opcode> opcodes)
    {
        this.opcodes = opcodes;
    }

    @Override
    public void execute(VirtualMachine context)
    {
        for(Opcode opcode : opcodes)
            if(opcode instanceof FunctionOpcode)
                opcode.execute(context);
    }
}
