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

package com.riverssen.core.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketConnection
{
    private String                  ip;
    private int                     port;
    private Socket                  socket;
    private DataInputStream         inputStream;
    private DataOutputStream        outputStream;
    private int                     type;

    public SocketConnection(String ip, int port) throws IOException
    {
        this.ip             = ip;
        this.port           = port;
        this.socket         = new Socket(ip, port);
        this.inputStream    = new DataInputStream(socket.getInputStream());
        this.outputStream   = new DataOutputStream(socket.getOutputStream());
    }

    public SocketConnection(ServerSocket socket) throws IOException
    {
        this.socket         = socket.accept();
        this.ip             = this.socket.getRemoteSocketAddress().toString();
        this.port           = this.socket.getPort();
        this.inputStream    = new DataInputStream(this.socket.getInputStream());
        this.outputStream   = new DataOutputStream(this.socket.getOutputStream());
    }

    public void sendMessage(Msg msg)
    {
        msg.send(outputStream);
    }

    public DataInputStream getInputStream()
    {
        return inputStream;
    }

    public DataOutputStream getOutputStream()
    {
        return outputStream;
    }

    public boolean isConnected()
    {
        return socket.isConnected();
    }

    public void closeConnection() throws IOException
    {
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    public String getIP()
    {
        return ip;
    }

    public int    getType()
    {
        return type;
    }

    public void   setType(int type)
    {
        this.type = type;
    }

    @Override
    public String toString()
    {
        return ip + port;
    }
}
