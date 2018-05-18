package com.riverssen.utils;

/** this interface represents an object that can be converted to a byte array for hashing **/
public interface Encodeable
{
    /** returns a raw byte array containing the hash of the object **/
    default byte[] encode(HashAlgorithm algorithm)
    {
        return algorithm.encode(getBytes());
    }
    /** return a hex representation of the object's hash **/
    default String encode16(HashAlgorithm algorithm)
    {
        return algorithm.encode16(getBytes());
    }
    /** return a hex representation of the object's hash **/

    default String encode32(HashAlgorithm algorithm)
    {
        return algorithm.encode32(getBytes());
    }
    /** return a hex representation of the object's hash **/

    default String encode58(HashAlgorithm algorithm)
    {
        return algorithm.encode58(getBytes());
    }
    /** return a hex representation of the object's hash **/

    default String encode64(HashAlgorithm algorithm)
    {
        return algorithm.encode64(getBytes());
    }
    /** return a byte array to feed into the hash function **/

    byte[] getBytes();
}
