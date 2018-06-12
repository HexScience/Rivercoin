package com.riverssen.core.mpp.compiler.opcodes;

import com.riverssen.core.mpp.compiler.Token;
import com.riverssen.core.mpp.runtime.vm.VirtualMachine;
import com.riverssen.core.mpp.Opcode;

import java.util.ArrayList;
import java.util.List;

public class FunctionOpcode implements Opcode
{
    private List<Opcode> opcodeList;

    public FunctionOpcode(Token body)
    {
        opcodeList = new ArrayList<>();
    }

    public FunctionOpcode add(Opcode opcode)
    {
        this.opcodeList.add(opcode);
        return this;
    }

    @Override
    public void execute(VirtualMachine context)
    {
        for (Opcode opcode : opcodeList) opcode.execute(context);
    }
}
