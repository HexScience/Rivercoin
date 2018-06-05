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

import java.text.ParseException;
import java.util.*;

public class ParsedProgram
{
    private Token   tokens;

    public ParsedProgram(LexedProgram program) throws ParseException
    {
        List<LexicalToken> tokens = new ArrayList<>();
        for(LexicalToken token : program.getTokens())
            if(token != null && !token.toString().isEmpty()) {
                tokens.add(token);
//                System.out.println(token.getType() + " " + token);
            }

        this.tokens = new Token(Token.Type.ROOT);
        parse(tokens, this.tokens, false);
    }

    private LexicalToken getNext(List<LexicalToken> tokens, int offset, String errmsg) throws ParseException
    {
        if(tokens.size() > 0)
        {
            LexicalToken currentToken = tokens.get(0);
            tokens.remove(0);
            return currentToken;
        } else throw new ParseException(errmsg, offset);
    }

    private Token getNextToken(List<LexicalToken> tokens, int offset, String errmsg) throws ParseException
    {
        try{
            if(tokens.size() > 0)
            {
                Token falsy = new Token(null);
                parse(tokens, falsy, true);
                return falsy.getTokens().get(0);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        throw new ParseException(errmsg, offset);
    }

    private Token getNextInBraces(List<LexicalToken> tokens, int offset, String errmsg) throws ParseException
    {
        return null;
    }

    private Token getNextInParenthesis(List<LexicalToken> tokens, int offset, String errmsg) throws ParseException
    {
        if(tokens.size() >= 2 && tokens.get(0).getType() == LexicalToken.Type.PARENTHESIS_OPEN)
        {
            LexicalToken parenthesis_open   = getNext(tokens, offset, errmsg);
            Token parenthesis               = new Token(Token.Type.PARENTHESIS);

            while(tokens.size() > 0)
            {
                LexicalToken currentToken = tokens.get(0);
                switch (currentToken.getType())
                {
                    case PARENTHESIS_CLOSED:
                            return parenthesis;
                    case PARENTHESIS_OPEN:
                            parenthesis.add(getNextInParenthesis(tokens, offset, errmsg));
                        break;
                    default:
                            parse(tokens, parenthesis, true);
                        break;
                }
            }
            return null;
        } else throw new ParseException(errmsg, offset);
    }

    private void parseClass(List<LexicalToken> tokens, Token rootm, LexicalToken currentToken) throws ParseException
    {
    }

    private void parseFunction(List<LexicalToken> tokens, Token rootm, LexicalToken currentToken) throws ParseException
    {
        LexicalToken    name            = getNext(tokens, currentToken.getOffset(), "function must have a name.");
        Token           parenthesis     = getNextInParenthesis(tokens, currentToken.getOffset(), "function must have arguments in parenthesis.");
        LexicalToken    symbol          = getNext(tokens, currentToken.getOffset(), "function must have a return symbol ':'.");
        LexicalToken    returnType      = getNext(tokens, currentToken.getOffset(), "function must have a return type.");

        Token           body            = null;
        try{
            body            = getNextInBraces(tokens, currentToken.getOffset(), "function must have a body");
        }
        catch (ParseException e)
        {
        }

        /** unimplemented method **/
        if(body == null)
        {
            Token       function        = new Token(Token.Type.METHOD_EMPTY_DECLARATION);

            function.add(name);
            function.add(parenthesis);
            function.add(symbol);
            function.add(returnType);
            rootm.add(function);
        } else {
            Token       function        = new Token(Token.Type.METHOD_DECLARATION);

            function.add(name);
            function.add(parenthesis);
            function.add(symbol);
            function.add(returnType);
            function.add(body);
            rootm.add(function);
        }
    }

    private void parseKeyword(List<LexicalToken> tokens, Token root) throws ParseException
    {
        LexicalToken currentToken = tokens.get(0);
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
            case "if":
                break;
            case "for":
                break;
            case "while":
                break;
        }
    }

    private void parseMath(List<LexicalToken> tokens, Token root, LexicalToken a) throws ParseException
    {
        List<LexicalToken> math_tokens = new ArrayList<>();
        math_tokens.add(a);

        while(tokens.size() > 0 && tokens.get(0).isMathOp())
        {
            LexicalToken operator   = getNext(tokens, a.getOffset(), "invalid math operation");
//            Token        operation= operator.asToken();
            LexicalToken b          = getNext(tokens, a.getOffset(), "invalid math operation a " + operator + " null");//getNextToken(tokens, a.getOffset(), "invalid math operation a " + operation.toString() + " null");
            math_tokens.add(operator);
            math_tokens.add(b);

//            LexicalToken c        = getNext(tokens, a.getOffset(), "invalid math operation b " + operation.toString() + " null");//getNextToken(tokens, a.getOffset(), "invalid math operation a " + operation.toString() + " null");
        }

        Stack<LexicalToken> output = new Stack<>();
        Stack<LexicalToken> stack  = new Stack<>();

        Stack<LexicalToken> result = new Stack<>();

        for (int i = 0; i<math_tokens.size(); ++i)
        {
            LexicalToken c = math_tokens.get(i);

            // If the scanned character is an operand, add it to output.
            if (!c.isMathOp())
                output.push(c);
            else
            {
                while (!stack.isEmpty() && prec(c.toString().charAt(0)) <= prec(stack.peek().toString().charAt(0)))
                    output.push(stack.pop());
                stack.push(c);
            }
        }

        while   (stack.size() > 0) output.push(stack.pop());
        while   (output.size() > 0) stack.push(stack.pop());

        while   (stack.size() > 2)
        {
            result.push(new Tok)
            System.out.println(output.pop());
        }

        System.out.println(output);
    }

    static int prec(char ch)
    {
        switch (ch)
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

        return -1;
    }

    private Token mathParse(HashMap<String, LexicalToken> map, String operation, String op, Token.Type type)
    {
        return new Token(Token.Type.INPUT).add(map.get(operation));
    }

    private void parse(List<LexicalToken> tokens, Token root, boolean onlyOnce) throws ParseException
    {
        while(tokens.size() > 0)
        {
            LexicalToken currentToken = tokens.get(0);

            switch (currentToken.getType())
            {
                case PARENTHESIS_OPEN:
                        root.add(getNextInParenthesis(tokens, currentToken.getOffset(), ""));
                    break;
                case NUMBER:
                        LexicalToken A = getNext(tokens, currentToken.getOffset(), "");
                        if(tokens.size() > 0 && tokens.get(0).isMathOp())
                        {
                            parseMath(tokens, root, A);
                        } else
                            root.add(new Token(Token.Type.INPUT).add(A));
                    break;
                case STRING:
                        root.add(new Token(Token.Type.INPUT).add(getNext(tokens, currentToken.getOffset(), "")));
                    break;
                case SYMBOL:
                    break;
                case KEYWORD:
                        parseKeyword(tokens, root);
                    break;
                case IDENTIFIER:
                        LexicalToken type       = getNext(tokens, currentToken.getOffset(), "");
                        LexicalToken name       = getNext(tokens, currentToken.getOffset(), "");
                        LexicalToken equals     = getNext(tokens, currentToken.getOffset(), "declarations should end with ';' or be initialized with '='");

                        Token declaration       = null;
                        if(equals.getType()     == LexicalToken.Type.EQUALS)
                        {
                            declaration         = new Token(Token.Type.FULL_DECLARATION).add(type).add(name);
                            parse               (tokens, declaration, true);
                            getNext             (tokens, currentToken.getOffset(), "");
                        } else if(equals.getType() == LexicalToken.Type.END)
                        {
                            declaration         = new Token(Token.Type.EMPTY_DECLARATION).add(type).add(name);
                            getNext             (tokens,     currentToken.getOffset(), "");
                        } else
                            throw new ParseException("Unidentified token. '" + equals + "'", currentToken.getOffset());

                        root.add(declaration);
                    break;
                default:
                    throw new ParseException("Token unidentified. '" + currentToken.toString() + "'", currentToken.getOffset());
            }

            if(onlyOnce) return;
        }
    }

    public Token getTokens()
    {
        return tokens;
    }
}
