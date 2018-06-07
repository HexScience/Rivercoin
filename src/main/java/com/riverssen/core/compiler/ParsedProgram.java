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

package com.riverssen.core.compiler;

import java.util.*;

public class ParsedProgram
{
    private Token   tokens;

    public ParsedProgram(LexedProgram program) throws ParseException
    {
        List<Token> tokens = new ArrayList<>();
        for(Token token : program.getTokens())
            if(token != null && !token.toString().isEmpty()) {
                tokens.add(token);
                token.getType();
            }

        this.tokens = new Token(Token.Type.ROOT);
        parse(tokens, this.tokens, false);
    }

    private Token getNext(List<Token> tokens, Token offset, String errmsg) throws ParseException
    {
        if(tokens.size() > 0)
        {
            Token currentToken = tokens.get(0);
            tokens.remove(0);
            return currentToken;
        } else throw new ParseException(errmsg, offset);
    }

    private Token getNextToken(List<Token> tokens, Token offset, String errmsg) throws ParseException
    {
        try{
            if(tokens.size() > 0)
            {
                Token falsy = new Token(Token.Type.ROOT);
                parse(tokens, falsy, true);
                return falsy.getTokens().get(0);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        throw new ParseException(errmsg, offset);
    }

    private Token   getNextValid(List<Token> tokens)
    {
        for(Token token : tokens) if(token.getType() != Token.Type.END) return token;
        return null;
    }

    private boolean nextOfType(List<Token> tokens, Token.Type type)
    {
        Token t = getNextValid(tokens);
        if(t != null)
        return t.getType() == type;
        return false;
    }

    private void skipToValid(List<Token> tokens)
    {
        while (tokens.size() > 0 && tokens.get(0).getType() == Token.Type.END)
            tokens.remove(0);
    }

    private Token getNextInBraces(List<Token> tokens, Token offset, String errmsg) throws ParseException
    {
        if(tokens.size() >= 2 && nextOfType(tokens, Token.Type.BRACES_OPEN))
        {
            skipToValid(tokens);
            Token parenthesis               = new Token(Token.Type.BRACES);
            parse(tokens, parenthesis, true);

            return parenthesis.getTokens().get(0);
        } else return null;
    }

    private Token getNextInParenthesis(List<Token> tokens, Token offset, String errmsg) throws ParseException
    {
        if(tokens.size() >= 2 && nextOfType(tokens, Token.Type.PARENTHESIS_OPEN))
        {
            skipToValid(tokens);
            Token parenthesis               = new Token(Token.Type.PARENTHESIS);
            parse(tokens, parenthesis, true);

            return parenthesis.getTokens().get(0);
        } else throw new ParseException(errmsg, offset);
    }

    private void parseClass(List<Token> tokens, Token rootm, Token currentToken) throws ParseException
    {
        Token    name               = getNext               (tokens, currentToken, "function must have a name.");

        Token extension = new Token(Token.Type.EXTEND);

        if(tokens.size() > 0 && tokens.get(0).toString().equals("extends"))
        {
            extension.add(getNext(tokens, currentToken, "class must extend a class type."));

            if (tokens.size() > 0 && tokens.get(0).toString().charAt(0) == ',') tokens.remove(0);

            while (tokens.size() > 1 && tokens.get(1).toString().charAt(0) == ',')
            {
                extension.add(getNext(tokens, currentToken, "class must extend a class type."));

                if (tokens.size() > 0 && tokens.get(0).toString().charAt(0) == ',') tokens.remove(0);
            }
        }

        Token clasz = new Token(Token.Type.EMPTY_CLASS_DECLARATION);

        clasz.add(name);

        Token body = getNextInBraces(tokens, currentToken, "");
        if(body != null) {
            clasz.add(body);
            clasz.setType(Token.Type.CLASS_DECLARATION);
        }
        if(extension.getTokens().size() > 0)
            clasz.add(extension);
    }

    private void parseFunction(List<Token> tokens, Token rootm, Token currentToken) throws ParseException
    {
        Token    name               = getNext               (tokens, currentToken, "function must have a name.");
        Token    parenthesis        = getNextInParenthesis  (tokens, currentToken, "function must have arguments in parenthesis.");
        Token    symbol             = getNext               (tokens, currentToken, "function must have a return symbol ':'.");
        Token    returnType         = getNext               (tokens, currentToken, "function must have a return type.");
        Token    body               = getNextInBraces       (tokens, currentToken, "function must have a body");

        /** unimplemented method **/
        if(body == null)
        {
            Token       function        = new Token(Token.Type.METHOD_EMPTY_DECLARATION);

            function.add(name);
            function.add(returnType);
            function.add(parenthesis);
            rootm.add(function);
        } else {
            Token       function        = new Token(Token.Type.METHOD_DECLARATION);

            function.add(name);
            function.add(returnType);
            function.add(parenthesis);
            function.add(body);
            rootm.add(function);
        }
    }

    private void parseNewKeyword(List<Token> tokens, Token rootm, Token currentToken) throws ParseException
    {
        Token neW = new Token(Token.Type.NEW);
        parse(tokens, neW, true);

        if(neW.getTokens().isEmpty()) throw new ParseException("new must be followed by a function", neW);

        if(neW.getTokens().get(0).getType() != Token.Type.METHOD_CALL)  throw new ParseException("new must be followed by a function " + neW, neW);

        neW.getTokens().get(0).setType(Token.Type.NEW);

        rootm.add(neW.getTokens().get(0));
    }

    private void parseKeyword(List<Token> tokens, Token root) throws ParseException
    {
        Token currentToken = tokens.get(0);
        tokens.remove(0);

        switch (currentToken.toString())
        {
            case "function":
                    parseFunction   (tokens, root, currentToken);
                break;
            case "fun":
                    parseFunction   (tokens, root, currentToken);
                break;
            case "class":
                    parseClass      (tokens, root, currentToken);
                break;
            case "new":
                    parseNewKeyword (tokens, root, currentToken);
            case "if":
                break;
            case "for":
                break;
            case "while":
                break;
        }
    }

    private Token strip(Token token)
    {
        if(token.getTokens().size() == 1 && token.getType() == Token.Type.PARENTHESIS)
            return token.getTokens().get(0);
        else return token;
    }

    private void parseMath(List<Token> tokens, Token root, Token a) throws ParseException
    {
        List<Token> math_tokens = new ArrayList<>();
        math_tokens.add(a);

        while(tokens.size() > 0 && tokens.get(0).isMathOp())
        {
            Token operator   = getNext(tokens, a, "invalid math operation");
            Token b          = getNextMath(tokens, a, "invalid math operation a " + operator + " null");//getNextToken(tokens, a.getOffset(), "invalid math operation a " + operation.toString() + " null");
            math_tokens.add(operator);
            math_tokens.add(b);
        }

        Stack<Token> output = new Stack<>();
        Stack<Token> stack  = new Stack<>();

        for (int i = 0; i<math_tokens.size(); ++i)
        {
            Token c = math_tokens.get(i);

            // If the scanned character is an operand, add it to output.
            if (!c.isMathOp())
                output.push(c);
            else
            {
                while (!stack.isEmpty() && prec(c) <= prec(stack.peek()))
                {
                    output.push(stack.pop().add(output.pop()).add(output.pop()));
                }
                stack.push(c);
            }
        }

        while   (stack.size() > 0)  output.push(stack.pop().add(output.pop()).add(output.pop()));

        root.add(output.pop());
    }

    private Token getOperator(Stack<Token> stack)
    {
        for(int i = 0; i < stack.size(); i ++)
        {
            if(stack.get(i).isMathOp()) {
                Token t = stack.get(i);
                stack.remove(i);

                return t;
            }
        }

        return null;
    }

    private Token getOperand(Stack<Token> stack)
    {
        int i = 0;
        while(i < stack.size())
        {
            if(!stack.get(i).isMathOp()) {
                Token t = stack.get(i);
                stack.remove(i);

                return t;
            }

            i ++;
        }

        return null;
    }

    private Token postFixToAST(Stack<Token> stack)
    {
        Token token = new Token(Token.Type.MATH_OP);

        while(stack.size() > 1)
        {
            Token a = getOperand(stack);
            Token b = getOperand(stack);
            Token o = getOperator(stack);
            o.add(a).add(b);

            if(o.toString().charAt(0) == '+')
                o.setType(Token.Type.ADDITION);
            else if(o.toString().charAt(0) == '-')
                o.setType(Token.Type.SUBTRACTION);
            else if(o.toString().charAt(0) == '*')
                o.setType(Token.Type.MULTIPLICATION);
            else if(o.toString().charAt(0) == '/')
                o.setType(Token.Type.SUBDIVISION);
            else if(o.toString().charAt(0) == '^')
                o.setType(Token.Type.POW);
            else if(o.toString().charAt(0) == '%')
                o.setType(Token.Type.MOD);

            stack.insertElementAt(o, 0);

            System.out.println("----------");
            for(int i = 0; i < stack.size(); i ++) System.out.println(stack.get(i).humanReadable(0));
        }

        return stack.pop();
    }

    private Token getNextMath(List<Token> tokens, Token offset, String errmsg) throws ParseException
    {
        try{
            if(tokens.size() > 0)
            {
                Token falsy = new Token(Token.Type.ROOT);
                parse(tokens, falsy, true, false);
                return falsy.getTokens().get(0);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        throw new ParseException(errmsg, offset);
    }

    static int prec(Token ch)
    {
        switch (ch.getType())
        {
            case MATH_OP:
                switch (ch.toString().charAt(0))
                {
                    case '+':
                    case '-':
                        return 1;

                    case '*':
                    case '/':
                        return 2;

                    case '%':
                        return 3;
                    case '^':
                        return 4;
                }
            case PARENTHESIS:
                return 2;
        }

        return -1;
    }

    private Token mathParse(HashMap<String, Token> map, String operation, String op, Token.Type type)
    {
        return new Token(Token.Type.INPUT).add(map.get(operation));
    }

    private void parse(List<Token> tokens, Token root, boolean onlyOnce) throws ParseException
    {
        this.parse(tokens, root, onlyOnce, true);
    }

    private void parse(List<Token> tokens, Token root, boolean onlyOnce, boolean parseMath) throws ParseException
    {
        this.parse(tokens, root, onlyOnce, parseMath, false);
    }

    private void parse(List<Token> tokens, Token root, boolean onlyOnce, boolean parseMath, boolean inParenthesis) throws ParseException
    {
        this.parse(tokens, root, onlyOnce, parseMath, inParenthesis, false, false);
    }

    private void parse(List<Token> tokens, Token root, boolean onlyOnce, boolean parseMath, boolean inParenthesis, boolean inBraces) throws ParseException
    {
        this.parse(tokens, root, onlyOnce, parseMath, inParenthesis, inBraces, false);
    }

    private void parse(List<Token> tokens, Token root, boolean onlyOnce, boolean parseMath, boolean inParenthesis, boolean inBraces, boolean inBrackets) throws ParseException
    {
        this.parse(tokens, root, onlyOnce, parseMath, inParenthesis, inBraces, inBrackets, false);
    }

    private void parse(List<Token> tokens, Token root, boolean onlyOnce, boolean parseMath, boolean inParenthesis, boolean inBraces, boolean inBrackets, boolean newLineAware) throws ParseException
    {
        while(tokens.size() > 0)
        {
            Token currentToken = tokens.get(0);
//            System.out.println(currentToken + " " + currentToken.getType());
            switch (currentToken.getType())
            {
                case PARENTHESIS_OPEN:
                        Token parenthesis = new Token(Token.Type.PARENTHESIS);
                        getNext(tokens, currentToken, "");
                        parse(tokens, parenthesis, false, true, true);
                        if(nextOfType(tokens, Token.Type.MATH_OP))
                            parseMath(tokens, root, parenthesis);
                        else
                            root.add(parenthesis);
                    break;
                case PARENTHESIS_CLOSED:
                    if(inParenthesis) {
                                tokens.remove(0);
                            return;
                        }
                        else throw new ParseException("Redundant ')'", currentToken);
                case BRACES_OPEN:
                    Token braces = new Token(Token.Type.BRACES);
                    getNext(tokens, currentToken, "");
                    parse(tokens, braces, false, true, false, true);

                    root.add(braces);
                    break;
                case BRACES_CLOSED:
                    if(inBraces) {
                        tokens.remove(0);
                        return;
                    }
                    else throw new ParseException("Redundant '}'", currentToken);
                case NUMBER:
                        Token A = getNext(tokens, currentToken, "");
                        if(tokens.size() > 0 && tokens.get(0).isMathOp() && parseMath)
                        {
                            parseMath(tokens, root, A);
                        } else
                            root.add(A);
                    break;
                case STRING:
                        root.add(new Token(Token.Type.INPUT).add(getNext(tokens, currentToken, "")));
                    break;
                case SYMBOL:
                        if(currentToken.toString().charAt(0) == ',') { tokens.remove(0); break; }
                        else
                            throw new ParseException("Token unidentified. '" + currentToken.toString() + "(" + currentToken.getType() + ")'", currentToken);
                case END:
                        tokens.remove(0);
                        if(newLineAware) return;
                    break;
                case KEYWORD:
                        parseKeyword(tokens, root);
                    break;
                case IDENTIFIER:
                        Token type       = getNext(tokens, currentToken, "");

                        if (getNextValid(tokens).isMathOp() && parseMath)
                        {
                            skipToValid(tokens);
                            parseMath(tokens, root, type);
                            break;
                        }
                        else if(tokens.size() > 2 && tokens.get(0).getType() == Token.Type.IDENTIFIER)
                        {
                            Token name              = getNext(tokens, currentToken, "");

                            Token declaration       = null;
                            if(nextOfType(tokens, Token.Type.EQUALS))
                            {
                                skipToValid(tokens);
                                tokens.remove(0);
                                declaration         = new Token(Token.Type.FULL_DECLARATION).add(type).add(name);
                                Token value         = new Token(Token.Type.VALUE);
                                parse               (tokens, value, false, true, false, false, false, true);
                                declaration.add(value);
                            } else
                                declaration         = new Token(Token.Type.EMPTY_DECLARATION).add(type).add(name);

                            root.add(declaration);
                            /** must be on same line to be valid, so nextValid isn't used here **/
                        } else if(tokens.size() > 0 && tokens.get(0).getType() == Token.Type.PARENTHESIS_OPEN)
                        {
                            root.add(new Token(Token.Type.METHOD_CALL).add(type).add(getNextInParenthesis(tokens, currentToken, "Method calls should end with parenthesis.")));
                        } else if(nextOfType(tokens, Token.Type.EQUALS))
                        {
                            skipToValid(tokens);
                            Token initialization    = new Token(Token.Type.INITIALIZATION);
                            skipToValid(tokens);
                            Token value             = new Token(Token.Type.VALUE);
                            parse               (tokens, value, false, true, false, false, false, true);
                            initialization.add(value);

                            root.add(initialization);
                        } else root.add(type);
                    break;
                default:
                    throw new ParseException("Token unidentified. '" + currentToken.toString() + "(" + currentToken.getType() + ")'", currentToken);
            }

            if(onlyOnce) return;
        }
    }

    public Token getTokens()
    {
        return tokens;
    }
}
