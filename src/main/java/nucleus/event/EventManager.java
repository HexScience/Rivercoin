package nucleus.event;

import nucleus.exceptions.EventFamilyDoesNotExistException;
import nucleus.system.Context;
import nucleus.threading.Async;
import nucleus.threading.ThreadedAccess;
import nucleus.util.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;

public class EventManager extends Async<EventManager.EventManagerE>
{
    class EventManagerE implements Runnable, ThreadedAccess{
        private Map<String, EventFamily>                eventFamilyMap;
        private Queue<Tuple<String, ActionableEvent>>   fireQueue;
        private Context context;

        private EventManagerE(Context context)
        {
            this.context        = context;
            this.eventFamilyMap = new HashMap<>();
            this.fireQueue      = new LinkedTransferQueue<>();

            eventFamilyMap.put("block", new EventFamily());
            eventFamilyMap.put("transaction", new EventFamily());
            eventFamilyMap.put("server", new EventFamily());
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

        public void async_fire(ActionableEvent event, String family)
        {
            fireQueue.add(new Tuple<>(family, event));
        }

        private final void safe_fire(ActionableEvent event, String family)
        {
            if (eventFamilyMap.containsKey(family))
                eventFamilyMap.get(family.toLowerCase()).fire(event);
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

        @Override
        public void run()
        {
            while (context.keepAlive())
            {
                while (fireQueue.size() > 0)
                {
                    Tuple<String, ActionableEvent> eventTuple = fireQueue.poll();
                    safe_fire(eventTuple.getJ(), eventTuple.getI());
                }
            }
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

    public EventManager(Context context)
    {
        asyncronousObject   = new EventManagerE(context);
        start();
    }

    public void register(EventListener listener, String family) throws EventFamilyDoesNotExistException
    {
        asyncronousObject.register(listener, family);
    }

    public void async_fire(ActionableEvent event, String family)
    {
        asyncronousObject.async_fire(event, family);
    }

    private final void safe_fire(ActionableEvent event, String family)
    {
        asyncronousObject.safe_fire(event, family);
    }

    public void fire(ActionableEvent event, String family) throws EventFamilyDoesNotExistException
    {
        asyncronousObject.fire(event, family);
    }
}