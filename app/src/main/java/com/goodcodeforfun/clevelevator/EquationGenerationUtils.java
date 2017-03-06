package com.goodcodeforfun.clevelevator;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EquationGenerationUtils {
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;
    public static final int DIFFICULTY_HARDER = 3;
    public static final int DIFFICULTY_NIGHTMARE = 4;
    public static final int DIFFICULTY_HARDEST = 5;
    private static final int DIFFICULTY_EASY_COEFFICIENT = 100000000;
    private static final int DIFFICULTY_MEDIUM_COEFFICIENT = 100000000;
    private static final int DIFFICULTY_HARD_COEFFICIENT = 10000000;
    private static final int DIFFICULTY_HARDER_COEFFICIENT = 10000000;
    private static final int DIFFICULTY_NIGHTMARE_COEFFICIENT = 10000000;
    private static final int DIFFICULTY_EASY_OPERATIONS_COUNT = 1;
    private static final int DIFFICULTY_MEDIUM_OPERATIONS_COUNT = 2;
    private static final int DIFFICULTY_HARD_OPERATIONS_COUNT = 3;
    private static final int DIFFICULTY_HARDER_OPERATIONS_COUNT = 4;
    private static final int DIFFICULTY_NIGHTMARE_OPERATIONS_COUNT = 5;
    private static final int minInteger = Integer.MIN_VALUE;
    private static final int maxInteger = Integer.MAX_VALUE;
    private static final String OPERATION_ADDITION = "+";
    private static final String OPERATION_SUBTRACTION = "-";
    private static final String OPERATION_MULTIPLICATION = "*";
    private static final String OPERATION_DIVISION = "/";
    private static final Random mRandom = new Random();

    static Equation generateEquation(@Difficulty int difficulty) {
        return new Equation(difficulty);
    }

    private static int applyCoefficient(int value, @Difficulty int difficulty) {
        int difficultyCoefficient;
        switch (difficulty) {
            case DIFFICULTY_NIGHTMARE:
                difficultyCoefficient = DIFFICULTY_NIGHTMARE_COEFFICIENT;
                break;
            case DIFFICULTY_HARDER:
                difficultyCoefficient = DIFFICULTY_HARDER_COEFFICIENT;
                break;
            case DIFFICULTY_HARD:
                difficultyCoefficient = DIFFICULTY_HARD_COEFFICIENT;
                break;
            case DIFFICULTY_MEDIUM:
                difficultyCoefficient = DIFFICULTY_MEDIUM_COEFFICIENT;
                break;
            case DIFFICULTY_EASY:
            default:
                difficultyCoefficient = DIFFICULTY_EASY_COEFFICIENT;
                break;
        }

        return value / difficultyCoefficient;
    }

    private static
    @Operation
    String getRandomOperation() {
        int min = 0;
        int max = 3;
        int randomIntFromRange = mRandom.nextInt(max + 1 - min) + min;
        String randomOperation;
        switch (randomIntFromRange) {
            case 3:
                randomOperation = OPERATION_DIVISION;
                break;
            case 2:
                randomOperation = OPERATION_MULTIPLICATION;
                break;
            case 1:
                randomOperation = OPERATION_SUBTRACTION;
                break;
            case 0:
            default:
                randomOperation = OPERATION_ADDITION;
                break;
        }
        return randomOperation;
    }

    private static int getIntResult(@Difficulty int difficulty) {
        int min = minInteger;
        int max = maxInteger;

        min = applyCoefficient(min, difficulty);
        max = applyCoefficient(max, difficulty);

        //zero is not accepted
        if (min == 0) {
            min++;
        }

        return mRandom.nextInt(max + 1 - min) + min;
    }

    private static int getRandomNumberLessThen(int max, @Difficulty int difficulty) {
        int min = applyCoefficient(minInteger, difficulty);
        if (max < min) {
            int buffer = max;
            max = min;
            min = buffer;
        }
        return mRandom.nextInt(max + 1 - min) + min;
    }

    private static int getRandomNumberMoreThen(int min, @Difficulty int difficulty) {
        int max = min + applyCoefficient(maxInteger, difficulty);
        return mRandom.nextInt(max + 1 - min) + min;
    }

    private static int getRandomNumber(@Difficulty int difficulty) {
        int min = applyCoefficient(minInteger, difficulty);
        int max = applyCoefficient(maxInteger, difficulty);
        return mRandom.nextInt(max + 1 - min) + min;
    }

    private static Expression generateExpression(int result, @Difficulty int difficulty) {
        Expression expression = new Expression();
        String operation = getRandomOperation();
        int firstOperand;
        int secondOperand;

        switch (operation) {
            case OPERATION_DIVISION:
                secondOperand = getRandomNumber(difficulty);
                firstOperand = result * secondOperand;
                break;
            case OPERATION_MULTIPLICATION:
                secondOperand = getRandomDivisor(getNumberDivisors(result), difficulty);
                firstOperand = result / secondOperand;
                break;
            case OPERATION_SUBTRACTION:
                firstOperand = getRandomNumberMoreThen(result, difficulty);
                secondOperand = firstOperand - result;
                break;
            case OPERATION_ADDITION:
            default:
                secondOperand = getRandomNumberLessThen(result, difficulty);
                firstOperand = result - secondOperand;
                break;
        }
        expression.setOperation(operation);
        expression.setFirstOperand(firstOperand);
        expression.setSecondOperand(secondOperand);
        return expression;
    }

    private static ArrayList<Integer> getNumberDivisors(int number) {
        if (number < 0) {
            number = Math.abs(number);
        }
        ArrayList<Integer> divisors = new ArrayList<>();
        divisors.add(1);
        for (int i = 2; i <= number / 2; i++) {
            if (number % i == 0) {
                divisors.add(i);
            }
        }
        return divisors;
    }

    private static int getRandomDivisor(ArrayList<Integer> divisors, @Difficulty int difficulty) {
        int min = 0;
        int max = divisors.size() - 1;
        switch (difficulty) {
            case DIFFICULTY_NIGHTMARE:
                break;
            case DIFFICULTY_HARDER:
                if (max > 3) {
                    max = 3;
                }
                break;
            case DIFFICULTY_HARD:
            case DIFFICULTY_MEDIUM:
                if (max > 2) {
                    max = 2;
                }
                break;
            case DIFFICULTY_EASY:
            default:
                if (max > 1) {
                    max = 1;
                }
                break;
        }
        int randomIntFromRange = mRandom.nextInt(max + 1 - min) + min;
        return divisors.get(randomIntFromRange);
    }

    @StringDef({OPERATION_ADDITION, OPERATION_SUBTRACTION, OPERATION_MULTIPLICATION, OPERATION_DIVISION})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Operation {
    }

    @IntDef({DIFFICULTY_EASY, DIFFICULTY_MEDIUM, DIFFICULTY_HARD, DIFFICULTY_HARDER, DIFFICULTY_NIGHTMARE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Difficulty {
    }

    static class Equation {
        private final Node root;
        private final int difficulty;

        int getResult() {
            return result;
        }

        int getFirstWrongResult() {
            return firstWrongResult;
        }

        int getSecondWrongResult() {
            return secondWrongResult;
        }

        private final int result;
        private final int firstWrongResult;
        private final int secondWrongResult;
        private final List<Node> leafs = new ArrayList<>();

        private Equation(@Difficulty int difficulty) {
            this.result = getIntResult(difficulty);
            this.difficulty = difficulty;
            Expression rootExpression = generateExpression(this.result, this.difficulty);
            this.firstWrongResult = 42;
            this.secondWrongResult = 43;
            this.root = new Node(rootExpression.getOperation(), Node.TYPE_OPERATION);
            this.root.children.add(new Node(rootExpression.getFirstOperandAsString(), Node.TYPE_OPERAND));
            this.root.children.add(new Node(rootExpression.getSecondOperandAsString(), Node.TYPE_OPERAND));
            this.leafs.add(this.root.children.get(0));
            this.leafs.add(this.root.children.get(1));
            expandEquationToMeetDifficulty();
        }

        private static void flattenBottomNode(Node node) {
            for (Node child : node.getChildren()) {
                if (child.type == Node.TYPE_OPERATION) {
                    flattenBottomNode(child);
                } else if (child.type == Node.TYPE_OPERAND) {
                    if (node.getChildren().size() > 0 &&
                            node.getChildren().get(0).type == Node.TYPE_OPERAND &&
                            node.getChildren().get(1).type == Node.TYPE_OPERAND) {
                        flopExpressionToOperand(node);
                    }
                }
            }
        }

        private static void flopExpressionToOperand(Node node) {
            if (node.getChildren().size() != 0) {
                node.setType(Node.TYPE_OPERAND);
                String operation = node.getData();
                String firstOperandString = node.getChildren().get(0).getData();
                String secondOperandString = node.getChildren().get(1).getData();
                Integer secondOperand = node.getChildren().get(1).getDataAsInteger();

                if (secondOperand != null) {
                    if (secondOperand < 0) {
                        if (OPERATION_ADDITION.equals(operation)) {
                            secondOperand = Math.abs(secondOperand);
                            operation = OPERATION_SUBTRACTION;
                        } else if (OPERATION_SUBTRACTION.equals(operation)) {
                            secondOperand = Math.abs(secondOperand);
                            operation = OPERATION_ADDITION;
                        }
                        secondOperandString = String.valueOf(secondOperand);
                    }
                }
                node.setData("(" + firstOperandString + " " + operation + " " + secondOperandString + ")");
                node.setChildren(new ArrayList<Node>());
            }
        }

        private void expandEquationToMeetDifficulty() {
            int expansionCount;
            switch (this.difficulty) {
                case DIFFICULTY_NIGHTMARE:
                    expansionCount = DIFFICULTY_NIGHTMARE_OPERATIONS_COUNT;
                    break;
                case DIFFICULTY_HARDER:
                    expansionCount = DIFFICULTY_HARDER_OPERATIONS_COUNT;
                    break;
                case DIFFICULTY_HARD:
                    expansionCount = DIFFICULTY_HARD_OPERATIONS_COUNT;
                    break;
                case DIFFICULTY_MEDIUM:
                    expansionCount = DIFFICULTY_MEDIUM_OPERATIONS_COUNT;
                    break;
                case DIFFICULTY_EASY:
                default:
                    expansionCount = DIFFICULTY_EASY_OPERATIONS_COUNT;
                    break;
            }
            for (int i = 1; i < expansionCount; i++) { //1 because root expression already has an operation
                expandRandomLeafNodeToExpression();
            }
        }

        private void expandRandomLeafNodeToExpression() {
            Node node = getRandomLeafNode();
            if (leafs.contains(node)) {
                leafs.remove(node);
            }
            Expression expression = generateExpression(node.getDataAsInteger(), this.difficulty);
            node.setData(expression.getOperation());
            node.setType(Node.TYPE_OPERATION);
            List<Node> children = new ArrayList<>();
            children.add(new Node(expression.getFirstOperandAsString(), Node.TYPE_OPERAND));
            children.add(new Node(expression.getSecondOperandAsString(), Node.TYPE_OPERAND));
            leafs.add(children.get(0));
            leafs.add(children.get(1));
            node.setChildren(children);
        }

        String printEquation() {
            StringBuilder equationStringBuilder = new StringBuilder();
            flattenEquation();
            equationStringBuilder.append(root.getData());
            equationStringBuilder.append(" = ?");
            return equationStringBuilder.toString();
        }

        private void flattenEquation() {
            while (root.type == Node.TYPE_OPERATION) {
                flattenBottomNode(root);
            }
        }

        private Node getRandomLeafNode() {
            int min = 0;
            int max = leafs.size() - 1;
            int randomNodeIndex = mRandom.nextInt(max + 1 - min) + min;
            return leafs.get(randomNodeIndex);
        }

        private static class Node {
            private static final int TYPE_OPERATION = 0;
            private static final int TYPE_OPERAND = 1;
            private List<Node> children = new ArrayList<>();
            private String data;
            private
            @NodeType
            int type;

            private Node(String data, int type) {
                this.data = data;
                this.type = type;
            }

            void setType(@NodeType int type) {
                this.type = type;
            }

            private String getData() {
                return data;
            }

            private void setData(String data) {
                this.data = data;
            }

            @Nullable
            private Integer getDataAsInteger() {
                int dataAsInt;
                try {
                    dataAsInt = Integer.parseInt(data);
                } catch (Exception e) {
                    return null;
                }
                return dataAsInt;
            }

            private List<Node> getChildren() {
                return children;
            }

            private void setChildren(List<Node> children) {
                this.children = children;
            }

            @IntDef({TYPE_OPERATION, TYPE_OPERAND})
            @Retention(RetentionPolicy.SOURCE)
            private @interface NodeType {
            }
        }
    }

    private static class Expression {
        private int firstOperand;
        private int secondOperand;
        private
        @Operation
        String operation;

        private int getFirstOperand() {
            return firstOperand;
        }

        private void setFirstOperand(int firstOperand) {
            this.firstOperand = firstOperand;
        }

        private String getFirstOperandAsString() {
            return getOperandAsString(getFirstOperand());
        }

        private String getSecondOperandAsString() {
            return getOperandAsString(getSecondOperand());
        }

        private String getOperandAsString(int operand) {
            return String.valueOf(operand);
        }

        private int getSecondOperand() {
            return secondOperand;
        }

        private void setSecondOperand(int secondOperand) {
            this.secondOperand = secondOperand;
        }

        private
        @Operation
        String getOperation() {
            return operation;
        }

        private void setOperation(@Operation String operation) {
            this.operation = operation;
        }
    }
}
