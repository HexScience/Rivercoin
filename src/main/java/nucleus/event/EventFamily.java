package nucleus.event;

import java.util.HashSet;
import java.util.Set;

public class EventFamily
{
    private Set<EventListener> listeners;

    public EventFamily()
    {
        this.listeners = new HashSet<>();
    }

    public void fire(ActionableEvent event)
    {
        for (EventListener listener : listeners)
            if (event.getType().equals(listener.getType()))
                listener.onEvent(event);
    }

    public void add(EventListener listener)
    {
        this.listeners.add(listener);
    }
}