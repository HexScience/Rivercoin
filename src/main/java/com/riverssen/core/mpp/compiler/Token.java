/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Riverssen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.riverssen.core.mpp.compiler;

import com.riverssen.core.RiverCoin;
import com.riverssen.core.mpp.exceptions.CompileException;
import com.riverssen.core.mpp.objects.*;
import com.riverssen.core.mpp.objects.Boolean;
import com.riverssen.core.mpp.objects.Float;
import com.riverssen.core.mpp.objects.Integer;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

public class Token implements Serializable
{
    private StringBuilder       value;
    private int                 line;
    private int                 offset;
    private int                 whitespace;
    private Type                type;
    private List<Token>         children;
    private Set<Modifier>       modifiers = new LinkedHashSet<>();
    private BigInteger          cost = BigInteger.ZERO;

    public boolean isMathOp()
    {
        return getType() == Type.MATH_OP;
    }

    public Collection<Modifier> getModifiers()
    {
        return modifiers;
    }

    public List<Token> getChildByType(Type type)
    {
        List<Token> tokens = new ArrayList<>();

        for(Token token : children) if(token.getType().equals(type)) ((ArrayList<Token>) tokens).add(token);

        return tokens;
    }

    public void setModifiers(Modifier modifier)
    {
        this.modifiers.add(modifier);
    }

    public boolean isModifier(Modifier modifier)
    {
        return modifiers.contains(modifier);
    }

    private Token getTokenByTypeName(Type type, String name)
    {
        for(Token token : getTokens())
            if(token.type.equals(type) && token.getTokens().get(0).toString().equals(name))
                return token;
            else if(token.type.equals(Type.BRACES))
                return token.getTokenByTypeName(type, name);

        return this;
    }

    private Type nameToType(String name)
    {
        if(name.equals("class"))
        {
            return Type.CLASS_DECLARATION;
        } else if(name.equals("method"))
        {
            return Type.METHOD_DECLARATION;
        } else if(name.equals("field"))
        {
            return Type.EMPTY_DECLARATION;
        }
        else return this.getType();
    }

    public Token getToken(String tokenNameOrValue)
    {
        if(tokenNameOrValue.equals("*")) return this;
        String heierarchy[] = tokenNameOrValue.split(" ");

        Token token = getTokenByTypeName(nameToType(heierarchy[0].split("::")[0]), heierarchy[0].split("::")[1]);

        for(int i = 1; i < heierarchy.length; i ++)
            token = token.getTokenByTypeName(nameToType(heierarchy[i].split("::")[0]), heierarchy[i].split("::")[1]);

        return token;
    }

    public static enum          Type implements Serializable {
        NAMESPACE,
        KEYWORD,
        IDENTIFIER,
        SYMBOL,
        STRING,
        NUMBER,
        PARENTHESIS_OPEN,
        PARENTHESIS_CLOSED,
        BRACES_OPEN,
        BRACES_CLOSED,
        BRACKETS_OPEN,
        BRACKETS_CLOSED,
        MATH_OP,
        EQUALS,
        END,
        ROOT,
        STATIC_ACCESS,
        PROCEDURAL_ACCESS,
        POINTER_ACCESS,
        INITIALIZATION,
        EMPTY_DECLARATION,
        FULL_DECLARATION,
        METHOD_CALL,
        METHOD_DECLARATION,
        METHOD_EMPTY_DECLARATION,
        CLASS_DECLARATION,
        EMPTY_CLASS_DECLARATION,
        PARENTHESIS,
        BRACES,
        INPUT,
        ADDITION,
        SUBTRACTION,
        MULTIPLICATION,
        SUBDIVISION,
        POW,
        MOD,
        BRACKETS,
        VALUE,
        NEW,
        EXTEND,
        IF,
        FOR,
        FOREACH,
        BOOL_OP,
        LESS_THAN,
        MORE_THAN,
        LESSTHAN_EQUAL,
        MORETHAN_EQUAL,
        ASSERT,
        AND,
        OR,
        RIGHT_SHIFT,
        LEFT_SHIFT,
        UNARY,
        PREUNARY,
        PLUSPLUS,
        MINUSMINUS,
        LIST,
        ARRAY,
        WHILE,
        DECIMAL,
        RETURN
    };

