package nucleus.threading;

public abstract class Async<E extends ThreadedAccess & Runnable>
{
    public static final int
            ERR = 0,
            EXCECPTION = 1,
            SUCCESS = 2,
            NO_EXECUTE = 3,
            RUNNING = 4,
            PREPARING = 5,
            NULL_OBJECT = 6;

    protected E asyncronousObject;
    protected Thread    current;

    public <T> T get(String name)
    {
        return asyncronousObject == null ? (T) (Integer) NULL_OBJECT : asyncronousObject.Get(name);
    }

    public <T> T blockingGet(String name)
    {
        return asyncronousObject == null ? (T) (Integer) NULL_OBJECT : asyncronousObject.blockingGet(name);
    }

    public void start()
    {
        if (asyncronousObject == null) return;
        current = new Thread(asyncronousObject);
        current.start();
    }

    public void abort()
    {
        if (current == null) return;
        current.interrupt();
    }
}