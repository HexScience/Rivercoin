package nucleus.event;

import nucleus.threading.ThreadedAccess;

import java.util.List;

public class EventOrganizer implements Runnable, ThreadedAccess
{
    private List<ActionableEvent> list;

    public EventOrganizer()
    {
    }

    public void pushEvent(ActionableEvent event)
    {
        list.add(event);
    }

    @Override
    public void run()
    {
    }

    @Override
    public <T> T blockingGet(String name)
    {
        return null;
    }

    @Override
    public <T> T Get(String name)
    {
        return null;
    }
}
