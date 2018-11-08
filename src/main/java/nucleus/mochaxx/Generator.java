package nucleus.mochaxx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generator
{
    private static String precCheck = "\\{precedence\\}=[\\w]+&";
    private static Map<String, Integer> PrecedenceMap = new HashMap<>();

    private static class Opcode{
        String      simpleName;
        String      simpleDescription;
        String      notSimpleDescription;
        String      switchStatementCode;
        boolean     simple;
        boolean     isMath;
        boolean     isBitwise;
        boolean     signed;
        boolean     constraned;

        Opcode(String name, String description, String code, boolean isSimple)
        {
            simpleName = name;
            simpleDescription = description;
            notSimpleDescription = description;
            switchStatementCode = code;
            this.simple = isSimple;
            this.constraned = true;
        }

        Opcode isMath(boolean isMath)
        {
            this.isMath = isMath;
            return this;
        }

        boolean isMath()
        {
            return isMath;
        }

        public Opcode isBitWise(boolean isBitwise)
        {
            this.isBitwise = isBitwise;
            return this;
        }

        public boolean isBitwise()
        {
            return isBitwise;
        }

        public Opcode signed(boolean signed)
        {
            this.signed = signed;
            return this;
        }

        public boolean signed()
        {
            return signed;
        }

        public Opcode setConstrained(boolean constrained)
        {
            this.constraned = constrained;
            return this;
        }

        public boolean isConstraned()
        {
            return constraned;
        }
    }
    private static class TypeInformation{
        String actualType;
        String simpleName;
        String prefixName;
        boolean bitwise = true;
        boolean math = true;
        boolean hasSignness = true;
        boolean simpleOps;

        TypeInformation(String a, String b, String c, boolean bitwise, boolean math, boolean hasSignness, boolean simplifiedOps)
        {
            actualType = a;
            simpleName = b;
            prefixName = c;
            this.bitwise = bitwise;
            this.math    = math;
            this.hasSignness = hasSignness;
            this.simpleOps = simplifiedOps;
        }
    }
    public static void generateVM()
    {
        Set<TypeInformation>    tpnames = new LinkedHashSet<>();
        Set<Opcode>             opcodes = new LinkedHashSet<>();

        tpnames.add(new TypeInformation("int_8"     , "Byte", "b", true, true, true, true));
        tpnames.add(new TypeInformation("int_16"    , "Short", "s", true, true, true, true));
        tpnames.add(new TypeInformation("int_32"    , "Int", "i", true, true, true, true));
        tpnames.add(new TypeInformation("int_64"    , "Long", "l", true, true, true, true));
        tpnames.add(new TypeInformation("int_128"   , "LongInt", "li", true, true, true, true));
        tpnames.add(new TypeInformation("int_256"   , "LongLong", "ll", true, true, true, true));

        tpnames.add(new TypeInformation("flt_32"    , "Float", "f", false, true, false, false));
        tpnames.add(new TypeInformation("flt_64"    , "Double", "d", false, true, false, false));
        tpnames.add(new TypeInformation("flt_128"   , "DoubleFloat", "df", false, true, false, false));
        tpnames.add(new TypeInformation("flt_256"   , "DoubleDouble", "dd", false, true, false, false));
        tpnames.add(new TypeInformation("pointer"   , "Pointer", "a", false, true, false, false));


        PrecedenceMap.put("int_8", 0);
        PrecedenceMap.put("int_16", 1);
        PrecedenceMap.put("int_32", 2);
        PrecedenceMap.put("int_64", 3);
        PrecedenceMap.put("int_128", 4);
        PrecedenceMap.put("int_256", 5);
        PrecedenceMap.put("flt_32", 2);
        PrecedenceMap.put("flt_64", 3);
        PrecedenceMap.put("flt_128", 4);
        PrecedenceMap.put("flt_256", 5);
        PrecedenceMap.put("pointer", 3);

        PrecedenceMap.put("uint_8", 0);
        PrecedenceMap.put("uint_16", 1);
        PrecedenceMap.put("uint_32", 2);
        PrecedenceMap.put("uint_64", 3);
        PrecedenceMap.put("uint_128", 4);
        PrecedenceMap.put("uint_256", 5);
        PrecedenceMap.put("uflt_32", 2);
        PrecedenceMap.put("uflt_64", 3);
        PrecedenceMap.put("uflt_128", 4);
        PrecedenceMap.put("uflt_256", 5);
        PrecedenceMap.put("upointer", 3);

        Map<String, String>
                operators = new LinkedHashMap<>();
        operators.put("+", "%t");
        operators.put("-", "%t");
        operators.put("*", "%t");
        operators.put("/", "%t");
        operators.put("%", "%t");
        operators.put(">>", "%t");
        operators.put("<<", "%t");
        operators.put("<", "%t");
        operators.put(">", "%t");
        operators.put("<=", "%t");
        operators.put(">=", "%t");
        operators.put("&", "%t");
        operators.put("|", "%t");
        operators.put("^", "%t");
        operators.put("=", "%t");


        opcodes.add(new Opcode("mark", "mark a specific location.",   "CHECK_POINTS[ops.getUnsignedShort()] = ops.address;", false).setConstrained(false));
        opcodes.add(new Opcode("jump", "jump to a specific location.",   "ops.address = ops.getUnsignedLong();", false).setConstrained(false));
        opcodes.add(new Opcode("jumptomark", "jump to a specific marked location.",   "ops.address = CHECK_POINTS[ops.getUnsignedShort()];", false).setConstrained(false));
        opcodes.add(new Opcode("newline", "print a \\n character into the console.",   "std::cout<<std::endl;", false).setConstrained(false));

        opcodes.add(new Opcode("invokestatic", "invoke a static method.",   "execute(globalTable, globalPointer, globalPointer, stack, globalTable[ops.getUnsignedLong()]);", false).setConstrained(false));
        opcodes.add(new Opcode("invokedynamic", "dynamically invoke a method using function pointers.",   "execute(globalTable, globalPointer, globalPointer, stack, globalTable[stack.popUnsignedLong()]);", false).setConstrained(false));
        opcodes.add(new Opcode("invokenative", "invoke a native method using function pointers.",   "nativeTable[stack.popUnsignedLong()]->execute(globalTable, nativeTable, globalPointer, globalPointer, stack, globalTable[stack.popUnsignedLong()]);", false).setConstrained(false));
        opcodes.add(new Opcode("if_t", "if true.",   "{ uint_32 jump = ops.getUnsignedInt(); if (stack.popByte() == 0) ops.address += ops.getUnsignedInt(); }", false).setConstrained(false));
        opcodes.add(new Opcode("if_f", "if not true.",   "{ uint_32 jump = ops.getUnsignedInt(); if (stack.popByte() > 0) ops.address += ops.getUnsignedInt(); }", false).setConstrained(false));

        opcodes.add(new Opcode("amemcpy", "copy memory to destination from source.",   "memcpy(stack.popPointer(), stack.popPointer(), stack.popUnsignedLong());", false).setConstrained(false));
        opcodes.add(new Opcode("bspmemcpy", "copy memory to destination from base pointer.",   "memcpy(stack.popPointer(), base, stack.popUnsignedLong());", false).setConstrained(false));
        opcodes.add(new Opcode("amemcpybsp", "copy memory to pointer from source.",   "memcpy(base, stack.popPointer(), stack.popUnsignedLong());", false).setConstrained(false));
        opcodes.add(new Opcode("amalloc", "push a const pointer to the stack.",   "stack.pushPointer(static_cast<pointer> (malloc(stack.popUnsignedLong())));", false).setConstrained(false));
        opcodes.add(new Opcode("acalloc", "push a const pointer to the stack.",   "stack.pushPointer(static_cast<pointer> (calloc(stack.popUnsignedLong(), stack.popUnsignedLong())));", false).setConstrained(false));
        opcodes.add(new Opcode("amallocs", "push a const pointer to the stack from size_int16_t.",   "stack.pushPointer(static_cast<pointer> (malloc(stack.popUnsignedShort())));", false).setConstrained(false));
        opcodes.add(new Opcode("acallocs", "push a const pointer to the stack from size_int16_t.",   "stack.pushPointer(static_cast<pointer> (calloc(stack.popUnsignedShort(), stack.popUnsignedShort())));", false).setConstrained(false));

        opcodes.add(new Opcode("abload", "load an int_8 from a pointer.",   "stack.pushByte(accessMemoryAndGetByte(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("asload", "load an int_16 from a pointer.",   "stack.pushShort(accessMemoryAndGetShort(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("aiload", "load an int_32 from a pointer.",   "stack.pushInt(accessMemoryAndGetInt(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("alload", "load an int_64 from a pointer.",   "stack.pushLong(accessMemoryAndGetLong(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("aliload", "load an int_128 from a pointer.",   "stack.pushLongInt(accessMemoryAndGetLongInt(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("allload", "load an int_256 from a pointer.",   "stack.pushLongLong(accessMemoryAndGetLongLong(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("afload", "load an flt_32 from a pointer.",   "stack.pushFloat(accessMemoryAndGetFloat(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("adload", "load an flt_64 from a pointer.",   "stack.pushDouble(accessMemoryAndGetDouble(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("adfload", "load an flt_128 from a pointer.",   "stack.pushDoubleFloat(accessMemoryAndGetDoubleFloat(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("addload", "load an flt_256 from a pointer.",   "stack.pushDoubleDouble(accessMemoryAndGetDoubleDouble(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("aaload", "load a pointer from a pointer.",   "stack.pushPointer(accessMemoryAndGetPointer(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));

        opcodes.add(new Opcode("abstore", "store an int_8 in a pointer.",   "(accessMemoryAndSetByte(stack.popPointer(), stack.popUnsignedLong(), stack.popByte()));", false).setConstrained(false));
        opcodes.add(new Opcode("asstore", "store an int_16 in a pointer.",   "(accessMemoryAndSetShort(stack.popPointer(), stack.popUnsignedLong(), stack.popShort()));", false).setConstrained(false));
        opcodes.add(new Opcode("aistore", "store an int_32 in a pointer.",   "(accessMemoryAndSetInt(stack.popPointer(), stack.popUnsignedLong(), stack.popInt()));", false).setConstrained(false));
        opcodes.add(new Opcode("alstore", "store an int_64 in a pointer.",   "(accessMemoryAndSetLong(stack.popPointer(), stack.popUnsignedLong(), stack.popLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("alistore", "store an int_128 in a pointer.",   "(accessMemoryAndSetLongInt(stack.popPointer(), stack.popUnsignedLong(), stack.popLongInt()));", false).setConstrained(false));
        opcodes.add(new Opcode("allstore", "store an int_256 in a pointer.",   "(accessMemoryAndSetLongLong(stack.popPointer(), stack.popUnsignedLong(), stack.popLongLong()));", false).setConstrained(false));
        opcodes.add(new Opcode("afstore", "store an flt_32 in a pointer.",   "(accessMemoryAndSetFloat(stack.popPointer(), stack.popUnsignedLong(), stack.popFloat()));", false).setConstrained(false));
        opcodes.add(new Opcode("adstore", "store an flt_64 in a pointer.",   "(accessMemoryAndSetDouble(stack.popPointer(), stack.popUnsignedLong(), stack.popDouble()));", false).setConstrained(false));
        opcodes.add(new Opcode("adfstore", "store an flt_128 in a pointer.",   "(accessMemoryAndSetDoubleFloat(stack.popPointer(), stack.popUnsignedLong(), stack.popDoubleFloat()));", false).setConstrained(false));
        opcodes.add(new Opcode("addstore", "store an flt_256 in a pointer.",   "(accessMemoryAndSetDoubleDouble(stack.popPointer(), stack.popUnsignedLong(), stack.popDoubleDouble()));", false).setConstrained(false));
        opcodes.add(new Opcode("aastore", "store a pointer in a pointer.",   "(accessMemoryAndSetPointer(stack.popPointer(), stack.popUnsignedLong(), stack.popPointer()));", false).setConstrained(false));



        opcodes.add(new Opcode("baconst", "push a byte array.",   "{ uint_64 length = stack.popUnsignedLong(); pointer p = static_cast<pointer> (calloc(length, length)); stack.pushPointer(p); accessMemoryAndSetUnsignedLong(p, 0, length); }", false).setConstrained(false));
        opcodes.add(new Opcode("baconsts", "push a byte array using size_int16_t",   "{ uint_64 length = static_cast<uint_64> (stack.popUnsignedShort()); pointer p = static_cast<pointer> (calloc(length, length)); stack.pushPointer(p); accessMemoryAndSetUnsignedLong(p, 0, length); }", false).setConstrained(false));
        opcodes.add(new Opcode("baconsti", "push a byte array using size_int32_t",   "{ uint_64 length = static_cast<uint_64> (stack.popUnsignedInt()); pointer p = static_cast<pointer> (calloc(length, length)); stack.pushPointer(p); accessMemoryAndSetUnsignedLong(p, 0, length); }", false).setConstrained(false));
        opcodes.add(new Opcode("bacast_a", "cast a byte array into a pointer",   "{ stack.pushPointer(stack.popPointer() + 8); }", false).setConstrained(false));
        opcodes.add(new Opcode("basizeof", "push the size of a byte array to stack",   "{ stack.pushUnsignedLong(static_cast<uint_64> (accessMemoryAndGetLong(stack.popPointer(), 0))); }", false).setConstrained(false));

        opcodes.add(new Opcode("printba", "print a byte array.", "{ pointer bytearray = stack.popPointer(); uint_64 bytearraysize   = static_cast<uint_64> (accessMemoryAndGetLong(bytearray, 0)); std::string data = \"[\"; for (uint_64 i = 0; i < bytearraysize; i ++) data += std::to_string(accessMemoryAndGetByte(bytearray + 8, i)) + ((i < bytearraysize - 1) ? \", \" : \"]\"); log(data); }", false));

        opcodes.add(new Opcode("const", "push a const %t into the stack.", "stack.push%T(ops.get%T());", false));
        opcodes.add(new Opcode("const_0", "push a const %t into the stack (value = 0).", "stack.push%T(0);", false));
        opcodes.add(new Opcode("const_1", "push a const %t into the stack (value = 1).", "stack.push%T(1);", true));
        opcodes.add(new Opcode("const_2", "push a const %t into the stack (value = 2).", "stack.push%T(2);", true));
        opcodes.add(new Opcode("const_3", "push a const %t into the stack (value = 3).", "stack.push%T(3);", true));
        opcodes.add(new Opcode("const_4", "push a const %t into the stack (value = 4).", "stack.push%T(4);", true));
        opcodes.add(new Opcode("const_5", "push a const %t into the stack (value = 5).", "stack.push%T(5);", true));
        opcodes.add(new Opcode("const_6", "push a const %t into the stack (value = 6).", "stack.push%T(6);", true));
        opcodes.add(new Opcode("const_9", "push a const %t into the stack (value = 9).", "stack.push%T(9);", true));
        opcodes.add(new Opcode("const_10", "push a const %t into the stack (value = 10).", "stack.push%T(10);", true));
        opcodes.add(new Opcode("const_11", "push a const %t into the stack (value = 11).", "stack.push%T(11);", true));
        opcodes.add(new Opcode("const_12", "push a const %t into the stack (value = 12).", "stack.push%T(12);", true));

        opcodes.add(new Opcode("load", "load a %t into the stack from local variable.", "stack.push%T(lvt[ops.getUnsignedShort()].%T);", false));
        opcodes.add(new Opcode("load_0", "load a %t into the stack from local variable 0.", "stack.push%T(lvt[0].%T);", false));
        opcodes.add(new Opcode("load_1", "load a %t into the stack from local variable 1.", "stack.push%T(lvt[1].%T);", false));
        opcodes.add(new Opcode("load_2", "load a %t into the stack from local variable 2.", "stack.push%T(lvt[2].%T);", false));
        opcodes.add(new Opcode("load_3", "load a %t into the stack from local variable 3.", "stack.push%T(lvt[3].%T);", false));
        opcodes.add(new Opcode("load_4", "load a %t into the stack from local variable 4.", "stack.push%T(lvt[4].%T);", false));

        opcodes.add(new Opcode("store", "store a %t from stack into local variable.",  "lvt[ops.getUnsignedShort()] = lve_%T(stack.pop%T());", false));
        opcodes.add(new Opcode("store", "store a %t from stack into local variable.",  "lvt[ops.getUnsignedShort()] = lve_%T(stack.pop%T());", false));
        opcodes.add(new Opcode("store_0", "store a %t from stack into local variable 0.",  "lvt[0] = lve_%T(stack.pop%T());", false));
        opcodes.add(new Opcode("store_1", "store a %t from stack into local variable 1.",  "lvt[1] = lve_%T(stack.pop%T());", false));
        opcodes.add(new Opcode("store_2", "store a %t from stack into local variable 2.",  "lvt[2] = lve_%T(stack.pop%T());", false));
        opcodes.add(new Opcode("store_3", "store a %t from stack into local variable 3.",  "lvt[3] = lve_%T(stack.pop%T());", false));
        opcodes.add(new Opcode("store_4", "store a %t from stack into local variable 4.",  "lvt[4] = lve_%T(stack.pop%T());", false));

        String prec = "{precedence}=b&";

        for (TypeInformation prefix : tpnames)
        {
            if (prefix.actualType.equals("pointer"))
            {
//                opcodes.add(new Opcode("add_" + prefix.prefixName, "add %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a + b);\n\t}", false).isMath(true).signed(true));
//                opcodes.add(new Opcode("sub_" + prefix.prefixName, "subtract %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a - b);\n\t}", false).isMath(true).signed(true));
            }
            else if (prefix.math)
            {
                opcodes.add(new Opcode("cast_" + prefix.prefixName, "cast %t to type " + prefix.simpleName + ".", "stack.push" + prefix.simpleName + "(static_cast<" + prefix.actualType + "> (stack.pop%T()));", false).signed(true));

                opcodes.add(new Opcode("add_" + prefix.prefixName, "add %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a + b);\n\t}", false).isMath(true).signed(true));
                opcodes.add(new Opcode("sub_" + prefix.prefixName, "subtract %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a - b);\n\t}", false).isMath(true).signed(true));
                opcodes.add(new Opcode("mul_" + prefix.prefixName, "multiply %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a * b);\n\t}", false).isMath(true).signed(true));
                opcodes.add(new Opcode("div_" + prefix.prefixName, "divide %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a / b);\n\t}", false).isMath(true).signed(true));


                opcodes.add(new Opcode("cmpl_" + prefix.prefixName, "compare %t less than type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                opcodes.add(new Opcode("cmpg_" + prefix.prefixName, "compare %t greater than type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                opcodes.add(new Opcode("cmpe_" + prefix.prefixName, "compare %t equal with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));

                opcodes.add(new Opcode("cmple_" + prefix.prefixName, "compare %t less than or equal to type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                opcodes.add(new Opcode("cmpge_" + prefix.prefixName, "compare %t greater than or equal to type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                opcodes.add(new Opcode("cmpne_" + prefix.prefixName, "compare %t not equal with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));

            }
        }

        for (TypeInformation prefix : tpnames)
        {
            if (prefix.actualType.equals("pointer"))
            {
            }
            else if (prefix.hasSignness)
            {
                String precName = prec.replace("b", "u" + prefix.actualType);

                if (prefix.bitwise && prefix.math)
                    opcodes.add(new Opcode("mod_" + prefix.prefixName, "modulo %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + precName + "();\n\tstack.push" + precName + "(a % b);\n\t}", false).isMath(true).isBitWise(true));
                opcodes.add(new Opcode("cast_u" + prefix.prefixName, "cast %t to unsigned type " + prefix.simpleName + ".", "stack.pushUnsigned" + prefix.simpleName + "(static_cast<u" + prefix.actualType + "> (stack.pop%T()));", false).signed(true));
//                    opcodes.add(new Opcode("ucast_u" + prefix.prefixName, "cast %t to unsigned type " + prefix.simpleName + ".", "{}", false).signed(true));
//                    opcodes.add(new Opcode("uadd_u" + prefix.prefixName, "add %t with unsigned type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
//                    opcodes.add(new Opcode("usub_u" + prefix.prefixName, "subtract %t with unsigned type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
//                    opcodes.add(new Opcode("umul_u" + prefix.prefixName, "multiply %t with unsigned type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
//                    opcodes.add(new Opcode("udiv_u" + prefix.prefixName, "divide %t with unsigned type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
                if (prefix.math)
                {
//                opcodes.add(new Opcode("ucast_" + prefix.prefixName, "cast %t to type " + prefix.simpleName + ".", "{}", false).signed(true));
//                opcodes.add(new Opcode("uadd_" + prefix.prefixName, "add %t with type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
//                opcodes.add(new Opcode("usub_" + prefix.prefixName, "subtract %t with type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
//                opcodes.add(new Opcode("umul_" + prefix.prefixName, "multiply %t with type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
//                opcodes.add(new Opcode("udiv_" + prefix.prefixName, "divide %t with type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
//

                    opcodes.add(new Opcode("add_u" + prefix.prefixName, "add %t with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"        + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.push"        + precName + "(a + b);\n\t}", false).isMath(true).signed(true));
                    opcodes.add(new Opcode("sub_u" + prefix.prefixName, "subtract %t with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"   + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.push"        + precName + "(a - b);\n\t}", false).isMath(true).signed(true));
                    opcodes.add(new Opcode("mul_u" + prefix.prefixName, "multiply %t with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"   + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.push"        + precName + "(a * b);\n\t}", false).isMath(true).signed(true));
                    opcodes.add(new Opcode("div_u" + prefix.prefixName, "divide %t with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"     + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.push"        + precName + "(a / b);\n\t}", false).isMath(true).signed(true));


                    opcodes.add(new Opcode("cmpl_u" + prefix.prefixName, "compare %t less than unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"      + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                    opcodes.add(new Opcode("cmpg_u" + prefix.prefixName, "compare %t greater than unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"   + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                    opcodes.add(new Opcode("cmpe_u" + prefix.prefixName, "compare %t equal with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"     + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));

                    opcodes.add(new Opcode("cmple_u" + prefix.prefixName, "compare %t less than or equal to unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"     + prefix.actualType + " a = stack.pop" + precName + "();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                    opcodes.add(new Opcode("cmpge_u" + prefix.prefixName, "compare %t greater than or equal to unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"  + prefix.actualType + " a = stack.pop" + precName + "();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                    opcodes.add(new Opcode("cmpne_u" + prefix.prefixName, "compare %t not equal with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"            + prefix.actualType + " a = stack.pop" + precName + "();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
                }
            }
        }

        for (TypeInformation prefix : tpnames)
        {
            if (prefix.bitwise && prefix.math)
            {
//                opcodes.add(new Opcode("mod_" + prefix.prefixName, "modulo %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a % b);\n\t}", false).isMath(true).isBitWise(true));
                opcodes.add(new Opcode("and_" + prefix.prefixName, "bitwise and %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a & b);\n\t}", false).isMath(true).isBitWise(true));
                opcodes.add(new Opcode("or_" + prefix.prefixName, "bitwise or %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a | b);\n\t}", false).isMath(true).isBitWise(true));
                opcodes.add(new Opcode("xor_" + prefix.prefixName, "bitwise xor %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a ^ b);\n\t}", false).isMath(true).isBitWise(true));
                opcodes.add(new Opcode("shftr_" + prefix.prefixName, "shift right %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a >> b);\n\t}", false).isMath(true).isBitWise(true));
                opcodes.add(new Opcode("shftl_" + prefix.prefixName, "shift left xor %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a << b);\n\t}", false).isMath(true).isBitWise(true));
            }
        }

        opcodes.add(new Opcode("dup", "duplicate a %t on the stack.", "stack.push%T(stack.peek%T());", false));
        opcodes.add(new Opcode("dup2", "duplicate a %t 2 times on the stack.", "stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());", false));
        opcodes.add(new Opcode("dup3", "duplicate a %t 3 times on the stack.", "stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());", false));
        opcodes.add(new Opcode("dup4", "duplicate a %t 4 times on the stack.", "stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());", false));
        opcodes.add(new Opcode("dup5", "duplicate a %t 5 times on the stack.", "stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());", false));

        opcodes.add(new Opcode("set", "set a %t from stack into a field on base pointer.", "accessMemoryAndSet%T(base, ops.getUnsignedInt(), stack.pop%T());", false));
        opcodes.add(new Opcode("get", "get a %t from a field on base pointer to stack.", "stack.push%T(accessMemoryAndGet%T(base, ops.getUnsignedInt()));", false));
        opcodes.add(new Opcode("vset", "set a %t from value into a field on base pointer.",  "accessMemoryAndSet%T(base, ops.getUnsignedInt(), ops.get%T());", false));
        opcodes.add(new Opcode("sget", "get a %t from a field on base pointer to stack using address from stack.", "stack.push%T(accessMemoryAndGet%T(base, stack.popUnsignedInt()));", false));
        opcodes.add(new Opcode("sset", "set a %t from stack into a field on base pointer using address from stack.", "accessMemoryAndSet%T(base, stack.popUnsignedInt(), stack.pop%T());", false));


        opcodes.add(new Opcode("setl", "set a %t from local variable into a field on base pointer.", "accessMemoryAndSet%T(base, ops.getUnsignedInt(), lvt[ops.getUnsignedShort()].%T);", false));
        opcodes.add(new Opcode("inc_1", "increment %t by 1 on stack.",  "lvt[ops.getUnsignedShort()].%T = lvt[ops.getUnsignedShort()].%T + 1;", false));
        opcodes.add(new Opcode("inc_2", "increment %t by 2 on stack.",  "lvt[ops.getUnsignedShort()].%T = lvt[ops.getUnsignedShort()].%T + 2;", false));
        opcodes.add(new Opcode("stinc_1", "increment %t by 1 on stack.",  "stack.push%T(stack.pop%T() + 1);", false));
        opcodes.add(new Opcode("stinc_2", "increment %t by 2 on stack.",  "stack.push%T(stack.pop%T() + 2);", false));

        opcodes.add(new Opcode("return", "return a %t into the main stack.", "stack_main.push%T(stack.pop%T());", false));

        opcodes.add(new Opcode("mainst", "store a %t from main stack in a local variable..",     "lvt[ops.getUnsignedShort()].%T = stack_main.pop%T();", false));
        opcodes.add(new Opcode("mainst_0", "store a %t from main stack in local variable 0.",   "lvt[0].%T = stack_main.pop%T();;", false));
        opcodes.add(new Opcode("mainst_1", "store a %t from main stack in local variable 1.",   "lvt[1].%T = stack_main.pop%T();;", false));
        opcodes.add(new Opcode("mainst_2", "store a %t from main stack in local variable 2.",   "lvt[2].%T = stack_main.pop%T();;", false));
        opcodes.add(new Opcode("mainst_3", "store a %t from main stack in local variable 3.",   "lvt[3].%T = stack_main.pop%T();;", false));

        opcodes.add(new Opcode("print", "print a %t from stack.",   "log(std::to_string(stack_main.pop%T()));", false).signed(true));



        Set<String> switch__ = new LinkedHashSet<>();
        Set<String> opcodes_ = new LinkedHashSet<>();
        Set<String> global   = new LinkedHashSet<>();

//        String checks[] = {"opcode.isMath()", "opcode.signed", "opcode.isBitwise()", "opcode.simple", "else"};
//        String cheks2[] = {"typeInformation.math", "typeInformation.hasSignness", "typeInformation.bitwise", "typeInformation.simpleOps", ""};

//        String checkerCode = "";

//        for (int i = 0; i < checks.length; i ++)
//        {
//            checkerCode += "if (" + checks[i] + ")\n{\n" +
//                    "\tif (" + cheks2[i] + ")\n\t{\n";
//
//            for (int j = 0; j < checks.length; j++)
//            {
//                if (i != j)
//                {
//                    checkerCode += "\t\tif (" + checks[j] + ")\n" +
//                            "\t\t{\n\t\t\tif (" + cheks2[j] + ")\n\t\t\t{\n";
//                    for (int k = 0; k < checks.length; k++)
//                    {
//                        if (k != j && k != i)
//                        {
//                            checkerCode += "\t\t\t\tif (" + checks[k] + ")\n" +
//                                    "\t\t\t\t{\n\t\t\t\t\tif (" + cheks2[k] + ")\n\t\t\t\t\t{\n";
//                            checkerCode += "\t\t\t\t\t}\n";
//                            checkerCode += "\t\t\t\t}\n";
//                        }
//                    }
//
//                    checkerCode += "\t\t\t}\n\t\t}\n";
//                }
//            }
//
//            checkerCode += "\n\t}\n}\n";
//        }

        for (Opcode opcode : opcodes)
        {
            if (!opcode.isConstraned())
            {
                String s = opcode.simpleName;
                int spaces = 20 - s.length();
                for (int x = 0; x < spaces; x++)
                    s += " ";

                opcodes_.add(opcode.simpleName + ",/** " + opcode.simpleDescription + " **/\n");
                switch__.add("${INFORMATION}\ncase ".replace("${INFORMATION}", "/**\n *" + opcode.simpleDescription + "\n */") + opcode.simpleName + ":\n\t" + opcode.switchStatementCode + "\n\tbreak;\n");
            }
        }

        for (TypeInformation typeInformation : tpnames)
        {
            String t = typeInformation.actualType;
            String T = typeInformation.simpleName;

            for (Opcode opcode : opcodes)
            {
                if (!opcode.isConstraned()) continue;

                if (typeInformation.actualType.equals("pointer") && opcode.isMath)
                {
//                    if (opcode.simpleName.startsWith("add") || opcode.simpleName.startsWith("sub"))
//                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                    continue;
                }
                else if (opcode.isMath())
                {
                    if (typeInformation.math)
                    {
                        if (opcode.signed)
                        {
                            if (typeInformation.hasSignness)
                            {
                                if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else if (opcode.isBitwise())
                        {
                            if (typeInformation.bitwise)
                            {
                                if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else if (opcode.simple)
                        {
                            if (typeInformation.simpleOps)
                            {
                                if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                       else
                            getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                    }
                }
                else if (opcode.signed)
                {
                    if (typeInformation.hasSignness)
                    {
                        if (opcode.isMath())
                        {
                            if (typeInformation.math)
                            {
                                if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else if (opcode.isBitwise())
                        {
                            if (typeInformation.bitwise)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else if (opcode.simple)
                        {
                            if (typeInformation.simpleOps)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else
                            getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);

                    }
                }
                else if (opcode.isBitwise())
                {
                    if (typeInformation.bitwise)
                    {
                        if (opcode.isMath())
                        {
                            if (typeInformation.math)
                            {
                                if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else if (opcode.signed)
                        {
                            if (typeInformation.hasSignness)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else if (opcode.simple)
                        {
                            if (typeInformation.simpleOps)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else
                            getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);

                    }
                }
                else if (opcode.simple)
                {
                    if (typeInformation.simpleOps)
                    {
                        if (opcode.isMath())
                        {
                            if (typeInformation.math)
                            {
                                if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else if (opcode.signed)
                        {
                            if (typeInformation.hasSignness)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else if (opcode.isBitwise())
                        {
                            if (typeInformation.bitwise)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
                            }
                        }
                        else
                            getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);

                    }
                }
                else
                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, tpnames);
            }
        }

        try{
            String opcodes__ = "";
            for (String line : opcodes_)
                opcodes__ += line;
            String switch___ = "";
            for (String line : switch__)
                switch___ += line;
            write(new File("./ops.txt"), opcodes__);
            write(new File("./switch.txt"), switch___);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

//        System.out.println(opcodes_.toString());
    }

    private static void write(File file, String string) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(string);
        writer.flush();
        writer.close();
    }

    private static void getOps(TypeInformation typeInformation, Opcode opcode, String t, String T, Set<String> global, Set<String> opcodes_, Set<String> switch__, Set<TypeInformation> typeInfo)
    {
        if (!global.contains(typeInformation.prefixName + opcode.simpleName))
        {
            String o = null;
            String s = o = typeInformation.prefixName + opcode.simpleName;
            int spaces = 20 - s.length();
            for (int x = 0; x < spaces; x++)
                s += " ";

            String code = "";
            boolean codesigned = false;

            Matcher matcher = Pattern.compile(precCheck).matcher(opcode.switchStatementCode);
            if (matcher.find())
            {
                code = matcher.group();
                code = code.substring(13, code.length() - 1);
                if (code.startsWith("u"))
                    codesigned = true;

                code = PrecedenceMap.get(code) > PrecedenceMap.get(typeInformation.actualType) ? code : typeInformation.actualType;
                if (code.startsWith("u"))
                    code = code.substring(1);
//                if (codesigned) code = "u" + code;
            }

            for (TypeInformation tpname : typeInfo)
//                if (("u" + tpname.actualType).equals(code))
//                    code = "Unsigned" + tpname.simpleName;
                if (tpname.actualType.equals(code))
                    code = codesigned ? "Unsigned" + tpname.simpleName : tpname.simpleName;

            opcodes_.add(s + ",/** " + opcode.simpleDescription.replace("%t", t).replace("%T", T) + " **/\n");
            switch__.add("${INFORMATION}\ncase ".replace("${INFORMATION}", "/**\n * " + opcode.notSimpleDescription.replace("%t", t).replace("%T", T) + "\n */") + o + ":\n\t" + opcode.switchStatementCode.replaceAll(precCheck, code).replace("%t", t).replace("%T", T) + "\n\tbreak;\n");
        }
        if (!global.contains(typeInformation.prefixName + "u" + opcode.simpleName) && typeInformation.hasSignness && opcode.signed())
        {
            T = "Unsigned" + T;
            t = "u" + t;

            String o = null;
            String s = o = typeInformation.prefixName + "u" + opcode.simpleName;
            int spaces = 20 - s.length();
            for (int x = 0; x < spaces; x ++)
                s += " ";

            String code = "";
            boolean codesigned = false;

            Matcher matcher = Pattern.compile(precCheck).matcher(opcode.switchStatementCode);
            if (matcher.find())
            {
                code = matcher.group();
                code = code.substring(13, code.length() - 1);
                if (code.startsWith("u"))
                    codesigned = true;

                code = PrecedenceMap.get(code) > PrecedenceMap.get(typeInformation.actualType) ? code : typeInformation.actualType;
                if (code.startsWith("u"))
                    code = code.substring(1);
//                if (codesigned) code = "u" + code;
            }

            boolean contains = false;

            for (TypeInformation tpname : typeInfo)
//                if (("u" + tpname.actualType).equals(code))
//                    code = "Unsigned" + tpname.simpleName;
                if (tpname.actualType.equals(code))
                {
                    contains = true;
                    code = codesigned ? "Unsigned" + tpname.simpleName : tpname.simpleName;
                }

            switch__.add("${INFORMATION}\ncase ".replace("${INFORMATION}", "/**\n * " + opcode.notSimpleDescription.replace("%t", "unsigned " + t).replace("%T", "Unsigned" + T) + "\n */") + o + ":\n\t" + opcode.switchStatementCode.replaceAll(precCheck, code).replace("%t", t).replace("%T", T) + "\n\tbreak;\n");
            opcodes_.add(s + ",/** " + opcode.simpleDescription.replace("%t", "unsigned " + t).replace("%T", "Unsigned" + T) + " **/\n");
        }
    }
}
