
package com.labs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Parser {

    private String expressionText;
    private List<String> funcValues;
    private Map<Character,Double> variableValues ;

    public Parser() {
        this.funcValues = Arrays.asList("sin", "sinh", "cos", "cosh",
                "tan", "tanh", "ctg", "sec", "cosec", "abs", "ln", "lg", "sqrt");
        this.variableValues = new HashMap<Character, Double>();
        variableValues.put('p',Math.PI);
        variableValues.put('e', Math.E);
    }

    /**
     * If such a variable exists then set its value
     * @param c - name of the variable, 1 letter
     * @param num - value of the variable
     */
    public void setVariableValues(Character c, double num){
        if(variableValues.containsKey(c))
            variableValues.put(c, num);
    }

    public String getExpressionText() {
        return expressionText;
    }

    /**
     * Sets the expression, nulls the variables
     * @param expressionText - mathematical expression for parsing
     */
    public void setExpressionText(String expressionText) {
        variableValues.put('x', null);
        variableValues.put('y', null);
        variableValues.put('z', null);
        this.expressionText = expressionText;
    }

    /**
     * Evaluates set expression
     * @return value of the expression after the evaluation
     */
    public double evaluate(){
        List<Lexeme> lexemes = addLexemes(expressionText);
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        return expr(lexemeBuffer);
    }

    public enum LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET,
        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION,
        FUNCTION,  NUMBER, VARIABLE, EOF
    }

    static class Lexeme {
        LexemeType type;
        String value;

        public Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    private class LexemeBuffer {
        private int pos;

        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        /**
         * Moves the position forward, returns an element
         * @return element on previous position
         */
        public Lexeme next() {
            return lexemes.get(pos++);
        }

        /**
         * Moves the position back, returns an element
         * @return element on previous position
         */
        public Lexeme back() {
            return lexemes.get(--pos);
        }

        /**
         * Returns current position
         * @return position
         */
        public int getPos() {
            return pos;
        }

        /**
         * Changes lexeme at a certain position
         * @param pos index of change
         * @param lex the lexeme to change to
         */
        public void changeLexemeAt(int pos, Lexeme lex) {lexemes.set(pos, lex);}
    }

    //|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

    /**
     * Parses set expression
     * @param expText the expression to parse
     * @return list of lexemes
     */
    public List<Lexeme> addLexemes(String expText) {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos< expText.length()) {
            char c = expText.charAt(pos);
            switch (c) {
                case '(':
                    lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                    pos++;
                    continue;
                case '+':
                    lexemes.add(new Lexeme(LexemeType.ADDITION, c));
                    pos++;
                    continue;
                case '-':
                    lexemes.add(new Lexeme(LexemeType.SUBTRACTION, c));
                    pos++;
                    continue;
                case '*':
                    lexemes.add(new Lexeme(LexemeType.MULTIPLICATION, c));
                    pos++;
                    continue;
                case '/':
                    lexemes.add(new Lexeme(LexemeType.DIVISION, c));
                    pos++;
                    continue;
                default:
                    if (c <= '9' && c >= '0') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while (c <= '9' && c >= '0');
                        lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                    } else {
                        if(variableValues.containsKey(c))
                        {
                            if (variableValues.get(c) == null){
                                System.out.println("Enter the value of your variable " + c);
                                Scanner in = new Scanner(System.in);
                                int variable = in.nextInt();
                                variableValues.put( c, (double) variable);
                            }
                            lexemes.add(new Lexeme(LexemeType.VARIABLE, c));
                        }
                        else if (c != ' ') {
                            String funcName = "" + c;
                            while(pos < expText.length())
                            {
                                pos++;
                                char newC = expText.charAt(pos);
                                if(newC == '(' && !funcName.equals("" + c))
                                {
                                    lexemes.add(new Lexeme(LexemeType.FUNCTION, funcName));
                                    pos--;
                                    break;
                                }
                                else if (newC == '(' && funcName.equals("" + c))
                                    throw new RuntimeException("Unexpected character at position " + pos);
                                else
                                {
                                    if (Character.isAlphabetic(newC))
                                        funcName += newC;
                                    else
                                        throw new RuntimeException("Unexpected character: " + c);
                                }
                            }
                        }
                        pos++;
                    }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }

    /**
     * Evaluates expression
     * @param lexemes buffer of lexemes
     * @return value of expression
     */
    private double expr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return addsubtr(lexemes);
        }
    }

    /**
     * Addition/Subtraction
     * @param lexemes lexeme buffer
     * @return value of subtraction/addition
     */
    private double addsubtr(LexemeBuffer lexemes) {
        double value = multdiv(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case ADDITION:
                    value += multdiv(lexemes);
                    break;
                case SUBTRACTION:
                    value -= multdiv(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    /**
     * Multiplication/Division
     * @param lexemes lexeme buffer
     * @return value of multiplication/division
     */
    private double multdiv(LexemeBuffer lexemes) {
        double value = factor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case MULTIPLICATION:
                    value *= factor(lexemes);
                    break;
                case DIVISION:
                    value /= factor(lexemes);
                    break;
                case EOF:
                case RIGHT_BRACKET:
                case ADDITION:
                case SUBTRACTION:
                    lexemes.back();
                    return value;
                default:
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
            }
        }
    }

    /**
     * Calculates the function value
     * @param func name of the function
     * @param num argument
     * @return value of the function
     */
    private double processFunction(String func, double num) {
        switch (func) {
            case "sin":
                return Math.sin(num);
            case "sinh":
                return Math.sinh(num);
            case "cos":
                return Math.cos(num);
            case "cosh":
                return Math.cosh(num);
            case "tan":
                return Math.tan(num);
            case "tanh":
                return Math.tanh(num);
            case "ctg":
                return 1/Math.tan(num);
            case "sec":
                return 1/Math.cos(num);
            case "cosec":
                return 1/Math.sin(num);
            case "abs":
                return Math.abs(num);
            case "ln":
                return Math.log(num);
            case "lg":
                return Math.log10(num);
            case "sqrt":
                return Math.sqrt(num);
            default:
                throw new RuntimeException("function '" + func + "' is not defined");
        }
    }

    /**
     * Evaluates numbers, variables, functions, brackets, unary minuses
     * @param lexemes lexeme buffer
     * @return value of the evaluation
     */
    private double factor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case NUMBER:
             return Integer.parseInt(lexeme.value);
            case VARIABLE:
                if (variableValues.containsKey(lexeme.value.charAt(0)))
                    return variableValues.get(lexeme.value.charAt(0));
                throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos() + " VARIABLE DOESN'T EXIST");
            case LEFT_BRACKET:
                double value = addsubtr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
                }
                return value;
            case FUNCTION:
                String f = lexeme.value;
                return processFunction(f, factor(lexemes));
            case SUBTRACTION:
                return -factor(lexemes);
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
        }
    }

}