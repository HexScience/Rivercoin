//
// Created by Abdullah Fadhil on 08.09.18.
//

#ifndef RIVERCOIN_CPP_MVE_H
#define RIVERCOIN_CPP_MVE_H

#include <vector>
#include <map>
#include <string>
#include <memory>
#include <stack>

enum Opcodes{
};

enum NativeType{
};

class LVT{
};

union ALU_Unit{
    char int_8;
    unsigned char uint_8;

    short int_16;
    unsigned short uint_16;

    int int_32;
    unsigned int uint_32;

    long int_64;
    unsigned long uint_64;

    float float32;
    double float64;
};

class Stack;

class Register{
    std::stack<ALU_Unit> mUnits;
public:
    void load(ALU_Unit, NativeType);
    bool  greaterThan();
    bool  lessThan();
    bool  greaterThanEq();
    bool  lessThanEq();
    void  add(Stack* stack);
    void  mul(Stack* stack);
    void  sub(Stack* stack);
    void  div(Stack* stack);
    void  mod(Stack* stack);
    void  pow(Stack* stack);
    void  AND(Stack* stack);
    void  OR(Stack* stack);
    void  XOR(Stack* stack);
};

class StackObject{
};

class Heap{
};

//References act like a shared pointer, once their lifecycle is reached, they are deleted.
class Reference{
    unsigned short mReferences;
    unsigned char* mObject;
public:
    Reference(unsigned char*);
    Reference(const Reference& o);
    ~Reference();
};

class Stack{
private:
    std::stack<char> mStack;
public:
    void aconst_null();
    void aconst_new(long size);
    void astore(LVT* t);

    void bconst_e();
    void bconst(char n);
    void bstore(LVT* t);

    void sconst_e();
    void sconst(short n);
    void sstore(LVT* t);

    void iconst_e();
    void iconst(int n);
    void istore(LVT* t);

    void lconst_e();
    void lconst(long n);
    void lstore(LVT* t);

    void fconst_e();
    void fconst(float n);
    void fstore(LVT* t);

    void dconst_e();
    void dconst(float n);
    void dstore(LVT* t);

    void csconst_e();
    void csconst(std::string n);
    void csstore(LVT* t);
};

class Method{
    std::vector<unsigned char> mOpcodes;
public:
    Method(std::vector<unsigned char> o);
    void invoke(Stack&, LVT&);
};

class GlobalSpace{
    std::vector<Method> mMethods;
};

class mve {
    GlobalSpace         mSpace;
    Register            mRegister;
    Stack               mStack;
    Heap                mHeap;
public:
    mve(std::vector<unsigned char> o);
    Method& getMethod(unsigned long address);
    void invokeMethod(unsigned long address);
};


#endif //RIVERCOIN_CPP_MVE_H
