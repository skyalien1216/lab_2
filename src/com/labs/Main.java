package com.labs;

// анализатор выражений, если выражение может существовать -> запросить доп данные\вывести результат иначе ругаемся!

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Main {

//    expr : addsubtr  EOF
//
//    addsubtr: multdiv ( ( '+' | '-' ) multdiv )
//
//    multdiv : factor ( ( '*' | '/' ) factor )
//
//    factor : NUMBER | '(' expr ')'

    public static void main(String[] args) {
/*        //String expressionText = "-x - 34 - 3 * (55 + 5 * (3 - 2)) * 2";
        String expressionText = "-x-1+3*-(-1 - (5 * 1 - 4)) * -x";
        Parser parser = new Parser();
        parser.setExpressionText(expressionText);
        //List<Parser.Lexeme> l =  parser.addLexemes(expressionText);
        //for (Parser.Lexeme i : l) { System.out.println(i);}
        System.out.println(parser.evaluate());*/

        String expressionText = "a(abs(-1))";
        Parser parser = new Parser();
        parser.setExpressionText(expressionText);
        System.out.println(parser.evaluate());
    }
}

