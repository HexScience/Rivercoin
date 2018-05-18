package com.riverssen.core.chain;

import com.riverssen.core.*;
import com.riverssen.core.consensus.ConsensusAlgorithm;
import com.riverssen.core.consensus.Solution;
import com.riverssen.core.security.PubKey;
import com.riverssen.core.security.Wallet;
import com.riverssen.core.tokens.RewardToken;
import com.riverssen.core.tokens.SignedTransaction;
import com.riverssen.core.system.LatestBlockInfo;
import com.riverssen.core.tokens.Token;
import com.riverssen.utils.FileUtils;
import com.riverssen.utils.HashUtil;
import com.riverssen.utils.MerkleTree;
import com.riverssen.utils.Tuple;

import java.io.*;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.DeflaterOutputStream;

public class Block
{
//    private static final Database $db = new Database("rvc-core");
    //String address
    //String in-transactions
    //String ot-transactions
//    private static final Table    $UTXO = $db.newTable("utxo", 3);
    private long                blockID;
    private String              lastHash;
    private StringBuilder       tokens;
    //    private StringBuilder   linkedHashes;
//    private int             index;
    private String              hash;
    private Block               parent;
    private TXIO                txio;
//    private long            solvedAt;
    //    private Solution        solution;
    private ByteBuffer          buffer;
    //    private ByteBuffer      linkedHashBuffer;
    private Long                nonce;
    private Block               byteBufferCopy;
    private List<Token>         tokenList;
    private MerkleTree          merkleTree;
    private ExecutorService     mineService;
    private boolean             mine;
    private long                shuffle;
    private String              block_version;
    private long                block_size;
    public static final long    MAX_BYTES = (long) (((1000_000.0 / 0.17625)) / 10.0); //divided by average compression ratio.
    private List<String>        hashList;
//    private Set<String> hashSet;

    public Block()
    {
        this((Block) null);
    }

    public Block(File file, int mode)
    {
        FileUtils.moveFromTemp(Config.getConfig().BLOCKCHAIN_DIRECTORY);

        try
        {
            DataInputStream stream = new DataInputStream(new FileInputStream(file));

            int headerSize = 26;

            byte header[] = new byte[headerSize];

            this.block_version = stream.readUTF();

            stream.read(header);

            ByteBuffer headerBuffer = ByteBuffer.wrap(header);

//            byte in[]       = out.toByteArray();
//            buffer          = ByteBuffer.wrap(in);

            nonce           = headerBuffer.getLong();
            blockID         = headerBuffer.getLong();
            block_size      = headerBuffer.getLong();
//            shuffle         = headerBuffer.getLong();
//            int sizeTokens  = headerBuffer.getInt();
//            int sizeTrnxns  = headerBuffer.getInt();
//            int sizeTokensD = headerBuffer.getInt();
//            int sizeTrnxnsD = headerBuffer.getInt();
            int sizeHeader  = headerBuffer.getShort();

//            buffer.putLong(nonce);
//            buffer.putLong(blockID);
//            buffer.putLong(shuffle);
//            buffer.putInt(tokenBytes.length);
//            buffer.putInt(transactions.length);
//            buffer.putShort((short) myHash.length);
//            buffer.put(myHash);
//            buffer.put(transactions);
//            buffer.put(tokenBytes);
//            buffer.flip();

            byte mhash[] = new byte[sizeHeader];
//            headerBuffer.get(mhash);
            stream.read(mhash);

            hash = HashUtil.blockHashImport(new String(mhash));

            MerkleTree tree = new MerkleTree();

//            if (mode == 0)
//            {
//                byte transactions[] = new byte[sizeTrnxns];
//                stream.read(transactions);
//                stream.close();
//
//                InflaterInputStream inflater = new InflaterInputStream(new ByteArrayInputStream(transactions));
//
//                int b = 0;
//
//                byte decompressed[] = new byte[sizeTrnxnsD];
//
//                inflater.read(decompressed);
//
////                while((b = inflater.read()) != -1)
////                    out.write(b);
//
////                out.flush();
////                out.close();
//
////                transactions = out.toByteArray();
//
////                String transactionS = new String(transactions, StandardCharsets.UTF_8);
//
////                System.out.println(transactionS);
//
//                txio = new TXIO(new DataInputStream(new ByteArrayInputStream(decompressed)));
//            }
//            else if (mode == 1)
//            {
//                byte transactions[] = new byte[sizeTrnxns];
//                byte tokensbblock[] = new byte[sizeTokens];
//
//                buffer.get(transactions);
//                buffer.get(tokensbblock);
//
//                stream.close();
//            }

        } catch (Exception e)
        {
            Logger.err("couldn't load block from file.");
            e.printStackTrace();
        }
    }

