package com.riverssen.core.block;

import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.headers.Exportable;
import com.riverssen.core.utils.SmartDataTransferer;

import java.io.DataOutputStream;
import java.io.IOException;

public class UTXOData implements Encodeable, Exportable
{
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    @Override
    public byte[] header() {
        return new byte[0];
    }

    @Override
    public byte[] content() {
        return new byte[0];
    }

    @Override
    public void export(SmartDataTransferer smdt) {

    }

    @Override
    public void export(DataOutputStream dost) throws IOException {

    }
}