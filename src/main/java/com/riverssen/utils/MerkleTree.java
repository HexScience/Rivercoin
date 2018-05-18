package com.riverssen.utils;

import com.riverssen.core.chain.Serialisable;
import com.riverssen.core.tokens.Token;
import com.riverssen.core.consensus.ConsensusAlgorithm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class MerkleTree implements Serialisable, Encodeable
{
    /** the root element of the merkle tree **/
    private TreeElement root;
    /** a flat representation of the merkle tree in a list of tokens **/
    private List<Token> list;
    /** a flat representation of the merkle tree in a list of hash strings **/
    private List<String> hashList;
    /** the size of the tree **/
    private int size;

    public MerkleTree(List<Token> tokenList)
    {
        load(tokenList);
    }

    @Override
    public void serialize(DataOutputStream stream) throws Exception
    {
        stream.writeShort(this.size);

        for (Token token : list)
            token.write(stream);
    }

    @Override
    public void deserialize(DataInputStream stream, String version) throws Exception
    {
        int i = stream.readInt();

        for (int j = 0; j < i; j++)
            list.add(Token.read(stream, version));

        load(list);
    }

    /** return a flat representation of the tree elements in a list of tokens **/
    public List<Token> flatten()
    {
        return list;
    }

    /** return a flat representation of the tree elements in a list of hashes **/
    public List<String> flattenHashes()
    {
        return this.hashList;
    }

    public String toJSON()
    {
        String json = "{";

        for(String hash : hashList) json += hash + ",";

        json = json.substring(0, json.length() - 2) + "}";

        return json;
    }

    private void load(List<Token> list)
    {
        this.list = Collections.synchronizedList(new ArrayList<>());
        this.list.addAll(list);

        PriorityQueue<TreeElement> elements = new PriorityQueue<>();

        for (Token token : list) elements.add(new TreeElement(token));

        int i = 0;

        while (elements.size() > 1)
            elements.add(new TreeElement(elements.poll(), elements.poll(), i++));

        this.root = elements.poll();
        this.size = list.size();
    }

    public MerkleTree()
    {
        list = new ArrayList<>();
        hashList = new ArrayList<>();
    }

    public void loadFromHeader(List<String> list)
    {
        this.list = Collections.synchronizedList(new ArrayList<>());
        this.hashList = Collections.synchronizedList(new ArrayList<>());
        this.hashList.addAll(list);

        PriorityQueue<TreeElement> elements = new PriorityQueue<>();

        for (String token : list) elements.add(new TreeElement(token));

        int i = 0;

        while (elements.size() > 1)
            elements.add(new TreeElement(elements.poll(), elements.poll(), i++));

        this.root = elements.poll();
        this.size = list.size();
    }

    public void buildTree()
    {
        PriorityQueue<TreeElement> elements = new PriorityQueue<>();

        for (Token token : list) elements.add(new TreeElement(token));

        int i = 0;

        while (elements.size() > 1)
            elements.add(new TreeElement(elements.poll(), elements.poll(), i++));

        this.root = elements.poll();
        this.size = list.size();
    }

    public String hash()
    {
        return root.hash();
    }

    public String consensusHash(long nonce, String parentBlockHash)
    {
        return ConsensusAlgorithm.applyPoW(nonce, parentBlockHash, this);
    }

    @Override
    public byte[] encode(HashAlgorithm algorithm)
    {
        return root.encode(algorithm);
    }

    @Override
    public byte[] getBytes()
    {
        return new byte[0];
    }

    public void add(Token token)
    {
        list.add(token);
    }

    private class TreeElement implements Comparable<TreeElement>, Serialisable, Encodeable
    {
        private Token token;
        private String hash;
        private TreeElement left;
        private TreeElement right;
        private int priority;

        TreeElement(Token token)
        {
            this.token = token;
            this.hash  = token.getHashAsString();
        }

        public TreeElement(TreeElement left, TreeElement right, int i)
        {
            this.left = left;
            this.right = right;
            this.priority = i;
        }

        public TreeElement(String token)
        {
            this.hash = token;
        }

        public boolean isLeaf()
        {
            return this.left == null && this.right == null;
        }

        @Override
        public int compareTo(TreeElement o)
        {
            return o.isLeaf() ? 1 : 0;
        }

        public String hash()
        {
            if (isLeaf())
                return hash;

            return HashUtil.hashToStringBase16(HashUtil.applySha256((left.hash() + right.hash()).getBytes()));
        }

        public void add(List<TreeElement> list)
        {
            if (isLeaf())
                list.add(this);

            else
            {
                left.add(list);
                right.add(list);
            }
        }

        /** this shouldn't be used, instead a flat represenation of the tree should suffice **/
        @Override
        public void serialize(DataOutputStream stream) throws Exception
        {
        }

        @Override
        public void deserialize(DataInputStream stream, String version) throws Exception
        {
        }

        @Override
        public byte[] encode(HashAlgorithm algorithm)
        {
            if(isLeaf()) return token.encode(algorithm);
            return algorithm.encode(ByteUtil.concatenate(left.encode(algorithm), right.encode(algorithm)));
        }

        @Override
        public byte[] getBytes()
        {
            if(isLeaf())
                return token.getBytes();

            return ByteUtil.concatenate(left.getBytes(), right.getBytes());
        }
    }
}