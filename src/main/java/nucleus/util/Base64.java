package nucleus.util;

public class Base64
{
    public static byte[] decode(String data)
    {
        return java.util.Base64.getUrlDecoder().decode(data);
    }

    public static String encode(byte bytes[])
    {
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