    public Block(Block parent)
    {
        this.lastHash = parent != null ? parent.getHash() : "0";
        this.tokens = new StringBuilder();
//        this.linkedHashes = new StringBuilder();
        this.parent = parent;
        this.txio = new TXIO();
        this.blockID = parent != null ? parent.getID() + 1L : 0L;
        this.tokenList = Collections.synchronizedList(new ArrayList<>());
        this.hashList = Collections.synchronizedList(new ArrayList<>());
        this.hashList.add(lastHash);
        this.shuffle = 0;
        this.block_version = RVCCore.version;
        this.tokens.append("\"id\":\"" + blockID + "\",\"lastHash\":\"" + lastHash + "\",\"block\":{");
//        this.hashSet = Collections.synchronizedSet(new HashSet<>());

//        this.solution = new Solution(this.getID(), RVCCore.get().getWallet().getPublicKey().getPublicAddress(), "");

        if (parent != null)
        {
            int i = 0;

            Block testParent = parent;
            while ((testParent = testParent.parent) != null)
            {
                if(i ++ > 5)
                    testParent.parent = null;
            };

            /**
             * Clear linked list from memory
             * If there are no references to the parent object
             * then garbage collection will take care of it
             */

//            if (i > 5) testParent.parent = null;
        }

//        linkedHashes.append(lastHash);
    }

    public synchronized boolean verifyWork(Solution solution)
    {
        return false;
    }

    /**
//     * @param tokenList a list of tokens sent by the peers
     * @return true if the solution is correct, and the block will stop attempting to mine.
     */
    public synchronized boolean verifyWork(long nonce, long blockID, String parentHash, Token reward)//List<Transaction> tokenList)
    {
        /**
         * check block is equal to this block, if it's older, that means block is already mined.
         */
        if(blockID != this.blockID) return false;

        /**
         * check block parent's hash is the same as ours, meaning the miner didn't fork the blockchain.
         */
        if(parentHash != this.getParentHash()) return false;

//        verifyTokenList();

        /**
         * Check miner reward equals the allowed reward
         */

//        if(!tokenList.get(tokenList.size() - 1).getAmount().equals(RiverCoin.fastRiverCoinValueToNanoCoinConversion(Config.getConfig().REWARD_PER_BLOCK_MINED + "")))
        if(!reward.getAmount().equals(RiverCoin.fastRiverCoinValueToNanoCoinConversion(Config.getConfig().REWARD_PER_BLOCK_MINED + "")))
            return false;

        /**
         * Proof of work verification
         */

        String lastHash = parentHash;

        for(int i = 0; i < tokenList.size() - 1; i ++)
            lastHash = tokenList.get(i).getHashAsString(lastHash);

//        return (tokenList.get(tokenList.size() - 1).getHashAsString(nonce, lastHash).substring(Config.getConfig().BLOCK_MINING_DIFFICULTY).equals(HashUtil.createDifficultyString()));
        return reward.getHashAsString(nonce, lastHash).substring(HashUtil.createDifficultyString().length()).equals(HashUtil.createDifficultyString());
    }

