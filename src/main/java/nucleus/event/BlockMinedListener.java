package nucleus.event;

public abstract class BlockMinedListener implements EventListener<RequestedBlockReceivedEvent>
{
    @Override
    public EventType getType()
    {
        return EventType.INTERNAL_NOTIFICATION;
    }
}