package nucleus.net;

import nucleus.net.protocol.Message;

public class MessageRoundTrip
{
    private static final long retry_time = 5_000L;
    private final Message msg;
    long lastTimestamp  = 0;
    long timeout        = 0;
    long retryTimes     = 0;
    boolean answered    = false;
    boolean mustreply;

    public MessageRoundTrip(Message msg)
    {
        this(msg, false);
    }

    public MessageRoundTrip(Message msg, boolean mustreply)
    {
        this(msg, mustreply, -1);
    }

    public MessageRoundTrip(Message msg, boolean mustreply, long timeOut)
    {
        this.msg = msg;
        this.mustreply = mustreply;
        saveTime();
    }

    public boolean isAnswered()
    {
        return answered;
    }

    public void answer()
    {
        answered = true;
    }

    public long getRetryTimes()
    {
        return retryTimes;
    }

    public void incRetryTimes()
    {
        ++retryTimes;
    }

    public long getTimeout()
    {
        return timeout;
    }

    public boolean hasTimedout()
    {
        return (retryTimes * retry_time) >= timeout;
    }

    public void saveTime()
    {
        lastTimestamp = System.currentTimeMillis();
    }

    public boolean hasToRetry()
    {
        return retry_time < System.currentTimeMillis() - lastTimestamp;
    }

    public Message getMsg()
    {
        return msg;
    }
}