    private synchronized boolean verifyTokenList()
    {
        /**
         * verify the tokens the miner verified.
         * first if statement makes sure both tokenlists are the correct size
         * if this blocks token list is smaller, that means the block missed some transactions.
         */

        if(tokenList.size() > this.tokenList.size())
            for(int i = 0; i < tokenList.size() - 1; i ++)
            { if (!verifyToken(tokenList.get(i))) return false; }
        else
            for(int i = 0; i < tokenList.size() - 1; i ++)
                if (!verifyToken(tokenList.get(i))) return false;
                else if (!this.tokenList.get(i).toJSON().equals(tokenList.get(i).toJSON())) return false;

        return true;
    }

    private synchronized boolean verifyToken(Token token)
    {
//        if(!this.hashSet.contains(token.getHashAsString())) return false;
        if(!TXIO.transactionSafe(token)) return false;

        if(token.isTransaction())
        {
            String json = token.toJSON();
            /**
             * Check the signature of the transaction.
             */

            PubKey sender   = new PubKey(token.getSenderAddress());
            PubKey receiver = new PubKey(token.getReceiverAddress());

            if(!sender.isValid())
            {
                Logger.err("sender is not valid!");
                return false;
            }

            if(!receiver.isValid())
            {
                Logger.err("receiver is not valid!");
                return false;
            }

            if(!sender.verifySignature(json, ((SignedTransaction)token).getSignature()))
            {
                Logger.err("signature invalid!");
                return false;
            }

            if(!hasFunds(token.getSenderAddress(), token.getAmount().toBigInteger()))
            {
                Logger.err("sender doesn't have the funds for this transaction!");
                return false;
            }
        }

//        this.hashSet.add(token.getHashAsString());

        return true;
    }

    public synchronized void add(Token token)
    {
//        if (isFull()) return Integer.MIN_VALUE;
//        int nonce = mine(token);

//        if(!verifyToken(token)) return;

        tokenList.add(token);
        tokens.append(token.toJSON());

        txio.add(token);
//        tokens.append("\"token" + index + "\":" + token.toJSON() + ",");

        lastHash = token.getHashAsString(lastHash);
        hashList.add(lastHash);

        block_size += token.size();
//        linkedHashes.append(lastHash);

//        index++;
    }

    public synchronized void mineBlock()
    {
        if (isFull())
        {
//            addReward();
            //pack();
//            generateHash();
//            mineMultiThreaded();
            mine();
        }
    }

    private synchronized void reArrange()
    {
        lastHash = getParentHash();
        Collections.shuffle(tokenList, new Random(shuffle = System.currentTimeMillis()));

        tokens = new StringBuilder();
        for(Token token : tokenList) addWithoutVerification(token);
    }

    private synchronized void addWithoutVerification(Token token)
    {
//        tokens.append("\"token" + index + "\":" + token.toJSON() + ",");
        lastHash = token.getHashAsString(lastHash);
        this.tokenList.add(token);
        this.block_size += token.size();
        this.txio.add(token);
    }

    private synchronized Tuple<ExecutorService, ExecutorCompletionService<Tuple<Long, String>>> createMiningThreads(RewardToken token)
    {
        ExecutorService service = Executors.newFixedThreadPool(Config.getConfig().MAX_MINING_THREADS);

        ExecutorCompletionService<Tuple<Long, String>> completionService = new ExecutorCompletionService<>(service);

        final long MAX = 25600;

//        System.out.println(Long.MAX_VALUE);

        for (int i = 0; i < MAX; i++)
        {
            final long max = (((long)Integer.MAX_VALUE * 2L) / MAX);//4_294_967_295L) / MAX;

            completionService.submit(new MineTask(lastHash, i * max - 1, i * max + max + 1, token));
        }

        return new Tuple<>(service, completionService);
    }

