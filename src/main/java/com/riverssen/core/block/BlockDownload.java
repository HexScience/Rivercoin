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

package com.riverssen.core.block;

import java.io.*;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

public class BlockDownload implements Comparable<BlockDownload>
{
    private volatile long blockID;
    private volatile DataInputStream blockData;

    public BlockDownload(DataInputStream stream) throws IOException {
        this.blockID    = stream.readLong();
        int size        = stream.readInt();
        byte data[]     = new byte[size];
        stream.read(data);
        this.blockData  = new DataInputStream(new InflaterInputStream(new ByteArrayInputStream(data)));
    }

    public static void upload(FullBlock block, DataOutputStream stream) throws IOException {
        stream.writeLong(block.getBlockID());
        ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
        DeflaterOutputStream stream1 = new DeflaterOutputStream(stream2);

        DataOutputStream  blockStream = new DataOutputStream(stream1);

        block.export(blockStream);
        blockStream.flush();
        blockStream.close();

        byte data[] = stream2.toByteArray();

        stream.writeInt(data.length);
        stream.write(data);
    }

    public DataInputStream decompressedInputStream()
    {
        return blockData;
    }

    public long getBlockID() {
        return blockID;
    }

    @Override
    public int compareTo(BlockDownload o) {
        if(getBlockID() > o.getBlockID()) return 1;
        else if(getBlockID() == o.getBlockID()) return 0;
        return -1;
    }
}
