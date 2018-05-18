package com.riverssen.utils;

public interface Hashable
{
    String keccak();
    String gost3411();
    String sha1();
    default String sha2() { return sha256(); }
    String sha3();
    String sha256();
    String sha512();
    String blake2b();
    String ripemd128();
    String ripemd160();
    String ripemd256();


    String fs_sha();
    String fs_ripeMD();
    String X11();
}