    private synchronized byte[] wrapFinalToken(Token token, String lastHash)
    {
        String t = token.toJSON();
        ByteBuffer bytes = ByteBuffer.allocate(t.length() + lastHash.length());

        bytes.put(t.getBytes());
        bytes.put(lastHash.getBytes());

        return bytes.array();
    }

    private synchronized long mine()
    {
        Logger.alert("started mining block[" + blockID + "]");
        long now =  System.currentTimeMillis();
        RewardToken rewardToken = new RewardToken(RVCCore.get().getWallet().getPublicKey().getPublicWalletAddress(),
                new RiverCoin(Config.getConfig().REWARD_PER_BLOCK_MINED));//,
//        solvedAt = System.currentTimeMillis();//);

        addWithoutVerification(rewardToken);

        block_size += rewardToken.size();

        this.mine = true;
        Tuple<String, Long> result = ConsensusAlgorithm.applyPoW(wrapFinalToken(rewardToken, lastHash), getParentHash());

        double seconds = (double)(System.currentTimeMillis() - now) / 1000.0;

        this.nonce = result.getJ();
        this.hash  = result.getI();
        this.mine  = false;
        pack();

        Logger.alert("block[" + getID() + "] mined and hash '" + getHash() + "' generated in: " + seconds + " seconds.");

//        Tuple<ExecutorService, ExecutorCompletionService<Tuple<Long, String>>> mineService = createMiningThreads(rewardToken);

//        this.mineService = mineService.getI();
//        this.mine        = true;
//
//        try
//        {
//            while (!mineService.getI().isTerminated() && mine)
//            {
//                final Future<Tuple<Long, String>> result = mineService.getJ().take();
//
//                if (result.get() != null)
//                {
//                    mineService.getI().shutdownNow();
//
//                    this.nonce  = result.get().getI();
//                    this.hash   = result.get().getJ();
//                    this.mine   = false;
//
//                    txio.add(rewardToken);
//                    tokenList.add(rewardToken);
//
//                    double seconds = (double)(System.currentTimeMillis() - now) / 1000.0;
//
//                    Logger.alert("block[" + getID() + "] mined and hash '" + getHash() + "' generated in: " + seconds + " seconds.");
//                    pack();
//
//                    return nonce;
//                }
//            }
//
////            reArrange();
//            mineService.getI().shutdownNow();
//            this.mineService = null;
//            this.mine = true;
//            Logger.err("block[" + getID() + "] couldn't be mined '" + getHash() + "' generated!");
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }

