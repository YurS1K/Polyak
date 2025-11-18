package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RPNCalcTest {

    private static final String TWO_POINT_FIVE = "2.5";
    private static final String THREE_POINT_ONE = "3.1";
    private static final String ONE_POINT_FIVE = "1.5";
    /**
     * Simple expressions conversion to RPN
     */
    @Test
    void testInfixToRPN_SimpleExpressions() {
        assertAll(
                () -> assertEquals(List.of("2", "3", "+"), RPNCalc.infixToRPN("2+3")),
                () -> assertEquals(List.of("5", "2", "-"), RPNCalc.infixToRPN("5-2")),
                () -> assertEquals(List.of("3", "4", "*"), RPNCalc.infixToRPN("3*4")),
                () -> assertEquals(List.of("8", "2", "/"), RPNCalc.infixToRPN("8/2")),
                () -> assertEquals(List.of("2", "3", "^"), RPNCalc.infixToRPN("2^3"))
        );
    }

    /**
     * Expressions with operator precedence conversion
     */
    @Test
    void testInfixToRPN_OperatorPrecedence() {
        assertAll(
                () -> assertEquals(List.of("2", "3", "*", "4", "+"),
                        RPNCalc.infixToRPN("2*3+4")),
                () -> assertEquals(List.of("2", "3", "4", "*", "+"),
                        RPNCalc.infixToRPN("2+3*4")),
                () -> assertEquals(List.of("2", "3", "+", "4", "*"),
                        RPNCalc.infixToRPN("(2+3)*4")),
                () -> assertEquals(List.of("2", "3", "4", "^", "*"),
                        RPNCalc.infixToRPN("2*3^4"))
        );
    }

    /**
     * Expressions with parentheses conversion
     */
    @Test
    void testInfixToRPN_WithParentheses() {
        assertAll(
                () -> assertEquals(List.of("2", "3", "+", "4", "*"),
                        RPNCalc.infixToRPN("(2+3)*4")),
                () -> assertEquals(List.of("2", "3", "4", "*", "+"),
                        RPNCalc.infixToRPN("2+(3*4)")),
                () -> assertEquals(List.of("2", "3", "4", "*", "5", "+", "+"),
                        RPNCalc.infixToRPN("2+((3*4)+5)"))
        );
    }

    /**
     * Expressions with decimal numbers conversion
     */
    @Test
    void testInfixToRPN_DecimalNumbers() {
        assertAll(
                () -> assertEquals(List.of(TWO_POINT_FIVE, THREE_POINT_ONE, "+"),
                        RPNCalc.infixToRPN("2.5 + 3.1")),
                () -> assertEquals(List.of(TWO_POINT_FIVE, "3", "*", ONE_POINT_FIVE, "+"),
                        RPNCalc.infixToRPN("2.5 * 3 + 1.5"))
        );
    }

    /**
     * Exception thrown for mismatched parentheses
     */
    @Test
    void testInfixToRPN_MismatchedParentheses() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> RPNCalc.infixToRPN("(2+3")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> RPNCalc.infixToRPN("2+3)"))
        );
    }

    /**
     * Exception thrown for invalid characters
     */
    @Test
    void testInfixToRPN_InvalidCharacters() {
        assertThrows(IllegalArgumentException.class,
                () -> RPNCalc.infixToRPN("2 + a"));
    }

    /**
     * Simple operations evaluation
     * @param rpn RPN expression
     * @param expected expected result
     */
    @ParameterizedTest
    @CsvSource({
            "2 3 +, 5.0",
            "5 2 -, 3.0",
            "3 4 *, 12.0",
            "8 2 /, 4.0",
            "2 3 ^, 8.0"
    })
    void testEvaluateRPN_SimpleOperations(String rpn, double expected) {
        List<String> expression = List.of(rpn.split(" "));
        assertEquals(expected, RPNCalc.evaluateRPN(expression), 1e-9);
    }

    /**
     * Complex expressions evaluation
     * @param rpn RPN expression
     * @param expected expected result
     */
    @ParameterizedTest
    @CsvSource({
            "'2 3 * 4 +', 10.0",
            "'2 3 4 * +', 14.0",
            "'2 3 + 4 *', 20.0",
            "'2 3 4 ^ *', 162.0",
            "'5 1 2 + 4 * + 3 -', 14.0"
    })
    void testEvaluateRPN_ComplexExpressions(String rpn, double expected) {
        List<String> expression = List.of(rpn.split(" "));
        assertEquals(expected, RPNCalc.evaluateRPN(expression), 1e-9);
    }

    /**
     * Decimal numbers evaluation
     */
    @Test
    void testEvaluateRPN_DecimalNumbers() {
        assertAll(
                () -> assertEquals(5.6,
                        RPNCalc.evaluateRPN(List.of(TWO_POINT_FIVE, THREE_POINT_ONE, "+")), 1e-9),
                () -> assertEquals(9.0,
                        RPNCalc.evaluateRPN(List.of(TWO_POINT_FIVE, "3", "*", ONE_POINT_FIVE, "+")), 1e-9)
        );
    }

    /**
     * Exception thrown for division by zero
     */
    @Test
    void testEvaluateRPN_DivisionByZero() {
        List<String> expression = List.of("5", "0", "/");
        assertThrows(ArithmeticException.class,
                () -> RPNCalc.evaluateRPN(expression));
    }

    /**
     * Exception thrown for insufficient operands
     */
    @Test
    void testEvaluateRPN_InsufficientOperands() {
        List<String> expression = List.of("2", "+");
        assertThrows(IllegalArgumentException.class,
                () -> RPNCalc.evaluateRPN(expression));
    }

    /**
     * Exception thrown for invalid expression
     */
    @Test
    void testEvaluateRPN_InvalidExpression() {
        List<String> expression = List.of("2", "3", "и");
        assertThrows(IllegalArgumentException.class,
                () -> RPNCalc.evaluateRPN(expression));
    }

    /**
     * Full integration test
     * @param infix infix expression
     * @param expected expected result
     */
    @ParameterizedTest
    @CsvSource({
            "2+3, 5.0",
            "2*3+4, 10.0",
            "(2+3)*4, 20.0",
            "2^3, 8.0",
            "2.5*3+1.5, 9.0"
    })
    void testFullIntegration(String infix, double expected) {
        List<String> rpn = RPNCalc.infixToRPN(infix);
        double result = RPNCalc.evaluateRPN(rpn);
        assertEquals(expected, result, 1e-9);
    }
}