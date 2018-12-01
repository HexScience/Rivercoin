package nucleus.event;

public interface BlockNotificationEventListener extends EventListener<BlockNotificationEvent>
{
    default EventType getType()
    {
        return EventType.NOTIFICATION;
    }
}