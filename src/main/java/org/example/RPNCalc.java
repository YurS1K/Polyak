package org.example;

import java.util.*;

public class RPNCalc {

    private static final Map<Character, Integer> OPERATOR_PRECEDENCE = new HashMap<>();
    static {
        OPERATOR_PRECEDENCE.put('+', 1);
        OPERATOR_PRECEDENCE.put('-', 1);
        OPERATOR_PRECEDENCE.put('*', 2);
        OPERATOR_PRECEDENCE.put('/', 2);
        OPERATOR_PRECEDENCE.put('^', 3);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Калькулятор с обратной польской нотацией");
        System.out.println("Поддерживаемые операции: +, -, *, /, ^, скобки ()");
        System.out.println("Пример: (2 + 3) * 4 - 5 / 2");
        System.out.println("Введите 'exit' для выхода");

        while (true) {
            System.out.print("\nВведите выражение: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break;
            }

            if (input.isEmpty()) {
                continue;
            }

            try {
                List<String> rpn = infixToRPN(input);
                System.out.println("ОПН: " + String.join(" ", rpn));

                double result = evaluateRPN(rpn);
                System.out.println("Результат: " + result);

            } catch (Exception e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Преобразование инфиксной записи в обратную польскую нотацию
     */
    public static List<String> infixToRPN(String expression) {
        List<String> output = new ArrayList<>();
        Stack<Character> operators = new Stack<>();

        expression = expression.replaceAll("\\s+", "");

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < expression.length() &&
                        (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    number.append(expression.charAt(i));
                    i++;
                }
                output.add(number.toString());
                continue;
            }
            else if (c == '(') {
                operators.push(c);
            }
            else if (c == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.add(String.valueOf(operators.pop()));
                }
                if (operators.isEmpty() || operators.peek() != '(') {
                    throw new IllegalArgumentException("Несогласованные скобки");
                }
                operators.pop();
            }
            else if (isOperator(c)) {
                while (!operators.isEmpty() &&
                        operators.peek() != '(' &&
                        hasHigherPrecedence(operators.peek(), c)) {
                    output.add(String.valueOf(operators.pop()));
                }
                operators.push(c);
            }
            else {
                throw new IllegalArgumentException("Недопустимый символ: " + c);
            }

            i++;
        }

        while (!operators.isEmpty()) {
            if (operators.peek() == '(') {
                throw new IllegalArgumentException("Несогласованные скобки");
            }
            output.add(String.valueOf(operators.pop()));
        }

        return output;
    }

    /**
     * Вычисление выражения в ОПН
     */
    public static double evaluateRPN(List<String> rpn) {
        Stack<Double> stack = new Stack<>();

        for (String s : rpn) {
            if (isNumber(s)) {
                stack.push(Double.parseDouble(s));
            } else if (isOperator(s.charAt(0))) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Недостаточно операндов для операции " + s);
                }

                double b = stack.pop();
                double a = stack.pop();
                double result = performOperation(s.charAt(0), a, b);
                stack.push(result);
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Некорректное выражение");
        }

        return stack.pop();
    }

    /**
     * Выполнение математической операции
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
                    throw new ArithmeticException("Деление на ноль");
                }
                return a / b;
            case '^':
                return Math.pow(a, b);
            default:
                throw new IllegalArgumentException("Неизвестный оператор: " + operator);
        }
    }

    /**
     * Проверка, является ли символ оператором
     */
    private static boolean isOperator(char c) {
        return OPERATOR_PRECEDENCE.containsKey(c);
    }

    /**
     * Проверка, является ли строка числом
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
     * Проверка приоритета операторов
     */
    private static boolean hasHigherPrecedence(char op1, char op2) {
        return OPERATOR_PRECEDENCE.getOrDefault(op1, 0) >= OPERATOR_PRECEDENCE.getOrDefault(op2, 0);
    }
}