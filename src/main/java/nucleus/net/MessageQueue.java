package nucleus.net;

import java.util.LinkedHashSet;
import java.util.Set;

public class MessageQueue
{
    private Set<MessageRoundtrip> messages;

    public MessageQueue()
    {
        this.messages = new LinkedHashSet<>();
    }
}
