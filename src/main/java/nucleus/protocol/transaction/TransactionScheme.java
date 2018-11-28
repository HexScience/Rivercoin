package nucleus.protocol.transaction;

import java.util.HashMap;
import java.util.Map;

public abstract class TransactionScheme
{
    private static final Map<String, TransactionScheme> schemes = new HashMap<>();

    public static final TransactionScheme getInstance(String name)
    {
        return schemes.get(name);
    }
}