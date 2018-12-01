package nucleus.event;

public class ActionableEvent<T>
{
    private EventType   type;
    private long        time;
    private T           data;

    public ActionableEvent(EventType type, long time, T data)
    {
        this.type = type;
        this.time = time;
        this.data = data;
    }

    public EventType getType()
    {
        return type;
    }

    public long getTime()
    {
        return time;
    }

    public T getData()
    {
        return data;
    }
}