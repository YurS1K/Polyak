package org.example;

import java.util.*;

public class RPNCalc {

    private static final Map<Character, Integer> OPERATOR_PRECEDENCE = new HashMap<>();
    private static final char LEFT_PARENTHESIS = '(';
    private static final char RIGHT_PARENTHESIS = ')';
    private static final char DECIMAL_POINT = '.';
    private static final int MIN_OPERANDS_FOR_BINARY_OPERATOR = 2;
    private static final int EXPECTED_STACK_SIZE_AFTER_EVALUATION = 1;

    static {
        OPERATOR_PRECEDENCE.put('+', 1);
        OPERATOR_PRECEDENCE.put('-', 1);
        OPERATOR_PRECEDENCE.put('*', 2);
        OPERATOR_PRECEDENCE.put('/', 2);
        OPERATOR_PRECEDENCE.put('^', 3);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Example: (2 + 3) * 4 - 5 / 2");
            System.out.println("Type 'exit' to quit");

            while (true) {
                System.out.print("\nEnter expression: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit")) {
                    break;
                }

                if (input.isEmpty()) {
                    continue;
                }

                try {
                    List<String> rpn = infixToRPN(input);
                    System.out.println("RPN: " + String.join(" ", rpn));

                    double result = evaluateRPN(rpn);
                    System.out.println("Result: " + result);

                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Convert infix notation to Reverse Polish Notation
     */
    public static List<String> infixToRPN(String expression) {
        List<String> output = new ArrayList<>();
        Stack<Character> operators = new Stack<>();

        expression = expression.replaceAll("\\s+", "");

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == DECIMAL_POINT) {
                StringBuilder number = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == DECIMAL_POINT)) {
                    number.append(expression.charAt(i));
                    i++;
                }
                output.add(number.toString());
                continue;
            }
            else if (c == LEFT_PARENTHESIS) {
                operators.push(c);
            }
            else if (c == RIGHT_PARENTHESIS) {
                while (!operators.isEmpty() && operators.peek() != LEFT_PARENTHESIS) {
                    output.add(String.valueOf(operators.pop()));
                }
                if (operators.isEmpty() || operators.peek() != LEFT_PARENTHESIS) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                operators.pop();
            }
            else if (isOperator(c)) {
                while (!operators.isEmpty() &&
                        operators.peek() != LEFT_PARENTHESIS &&
                        hasHigherPrecedence(operators.peek(), c)) {
                    output.add(String.valueOf(operators.pop()));
                }
                operators.push(c);
            }
            else {
                throw new IllegalArgumentException("Invalid character: " + c);
            }

            i++;
        }

        while (!operators.isEmpty()) {
            if (operators.peek() == LEFT_PARENTHESIS) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(String.valueOf(operators.pop()));
        }

        return output;
    }

    /**
     * Evaluate RPN expression
     */
    public static double evaluateRPN(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                if (stack.size() < MIN_OPERANDS_FOR_BINARY_OPERATOR) {
                    throw new IllegalArgumentException("Not enough operands for operation " + token);
                }

                double b = stack.pop();
                double a = stack.pop();
                double result = performOperation(token.charAt(0), a, b);
                stack.push(result);
            }
        }

        if (stack.size() != EXPECTED_STACK_SIZE_AFTER_EVALUATION) {
            throw new IllegalArgumentException("Invalid expression");
        }

        return stack.pop();
    }

    /**
     * Perform mathematical operation
     */
    private static double performOperation(char operator, double a, double b) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            case '^':
                return Math.pow(a, b);
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    /**
     * Check if character is an operator
     */
    private static boolean isOperator(char c) {
        return OPERATOR_PRECEDENCE.containsKey(c);
    }

    /**
     * Check if string is a number
     */
    private static boolean isNumber(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Check operator precedence
     */
    private static boolean hasHigherPrecedence(char op1, char op2) {
        return OPERATOR_PRECEDENCE.getOrDefault(op1, 0) >= OPERATOR_PRECEDENCE.getOrDefault(op2, 0);
    }
}