package nucleus.consensys;

import nucleus.event.*;
import nucleus.exceptions.EventFamilyDoesNotExistException;
import nucleus.mining.AsyncMiner;
import nucleus.mining.MiningThread;
import nucleus.mining.Nonce;
import nucleus.net.protocol.Message;
import nucleus.net.protocol.message.BlockNotifyMessage;
import nucleus.net.protocol.message.BlockRequestMessage;
import nucleus.net.server.IpAddress;
import nucleus.protocols.protobufs.Block;
import nucleus.system.Context;
import nucleus.system.Parameters;
import nucleus.threading.Async;
import nucleus.util.Base58;
import nucleus.util.ByteUtil;
import nucleus.util.Logger;
import nucleus.util.SortedLinkedQueue;
import nucleus.versioncontrol.Version;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.Queue;

public class BlockChain
{
    /**
     * The current NKContext
     */
    private Context context;
    /**
     * The current block.
     * This block is not finalized yet, meaning
     * it's not added to the blockchain yet.
     *
     * New transactions are added to this block,
     * and then the block is mined and validated
     * then sent out to peers, if a block-notification
     * is received the program has to choose between
     * the two.
     */
    private Block       current;
    private ForkManager forkManager;
    private AsyncMiner  miner;
    private long        requests;

    public BlockChain(Context context) throws EventFamilyDoesNotExistException
    {
        this.context = context;
        this.forkManager = new ForkManager();

        /**
         * Register two event listeners to the event manager,
         * so that any block related events will be listened to.
         */
        this.context.getEventManager().register((BlockNotificationEventListener) (_BlockNotificationEvent_)->{onEventBlockNotify(_BlockNotificationEvent_); }, "Block");
        this.context.getEventManager().register((RequestedBlockReceivedListener) (_RequestedBlockReceivedEvent_)->{onEventRequestedBlockReceived(_RequestedBlockReceivedEvent_); }, "Block");
        this.context.getEventManager().register((BlockMinedListener) (_MinedBlockEvent_)->{onBlockMinedEvent(_MinedBlockEvent_); }, "Block");
        this.miner = new AsyncMiner();
    }

    /**
     * @param event
     * This function gets called when a block is sent as a NOTIFICATION
     * most blocks received through this method are within the same blo
     * -ck height of our current block, therefore we check if it's hei
     * -ght is larger than or equal to our current block, never the
     * less, the block is first verified for validity, and then fur
     * -ther checks are done to decide what to do with the block.
     */
    public void onEventBlockNotify(BlockNotificationEvent event)
    {
        long blockHeight = event.getData().getBlock().getHeader().getBlockID();

        forkManager.add(event.getData());

        if (blockHeight == current.getHeader().getBlockID())
            handleSolvedBlock(event.getData());
        else if (blockHeight > current.getHeader().getBlockID())
            handleFutureBlock(event.getData());
    }

    /**
     * @param event
     * This functions is called when a block sent as a REPLY to a pre
     * -vious request the program has made to specific peers.
     * Any blocks received using this function get added to the block-
     * queue.
     */
    public void onEventRequestedBlockReceived(RequestedBlockReceivedEvent event)
    {
    }

    public void requestBlockFromPeer(long block, IpAddress peer)
    {
        Message request = new BlockRequestMessage(block);

        context.getServerManager().request(request, peer);
        byte checksum[] = request.getCheckSum();
    }

    /**
     * @param event
     * This function is called when a block is successfully mined,
     * if the current fork at this block-height is empty or this
     * block seems more favourable, then the block is added to it.
     *
     * The block is also sent to all other peers regardless of
     * whether or not it is favourable.
     */
    public void onBlockMinedEvent(BlockMinedEvent event)
    {
        forkManager.add(event.getData());
    }

    /**
     * @param block
     * This function handles any blocks that are received from
     * other peers that match the current chain's block-height.
     *
     * If the block is validated before the current block is
     * mined, then this block gets appended to the blockchain.
     */
    private void handleSolvedBlock(DownloadedBlock block)
    {
        traceForeignBlockChain(block);
    }

    /**
     * @param block
     * This function handles any blocks that are received
     * from other peers that are higher that the program's
     * current block-height;
     * The block received will be validated then added to
     * a blockqueue, then the program will check for pre
     * -vious blocks that lead to the future-block. If
     * they are found and validated then the chain will
     * append this block and any other previous blocks
     * that directly lead to it.
     */
    private void handleFutureBlock(DownloadedBlock block)
    {
    }

    /**
     * this function traces back a block to it's forkpoint,
     * if there is a future block and it's valid, that means
     * the current main-chain is short, so the program retra
     * -ces the future block to it's fork point, imports all
     * blocks since that point, and adjusts the chain.
     */
    private void traceForeignBlockChain(DownloadedBlock block)
    {
        long long_height = block.getBlock().getHeader().getBlockID();
        long crnt_height = current.getHeader().getBlockID();

        Queue<Block> trueFork = new SortedLinkedQueue<>();

        Block futureBlock = block.getBlock();
        /**
         * Get the previous block
         * If the previous block
         * matches our main chain
         * then we stop, if not
         * then we continue find
         * -ing older blocks, unt
         * -il we find the fork-
         * root.
         */
        Block forkedBlock = findByHash(forkManager, futureBlock.getHeader().getParentHash());

        /**
         * If a matching block cannot be found
         * then request it from the peer and
         * continue until the next realignment.
         */
        if (forkedBlock == null)
        {
            requestBlockFromPeer(futureBlock.getHeader().getBlockID(), block.getSender());
            return;
        }

        /**
         * Add the preleading block.
         */
        trueFork.add(forkedBlock);

        forkedBlock = getBlock(futureBlock.getHeader().getBlockID() - 1);

        if (forkedBlock != null && ByteUtil.equals(forkedBlock.getHeader().getHash(), futureBlock.getHeader().getHash()))
        {
        }
    }

