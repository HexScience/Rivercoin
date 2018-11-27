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
        String      humanReadableCode;

        Opcode(String name, String description, String code, boolean isSimple)
        {
            simpleName = name;
            simpleDescription = description;
            notSimpleDescription = description;
            switchStatementCode = code;
            this.simple = isSimple;
            this.constraned = true;
            this.humanReadableCode = "";
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

        public Opcode setHumanReadable(String humanReadable)
        {
            this.humanReadableCode = humanReadable;
            return this;
        }

        public String getHumanReadableReaderCode()
        {
            if (humanReadableCode.length() == 0) return "";

            if (humanReadableCode.contains(":"))
            {
                String split[] = humanReadableCode.split(":");
                String collectiveMany = "";

                for (String string : split)
                    collectiveMany += "add(x);\n".replace("x", "" + string + "(in.poll())");

                return collectiveMany;
            }
            else return "add(x);".replace("x", "" + humanReadableCode + "(in.poll())");
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

        tpnames.add(new TypeInformation("int_8"     , "Byte", "b", false, false, true, false));
        tpnames.add(new TypeInformation("int_16"    , "Short", "s", false, false, true, false));
        tpnames.add(new TypeInformation("int_32"    , "Int", "i", true, false, true, true));
        tpnames.add(new TypeInformation("int_64"    , "Long", "l", true, false, true, false));
        tpnames.add(new TypeInformation("int_128"   , "LongInt", "li", true, true, false, false));
        tpnames.add(new TypeInformation("int_256"   , "LongLong", "ll", true, true, true, false));

        tpnames.add(new TypeInformation("flt_32"    , "Float", "f", false, true, false, false));
        tpnames.add(new TypeInformation("flt_64"    , "Double", "d", false, true, false, false));
        tpnames.add(new TypeInformation("flt_128"   , "DoubleFloat", "df", false, false, false, false));
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

        for (TypeInformation first : tpnames)
            for (TypeInformation second : tpnames)
            {
                if (first.equals(second)) continue;

                boolean up = PrecedenceMap.get(first.actualType) < PrecedenceMap.get(second.actualType);

                if (up)
                {
                    if (first.hasSignness)
                        System.out.println("u" + first.prefixName + "2" + second.prefixName + " [convert an Unsigned %T to a %sT]".replace("%T", first.simpleName).replaceAll("%sT", second.simpleName) + " p=0");
                }
                else {
                    System.out.println(first.prefixName + "2" + second.prefixName + " [convert a %T to a %sT]".replace("%T", first.simpleName).replaceAll("%sT", second.simpleName) + " p=0");
                }
            }

        System.exit(0);

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

        opcodes.add(new Opcode("func", "(This opcode won't be interpreted) it creates a marker for an OP_STACK (must end with a return_op of some kind).",   "", false).setConstrained(false));

//        opcodes.add(new Opcode("sha3", "perform a sha3 on pointer data.",    "", false).setConstrained(false));
//        opcodes.add(new Opcode("sha256", "perform a sha256 on pointer data.",    "", false).setConstrained(false));
//        opcodes.add(new Opcode("ripemd160", "perform a ripemd160 on pointer data.",   "", false).setConstrained(false));
        opcodes.add(new Opcode("hash", "perform a hash on pointer data.",   "", false).setConstrained(false));


        opcodes.add(new Opcode("timens", "push a (uint_64) current time in nanoseconds.",   "stack.pushUnsignedLong(static_cast<uint_64> (MvM::gtimens()));", false).setConstrained(false));
        opcodes.add(new Opcode("timems", "push a (uint_64) current time in milliseconds.",   "stack.pushUnsignedLong(static_cast<uint_64> (MvM::gtimems()));", false).setConstrained(false));

        opcodes.add(new Opcode("push", "push a (uint_64) from data.",   "stack.pushUnsignedLong(static_cast<uint_64> (MvM::gtimems()));", false).setConstrained(false));
        opcodes.add(new Opcode("push_0", "push a (uint_64) with value 0.",   "stack.pushUnsignedLong(static_cast<uint_64> (MvM::gtimems()));", false).setConstrained(false));
        opcodes.add(new Opcode("push_1", "push a (uint_64) 1.",   ";", false).setConstrained(false));
        opcodes.add(new Opcode("push_2", "push a (uint_64) 2.",   ";", false).setConstrained(false));
        opcodes.add(new Opcode("push_3", "push a (uint_64) 3.",   ";", false).setConstrained(false));
        opcodes.add(new Opcode("push_4", "push a (uint_64) 4.",   ";", false).setConstrained(false));

        opcodes.add(new Opcode("strconst", "push a constant string.",   "{ uint_16 size_ = stack.popUnsignedShort(); pointer str = static_cast<pointer> (calloc(size_ * 2 + 2, size_ * 2 + 2)); MvM::accessMemoryAndSetUnsignedShort(str, 0, size_); stack.pushPointer(str);}", false).setConstrained(false));
        opcodes.add(new Opcode("strload", "load a string.",   "stack.pushUnsignedLong(lvt[ops.getUnsignedShort()].UnsignedLong);", false).setHumanReadable("UnsignedLong").setHumanReadable("UnsignedShort").setConstrained(false));
        opcodes.add(new Opcode("strstore", "store a string.",   "lvt[ops.getUnsignedShort()] = MvM::lve_Long(stack.popUnsignedLong());", false).setHumanReadable("UnsignedShort").setConstrained(false));
        opcodes.add(new Opcode("strsizeof", "store a string.",   "stack.pushUnsignedLong(static_cast<uint_64> (MvM::accessMemoryAndGetUnsignedShort(stack.popPointer(), 0)));", false).setConstrained(false));
        opcodes.add(new Opcode("strcast_a", "cast a string to a pointer.",   "{ pointer s = stack.popPointer(); uint_16 l = MvM::accessMemoryAndGetUnsignedShort(s, 0); pointer b = static_cast<pointer> (calloc(l * 2, l * 2)); memcpy(b, s + 2, l * 2); stack.pushPointer(b); }", false).setConstrained(false));
        opcodes.add(new Opcode("strcast_ba", "cast a string to a byte array.",   "{ pointer s = stack.popPointer(); uint_16 l = MvM::accessMemoryAndGetUnsignedShort(s, 0); pointer b = static_cast<pointer> (calloc(l * 2 + 6, l * 2 + 6)); memcpy(b + 6, s, l * 2); MvM::accessMemoryAndSetUnsignedLong(b, 0, static_cast<uint_64> (l)); stack.pushPointer(b); }", false).setHumanReadable("UnsignedShort").setConstrained(false));
        opcodes.add(new Opcode("strprint", "print a string.",   "MvM::printString(stack.popPointer());", false).setConstrained(false));
        opcodes.add(new Opcode("strreturn", "return a string from scope.",   "stack_main.pushUnsignedLong(stack.popUnsignedLong()); return;", false).setConstrained(false));

        opcodes.add(new Opcode("mark", "mark a specific location.",   "CHECK_POINTS[ops.getUnsignedShort()] = ops.address + 2;", false).setHumanReadable("UnsignedShort").setConstrained(false));
        opcodes.add(new Opcode("jump", "jump to a specific location.",   "ops.address = ops.getUnsignedLong();", false).setHumanReadable("UnsignedLong").setConstrained(false));
        opcodes.add(new Opcode("jumptomark", "jump to a specific marked location.",   "ops.address = CHECK_POINTS[ops.getUnsignedShort()];", false).setHumanReadable("UnsignedShort").setConstrained(false));
        opcodes.add(new Opcode("newline", "print a \\n character into the console.",   "std::cout<<std::endl;", false).setConstrained(false));
        opcodes.add(new Opcode("ret", "return from this scope.",   "ops.address = ops.length; return;", false).setConstrained(false));

//        opcodes.add(new Opcode("invoke", "invoke a method (object oriented).",   "execute(globalTable, nativeTable, globalPointer, stack.popPointer(), stack, globalTable[ops.getUnsignedLong()]);", false).setHumanReadable("UnsignedLong").setConstrained(false));
        opcodes.add(new Opcode("invoke", "invoke a method.",   "MvM::execute(globalTable, nativeTable, globalPointer, globalPointer, stack, globalTable[ops.getUnsignedLong()]);", false).setHumanReadable("UnsignedLong").setConstrained(false));
        opcodes.add(new Opcode("invokedynamic", "dynamically invoke a method using function pointers.",   "MvM::execute(globalTable, nativeTable, globalPointer, globalPointer, stack, globalTable[stack.popUnsignedLong()]);", false).setConstrained(false));
        opcodes.add(new Opcode("invokespecial", "invoke a native method using function pointers.",   "nativeTable[stack.popUnsignedLong()]->execute(globalTable, nativeTable, globalPointer, globalPointer, stack, globalTable[stack.popUnsignedLong()]);", false).setConstrained(false));
        opcodes.add(new Opcode("if_t", "if true.",   "{ uint_32 jump = ops.getUnsignedInt(); if (stack.popByte() == 0) ops.address += jump; }", false).setHumanReadable("UnsignedInt").setConstrained(false));
        opcodes.add(new Opcode("if_f", "if not true.",   "{ uint_32 jump = ops.getUnsignedInt(); if (stack.popByte() > 0) ops.address += jump; }", false).setHumanReadable("UnsignedInt").setConstrained(false));
        opcodes.add(new Opcode("negate", "negate a boolean.",   "stack.pushByte(!((bool) stack.popByte()));", false).setConstrained(false));

        opcodes.add(new Opcode("amemcpy", "copy memory to destination from source.",   "memcpy(stack.popPointer(), stack.popPointer(), stack.popUnsignedLong());", false).setConstrained(false));
        opcodes.add(new Opcode("bspmemcpy", "copy memory to destination from base pointer.",   "memcpy(stack.popPointer(), base, stack.popUnsignedLong());", false).setConstrained(false));
        opcodes.add(new Opcode("amemcpybsp", "copy memory to pointer from source.",   "memcpy(base, stack.popPointer(), stack.popUnsignedLong());", false).setConstrained(false));
        opcodes.add(new Opcode("amalloc", "push a const pointer to the stack.",   "stack.pushPointer(static_cast<pointer> (malloc(stack.popUnsignedLong())));", false).setConstrained(false));
        opcodes.add(new Opcode("acalloc", "push a const pointer to the stack.",   "stack.pushPointer(static_cast<pointer> (calloc(stack.popUnsignedLong(), stack.popUnsignedLong())));", false).setConstrained(false));
        opcodes.add(new Opcode("amallocs", "push a const pointer to the stack from size_int16_t.",   "stack.pushPointer(static_cast<pointer> (malloc(stack.popUnsignedShort())));", false).setConstrained(false));
        opcodes.add(new Opcode("acallocs", "push a const pointer to the stack from size_int16_t.",   "stack.pushPointer(static_cast<pointer> (calloc(stack.popUnsignedShort(), stack.popUnsignedShort())));", false).setConstrained(false));
        opcodes.add(new Opcode("adelel", "delete a pointer.",   "delete (stack.popPointer());", false).setConstrained(false));

//        opcodes.add(new Opcode("abload", "load an int_8 from a pointer.",   "stack.pushByte(MvM::accessMemoryAndGetByte(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("asload", "load an int_16 from a pointer.",   "stack.pushShort(MvM::accessMemoryAndGetShort(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("aiload", "load an int_32 from a pointer.",   "stack.pushInt(MvM::accessMemoryAndGetInt(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("alload", "load an int_64 from a pointer.",   "stack.pushLong(MvM::accessMemoryAndGetLong(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("aliload", "load an int_128 from a pointer.",   "stack.pushLongInt(MvM::accessMemoryAndGetLongInt(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("allload", "load an int_256 from a pointer.",   "stack.pushLongLong(MvM::accessMemoryAndGetLongLong(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("afload", "load an flt_32 from a pointer.",   "stack.pushFloat(MvM::accessMemoryAndGetFloat(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("adload", "load an flt_64 from a pointer.",   "stack.pushDouble(MvM::accessMemoryAndGetDouble(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("adfload", "load an flt_128 from a pointer.",   "stack.pushDoubleFloat(MvM::accessMemoryAndGetDoubleFloat(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("addload", "load an flt_256 from a pointer.",   "stack.pushDoubleDouble(MvM::accessMemoryAndGetDoubleDouble(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("aaload", "load a pointer from a pointer.",   "stack.pushPointer(MvM::accessMemoryAndGetPointer(stack.popPointer(), stack.popUnsignedLong()));", false).setConstrained(false));
//
//        opcodes.add(new Opcode("abstore", "store an int_8 in a pointer.",   "(MvM::accessMemoryAndSetByte(stack.popPointer(), stack.popUnsignedLong(), stack.popByte()));", false).setConstrained(false));
//        opcodes.add(new Opcode("asstore", "store an int_16 in a pointer.",   "(MvM::accessMemoryAndSetShort(stack.popPointer(), stack.popUnsignedLong(), stack.popShort()));", false).setConstrained(false));
//        opcodes.add(new Opcode("aistore", "store an int_32 in a pointer.",   "(MvM::accessMemoryAndSetInt(stack.popPointer(), stack.popUnsignedLong(), stack.popInt()));", false).setConstrained(false));
//        opcodes.add(new Opcode("alstore", "store an int_64 in a pointer.",   "(MvM::accessMemoryAndSetLong(stack.popPointer(), stack.popUnsignedLong(), stack.popLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("alistore", "store an int_128 in a pointer.",   "(MvM::accessMemoryAndSetLongInt(stack.popPointer(), stack.popUnsignedLong(), stack.popLongInt()));", false).setConstrained(false));
//        opcodes.add(new Opcode("allstore", "store an int_256 in a pointer.",   "(MvM::accessMemoryAndSetLongLong(stack.popPointer(), stack.popUnsignedLong(), stack.popLongLong()));", false).setConstrained(false));
//        opcodes.add(new Opcode("afstore", "store an flt_32 in a pointer.",   "(MvM::accessMemoryAndSetFloat(stack.popPointer(), stack.popUnsignedLong(), stack.popFloat()));", false).setConstrained(false));
//        opcodes.add(new Opcode("adstore", "store an flt_64 in a pointer.",   "(MvM::accessMemoryAndSetDouble(stack.popPointer(), stack.popUnsignedLong(), stack.popDouble()));", false).setConstrained(false));
//        opcodes.add(new Opcode("adfstore", "store an flt_128 in a pointer.",   "(MvM::accessMemoryAndSetDoubleFloat(stack.popPointer(), stack.popUnsignedLong(), stack.popDoubleFloat()));", false).setConstrained(false));
//        opcodes.add(new Opcode("addstore", "store an flt_256 in a pointer.",   "(MvM::accessMemoryAndSetDoubleDouble(stack.popPointer(), stack.popUnsignedLong(), stack.popDoubleDouble()));", false).setConstrained(false));
//        opcodes.add(new Opcode("aastore", "store a pointer in a pointer.",   "(MvM::accessMemoryAndSetPointer(stack.popPointer(), stack.popUnsignedLong(), stack.popPointer()));", false).setConstrained(false));



        opcodes.add(new Opcode("baconst", "push a byte array.",   "{ uint_64 length = stack.popUnsignedLong(); pointer p = static_cast<pointer> (calloc(length, length)); stack.pushPointer(p); MvM::accessMemoryAndSetUnsignedLong(p, 0, length); }", false).setConstrained(false));
        opcodes.add(new Opcode("baconsts", "push a byte array using size_int16_t",   "{ uint_64 length = static_cast<uint_64> (stack.popUnsignedShort()); pointer p = static_cast<pointer> (calloc(length, length)); stack.pushPointer(p); MvM::accessMemoryAndSetUnsignedLong(p, 0, length); }", false).setConstrained(false));
        opcodes.add(new Opcode("baconsti", "push a byte array using size_int32_t",   "{ uint_64 length = static_cast<uint_64> (stack.popUnsignedInt()); pointer p = static_cast<pointer> (calloc(length, length)); stack.pushPointer(p); MvM::accessMemoryAndSetUnsignedLong(p, 0, length); }", false).setConstrained(false));
        opcodes.add(new Opcode("bacast_a", "cast a byte array into a pointer",   "{ stack.pushPointer(stack.popPointer() + 8); }", false).setConstrained(false));
        opcodes.add(new Opcode("basizeof", "push the size of a byte array to stack",   "{ stack.pushUnsignedLong(static_cast<uint_64> (MvM::accessMemoryAndGetLong(stack.popPointer(), 0))); }", false).setConstrained(false));

        opcodes.add(new Opcode("printba", "print a byte array.", "{ pointer bytearray = stack.popPointer(); uint_64 bytearraysize   = static_cast<uint_64> (MvM::accessMemoryAndGetLong(bytearray, 0)); std::string data = \"[\"; for (uint_64 i = 0; i < bytearraysize; i ++) data += std::to_string(MvM::accessMemoryAndGetByte(bytearray + 8, i)) + ((i < bytearraysize - 1) ? \", \" : \"]\"); MvM::log(data); }", false).setConstrained(false));

        opcodes.add(new Opcode("const", "push a const %t into the stack.", "stack.push%T(ops.get%T());", false).setHumanReadable("%T"));
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

        opcodes.add(new Opcode("load", "load a %t into the stack from local variable.", "stack.push%T(lvt[ops.getUnsignedShort()].%T);", false).setHumanReadable("UnsignedShort"));
        opcodes.add(new Opcode("load_0", "load a %t into the stack from local variable 0.", "stack.push%T(lvt[0].%T);", true));
        opcodes.add(new Opcode("load_1", "load a %t into the stack from local variable 1.", "stack.push%T(lvt[1].%T);", true));
        opcodes.add(new Opcode("load_2", "load a %t into the stack from local variable 2.", "stack.push%T(lvt[2].%T);", true));
        opcodes.add(new Opcode("load_3", "load a %t into the stack from local variable 3.", "stack.push%T(lvt[3].%T);", true));
        opcodes.add(new Opcode("load_4", "load a %t into the stack from local variable 4.", "stack.push%T(lvt[4].%T);", true));

        opcodes.add(new Opcode("store", "store a %t from stack into local variable.",  "lvt[ops.getUnsignedShort()] = MvM::lve_%T(stack.pop%T());", false).setHumanReadable("UnsignedShort"));
        opcodes.add(new Opcode("store_0", "store a %t from stack into local variable 0.",  "lvt[0] = MvM::lve_%T(stack.pop%T());", true));
        opcodes.add(new Opcode("store_1", "store a %t from stack into local variable 1.",  "lvt[1] = MvM::lve_%T(stack.pop%T());", true));
        opcodes.add(new Opcode("store_2", "store a %t from stack into local variable 2.",  "lvt[2] = MvM::lve_%T(stack.pop%T());", true));
        opcodes.add(new Opcode("store_3", "store a %t from stack into local variable 3.",  "lvt[3] = MvM::lve_%T(stack.pop%T());", true));
        opcodes.add(new Opcode("store_4", "store a %t from stack into local variable 4.",  "lvt[4] = MvM::lve_%T(stack.pop%T());", true));

        String prec = "{precedence}=b&";


//        opcodes.add(new Opcode("cast", "cast %t to type.", "stack.push%T(static_cast<" + prefix.actualType + "> (stack.pop%T()));", false).signed(true));

        opcodes.add(new Opcode("add", "add two ints/longs.", "stack.pushLong(stack.popLong() + stack.popLong());", false).signed(true).setConstrained(false));
        opcodes.add(new Opcode("sub", "subtract ints/longs.", "stack.pushLong(stack.popLong() - stack.popLong());", false).signed(true).setConstrained(false));
        opcodes.add(new Opcode("mul", "multiply two ints/longs.", "stack.pushLong(stack.popLong() * stack.popLong());", false).signed(true).setConstrained(false));
        opcodes.add(new Opcode("div", "divide two ints/longs.", "stack.pushLong(stack.popLong() / stack.popLong());", false).signed(true).setConstrained(false));

        opcodes.add(new Opcode("addu", "add two ints/longs.", "stack.pushUnsignedLong(stack.popUnsignedLong() + stack.popUnsignedLong());", false).signed(true).setConstrained(false));
        opcodes.add(new Opcode("subu", "subtract ints/longs.", "stack.pushUnsignedLong(stack.popUnsignedLong() - stack.popUnsignedLong());", false).signed(true).setConstrained(false));
        opcodes.add(new Opcode("mulu", "multiply two ints/longs.", "stack.pushUnsignedLong(stack.popUnsignedLong() * stack.popUnsignedLong());", false).signed(true).setConstrained(false));
        opcodes.add(new Opcode("divu", "divide two ints/longs.", "stack.pushUnsignedLong(stack.popUnsignedLong() / stack.popUnsignedLong());", false).signed(true).setConstrained(false));

//        opcodes.add(new Opcode("fadd", "add two floats.", "stack.pushFloat(stack.popFloat() + stack.popFloat());", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fsub", "subtract floats.", "stack.pushFloat(stack.popFloat() - stack.popFloat());", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fmul", "multiply two floats.", "stack.pushFloat(stack.popFloat() * stack.popFloat());", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fdiv", "divide two floats.", "stack.pushFloat(stack.popFloat() / stack.popFloat());", false).isMath(true).signed(true).setConstrained(false));
////
//        opcodes.add(new Opcode("fadd", "add two floats.", "stack.pushFloat(stack.popFloat() + stack.popFloat());", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fsub", "subtract floats.", "stack.pushFloat(stack.popFloat() - stack.popFloat());", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fmul", "multiply two floats.", "stack.pushFloat(stack.popFloat() * stack.popFloat());", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fdiv", "divide two floats.", "stack.pushFloat(stack.popFloat() / stack.popFloat());", false).isMath(true).signed(true).setConstrained(false));
//
        opcodes.add(new Opcode("add", "add two %Ts.", "stack.push%T(stack.pop%T() + stack.pop%T());", false).isMath(true).signed(true));
        opcodes.add(new Opcode("sub", "subtract two %Ts.", "stack.push%T(stack.pop%T() - stack.pop%T());", false).isMath(true).signed(true));
        opcodes.add(new Opcode("mul", "multiply two %Ts.", "stack.push%T(stack.pop%T() * stack.pop%T());", false).isMath(true).signed(true));
        opcodes.add(new Opcode("div", "divide two %Ts.", "stack.push%T(stack.pop%T() / stack.pop%T());", false).isMath(true).signed(true));


//        for (TypeInformation prefix : tpnames)
//        {
//            if (prefix.actualType.equals("pointer"))
//            {
////                opcodes.add(new Opcode("add_" + prefix.prefixName, "add %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a + b);\n\t}", false).isMath(true).signed(true));
////                opcodes.add(new Opcode("sub_" + prefix.prefixName, "subtract %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a - b);\n\t}", false).isMath(true).signed(true));
//            }
//            else if (prefix.math)
//            {
//                opcodes.add(new Opcode("cast_" + prefix.prefixName, "cast %t to type " + prefix.simpleName + ".", "stack.push" + prefix.simpleName + "(static_cast<" + prefix.actualType + "> (stack.pop%T()));", false).signed(true));
//
//                opcodes.add(new Opcode("add_" + prefix.prefixName, "add %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a + b);\n\t}", false).isMath(true).signed(true));
//                opcodes.add(new Opcode("sub_" + prefix.prefixName, "subtract %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a - b);\n\t}", false).isMath(true).signed(true));
//                opcodes.add(new Opcode("mul_" + prefix.prefixName, "multiply %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a * b);\n\t}", false).isMath(true).signed(true));
//                opcodes.add(new Opcode("div_" + prefix.prefixName, "divide %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a / b);\n\t}", false).isMath(true).signed(true));
//
//
////                opcodes.add(new Opcode("cmpl_" + prefix.prefixName, "compare %t less than type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////                opcodes.add(new Opcode("cmpg_" + prefix.prefixName, "compare %t greater than type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////                opcodes.add(new Opcode("cmpe_" + prefix.prefixName, "compare %t equal with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////
////                opcodes.add(new Opcode("cmple_" + prefix.prefixName, "compare %t less than or equal to type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////                opcodes.add(new Opcode("cmpge_" + prefix.prefixName, "compare %t greater than or equal to type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////                opcodes.add(new Opcode("cmpne_" + prefix.prefixName, "compare %t not equal with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
//
//            }
//        }



//        opcodes.add(new Opcode("icmpl", "compare an int less than int.", "{\n\tuint_32 b = stack.popInt();\n\tuint_32 a = stack.popInt();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//        opcodes.add(new Opcode("icmpg", "compare an int greater than int.", "{\n\tuint_32 b = stack.popInt();\n\tuint_32 a = stack.popInt();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//        opcodes.add(new Opcode("icmpe", "compare an int equal with int.", "{\n\tuint_32 b = stack.popInt();\n\tuint_32 a = stack.popInt();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//
//        opcodes.add(new Opcode("icmple", "compare an int less than or equal to int.", "{\n\tuint_32 b = stack.popInt();\n\tuint_32 a = stack.popInt();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("icmpge", "compare an int greater than or equal to int.", "{\n\tuint_32 b = stack.popInt();\n\tuint_32 a = stack.popInt();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("icmpne", "compare an int not equal with int.", "{\n\tuint_32 b = stack.popInt();\n\tuint_32 a = stack.popInt();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));


        opcodes.add(new Opcode("cmpl", "compare a long less than long.", "{\n\tint_64 b = stack.popLong();\n\tint_64 a = stack.popLong();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("cmpg", "compare a long greater than long.", "{\n\tint_64 b = stack.popLong();\n\tint_64 a = stack.popLong();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("cmpe", "compare a long equal with long.", "{\n\tint_64 b = stack.popLong();\n\tint_64 a = stack.popLong();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("cmple", "compare a long less than or equal to long.", "{\n\tint_64 b = stack.popLong();\n\tint_64 a = stack.popLong();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
        opcodes.add(new Opcode("cmpge", "compare a long greater than or equal to long.", "{\n\tint_64 b = stack.popLong();\n\tint_64 a = stack.popLong();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
        opcodes.add(new Opcode("cmpne", "compare a long not equal with long.", "{\n\tint_64 b = stack.popLong();\n\tint_64 a = stack.popLong();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));

        opcodes.add(new Opcode("ucmp",    "compare a ulong int with another ulong.", "{\n\tuint_64 b = stack.popUnsignedLong();\n\tuint_64 a = stack.popUnsignedLong();\n\tstack.pushByte((a < b) ? -1 : ((a > b) ? 1 : 0));\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("cmpu",    "compare a long int with a ulong.", "{\n\tuint_64 b = stack.popUnsignedLong();\n\tuint_64 a = stack.popLong();\n\tstack.pushByte((a < b) ? -1 : ((a > b) ? 1 : 0));\n\t}", false).isMath(true).setConstrained(false));

        opcodes.add(new Opcode("cmpll",    "compare a 256bit int with another 256bit int.", "{\n\tint_256 b = stack.popLongLong();\n\tint_256 a = stack.popLongLong();\n\tstack.pushByte((a < b) ? -1 : ((a > b) ? 1 : 0));\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("ucmpll",    "compare a u256bit int with another u256bit int.", "{\n\tuint_256 b = stack.popUnsignedLongLong();\n\tuint_256 a = stack.popUnsignedLongLong();\n\tstack.pushByte((a < b) ? -1 : ((a > b) ? 1 : 0));\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("cmpull",    "compare a 256bit int with a u256bit int.", "{\n\tuint_256 b = stack.popUnsignedLongLong();\n\tint_256 a = stack.popLongLong();\n\tstack.pushByte((a < b) ? -1 : ((a > b) ? 1 : 0));\n\t}", false).isMath(true).setConstrained(false));

        opcodes.add(new Opcode("dcmpl", "compare a double less than double.", "{\n\tflt_64 b = stack.popDouble();\n\tflt_64 a = stack.popDouble();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("dcmpg", "compare a double greater than double.", "{\n\tflt_64 b = stack.popDouble();\n\tflt_64 a = stack.popDouble();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("dcmpe", "compare a double equal with double.", "{\n\tflt_64 b = stack.popDouble();\n\tflt_64 a = stack.popDouble();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
        opcodes.add(new Opcode("dcmple", "compare a double less than or equal to double.", "{\n\tflt_64 b = stack.popDouble();\n\tflt_64 a = stack.popDouble();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
        opcodes.add(new Opcode("dcmpge", "compare a double greater than or equal to double.", "{\n\tflt_64 b = stack.popDouble();\n\tflt_64 a = stack.popDouble();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
        opcodes.add(new Opcode("dcmpne", "compare a double not equal with double.", "{\n\tflt_64 b = stack.popDouble();\n\tflt_64 a = stack.popDouble();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));



//        opcodes.add(new Opcode("fcmpl", "compare a float less than float.", "{\n\tflt_32 b = stack.popFloat();\n\tflt_32 a = stack.popFloat();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//        opcodes.add(new Opcode("fcmpg", "compare a float greater than float.", "{\n\tflt_32 b = stack.popFloat();\n\tflt_32 a = stack.popFloat();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//        opcodes.add(new Opcode("fcmpe", "compare a float equal with float.", "{\n\tflt_32 b = stack.popFloat();\n\tflt_32 a = stack.popFloat();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//
//        opcodes.add(new Opcode("fcmple", "compare a float less than or equal to float.", "{\n\tflt_32 b = stack.popFloat();\n\tuint_32 a = stack.popFloat();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fcmpge", "compare a float greater than or equal to float.", "{\n\tflt_32 b = stack.popFloat();\n\tuint_32 a = stack.popFloat();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fcmpne", "compare a float not equal with float.", "{\n\tflt_32 b = stack.popFloat();\n\tuint_32 a = stack.popFloat();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));




//        opcodes.add(new Opcode("fcmpl", "compare a float less than float.", "{\n\tflt_32 b = stack.popFloat();\n\tflt_32 a = stack.popFloat();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//        opcodes.add(new Opcode("fcmpg", "compare a float greater than float.", "{\n\tflt_32 b = stack.popFloat();\n\tflt_32 a = stack.popFloat();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//        opcodes.add(new Opcode("fcmpe", "compare a float equal with float.", "{\n\tflt_32 b = stack.popFloat();\n\tflt_32 a = stack.popFloat();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).setConstrained(false));
//
//        opcodes.add(new Opcode("fcmple", "compare a float less than or equal to float.", "{\n\tflt_32 b = stack.popFloat();\n\tuint_32 a = stack.popFloat();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fcmpge", "compare a float greater than or equal to float.", "{\n\tflt_32 b = stack.popFloat();\n\tuint_32 a = stack.popFloat();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));
//        opcodes.add(new Opcode("fcmpne", "compare a float not equal with float.", "{\n\tflt_32 b = stack.popFloat();\n\tuint_32 a = stack.popFloat();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true).setConstrained(false));

//        for (TypeInformation prefix : tpnames)
//        {
//            if (prefix.actualType.equals("pointer"))
//            {
//            }
//            else if (prefix.hasSignness)
//            {
//                String precName = prec.replace("b", "u" + prefix.actualType);
//
//                if (prefix.bitwise && prefix.math)
//                    opcodes.add(new Opcode("mod_" + prefix.prefixName, "modulo %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + precName + "();\n\tstack.push" + precName + "(a % b);\n\t}", false).isMath(true).isBitWise(true));
//                opcodes.add(new Opcode("cast_u" + prefix.prefixName, "cast %t to unsigned type " + prefix.simpleName + ".", "stack.pushUnsigned" + prefix.simpleName + "(static_cast<u" + prefix.actualType + "> (stack.pop%T()));", false).signed(true));
////                    opcodes.add(new Opcode("ucast_u" + prefix.prefixName, "cast %t to unsigned type " + prefix.simpleName + ".", "{}", false).signed(true));
////                    opcodes.add(new Opcode("uadd_u" + prefix.prefixName, "add %t with unsigned type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
////                    opcodes.add(new Opcode("usub_u" + prefix.prefixName, "subtract %t with unsigned type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
////                    opcodes.add(new Opcode("umul_u" + prefix.prefixName, "multiply %t with unsigned type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
////                    opcodes.add(new Opcode("udiv_u" + prefix.prefixName, "divide %t with unsigned type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
//                if (prefix.math)
//                {
////                opcodes.add(new Opcode("ucast_" + prefix.prefixName, "cast %t to type " + prefix.simpleName + ".", "{}", false).signed(true));
////                opcodes.add(new Opcode("uadd_" + prefix.prefixName, "add %t with type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
////                opcodes.add(new Opcode("usub_" + prefix.prefixName, "subtract %t with type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
////                opcodes.add(new Opcode("umul_" + prefix.prefixName, "multiply %t with type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
////                opcodes.add(new Opcode("udiv_" + prefix.prefixName, "divide %t with type " + prefix.simpleName + ".", "{}", false).isMath(true).signed(true));
////
//
//                    opcodes.add(new Opcode("add_u" + prefix.prefixName, "add %t with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"        + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.push"        + precName + "(a + b);\n\t}", false).isMath(true).signed(true));
//                    opcodes.add(new Opcode("sub_u" + prefix.prefixName, "subtract %t with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"   + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.push"        + precName + "(a - b);\n\t}", false).isMath(true).signed(true));
//                    opcodes.add(new Opcode("mul_u" + prefix.prefixName, "multiply %t with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"   + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.push"        + precName + "(a * b);\n\t}", false).isMath(true).signed(true));
//                    opcodes.add(new Opcode("div_u" + prefix.prefixName, "divide %t with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"     + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.push"        + precName + "(a / b);\n\t}", false).isMath(true).signed(true));
//
//
////                    opcodes.add(new Opcode("cmpl_u" + prefix.prefixName, "compare %t less than unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"      + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.pushByte((a < b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////                    opcodes.add(new Opcode("cmpg_u" + prefix.prefixName, "compare %t greater than unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"   + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.pushByte((a > b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////                    opcodes.add(new Opcode("cmpe_u" + prefix.prefixName, "compare %t equal with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"     + prefix.actualType + " a = stack.pop"          + precName + "();\n\tstack.pushByte((a == b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////
////                    opcodes.add(new Opcode("cmple_u" + prefix.prefixName, "compare %t less than or equal to unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"     + prefix.actualType + " a = stack.pop" + precName + "();\n\tstack.pushByte((a <= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////                    opcodes.add(new Opcode("cmpge_u" + prefix.prefixName, "compare %t greater than or equal to unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"  + prefix.actualType + " a = stack.pop" + precName + "();\n\tstack.pushByte((a >= b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
////                    opcodes.add(new Opcode("cmpne_u" + prefix.prefixName, "compare %t not equal with unsigned type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t"            + prefix.actualType + " a = stack.pop" + precName + "();\n\tstack.pushByte((a != b) ? 1 : 0);\n\t}", false).isMath(true).signed(true));
//                }
//            }
//
//
//            opcodes.add(new Opcode("swap" + prefix.prefixName, "swap the 2 topmost %t elements where the top element is a " + prefix.simpleName + ".", "{ " + prefix.actualType + " a = stack.pop" + prefix.simpleName + "(); %t b = stack.pop%T(); stack.push" + prefix.simpleName + "(a); stack.push%T(b); }", false));
//        }

//        for (TypeInformation prefix : tpnames)
//        {
//            if (prefix.bitwise && prefix.math)
//            {
////                opcodes.add(new Opcode("mod_" + prefix.prefixName, "modulo %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a % b);\n\t}", false).isMath(true).isBitWise(true));
////                opcodes.add(new Opcode("and_" + prefix.prefixName, "bitwise and %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a & b);\n\t}", false).isMath(true).isBitWise(true));
////                opcodes.add(new Opcode("or_" + prefix.prefixName, "bitwise or %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a | b);\n\t}", false).isMath(true).isBitWise(true));
////                opcodes.add(new Opcode("xor_" + prefix.prefixName, "bitwise xor %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a ^ b);\n\t}", false).isMath(true).isBitWise(true));
////                opcodes.add(new Opcode("shftr_" + prefix.prefixName, "shift right %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a >> b);\n\t}", false).isMath(true).isBitWise(true));
////                opcodes.add(new Opcode("shftl_" + prefix.prefixName, "shift left xor %t with type " + prefix.simpleName + ".", "{\n\t%t b = stack.pop%T();\n\t" + prefix.actualType + " a = stack.pop" + prefix.simpleName + "();\n\tstack.push" + prec.replace("b", prefix.actualType) + "(a << b);\n\t}", false).isMath(true).isBitWise(true));
//            }
//        }


        opcodes.add(new Opcode("mod",   "mod an int with an int.", "{\n\tstack.pushLong(stack.popLong() % stack.popLong());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("umod",   "mod a uint with a uint.", "{\n\tstack.pushUnsignedLong(stack.popUnsignedLong() % stack.popUnsignedLong());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
        opcodes.add(new Opcode("llmod",   "mod an int 256 with an int 256.", "{\n\tstack.pushUnsignedLongLong(stack.popUnsignedLongLong() % stack.popUnsignedLongLong());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));

        opcodes.add(new Opcode("and_",   "bitwise and int with int.", "{\n\tstack.pushUnsignedLong(stack.popUnsignedLong() & stack.popUnsignedLong());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
        opcodes.add(new Opcode("or_",    "bitwise or int with int.", "{\n\tstack.pushUnsignedLong(stack.popUnsignedLong() | stack.popUnsignedLong());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
        opcodes.add(new Opcode("xor_",   "bitwise xor int with int.", "{\n\tstack.pushUnsignedLong(stack.popUnsignedLong() ^ stack.popUnsignedLong());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
        opcodes.add(new Opcode("not_",   "bitwise not int with int.", "{\n\tstack.pushUnsignedLong(stack.popUnsignedLong() ~ stack.popUnsignedLong());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
        opcodes.add(new Opcode("rshft_",   "right shift int with byte.", "{\n\tstack.pushUnsignedLong(stack.popUnsignedLong() >> stack.popUnsignedByte());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
        opcodes.add(new Opcode("lshft_",   "left shift int with byte.", "{\n\tstack.pushUnsignedLong(stack.popUnsignedLong() << stack.popUnsignedByte());\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));


//        opcodes.add(new Opcode("iand", "bitwise and int with int.", "{\n\tuint_32 b = stack.popUnsignedInt();\n\tuint_32 a = stack.popUnsignedInt();\n\tstack.pushUnsignedInt(a & b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("ior", "bitwise or int with int.", "{\n\tuint_32 b = stack.popUnsignedInt();\n\tuint_32 a = stack.popUnsignedInt();\n\tstack.pushUnsignedInt(a | b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("ixor", "bitwise xor int with int.", "{\n\tuint_32 b = stack.popUnsignedInt();\n\tuint_32 a = stack.popUnsignedInt();\n\tstack.pushUnsignedInt(a ^ b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("ishftr", "shift right int with int.", "{\n\tuint_32 b = stack.popUnsignedInt();\n\tuint_32 a = stack.popUnsignedInt();\n\tstack.pushUnsignedInt(a >> b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("ishftl", "shift left xor int with int.", "{\n\tuint_32 b = stack.popUnsignedInt();\n\tuint_32 a = stack.popUnsignedInt();\n\tstack.pushUnsignedInt(a << b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//
//
//        opcodes.add(new Opcode("land", "bitwise and long with long.", "{\n\tuint_64 b = stack.popUnsignedLong();\n\tuint_64 a = stack.popUnsignedLong();\n\tstack.pushUnsignedLong(a & b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("lor", "bitwise or long with long.", "{\n\tuint_64 b = stack.popUnsignedLong();\n\tuint_64 a = stack.popUnsignedLong();\n\tstack.pushUnsignedLong(a | b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("lxor", "bitwise xor long with long.", "{\n\tuint_64 b = stack.popUnsignedLong();\n\tuint_64 a = stack.popUnsignedLong();\n\tstack.pushUnsignedLong(a ^ b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("lshftr", "shift right long with long.", "{\n\tuint_64 b = stack.popUnsignedLong();\n\tuint_64 a = stack.popUnsignedLong();\n\tstack.pushUnsignedLong(a >> b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("lshftl", "shift left xor long with long.", "{\n\tuint_64 b = stack.popUnsignedLong();\n\tuint_64 a = stack.popUnsignedLong();\n\tstack.pushUnsignedLong(a << b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//
//
//        opcodes.add(new Opcode("lland", "bitwise and long long with long.", "{\n\tuint_256 b = stack.popUnsignedLongLong();\n\tuint_256 a = stack.popUnsignedLongLong();\n\tstack.pushUnsignedLongLong(a & b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("llor", "bitwise or long long with long.", "{\n\tuint_256 b = stack.popUnsignedLongLong();\n\tuint_256 a = stack.popUnsignedLongLong();\n\tstack.pushUnsignedLongLong(a | b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("llxor", "bitwise xor long long with long.", "{\n\tuint_256 b = stack.popUnsignedLongLong();\n\tuint_256 a = stack.popUnsignedLongLong();\n\tstack.pushUnsignedLongLong(a ^ b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("llshftr", "shift right long long with long.", "{\n\tuint_256 b = stack.popUnsignedLongLong();\n\tuint_256 a = stack.popUnsignedLongLong();\n\tstack.pushUnsignedLongLong(a >> b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));
//        opcodes.add(new Opcode("llshftl", "shift left xor long long with long.", "{\n\tuint_256 b = stack.popUnsignedLongLong();\n\tuint_256 a = stack.popUnsignedLongLong();\n\tstack.pushUnsignedLongLong(a << b);\n\t}", false).isMath(true).isBitWise(true).setConstrained(false));



        opcodes.add(new Opcode("dup", "duplicate an element on the stack.", "stack.pushUnsignedLong(stack.peekUnsignedLong());", false).setConstrained(false));
        opcodes.add(new Opcode("dup2", "duplicate an element 2 times on the stack.", "stack.pushUnsignedLong(stack.peekUnsignedLong());stack.pushUnsignedLong(stack.peekUnsignedLong());", true).setConstrained(false));
        opcodes.add(new Opcode("dup3", "duplicate an element 3 times on the stack.", "stack.pushUnsignedLong(stack.peekUnsignedLong());stack.pushUnsignedLong(stack.peekUnsignedLong());stack.pushUnsignedLong(stack.peekUnsignedLong());", true).setConstrained(false));
//        opcodes.add(new Opcode("dup4", "duplicate a %t 4 times on the stack.", "stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());", false));
//        opcodes.add(new Opcode("dup5", "duplicate a %t 5 times on the stack.", "stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());stack.push%T(stack.peek%T());", false));
//        opcodes.add(new Opcode("swap", "swap the 2 topmost %t elements.", "{ %t a = stack.pop%T(); %t b = stack.pop%T(); stack.push%T(a); stack.push%T(b); }", false));
        opcodes.add(new Opcode("swap", "swap the 2 topmost elements.", "{ uint_64 a = stack.popUnsignedLong(); uint_64 b = stack.popUnsignedLong(); stack.pushUnsignedLong(a); stack.pushUnsignedLong(b); }", false).setConstrained(false));
//        opcodes.add(new Opcode("rot", "roState the 3 topmost %t elements.", "{ %t a = stack.pop%T(); %t b = stack.pop%T(); %t c = stack.pop%T(); stack.push%T(a); stack.push%T(b); stack.push%T(c); }", false));

        opcodes.add(new Opcode("set", "set a %t from stack into a field on pointer.", "MvM::accessMemoryAndSet%T(stack.popPointer(), stack.popUnsignedLong(), stack.pop%T());", false));
        opcodes.add(new Opcode("get", "get a %t from a field on a pointer to stack.", "stack.push%T(MvM::accessMemoryAndGet%T(stack.popPointer(), stack.popUnsignedLong()));", false));
//        opcodes.add(new Opcode("vset", "set a %t from value into a field on base pointer.",  "MvM::accessMemoryAndSet%T(base, ops.getUnsignedInt(), ops.get%T());", false).setHumanReadable("UnsignedInt:%T"));
//        opcodes.add(new Opcode("sget", "get a %t from a field on base pointer to stack using address from stack.", "stack.push%T(MvM::accessMemoryAndGet%T(base, stack.popUnsignedInt()));", false));
//        opcodes.add(new Opcode("sset", "set a %t from stack into a field on base pointer using address from stack.", "MvM::accessMemoryAndSet%T(base, stack.popUnsignedInt(), stack.pop%T());", false));


//        opcodes.add(new Opcode("setl", "set a %t from local variable into a field on base pointer.", "MvM::accessMemoryAndSet%T(base, ops.getUnsignedInt(), lvt[ops.getUnsignedShort()].%T);", false).setHumanReadable("UnsignedInt:UnsignedShort"));
        opcodes.add(new Opcode("inc1", "increment a local variable %t by 1.",  "{ uint_16 point = ops.getUnsignedShort(); lvt[point].%T = lvt[point].%T + 1; }", true).setHumanReadable("UnsignedShort"));
        opcodes.add(new Opcode("inc2", "increment a local variable %t by 2.",  "{ uint_16 point = ops.getUnsignedShort(); lvt[point].%T = lvt[point].%T + 2; }", true).setHumanReadable("UnsignedShort"));
//        opcodes.add(new Opcode("inci1", "increment an integer by 1 on stack.",  "stack.pushLong(stack.popLong() + 1);", false).setConstrained(false));
//        opcodes.add(new Opcode("inci2", "increment an imteger by 2 on stack.",  "stack.pushLong(stack.popLong() + 2);", false).setConstrained(false));
//        opcodes.add(new Opcode("inc1", "increment a %t by 1 on stack.",  "stack.push%T(stack.pop%T() + 1);", false).setConstrained(false));
//        opcodes.add(new Opcode("inc2", "increment a %t by 1 on stack.",  "stack.push%T(stack.pop%T() + 1);", false).setConstrained(false));

        opcodes.add(new Opcode("pret", "return the top element into the main stack.", "ops.address = ops.length; stack_main.pushUnsignedLong(stack.popUnsignedLong()); return;", false).setConstrained(false));
        opcodes.add(new Opcode("pret2", "return the top 2 elements (128bit) into the main stack.", "ops.address = ops.length; stack_main.pushUnsignedLongInt(stack.popUnsignedLongInt()); return;", false).setConstrained(false));
        opcodes.add(new Opcode("pret4", "return the top 4 elements (256bit) into the main stack.", "ops.address = ops.length; stack_main.pushUnsignedLongLong(stack.popUnsignedLongLong()); return;", false).setConstrained(false));

        opcodes.add(new Opcode("mainst", "store a %t from main stack in a local variable..",     "lvt[ops.getUnsignedShort()].%T = stack_main.pop%T();", false).setHumanReadable("UnsignedShort"));
        opcodes.add(new Opcode("mainst_0", "store a %t from main stack in local variable 0.",   "lvt[0].%T = stack_main.pop%T();;", true));
        opcodes.add(new Opcode("mainst_1", "store a %t from main stack in local variable 1.",   "lvt[1].%T = stack_main.pop%T();;", true));
        opcodes.add(new Opcode("mainst_2", "store a %t from main stack in local variable 2.",   "lvt[2].%T = stack_main.pop%T();;", true));
        opcodes.add(new Opcode("mainst_3", "store a %t from main stack in local variable 3.",   "lvt[3].%T = stack_main.pop%T();;", true));

        opcodes.add(new Opcode("print", "print a %t from stack.",   "MvM::log(std::to_string(stack.pop%T()));", false).signed(true));



        Set<String> switch__    = new LinkedHashSet<>();
        Set<String> opcodes_    = new LinkedHashSet<>();
        Set<String> reader_     = new LinkedHashSet<>();
        Set<String> global      = new LinkedHashSet<>();
        Set<String> map         = new LinkedHashSet<>();

        Set<String> headers     = new LinkedHashSet<>();
        Set<String> implnts     = new LinkedHashSet<>();
        Set<String> funcimp     = new LinkedHashSet<>();

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
                long id = opcodes_.size();
                String s = opcode.simpleName + " = " + id;
                int spaces = 20 - s.length();
                for (int x = 0; x < spaces; x++)
                    s += " ";

                opcodes_.add(s + ",/** " + opcode.simpleDescription + " **/\n");
                switch__.add("${INFORMATION}\ncase ".replace("${INFORMATION}", "/**\n *" + opcode.simpleDescription + "\n */") + opcode.simpleName + ":\n\t" + opcode.switchStatementCode + "\n\tbreak;\n");

                if (opcode.getHumanReadableReaderCode().length() > 0)
                    reader_.add("case \"" + opcode.simpleName + "\":\n\t" + "add(" + id + "); " + opcode.getHumanReadableReaderCode() + "\n\tbreak;\n");




                headers.add("void " + opcode.simpleName + "_impl" + funcargs + ";\n");
                funcimp.add("impl_funcs[" + id + "] = &" + opcode.simpleName + "_impl;\n");
                implnts.add("void funcs::" + opcode.simpleName + "_impl" + funcargs + "\n{\t" +
                        "${INFORMATION}\n".replace("${INFORMATION}", "/**\n * " + opcode.notSimpleDescription + "\n */") + "\n\t" + opcode.switchStatementCode + "\n\t\n" + "\n}\n");



                map.add("map.put(\"" + opcode.simpleName + "\", " + id + ");\n");
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
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else if (opcode.isBitwise())
                        {
                            if (typeInformation.bitwise)
                            {
                                if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else if (opcode.simple)
                        {
                            if (typeInformation.simpleOps)
                            {
                                if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                       else
                            getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
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
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else if (opcode.isBitwise())
                        {
                            if (typeInformation.bitwise)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else if (opcode.simple)
                        {
                            if (typeInformation.simpleOps)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else
                            getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);

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
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else if (opcode.signed)
                        {
                            if (typeInformation.hasSignness)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.simple)
                                {
                                    if (typeInformation.simpleOps)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else if (opcode.simple)
                        {
                            if (typeInformation.simpleOps)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else
                            getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);

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
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else if (opcode.signed)
                        {
                            if (typeInformation.hasSignness)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.isBitwise())
                                {
                                    if (typeInformation.bitwise)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else if (opcode.isBitwise())
                        {
                            if (typeInformation.bitwise)
                            {
                                if (opcode.isMath())
                                {
                                    if (typeInformation.math)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else if (opcode.signed)
                                {
                                    if (typeInformation.hasSignness)
                                        getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                                }
                                else
                                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
                            }
                        }
                        else
                            getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);

                    }
                }
                else
                    getOps(typeInformation, opcode, t, T, global, opcodes_, switch__, reader_, map, headers, implnts, funcimp, tpnames);
            }
        }

        try{
            String opcodes__ = "";
            for (String line : opcodes_)
                opcodes__ += line;
            String switch___ = "";
            for (String line : switch__)
                switch___ += line;
            String read_____ = "";
            for (String line : reader_)
                read_____ += line;
            String mapi_____ = "";
            for (String line : map)
                mapi_____ += line;
            String headers__ = "";
            for (String line : headers)
                headers__ += line;
            String implmnts_ = "";
            for (String line : implnts)
                implmnts_ += line;
            String funcimps_ = "";
            for (String line : funcimp)
                funcimps_ += line;

            write(new File("./ops.txt"), opcodes__);
            write(new File("./switch.txt"), switch___);
            write(new File("./read_switch.txt"), read_____);
            write(new File("./map.txt"), mapi_____);
            write(new File("./headers.txt"), headers__);
            write(new File("./implementations.txt"), implmnts_);
            write(new File("./declarations.txt"), funcimps_);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

//        System.out.println(opcodes_.toString());
    }

    static final String funcargs = "(OP_STACK* globalTable, MochaNativeInterface** nativeTable, pointer globalPointer, pointer basePointer, Stack& stack_main, Stack& stack, OP_STACK& ops, std::map<uint_32, localvarelement>& lvt, std::map<uint_32, uint_64>& CHECK_POINTS, pointer base)";

    private static void write(File file, String string) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(string);
        writer.flush();
        writer.close();
    }

    private static void getOps(TypeInformation typeInformation, Opcode opcode, String t, String T, Set<String> global, Set<String> opcodes_, Set<String> switch__, Set<String> reader_, Set<String> map, Set<String> headers, Set<String> implnts, Set<String> funcimp, Set<TypeInformation> typeInfo)
    {
        if (!global.contains(typeInformation.prefixName + opcode.simpleName))
        {
            long id = opcodes_.size();

            global.add(typeInformation.prefixName + opcode.simpleName);
            String o = null;
            String s = o = (typeInformation.prefixName + opcode.simpleName);
            s += " = " + id;

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





            headers.add("void " + o + "_impl" + funcargs + ";\n");
            implnts.add("void funcs::" + o + "_impl" + funcargs + "\n{\t" +
                    "${INFORMATION}\n".replace("${INFORMATION}", "/**\n * " + opcode.notSimpleDescription.replace("%t", t).replace("%T", T) + "\n */") + "\n\t" + opcode.switchStatementCode.replaceAll(precCheck, code).replace("%t", t).replace("%T", T) + "\n\t\n" + "\n}\n");

            funcimp.add("impl_funcs[" + id + "] = &" + o + "_impl;\n");

            opcodes_.add(s + ",/** " + opcode.simpleDescription.replace("%t", t).replace("%T", T) + " **/\n");
            switch__.add("${INFORMATION}\ncase ".replace("${INFORMATION}", "/**\n * " + opcode.notSimpleDescription.replace("%t", t).replace("%T", T) + "\n */") + o + ":\n\t" + opcode.switchStatementCode.replaceAll(precCheck, code).replace("%t", t).replace("%T", T) + "\n\tbreak;\n");
            if (opcode.getHumanReadableReaderCode().length() > 0)
                reader_.add("case \"" + o + "\":\n\t" + "add(" + id + "); " + opcode.getHumanReadableReaderCode().replace("%T", T) + "\n\tbreak;\n");

            map.add("map.put(\"" + o + "\", " + id + ");\n");
        }

        if (!global.contains(typeInformation.prefixName + "u" + opcode.simpleName) && (typeInformation.hasSignness && opcode.signed()))
        {
            long id = opcodes_.size();

            global.add(typeInformation.prefixName + "u" + opcode.simpleName);
            T = "Unsigned" + T;
            t = "u" + t;

            String o = null;
            String s = o = typeInformation.prefixName + "u" + opcode.simpleName;
            s += " = " + id;

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





            headers.add("void " + o + "_impl" + funcargs + ";\n");
            implnts.add("void funcs::" + o + "_impl" + funcargs + "\n{\t" +
                    "${INFORMATION}\n ".replace("${INFORMATION}", "/**\n * " + opcode.notSimpleDescription.replace("%t", "unsigned " + t).replace("%T", "Unsigned" + T) + "\n */") + "\n\t" + opcode.switchStatementCode.replaceAll(precCheck, code).replace("%t", t).replace("%T", T) + "\n\t;\n}\n");

            funcimp.add("impl_funcs[" + id + "] = &" + o + "_impl;\n");


            switch__.add("${INFORMATION}\ncase ".replace("${INFORMATION}", "/**\n * " + opcode.notSimpleDescription.replace("%t", "unsigned " + t).replace("%T", "Unsigned" + T) + "\n */") + o + ":\n\t" + opcode.switchStatementCode.replaceAll(precCheck, code).replace("%t", t).replace("%T", T) + "\n\tbreak;\n");
            opcodes_.add(s + ",/** " + opcode.simpleDescription.replace("%t", "unsigned " + t).replace("%T", "Unsigned" + T) + " **/\n");
            if (opcode.getHumanReadableReaderCode().length() > 0)
                reader_.add("case \"" + o + "\":\n\t" + "add(" + id + "); " + opcode.getHumanReadableReaderCode().replace("%T", T) + "\n\tbreak;\n");

            map.add("map.put(\"" + o + "\", " + id + ");\n");
        }
    }
}
