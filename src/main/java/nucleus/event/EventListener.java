package nucleus.event;

public interface EventListener<T extends ActionableEvent>
{
    void onEvent(T event);
    EventType getType();
}