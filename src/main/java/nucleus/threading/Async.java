package nucleus.threading;

public abstract class Async<E extends ThreadedAccess & Runnable>
{
    protected E asyncronousObject;

    public <T> T get(String name)
    {
        return asyncronousObject.Get(name);
    }

    public <T> T blockingGet(String name)
    {
        return asyncronousObject.blockingGet(name);
    }
}