    public static Block findByHash(ForkManager manager, byte[] hash)
    {
        for (ForkI forkI : manager.get())
            for (Block block : forkI.get())
                if (ByteUtil.equals(hash, block.getHeader().getHash()))
                    return block;

        return null;
    }

    /**
     * This function will lock a block and attempt to find
     * a solution for it, if the block is solved before any
     * solutions are found by other peers, it will be appen
     * -ded to the blockchain.
     *
     * Regardless of whether or not this block is the first
     * to be solved, it will be sent to other peers.
     */
    public void solveBlock()
    {
        double difficulty = getDifficulty(current);
        current.lock(difficulty);

        try
        {
            miner.setMinerInstance(new MiningThread(current.getHeader().getForMining().getBytes(), difficulty));
            miner.start();

            int code = miner.get("code");

            switch (code)
            {
                case Async.RUNNING:
                    return;
                case Async.ERR:
                    Logger.err("an error occurred: could not mine block '" + current.getHeader().getBlockID() + "'. errcode: '" + code + "'.");
                    return;
                case Async.EXCECPTION:
                    Logger.err("an error occurred: could not mine block '" + current.getHeader().getBlockID() + "'. errcode: '" + code + "'.");
                    return;
                case Async.NO_EXECUTE:
                    miner.start();
                    return;
                case Async.PREPARING:
                        return;
                case Async.SUCCESS:
                    Logger.alert("block '" + current.getHeader().getBlockID() + "' mined successfully!");
                    handleLocallySolvedBlock();
                    return;
                case Async.NULL_OBJECT:
                    miner.setMinerInstance(new MiningThread(current.getHeader().getForMining().getBytes(), difficulty));
                    miner.start();
                    return;
                    default:
                        return;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This function checks if the current miner succeeded in solving
     * the current block, if it has it calles the handlelocallysolvedblock
     * function and continues with the protocol.
     */
    protected void checkBlockSolved()
    {
        int code = miner.get("code");
        if (code == Async.SUCCESS)
            handleLocallySolvedBlock();
        else if (code == Async.ERR || code == Async.EXCECPTION)
            solveBlock();
    }

    /**
     * This function pulls the solution data from the AsyncMiner instance
     * and broadcasts it to the peers, it then adds the block to the chain
     * and continutes with the protocol.
     */
    protected void handleLocallySolvedBlock()
    {
        byte hash[] = ByteUtil.toByteArray((long[]) miner.get("hash"));
        Nonce nonce = miner.get("nonce");

        current.solve(nonce, hash);
        context.getServerManager().sendMessage(new BlockNotifyMessage(current));

        forkManager.add(current);

        newBlock();

        Logger.alert("result: " + Base58.encode(hash));
    }

    protected void newBlock()
    {
        current = new Block(current.getHeader().getBlockID() + 1, current.getHeader().getHash(), Version.getLatest().getVersion());
    }

    /**
     * This function will attempt to realign the chain;
     * Every set amount of time every chain in the network
     * will need to be reset and aligned into one chain, peers
     * will attempt to align their chains to the same fork
     * by validating any orphaned/queued blocks and voting
     * on the best fork (?).
     *
     * This function should be called every 10 minutes.
     */
    protected void realign()
    {
        /**
         * 50 blocks * 12seconds each = 10 minutes.
         */
        if (getMain().size() == 50)
        {
        }
    }

    /**
     * @param blocks
     * This function will attempt to serialize the best chain
     * since the last 10 minutes to disk.
     */
    protected void serialize(SortedLinkedQueue<Block> blocks)
    {
    }

    protected double getDifficulty(final Block block)
    {
        Block prev = getBlock(block.getHeader().getBlockID() - 1);
        Block bbfr = getBlock(prev.getHeader().getBlockID() - 1);

        return Parameters.calculateDifficulty(prev.getHeader().getTimeStamp(), bbfr.getHeader().getTimeStamp(), prev.getHeader().getDifficulty());
    }

    protected static Block getBlock(Queue<Block> blocks, final long block)
    {
        for (Block find : blocks)
            if (find.getHeader().getBlockID() == block)
                return find;
            else if (find.getHeader().getBlockID() > block)
                break;

        return null;
    }

    /**
     * @param block The block height of the block to be found.
     * @return The block if it exists on the main chain, else
     * return the block loaded from the serializer.
     */
    protected Block getBlock(final long block)
    {
        for (Block find : forkManager.getMain().get())
            if (find.getHeader().getBlockID() == block)
                return find;
            else if (find.getHeader().getBlockID() > block)
                break;

        return loadBlock(block);
    }

    protected Block loadBlock(final long block)
    {
        return context.getSerializer().loadBlock(block);
    }

    protected Queue<Block> getMain()
    {
        return forkManager.getMain().get();
    }

    public long chainSize()
    {
        return current == null ? -1 : current.getHeader().getBlockID();
    }

    /**
     * The main chain loop
     */
    public void run()
    {
        while (context.keepAlive())
        {
            realign();
            checkBlockSolved();
        }
    }
}