package com.riverssen.nucleus.mochaxx;

import com.riverssen.nucleus.exceptions.MXTypeNotSupportedException;

import java.nio.ByteBuffer;

public class Mochaxx
{
    public static final class mx_struct{
        private final long pointer;

        public mx_struct(long pointer)
        {
            this.pointer = pointer;
        }

        public <T> T load(Class<T> t, int index) throws MXTypeNotSupportedException
        {
            if (t == Byte.class)
                return (T) (Byte) mx_loadbyte(index);
            else if (t == Character.class)
                return (T) (Short) mx_loadshort(index);
            else if (t == Short.class)
                return (T) (Short) mx_loadshort(index);
            else if (t == Integer.class)
                return (T) (Integer) mx_loadint(index);
            else if (t == Long.class)
                return (T) (Long) mx_loadlong(index);
            else if (t == Float.class)
                return (T) (Float) mx_loadfloat(index);
            else if (t == Long.class)
                return (T) (Double) mx_loaddouble(index);
            else if (t == mx_longint.class) { ByteBuffer buffer = mx_longint.buffer(); mx_loadlongint(index, buffer); return (T) buffer; }
            else if (t == mx_longlong.class) { ByteBuffer buffer = mx_longlong.buffer(); mx_loadlonglong(index, buffer); return (T) buffer; }
            else if (t == mx_doublefloat.class) { ByteBuffer buffer = mx_doublefloat.buffer(); mx_loaddoublefloat(index, buffer); return (T) buffer; }
            else if (t == mx_doubledouble.class) { ByteBuffer buffer = mx_doubledouble.buffer(); mx_loaddoubledouble(index, buffer); return (T) buffer; }
            else if (t == mx_struct.class) return (T) new mx_struct(mx_loadpointeraslong(index));
            else if (t == null) throw new MXTypeNotSupportedException("null");
            else throw new MXTypeNotSupportedException(t.getSimpleName());
        }
    }

    public static final class mx_longint { static final ByteBuffer buffer() { return ByteBuffer.allocateDirect(16); } }
    public static final class mx_longlong { static final ByteBuffer buffer() { return ByteBuffer.allocateDirect(32); } }
    public static final class mx_doublefloat { static final ByteBuffer buffer() { return ByteBuffer.allocateDirect(16); } }
    public static final class mx_doubledouble { static final ByteBuffer buffer() { return ByteBuffer.allocateDirect(32); } }

    public static final native byte     mx_loadbyte(long loc);
    public static final native short    mx_loadshort(long loc);
    public static final native int      mx_loadint(long loc);
    public static final native long     mx_loadlong(long loc);
    public static final native long     mx_loadpointeraslong(long loc);
    public static final native float    mx_loadfloat(long loc);
    public static final native double   mx_loaddouble(long loc);

    public static final native void     mx_loadlongint(long loc, ByteBuffer buffer);
    public static final native void     mx_loadlonglong(long loc, ByteBuffer buffer);
    public static final native void     mx_loaddoublefloat(long loc, ByteBuffer buffer);
    public static final native void     mx_loaddoubledouble(long loc, ByteBuffer buffer);
}