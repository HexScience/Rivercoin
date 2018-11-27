package nucleus.net;

import nucleus.net.protocol.Message;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue
{
    private Queue<Message> messages;

    public MessageQueue()
    {
        this.messages = new ConcurrentLinkedQueue<>();
    }

    public void push(Message message)
    {
        this.messages.add(message);
    }

    public Message get()
    {
        return messages.poll();
    }
}