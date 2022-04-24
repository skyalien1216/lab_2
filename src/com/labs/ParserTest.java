package com.labs;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void evaluateX() {
        String expressionText = "-x-1-3*(-1 - (5 * 1 - 4)) * -x";
        Parser parser = new Parser();
        parser.setExpressionText(expressionText);
        parser.setVariableValues('x', 1);
        assertEquals(-8,  parser.evaluate());
    }

    @Test
    void evaluateXY() {
        String expressionText = "x - y * 2 + y * ( x * y)";
        Parser parser = new Parser();
        parser.setExpressionText(expressionText);
        parser.setVariableValues('x', 5);
        parser.setVariableValues('y', 2);
        assertEquals(21,  parser.evaluate());
    }

    @Test
    void evaluateSimpleExpression1() {
        String expressionText = "-(2*3*2-5) * -3";
        Parser parser = new Parser();
        parser.setExpressionText(expressionText);
        assertEquals(21,  parser.evaluate());
    }

    @Test
    void evaluateSimpleFuncExpression() {
        String expressionText = "abs(3*-3)";
        Parser parser = new Parser();
        parser.setExpressionText(expressionText);
        assertEquals(9,  parser.evaluate());
    }

    @Test
    void evaluateFuncExpression() {
        String expressionText = "cos(-x * (sin(p/2) - 1)) * sqrt(abs(-5)*5 + 25 - x)";
        Parser parser = new Parser();
        parser.setExpressionText(expressionText);
        parser.setVariableValues('x', 1);
        assertEquals(7,  parser.evaluate());
    }

    @Test
    void addLexemes() {
        String expressionText = "-1 * 3/(12 + 3)*e";
        Parser parser = new Parser();
        parser.setExpressionText(expressionText);
        List<Parser.Lexeme> test = new ArrayList<Parser.Lexeme>();
        test.add(new Parser.Lexeme(Parser.LexemeType.SUBTRACTION, '-'));
        test.add(new Parser.Lexeme(Parser.LexemeType.NUMBER, '1'));
        test.add(new Parser.Lexeme(Parser.LexemeType.MULTIPLICATION, '*'));
        test.add(new Parser.Lexeme(Parser.LexemeType.NUMBER, '3'));
        test.add(new Parser.Lexeme(Parser.LexemeType.DIVISION, '/'));
        test.add(new Parser.Lexeme(Parser.LexemeType.LEFT_BRACKET, '('));
        test.add(new Parser.Lexeme(Parser.LexemeType.NUMBER, "12"));
        test.add(new Parser.Lexeme(Parser.LexemeType.ADDITION, '+'));
        test.add(new Parser.Lexeme(Parser.LexemeType.NUMBER, '3'));
        test.add(new Parser.Lexeme(Parser.LexemeType.RIGHT_BRACKET, ')'));
        test.add(new Parser.Lexeme(Parser.LexemeType.MULTIPLICATION, '*'));
        test.add(new Parser.Lexeme(Parser.LexemeType.VARIABLE, 'e'));
        test.add(new Parser.Lexeme(Parser.LexemeType.EOF, ""));
        List<Parser.Lexeme> lexemes =  parser.addLexemes(expressionText);
        assertEquals(test.size(), lexemes.size());
        int n = lexemes.size();
        for (int i =0; i < n; i++)
        {
            assertEquals(test.get(i).type, lexemes.get(i).type);
            assertEquals(test.get(i).value, lexemes.get(i).value);
        }
    }
}