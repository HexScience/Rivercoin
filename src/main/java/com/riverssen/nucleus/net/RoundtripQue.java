package com.riverssen.nucleus.net;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RoundtripQue
{
    private Queue<MessageRoundTrip> messages;

    public RoundtripQue()
    {
        this.messages = new ConcurrentLinkedQueue<>();
    }

    public void push(MessageRoundTrip message)
    {
        this.messages.add(message);
    }

    public MessageRoundTrip get()
    {
        return messages.poll();
    }
}
