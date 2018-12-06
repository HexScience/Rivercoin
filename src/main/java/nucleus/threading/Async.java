package nucleus.threading;

public abstract class Async<E extends ThreadedAccess & Runnable>
{
    protected E asyncronousObject;
    protected Thread    current;

    public <T> T get(String name)
    {
        return asyncronousObject.Get(name);
    }

    public <T> T blockingGet(String name)
    {
        return asyncronousObject.blockingGet(name);
    }

    public void start()
    {
        current = new Thread(asyncronousObject);
        current.start();
    }

    public void abort()
    {
        current.interrupt();
    }
}