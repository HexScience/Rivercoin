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
import com.riverssen.core.mpp.objects.Boolean;
import com.riverssen.core.mpp.objects.Float;
import com.riverssen.core.mpp.objects.Integer;
import com.riverssen.core.mpp.objects.LoopContainer;
import com.riverssen.core.mpp.runtime.StringObject;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

import static com.riverssen.core.mpp.compiler.Opcode.PUSH_FLOAT;
import static com.riverssen.core.mpp.compiler.Opcode.PUSH_INT;

public class Token implements Serializable
{
    private StringBuilder       value;
    private int                 line;
    private int                 offset;
    private int                 whitespace;
    private Type                type;
    private List<Token>         children;
    private Set<Modifier>       modifiers = new LinkedHashSet<>();

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
        return this.interpret(context, self, fcontext, fself, proc, false, args);
    }

    public Container interpret(Container context, Container self, Container fcontext, Container fself, boolean proc, boolean mproc, Container ...args) throws CompileException
    {
        switch (type)
        {
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
                String name = getTokens().get(0).toString();
                String type = getTokens().get(1).toString();
                Token value = getTokens().get(2);

                if (context.get(name) != null) throw new CompileException("object '" + name + "' already defined.", this);

                Container returnedValue = value.interpret(context, self, fcontext, fself, proc, args);

                context.setField(name, returnedValue);
                break;
            case INITIALIZATION:
                String name_ = getTokens().get(0).toString();
                Token value_ = getTokens().get(1);

                Container returnedValue_ = value_.interpret(context, self, fcontext, fself, proc, args);
                if(context.get(name_) != null)
                    context.setField(name_, returnedValue_);
                else self.setField(name_, returnedValue_);
                break;
            case PROCEDURAL_ACCESS:
                Container returnee = getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);//self;

//                Container rvlue = getTokens().get(0).interpret(context, self, args);

//                if(context.get(getTokens().get(0).toString()) != null) returnee = context.get(getTokens().get(0).toString());
//                else if(self.get(getTokens().get(0).toString()) != null) returnee = self.get(getTokens().get(0).toString());

//                else throw new CompileException("identifier '" + getTokens().get(0).toString() + "' not defined.", this);

                if(getTokens().get(1).getType().equals(Type.INITIALIZATION))
                {
                    String name__ = getTokens().get(0).toString();
                    Token value__ = getTokens().get(1);

                    Container returnedValue__ = value__.interpret(context, self, fcontext, fself, true, args);
                    if (returnee.get(name__) != null) returnee.setField(name__, returnedValue__);
                    else self.setField(name__, returnedValue__);
                } else
                return getTokens().get(1).interpret(returnee, Container.EMPTY, fcontext, fself, true, args);
            case VALUE:
                    return getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);
            case INPUT:
                    return getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);
            case NUMBER:
                    return new Integer(Long.valueOf(toString()));
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
                Container a = getTokens().get(0).interpret(context, self, fcontext, fself, proc, args);
                Container b = getTokens().get(1).interpret(context, self, fcontext, fself, proc, args);
                return new Boolean(a.equals(b));
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
                break;
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
                cost = cost.add(new RiverCoin("0.00035").toBigInteger());
                break;
            case FOR:
                cost = cost.add(new RiverCoin("0.00025").toBigInteger());
                break;
            case CLASS_DECLARATION:
                cost = cost.add(new RiverCoin("0.05").toBigInteger());
                break;
            case METHOD_CALL:
                cost = cost.add(new RiverCoin("0.00025").toBigInteger());
                break;
            case EMPTY_DECLARATION:
                cost = cost.add(new RiverCoin("0.00025").toBigInteger());
                break;
            case FULL_DECLARATION:
                cost = cost.add(new RiverCoin("0.000175").toBigInteger());
                break;
            case WHILE:
                cost = cost.add(new RiverCoin("0.00825").toBigInteger());
                break;
            default:
                cost = cost.add(new RiverCoin("0.0002563").toBigInteger());
                break;
        }

        return cost;
    }

    @Override
    public String toString()
    {
        return value.toString();//humanReadable(0);
    }
}