        return nonce;
    }

    public synchronized boolean isFull()
    {
        return block_size >= MAX_BYTES;
    }

    private synchronized void pack()
    {
        byte tokenBytes[]       = null;
        byte transactions[]     = null;//
        int  sizeOfTransactions = 0;
        int  sizeOfTokenBytes   = 0;
        byte myHash[]           = HashUtil.blockHashExport(getHash()).getBytes();
        ByteArrayOutputStream byteStream = null;

        DataOutputStream stream = new DataOutputStream(new DeflaterOutputStream(byteStream = new ByteArrayOutputStream()));

        try{
            merkleTree = new MerkleTree();
            merkleTree.loadFromHeader(hashList);
            merkleTree.serialize(stream);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        byte bytes[] = byteStream.toByteArray();

        buffer = ByteBuffer.allocate(26 + myHash.length + bytes.length);
        buffer.putLong(nonce);
        buffer.putLong(blockID);
        buffer.putLong(block_size);
        buffer.putShort((short) myHash.length);
        buffer.put(myHash);

        buffer.put(bytes);
        buffer.flip();
    }

    public synchronized void generateHash(byte data[])
    {
        hash = HashUtil.hashToStringBase16(HashUtil.applySha256(data));
    }

    public synchronized void buildTree()
    {
    }

    public synchronized String getHash()
    {
        return hash;
    }

    public synchronized void export()
    {
        Logger.alert("BLOCKSIZE: " + tokenList.size());
        try
        {
            File file = new File(Config.getConfig().BLOCKCHAIN_DIRECTORY + "\\block[" + blockID + "]");
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            stream.writeUTF(RVCCore.version);
            stream.write(buffer.array());

            stream.flush();
            stream.close();

            LatestBlockInfo blockInfo = new LatestBlockInfo();
            blockInfo.read();

            blockInfo.write(this.blockID, blockInfo.getLastBlockCheck(), blockInfo.getLastBlockCheckTimestamp(), blockInfo.getDifficulty(), blockInfo.getTotalHashes().add(new BigInteger(nonce + "")));

            Config.calculateDifficulty();

            Wallet.updateBalance(this.txio);
            Logger.alert("block[" + blockID + "}: exported.");
        } catch (Exception e)
        {
            Logger.err("couldn't export block!");
            e.printStackTrace();
        }
    }

    public synchronized long getID()
    {
        return blockID;
    }

    /**
     * @return An empty block for work verification.
     */
    public synchronized Block copy()
    {
        return new Block(this.parent);
    }

    public synchronized BigInteger getBalance(String publicAddress)
    {
        return Wallet.readBalance(publicAddress);
//        return getBalance(publicAddress, BigInteger.ZERO);
    }

//    public synchronized BigInteger getBalance(String publicAddress, BigInteger balance)
//    {
////        System.out.println(this.txio.toString());
//
//        balance = getImmediateBalance(publicAddress, balance);
//
//        Block parent = getParent();
//
//        if (parent != null) balance = parent.getBalance(publicAddress, balance);
//
//        return balance;
//    }
//
    private synchronized BigInteger getImmediateBalance(String publicAddress, BigInteger balance)
    {
//        Logger.err("hello ? " + blockID);

        Tuple<List<Tuple<RiverCoin, String>>, List<Tuple<RiverCoin, String>>> txio = this.txio.get(publicAddress).get();

        System.out.println(this.txio.get(publicAddress).length());

        for (Tuple<RiverCoin, String> tuple : txio.getI())
                balance = balance.add(tuple.getI().toBigInteger());

        for (Tuple<RiverCoin, String> tuple : txio.getJ())
                balance = balance.subtract(tuple.getI().toBigInteger());

        return balance;
    }

    public synchronized boolean hasFunds(String publicAddress, BigInteger amt)
    {
//        BigInteger balance = BigInteger.ZERO;
//        Block toCheck = this;
//
//        while (balance.compareTo(amt) < 0)
//        {
//            balance = toCheck.getImmediateBalance(publicAddress, balance);
//
//            if (toCheck.blockID == 0)
//                break;
//
//            toCheck = toCheck.getParent();
//        }

//        return balance.compareTo(amt) >= 0;

        return getBalance(publicAddress).compareTo(amt) >= 0;
    }

    public synchronized Block getParent()
    {
        if (parent != null) return parent;

        if (blockID == 0L)
            return null;

            //TODO: read from disk.
        else return new Block(new File(Config.getConfig().BLOCKCHAIN_DIRECTORY + "block[" + (blockID - 1L) + "]"), 0);
    }

    public synchronized String getParentHash()
    {
        Block parent = getParent();

        if(parent != null) return parent.getHash();

        return "0";
    }

    public synchronized String linkedHash()
    {
        return lastHash;
    }

    public synchronized Block copyEntire()
    {
        Block block = new Block();
        block.parent = this.parent;

        block.hash  = this.hash;
        block.tokenList.addAll(this.tokenList);
        block.lastHash  = lastHash;
        block.txio.addAll(this.txio);

        return block;
    }

    public synchronized ExecutorService getMiningService()
    {
        return mineService;
    }

    public synchronized void stopMining()
    {
        mine = false;
        mineService.shutdownNow();
    }

    public void submitSolution(Solution solution)
    {
//        this.nonce = solution.getNonce();
//        switchRewardToken(solution.getReward());
    }

    private synchronized void switchRewardToken(Token token)
    {
        if(this.tokenList.size() > Config.getConfig().TOKENS_PER_BLOCK_TOMNE)
            this.tokenList.set(this.tokenList.size() - 1, token);
        else if(this.tokenList.size() == Config.getConfig().TOKENS_PER_BLOCK_TOMNE)
            this.tokenList.add(token);
    }

    public synchronized boolean mined()
    {
        return buffer != null;
    }

    public synchronized boolean isMining()
    {
        return mine;
    }

//    public synchronized void mine(long beginIndex, long timeOutIndex, List<String> peerAddresses)
//    {
//        BigDecimal rewardPerPeer = new BigDecimal(Config.getConfig().REWARD_PER_BLOCK_MINED)/**.multiply(new BigDecimal("0.95"))**/.divide(new BigDecimal(peerAddresses.size()));
////        BigDecimal rewardForPeer = new BigDecimal(Config.getConfig().REWARD_PER_BLOCK_MINED).multiply(new BigDecimal("0.05")).subtract(rewardPerPeer);//.divide(new BigDecimal(peerAddresses.size()));
//
//        for(String peer : peerAddresses)
//            addWithoutVerification(new RewardToken(peer, new RiverCoin(rewardPerPeer.toPlainString())));
//
////        Logger.alert("started mining block[" + blockID + "]");
////        RewardToken token = new RewardToken(RVCCore.get().getWallet().getPublicKey().getPublicAddress(),
////                RiverCoin.fastRiverCoinValueToNanoCoinConversion(Config.getConfig().REWARD_PER_BLOCK_MINED + ""),
////                solvedAt = System.currentTimeMillis());
//
//        RewardToken token = (RewardToken) tokenList.get(tokenList.size() - 1);
//
//        Tuple<ExecutorService, ExecutorCompletionService<Tuple<Long, String>>> mineService = createMiningThreads(token, beginIndex, timeOutIndex);
//
//        this.mineService = mineService.getI();
//        this.mine        = true;
//
//        try
//        {
//            while (!mineService.getI().isTerminated() && mine)
//            {
//                final Future<Tuple<Long, String>> result = mineService.getJ().take();
//
//                if (result.get() != null)
//                {
//                    mineService.getI().shutdownNow();
//
//                    this.nonce  = result.get().getI();
//                    this.hash   = result.get().getJ();
//                    this.mine   = false;
//
//                    tokens.append(token.toJSON());
//                    txio.add(token);
//
//                    Logger.alert("block[" + getID() + "] mined and hash '" + getHash() + "' generated!");
//                    Logger.alert("current balance: " + Wallet.readBalance(RVCCore.get().getWallet().getPublicKey().getPublicAddress()));
//                    pack();
//                    return;
//                }
//            }
//
////            reArrange();
//            mineService.getI().shutdownNow();
//            this.mineService = null;
//            this.mine = true;
//            Logger.err("block[" + getID() + "] couldn't be mined!");
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return;
//    }
//
//    private synchronized Tuple<ExecutorService, ExecutorCompletionService<Tuple<Long, String>>> createMiningThreads(RewardToken token, long beginIndex, long timeOutIndex)
//    {
//        ExecutorService service = Executors.newFixedThreadPool(Config.getConfig().MAX_MINING_THREADS);
//
//        ExecutorCompletionService<Tuple<Long, String>> completionService = new ExecutorCompletionService<>(service);
//
//        final long MAX = 25600;
//
//        for (int i = 0; i < MAX; i++)
//        {
//            final long max = (((long)(timeOutIndex)) / MAX);//4_294_967_295L) / MAX;
//
//            completionService.submit(new MineTask(lastHash, beginIndex + i * max - 1, beginIndex + i * max + max + 1, token));
//        }
//
//        return new Tuple<>(service, completionService);
//    }
}