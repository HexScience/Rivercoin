package nucleus.protocols;

import nucleus.event.BlockNotificationEvent;
import nucleus.event.BlockNotificationEventListener;
import nucleus.exceptions.EventFamilyDoesNotExistException;
import nucleus.system.Context;

public class BlockChain implements BlockNotificationEventListener
{
    private Context context;

    public BlockChain(Context context) throws EventFamilyDoesNotExistException
    {
        this.context = context;
        this.context.getEventManager().register((BlockNotificationEventListener) this, "Block");
    }

    @Override
    public void onEvent(BlockNotificationEvent event)
    {
    }
}