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

import com.riverssen.core.headers.ContextI;
import com.riverssen.core.networking.messages.BasicMessage;
import com.riverssen.core.networking.messages.GoodByeMessage;
import com.riverssen.core.system.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Client implements Runnable
{
    private volatile SocketConnection          connection;
    private volatile ContextI                  context;

    private volatile Map<String, BasicMessage> cache;
    private volatile Set<BasicMessage>         toSend;
    private volatile String             lock;
    private volatile AtomicBoolean         relay;
    private volatile AtomicLong            version;
    private volatile AtomicLong            chainSize;
    private volatile AtomicBoolean         greeted;
    private volatile AtomicBoolean         blocked;

    public Client(SocketConnection connection, ContextI context)
    {
        this.connection = connection;
        this.context    = context;
        this.cache      = Collections.synchronizedMap(new LinkedHashMap<>());
        this.toSend     = Collections.synchronizedSet(new LinkedHashSet<>());
        this.version    = new AtomicLong(0);
        this.chainSize  = new AtomicLong(0);
        this.greeted    = new AtomicBoolean(false);
        this.relay      = new AtomicBoolean(false);
        this.blocked    = new AtomicBoolean(false);
    }

    public final void sendMessage(BasicMessage message)
    {
        this.sendMessage(message, "");
    }

    public final void sendMessage(BasicMessage message, String key)
    {
        if(keyMatch(key))
            forceSendMessage(message);
         else toSend.add(message);
    }

    private synchronized void forceSendMessage(BasicMessage message)
    {
        if(!cache.containsKey(message.getHashCode()))
            cache.put(message.getHashCode(), message);

        try{
            message.sendMessage(connection, context);
            connection.getOutputStream().flush();
            message.send();
        } catch (IOException e)
        {
            message.send();
        }

        if(message.stopAttemptingToSend())
            cache.remove(message.getHashCode());
    }

    public synchronized void update() throws IOException
    {
        while (connection.getInputStream().available() > 0)
        {
            BasicMessage message = safeNext();

            Logger.alert("--------MSG---------");
            Logger.alert(message + "");

            if(message != null)
                message.onReceive(this, connection, context);
        }

        for(BasicMessage message : toSend)
            forceSendMessage(message);

        Set<String> toRemove = new HashSet<>();

        for(String message : cache.keySet())
            if(cache.get(message).stopAttemptingToSend())
                toRemove.add(message);

        for(String message : toRemove)
            cache.remove(message);

        toSend.clear();
    }

    public final boolean keyMatch(String key)
    {
        if(lock == null) return true;

        return lock.equals(key);
    }

    public synchronized boolean lock(String key)
    {
        if(lock != null)
            return false;

        lock = key;

        return true;
    }

    public synchronized boolean unlock(String key)
    {
        if(lock == null) return true;

        if(lock.equals(key))
            lock = null;

        return lock == null;
    }

    public synchronized boolean isLocked()
    {
        return lock != null;
    }

    public boolean isRelay()
    {
        return relay.get();
    }

    public synchronized void setVersion(long l)
    {
        this.version.set(l);
    }

    public void setChainSize(long l)
    {
        System.out.println("CHAINSIZE ");
        this.chainSize.set(l);
    }

    public synchronized void setIsRelay(boolean r)
    {
        this.relay.set(r);
    }

    public synchronized void setGreeted(boolean g)
    {
        this.greeted.set(g);
    }

    public synchronized boolean isGreeted()
    {
        return greeted.get();
    }

    public synchronized void removeMessage(String s)
    {
        cache.remove(s);
    }

    public synchronized void resend(String s)
    {
        if(cache.containsKey(s))
            sendMessage(cache.get(s));
    }

    public long getChainSize()
    {
        return chainSize.get();
    }

    @Override
    public void run()
    {
        while (context.isRunning() && connection.isConnected())
        {
            try
            {
                update();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            try
            {
                Thread.sleep(12L);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public synchronized void closeConnection() throws IOException
    {
        sendMessage(new GoodByeMessage());
        connection.closeConnection();
    }

    private synchronized BasicMessage safeNext() throws IOException
    {
        int type = connection.getInputStream().readInt();

        return BasicMessage.decipher(type, this);
    }

    public synchronized BasicMessage next() throws IOException
    {
        long now = System.currentTimeMillis();
        while (connection.getInputStream().available() == 0) {
            if(System.currentTimeMillis() - now >= 230_000L) return null;
        }

        return safeNext();
    }

    public synchronized void block()
    {
        this.blocked.set(true);
    }

    public synchronized void unblock()
    {
        this.blocked.set(false);
    }

    public synchronized boolean isBlocked()
    {
        return blocked.get();
    }

    public synchronized BasicMessage getReply(String digest)
    {
        return null;
    }

    @Override
    public synchronized String toString() {
        return "client{ip: " + connection.getIP() + " relay: " + relay + " chain: " + chainSize + "}";
    }
}
