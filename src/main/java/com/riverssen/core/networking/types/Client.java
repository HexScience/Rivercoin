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

package com.riverssen.core.networking.types;

import com.riverssen.core.FullBlock;
import com.riverssen.core.block.BlockHeader;
import com.riverssen.core.headers.ContextI;
import com.riverssen.core.headers.TransactionI;
import com.riverssen.core.networking.Communicator;
import com.riverssen.core.networking.NetworkManager;
import com.riverssen.core.networking.SocketConnection;
import com.riverssen.core.utils.ByteUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Client implements Communicator
{
    private SocketConnection        connection;
    private Map<Integer, byte[]>    unfulfilled;
    private long                    version;

    public Client(SocketConnection connection)
    {
        this.connection = connection;
        this.unfulfilled= new LinkedHashMap<>();
    }

    @Override
    public void closeConnection() throws IOException
    {
        connection.getOutputStream().writeInt(OP_HALT);
    }

    @Override
    public String getIP()
    {
        return connection.getIP();
    }

    @Override
    public int getType()
    {
        return 0;
    }

    @Override
    public void readInbox(ContextI context)
    {
        try{
            while(connection.getInputStream().available() > 0)
            {
                int OP = connection.getInputStream().readInt();
                int hashCode = -1;

                switch (OP)
                {
                    case OP_GREET:
                            this.version = connection.getInputStream().readLong();
                            greet(context);
                        break;
                    case OP_TXN:
                        hashCode = connection.getInputStream().readInt();
                        try{
                            TransactionI transaction = TransactionI.read(connection.getInputStream());
                            success(hashCode);

                            if(transaction.valid(context))
                            {
                                context.getTransactionPool().addRelayed(transaction);

                                Set<Communicator> peers = new LinkedHashSet<>(context.getNetworkManager().getCommunicators());
                                peers.remove(this);

                                for(Communicator communicator : peers)
                                    communicator.sendTransaction(transaction);
                            }
                        } catch (Exception e)
                        {
                            failed(hashCode);
                        }
                        break;
                    case OP_FAILED:
                        break;
                    case OP_SUCCESS:
                        unfulfilled.remove(connection.getInputStream().readInt());
                        break;
                }
            }
        } catch (Exception e)
        {
        }
    }

    private void success(int hashcode)
    {
        try{
            connection.getOutputStream().writeInt(OP_SUCCESS);
            connection.getOutputStream().writeInt(hashcode);
            connection.getOutputStream().flush();
        } catch (Exception e)
        {
        }
    }

    private void failed(int hashcode)
    {
        try{
            connection.getOutputStream().writeInt(OP_FAILED);
            connection.getOutputStream().writeInt(hashcode);
            connection.getOutputStream().flush();
        } catch (Exception e)
        {
        }
    }

    private void greet(ContextI context)
    {
        try{
            connection.getOutputStream().writeInt(OP_GREET1);
            connection.getOutputStream().writeLong(context.getVersionBytes());
            connection.getOutputStream().flush();
        } catch (Exception e)
        {
        }
    }

    @Override
    public void requestBlock(long block, ContextI context)
    {
        try
        {
            connection.getOutputStream().writeInt(OP_REQUEST);
            connection.getOutputStream().writeInt(OP_BLK);
            connection.getOutputStream().writeLong(block);

            connection.getOutputStream().flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void requestBlockHeader(long block, ContextI context)
    {
        try
        {
            connection.getOutputStream().writeInt(OP_REQUEST);
            connection.getOutputStream().writeInt(OP_BKH);
            connection.getOutputStream().writeLong(block);

            connection.getOutputStream().flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void requestListOfCommunicators(NetworkManager network)
    {
        try
        {
            connection.getOutputStream().writeInt(OP_REQUEST);
            connection.getOutputStream().writeInt(OP_PRS);

            connection.getOutputStream().flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void requestLatestBlockInfo(ContextI context)
    {
        try
        {
            connection.getOutputStream().writeInt(OP_REQUEST);
            connection.getOutputStream().writeInt(OP_BKI);

            connection.getOutputStream().flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendHandShake(long version)
    {
        try
        {
            connection.getOutputStream().writeInt(OP_GREET);
            connection.getOutputStream().writeLong(version);

            connection.getOutputStream().flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendTransaction(TransactionI transaction)
    {
        byte data[] = ByteUtil.getBytes(transaction);

        try
        {
            connection.getOutputStream().writeInt(OP_TXN);
            connection.getOutputStream().writeInt(data.hashCode());
            connection.getOutputStream().write(data);

            connection.getOutputStream().flush();

            unfulfilled.put(data.hashCode(), data);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBlock(FullBlock block)
    {
        byte data[] = ByteUtil.getBytes(block);

        try
        {
            connection.getOutputStream().writeInt(OP_BLK);
            connection.getOutputStream().writeInt(data.hashCode());
            connection.getOutputStream().write(data);

            connection.getOutputStream().flush();

            unfulfilled.put(data.hashCode(), data);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendBlockHeader(BlockHeader header)
    {
        byte data[] = ByteUtil.getBytes(header);

        try
        {
            connection.getOutputStream().writeInt(OP_BKH);
            connection.getOutputStream().writeInt(data.hashCode());
            connection.getOutputStream().write(data);

            connection.getOutputStream().flush();

            unfulfilled.put(data.hashCode(), data);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendListOfCommunicators(Set<Communicator> list)
    {
        StringBuilder builder = new StringBuilder();

        for (Communicator communicator : list) builder.append(communicator.getIP() + ",");
        byte data[] = builder.toString().getBytes();

        try
        {
            connection.getOutputStream().writeInt(OP_PRS);
            connection.getOutputStream().writeInt(data.hashCode());
            connection.getOutputStream().write(data);

            connection.getOutputStream().flush();

            unfulfilled.put(data.hashCode(), data);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLatestBlockInfo(long block)
    {
    }
}
