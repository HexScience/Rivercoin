package nucleus.threading;

public interface ThreadedAccess
{
    <T> T blockingGet(String name);
    <T> T Get(String name);
}
