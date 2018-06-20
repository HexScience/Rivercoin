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

package com.riverssen.core.networking.messages;

import com.riverssen.core.headers.ContextI;
import com.riverssen.core.networking.Client;
import com.riverssen.core.networking.SocketConnection;

import java.io.IOException;

public abstract class BasicMessage
{
    public static final int OP_GREET    = 0;
    public static final int OP_HALT     = 1;
    public static final int OP_TXN      = 2;
    public static final int OP_BLK      = 3;
    public static final int OP_BKH      = 4;
    public static final int OP_PRS      = 5;
    public static final int OP_GREET1   = 6;
    public static final int OP_FAILED   = 7;
    public static final int OP_SUCCESS  = 8;

    public static final int OP_REQUEST  = 9;
    public static final int OP_BKI      = 10;
    public static final int OP_FAILED0  = 11;
    public static final int OP_ALL      = 12;
    public static final int OP_NODE     = 13;
    public static final int OP_OTHER    = 14;

    public static final int OP_MSG      = 2;


    String hashCode;
    int timesSent;

    public BasicMessage(String hashCodee)
    {
        this.hashCode = hashCode;
    }

    public void send()
    {
        timesSent ++;
    }
    public abstract void sendMessage(SocketConnection connection, ContextI context) throws IOException;
    public abstract void onReceive(Client client, SocketConnection connection, ContextI context) throws IOException;
//    public abstract void onReply(Client client, SocketConnection connection, ContextI context) throws IOException;

    public static BasicMessage decipher(int type, Client client)
    {
        switch (type)
        {
            case OP_GREET:
                return new GreetMessage();
            case OP_GREET1:
                //prevent spam
                if(!client.isGreeted())
                return new GreetReplyMessage();
                return null;
            case OP_BLK:
                return new BlockMessage();
            case OP_FAILED: return new FailedMessage();
            case OP_SUCCESS: return new SuccessMessage();

                default:
                    return null;
        }
    }

    public String getHashCode()
    {
        return hashCode;
    }

    public boolean stopAttemptingToSend()
    {
        return timesSent > 10 || timesSent < 0;
    }
}