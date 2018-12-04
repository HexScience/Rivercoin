package nucleus.event;

public abstract class BlockMinedListener implements EventListener<BlockMinedEvent>
{
    @Override
    public EventType getType()
    {
        return EventType.INTERNAL_NOTIFICATION;
    }
}