    public Token(String value, int line, int offset, int whitespace)
    {
        this.value = new StringBuilder(value);
        this.line = line;
        this.offset = offset;
        this.whitespace = whitespace;
        this.children   = new ArrayList<>();
    }

    public Token(char value, int line, int offset, int whitespace)
    {
        this.value = new StringBuilder(value);
        this.line = line;
        this.offset = offset;
        this.whitespace = whitespace;
        this.children   = new ArrayList<>();
    }

    public Token(Type type)
    {
        this.type       = type;
        this.value      = new StringBuilder(type.toString());
        this.children   = new ArrayList<>();
    }

    private Token()
    {
    }

    public Token append(char chr)
    {
        value.append(chr);
        return this;
    }

    public Token append(String chr)
    {
        value.append(chr);
        return this;
    }

    public Token add(Token token)
    {
        this.children.add(token);
        return this;
    }

    public Container interpret(Container context, Container self, Container fcontext, Container fself, boolean proc, Container ...args) throws CompileException
    {
        return this.interpret(context, self, fcontext, fself, Container.EMPTY, proc, false, args);
    }

    public Container interpret(Container context, Container self, Container fcontext, Container fself, boolean proc, boolean mproc, Container ...args) throws CompileException
    {
        return this.interpret(context, self, fcontext, fself, Container.EMPTY, proc, mproc, args);

    }

    public Container interpret(Container context, Container self, Container fcontext, Container fself, Container initType, boolean proc, boolean mproc, Container ...args) throws CompileException
    {
        return this.interpret("null", context, self, fcontext, fself, initType, proc, mproc, args);
    }

