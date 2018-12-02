package nucleus.event;

import nucleus.protocols.DownloadedBlock;

public class RequestedBlockReceivedEvent extends ActionableEvent<DownloadedBlock>
{
    public RequestedBlockReceivedEvent(long time, DownloadedBlock data)
    {
        super(EventType.REPLY_TO_REQUEST, time, data);
    }
}