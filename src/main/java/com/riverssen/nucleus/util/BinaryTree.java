package com.riverssen.nucleus.util;

import java.math.BigInteger;

public class BinaryTree
{
    private class TreeElement
    {
        private BigInteger  priority;
        private byte[]      element;

        private TreeElement leftNode;
        private TreeElement rightNode;

        public TreeElement(byte[] element, BigInteger priority)
        {
            this.priority = priority;
            this.element = element;
        }

        public TreeElement(TreeElement left, TreeElement right)
        {
            this.leftNode = left;
            this.rightNode = right;
        }

        public boolean isLeaf()
        {
            return leftNode == null && rightNode == null;
        }

        public void insert(byte[] element, BigInteger priority)
        {
            TreeElement node = new TreeElement(element, priority);

            if (leftNode != null && rightNode != null)
            {
                if (leftNode.priority.compareTo(priority) <= 0)
                    leftNode.insert(element, priority);
                else rightNode.insert(element, priority);
            } else {
                if (leftNode == null && rightNode != null)
                {
                    if (rightNode.priority.compareTo(priority) <= 0)
                        rightNode.insert(element, priority);
                    else leftNode = node;
                } else if (rightNode == null && leftNode != null)
                {
                    if (leftNode.priority.compareTo(priority) <= 0)
                        leftNode.insert(element, priority);
                    else rightNode = node;
                } else if (leftNode == null && rightNode == null)
                    leftNode = node;
            }
        }

        public byte[] build()
        {
            if (isLeaf())
                return HashUtil.applyKeccak(element);

            else
            {
                byte left[] = new byte[0];
                byte right[] = new byte[0];

                if (leftNode != null)
                    left = leftNode.build();
                if (rightNode != null)
                    right = rightNode.build();


                return HashUtil.applyKeccak(ByteUtil.concatenate(right, left));
            }
        }
    }

    private TreeElement root;

    public BinaryTree()
    {
    }

    public void insert(byte[] element, BigInteger priority)
    {
        if (root == null)
            root = new TreeElement(element, priority);

        else root.insert(element, priority);
    }

    public void insert(byte[] element)
    {
        if (root == null)
            root = new TreeElement(element, new BigInteger(element).abs());

        else root.insert(element, new BigInteger(element).abs());
    }

    public byte[] build()
    {
        return root.build();
    }

    public void clear()
    {
        root = null;
    }
}