    public Container interpret(String accessorType, Container context, Container self, Container fcontext, Container fself, Container initType, boolean proc, boolean mproc, Container ...args) throws CompileException
    {
        switch (type)
        {
            case MATH_OP:
                    Container b = getTokens().get(0).interpret(context, self, fcontext, fself, initType, proc, mproc, args);
                    Container a = getTokens().get(1).interpret(context, self, fcontext, fself, initType, proc, mproc, args);

                    switch (toString().charAt(0))
                    {
                        case '*':
                            return a.multiplication(b);
                        case '+':
                            return a.addition(b);
                        case '-':
                            return a.submission(b);
                        case '/':
                            return a.subdivision(b);
                        case '%':
                            return a.modulo(b);
                        case '^':
                            return a.power(b);
                    }

                    return Container.EMPTY;
            case BRACKETS:
                return getTokens().get(0).interpret(context, self, fcontext, fself, proc, args).bracketGet(getTokens().get(1).interpret(context, self, fcontext, fself, proc, args));
            case NEW:
                if(getTokens().size() > 0)
                {
                    Container arguments[] = new Container[getTokens().get(1).getTokens().size()];
                    for(int i = 0; i < arguments.length; i ++)
                        arguments[i] = getTokens().get(1).getTokens().get(i).interpret(context, self, fcontext, fself, proc, args);

                    String methodName = getTokens().get(0).toString();

                    if(context.get(methodName) != null) return context.callMethod(toString(), arguments);
                    else if(self.get(methodName) != null) return self.callMethod(toString(), arguments);
                    else if(context.getGlobal().get(methodName) != null) return context.getGlobal().callMethod(methodName, arguments);
                    else throw new CompileException("method '" + methodName + "' not defined.", this);
                }

                if(context.get(toString()) != null) return context.callMethod(toString());
                else if(self.get(toString()) != null) return self.callMethod(toString());
                else if(context.getGlobal().get(toString()) != null) return context.getGlobal().callMethod(toString());
                else throw new CompileException("identifier 'new " + toString() + "' not defined.", this);
            case FULL_DECLARATION:
                String type = getTokens().get(0).toString();
                String name = getTokens().get(1).toString();
                Token value = getTokens().get(2);

                Container initType0 = null;

                try{
                    initType0 = fself.getGlobal().get(type.toString());
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

//                if (initType0 == null)
//                    throw new CompileException("Type '" + type + "' doesn't exist.", this);

                if (context.get(name) != null) throw new CompileException("object '" + name + "' already defined.", this);

                Container returnedValue = value.interpret(context, self, fcontext, fself, initType0, proc, mproc, args);

                context.setField(name, returnedValue);
                break;
            case INITIALIZATION:
                String name_ = getTokens().get(0).toString();
                Token value_ = getTokens().get(1);

                if(context.get(name_) != null)
                {
                    Container initType0_ = null;
                    try{fself.getGlobal().get(context.get(name_).getType());} catch (Exception e)
                    {
                        initType0_ = Container.VOID;
                    }
                    Container returnedValue_ = value_.interpret(context, self, fcontext, fself, initType0_, proc, mproc, args);

                    context.setField(name_, returnedValue_);
                }
                else {
                    Container initType0_ = null;
                    try{fself.getGlobal().get(self.get(name_).getType());} catch (Exception e)
                    {
                        initType0_ = Container.VOID;
                    }
                    Container returnedValue_ = value_.interpret(context, self, fcontext, fself, initType0_, proc, mproc, args);

                    self.setField(name_, returnedValue_);
                }
                break;
            case PROCEDURAL_ACCESS:
                Container returnee   = getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);//self;
                String accessor_type = returnee.name;
//                Container rvlue = getRoot().get(0).interpret(context, self, args);

//                if(context.get(getRoot().get(0).toString()) != null) returnee = context.get(getRoot().get(0).toString());
//                else if(self.get(getRoot().get(0).toString()) != null) returnee = self.get(getRoot().get(0).toString());

//                else throw new CompileException("identifier '" + getRoot().get(0).toString() + "' not defined.", this);

                if(getTokens().get(1).getType().equals(Type.INITIALIZATION))
                {
                    String name__ = getTokens().get(0).toString();
                    Token value__ = getTokens().get(1);

                    if (returnee.get(name__) != null){
                        Container initType0_ = null;
                        try{initType0_ = fself.getGlobal().get(returnee.get(name__).getType());} catch (Exception e)
                        {
                            initType0_ = Container.VOID;
                        }
                        Container returnedValue__ = value__.interpret(accessor_type, context, self, fcontext, fself, initType0_, true, false, args);

                        returnee.setField(name__, returnedValue__);
                    }
                    else {
                        Container initType0_ = null;
                        try{fself.getGlobal().get(self.get(name__).getType());} catch (Exception e)
                        {
                            initType0_ = Container.VOID;
                        }
                        Container returnedValue__ = value__.interpret(accessor_type, context, self, fcontext, fself, initType0_, true, false, args);

                        self.setField(name__, returnedValue__);
                    }
                } else
                return getTokens().get(1).interpret(accessor_type, returnee, Container.EMPTY, fcontext, fself, initType, true, false, args);
            case VALUE:
                    return getTokens().get(0).interpret(context, self, fcontext, fself, initType, proc, mproc, args);
            case INPUT:
                    return getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);
            case NUMBER:
                if(initType == null) return new uint256(toString());
                    return initType.newInstance(Long.valueOf(toString()));
            case DECIMAL:
                    return new Float(Double.valueOf(toString()));
            case IDENTIFIER:
                    if(context.get(toString()) != null) return context.get(toString());
                    else if(self.get(toString()) != null) return self.get(toString());
                    else throw new CompileException("identifier '" + toString() + "' not defined.", this);
            case METHOD_CALL:
                        if(proc)
                        {
                            if(getTokens().size() == 0) throw new CompileException("method '" + this.toString() + "' not defined.", this);
                            Container arguments[] = new Container[getTokens().get(1).getTokens().size()];
                            for(int i = 0; i < arguments.length; i ++)
                                arguments[i] = getTokens().get(1).getTokens().get(i).interpret(fcontext, fself, fcontext, fself, proc, mproc, args);

                            String methodName = getTokens().get(0).toString();

                            if(context.get(methodName) != null) return context.callMethod(methodName, arguments);
                            else throw new CompileException("method '" + methodName + "' not defined in '" + context + "'.", this);
                        }
                        else {
                            if(getTokens().size() == 0) throw new CompileException("method '" + this.toString() + "' not defined.", this);
                            Container arguments[] = new Container[getTokens().get(1).getTokens().size()];
                            for(int i = 0; i < arguments.length; i ++)
                                arguments[i] = getTokens().get(1).getTokens().get(i).interpret(fcontext, fself, fcontext, fself, proc, mproc, args);

                            String methodName = getTokens().get(0).toString();

                            if(fcontext.get(methodName) != null) return fcontext.callMethod(methodName, arguments);
                            else if(fself.get(methodName) != null) return fself.callMethod(methodName, arguments);
                            else if(fcontext.getGlobal().get(methodName) != null) return fcontext.getGlobal().callMethod(methodName, arguments);
                            else throw new CompileException("method '" + methodName + "' not defined.", this);
                        }
            case ASSERT:
                Container a_ = getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);
                Container b_ = getTokens().get(1).interpret(context, self, fcontext, fself, proc, args);
                return new Boolean(a_.equals(b_));
            case STRING: return new StringObject(toString().substring(1, toString().length() - 1));
            case IF:
                Container conditions = getTokens().get(0).getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);
                if((boolean)conditions.asJavaObject() == true)
                {
                    Token token = getTokens().get(1);
                    LoopContainer container = new LoopContainer(context);

                    if(token.getType().equals(Type.BRACES))
                    {
                        for (Token tkn : token.getTokens())
                            tkn.interpret(container, self, fcontext, fself, proc, args);
                    } else token.interpret(container, self, fcontext, fself, proc, args);
                }
                break;
            case PARENTHESIS:
                    Container retval = Container.EMPTY;
                    for(Token token : getTokens()) retval = token.interpret(context, self, fcontext, fself, proc, args);

                    return retval;
            case RETURN:
                return getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);
        }
        return null;
    }

    public Token clone()
    {
        Token token = new Token();
        token.type  = type;
        token.line  = line;
        token.offset= offset;
        token.value = new StringBuilder(value);

        for (Token child : children)
            token.add(child.clone());
        return token;
    }

    public int getLine()
    {
        return line;
    }

    public int getOffset()
    {
        return offset;
    }

    public int getWhitespace()
    {
        return whitespace;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public Type getType()
    {
        if (type == null)
        {
            final char separators[] = { '.', '=', '+', '-', '\'', '"', ',', '<', '>', '?', ';', ':', '!', '\\', '/', '[', ']', '{', '}', '(', ')', '*', '&', '^', '%', '$', '#', '@', '~' };

            boolean separator = false;

            if (toString().length() == 1)
            {
                if (toString().charAt(0) == ')')
                {
                    type = Type.PARENTHESIS_CLOSED;
                    return type;
                } else if (toString().charAt(0) == '(')
                {
                    type = Type.PARENTHESIS_OPEN;
                    return type;
                } else if (toString().charAt(0) == '}')
                {
                    type = Type.BRACES_CLOSED;
                    return type;
                } else if (toString().charAt(0) == '{')
                {
                    type = Type.BRACES_OPEN;
                    return type;
                } else if (toString().charAt(0) == ']')
                {
                    type = Type.BRACKETS_CLOSED;
                    return type;
                } else if (toString().charAt(0) == '[')
                {
                    type = Type.BRACKETS_OPEN;
                    return type;
                } else if (toString().charAt(0) == ';')
                {
                    type = Type.END;
                    return type;
                } else if (toString().charAt(0) == '=')
                {
                    type = Type.EQUALS;
                    return type;
                } else if (toString().charAt(0) == '+' || toString().charAt(0) == '-' || toString().charAt(0) == '*'
                        || toString().charAt(0) == '/' || toString().charAt(0) == '%')
                {
                    type = Type.MATH_OP;
                    return type;
                } else if (toString().charAt(0) == '&')
                {
                    type = Type.AND;
                    return type;
                } else if (toString().charAt(0) == '|')
                {
                    type = Type.OR;
                    return type;
                } else if (toString().charAt(0) == '<')
                {
                    type = Type.LESS_THAN;
                    return type;
                } else if (toString().charAt(0) == '>')
                {
                    type = Type.MORE_THAN;
                    return type;
                }

                for (char s : separators) if (toString().charAt(0) == s) separator = true;
            }

            if (separator) {
                if(toString().charAt(0) == '.') {
                    type = Type.PROCEDURAL_ACCESS;
                    return type;
                }
                type = Type.SYMBOL;
            }
            else
            {
                if (toString().startsWith("\"") || toString().startsWith("'")) type = Type.STRING;
                else if (toString().equals("extends"))
                    type = Type.EXTEND;
                else if (toString().matches("([_]*[A-z]+\\d*)+")) {
                    if(isKeyword()) type = Type.KEYWORD;
                    else
                    type = Type.IDENTIFIER;
                }
                else if (toString().matches("(\\d[_]*\\d*)+")) type = Type.NUMBER;
            }
        }
        return this.type;
    }

    public void setCost(BigInteger cost)
    {
        this.cost = cost;
    }

    private boolean isKeyword()
    {
        final String keywords[] = {"function", "fun", "new", "class", "static", "public", "private", "protected", "const", "final", "extend", "header", "if", "for", "while", "foreach", "then", "namespace", "return"};
        for(String string : keywords) if(toString().equals(string)) return true;
        return false;
    }

    public List<Token> getTokens()
    {
        return this.children;
    }

    private String whitespace(int length)
    {
        length*=4;
        String string = "";

        for(int i = 0; i < length; i ++)
            string += ' ';

        return string;
    }

    public String humanReadable(int i)
    {
        String s = (whitespace(i) + type + " " + value) + "\n";
        for(Token token : children)
            s += token.humanReadable(i + 1) + "\n";
        return s;
    }

    public boolean isInteger()
    {
        return toString().contains(".");
    }

    public BigInteger calculateCost()
    {
        BigInteger cost = BigInteger.ZERO;

        for(Token token : getTokens())
            cost = cost.add(token.calculateCost());

        switch (type)
        {
            case IF:
                cost = cost.add(new RiverCoin("0.000035").toBigInteger());
                break;
            case NEW:
                cost = cost.add(new RiverCoin("0.0000035").toBigInteger());
                break;
            case INITIALIZATION:
                cost = cost.add(new RiverCoin("0.00000135").toBigInteger());
                break;
            case FOR:
                cost = cost.add(new RiverCoin("0.000025").toBigInteger());
                break;
            case CLASS_DECLARATION:
                cost = cost.add(new RiverCoin("0.025").toBigInteger());
                break;
            case METHOD_CALL:
                cost = cost.add(new RiverCoin("0.00025").toBigInteger());
                break;
            case EMPTY_DECLARATION:
                cost = cost.add(new RiverCoin("0.0000025").toBigInteger());
                break;
            case FULL_DECLARATION:
                cost = cost.add(new RiverCoin("0.0000075").toBigInteger());
                break;
            case WHILE:
                cost = cost.add(new RiverCoin("0.000825").toBigInteger());
                break;
            default:
                /** price per byte **/
                cost = cost.add(new RiverCoin("0.0000000267028809").toBigInteger());
                break;
        }

        return cost.add(cost);
    }

    @Override
    public String toString()
    {
        return value.toString();//humanReadable(0);
    }
}