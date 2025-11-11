package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RPNCalcTest {

    /**
     * Преобразование простых выражений в ОПН
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
     * Преобразование выражений с приоритетами операций
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
     * Преобразование выражений со скобками
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
     * Преобразование выражений с десятичными числами
     */
    @Test
    void testInfixToRPN_DecimalNumbers() {
        assertAll(
                () -> assertEquals(List.of("2.5", "3.1", "+"),
                        RPNCalc.infixToRPN("2.5 + 3.1")),
                () -> assertEquals(List.of("2.5", "3", "*", "1.5", "+"),
                        RPNCalc.infixToRPN("2.5 * 3 + 1.5"))
        );
    }

    /**
     * Выброс исключения при несогласованных скобках
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
     * Выброс исключения при недопустимых символах
     */
    @Test
    void testInfixToRPN_InvalidCharacters() {
        assertThrows(IllegalArgumentException.class,
                () -> RPNCalc.infixToRPN("2 + a"));
    }

    /**
     * Вычисление простых операций
     * @param rpn
     * @param expected
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
     * Вычисление сложных выражений
     * @param rpn
     * @param expected
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
     * Вычисление выражений с десятичными числами
     */
    @Test
    void testEvaluateRPN_DecimalNumbers() {
        assertAll(
                () -> assertEquals(5.6,
                        RPNCalc.evaluateRPN(List.of("2.5", "3.1", "+")), 1e-9),
                () -> assertEquals(9.0,
                        RPNCalc.evaluateRPN(List.of("2.5", "3", "*", "1.5", "+")), 1e-9)
        );
    }

    /**
     * Выброс исключения при делении на ноль
     */
    @Test
    void testEvaluateRPN_DivisionByZero() {
        List<String> expression = List.of("5", "0", "/");
        assertThrows(ArithmeticException.class,
                () -> RPNCalc.evaluateRPN(expression));
    }

    /**
     * Выброс исключения при недостатке операндов
     */
    @Test
    void testEvaluateRPN_InsufficientOperands() {
        List<String> expression = List.of("2", "+");
        assertThrows(IllegalArgumentException.class,
                () -> RPNCalc.evaluateRPN(expression));
    }

    /**
     * Выброс исключения при некорректном выражении
     */
    @Test
    void testEvaluateRPN_InvalidExpression() {
        List<String> expression = List.of("2", "3", "4");
        assertThrows(IllegalArgumentException.class,
                () -> RPNCalc.evaluateRPN(expression));
    }

    /**
     * Интеграционный тест: преобразование и вычисление
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