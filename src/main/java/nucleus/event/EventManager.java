package nucleus.event;

import nucleus.exceptions.EventFamilyDoesNotExistException;

import java.util.HashMap;
import java.util.Map;

public class EventManager
{
    private Map<String, EventFamily> eventFamilyMap;

    public EventManager()
    {
        this.eventFamilyMap = new HashMap<>();
        eventFamilyMap.put("block", new EventFamily());
        eventFamilyMap.put("transaction", new EventFamily());
    }

    public void register(EventListener listener, String family) throws EventFamilyDoesNotExistException
    {
        try{
            eventFamilyMap.get(family.toLowerCase()).add(listener);
        } catch (NullPointerException e)
        {
            throw new EventFamilyDoesNotExistException(family);
        }
    }

    public void fire(ActionableEvent event, String family) throws EventFamilyDoesNotExistException
    {
        try{
            eventFamilyMap.get(family.toLowerCase()).fire(event);
        } catch (NullPointerException e)
        {
            throw new EventFamilyDoesNotExistException(family);
        }
    }
}