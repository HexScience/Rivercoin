package nucleus.unsafe;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Mem
{
    private static Unsafe unsafe;

    public static final void init() throws NoSuchFieldException, IllegalAccessException
    {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        unsafe = (Unsafe) f.get(null);
    }

    public static final long malloc(long size)
    {
        return unsafe.allocateMemory(size);
    }

    public static final void put(long pointer, byte[] bytes)
    {
    }
}