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

package com.riverssen.core.old_networking;

import com.riverssen.core.headers.Encodeable;
import com.riverssen.core.headers.Exportable;
import com.riverssen.core.utils.ByteUtil;
import com.riverssen.core.utils.SmartDataTransferer;
import com.riverssen.core.utils.Tuple;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/** This DataPacket class is used to decrease spam in the network **/
public class DataPacket<T extends Exportable & Encodeable> implements Encodeable, Exportable
{
    private final T     packet;
    private final long  time;
    private final int   type;

    public DataPacket(T exportable, int type)
    {
        this.packet = exportable;
        this.time   = System.currentTimeMillis();
        this.type   = type;
    }

    @Override
    public byte[] getBytes() {
        return packet.data();
    }

    private Tuple<byte[], Long> hashcash()
    {
        byte data[] = ByteUtil.concatenate(ByteUtil.encodei(type), ByteUtil.encode(time), new byte[8]);

        ByteBuffer buffer = ByteBuffer.wrap(data);

        String hashCashDifficulty = "000000";
        long   nonce              = -1;

        while(ByteUtil.defaultEncoder().encode16(buffer.array()).startsWith(hashCashDifficulty))
            buffer.putLong(12, ++ nonce);

        return new Tuple<>(buffer.array(), nonce);
    }

    @Override
    public byte[] header() {

        Tuple<byte[], Long> hashcash = hashcash();
        return ByteUtil.concatenate(ByteUtil.encodei(type), ByteUtil.encode(time), hashcash.getI(), ByteUtil.encode(hashcash.getJ()));
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