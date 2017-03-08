package com.goodcodeforfun.clevelevator;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class EquationGenerationUtils {
    public static final int FORCED_DIFFICULTY_NONE = 0;
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;
    public static final int DIFFICULTY_HARDER = 4;
    public static final int DIFFICULTY_NIGHTMARE = 5;
    public static final int DIFFICULTY_HARDEST = 6;
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
    private static final int INVERTED_WRONG_RESULT = 0;
    private static final int CLOSE_BY_LENGTH_WRONG_RESULT = 1;
    private static final int RANDOM_WRONG_RESULT = 2;
    private static final int RANDOM_MORE_THAN_WRONG_RESULT = 3;
    private static final int RANDOM_LESS_THAN_WRONG_RESULT = 4;
    private static final int RANDOM_DIVISOR_WRONG_RESULT = 5;

    static Equation generateEquation(int difficulty) {
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

    private static int getRandomSingleMultiplier() {
        return mRandom.nextInt(10);
    }

    private static int getRandomMoreThanWrongResult(int result, @Difficulty int difficulty) {
        return getRandomNumberLessThen(result, difficulty);
    }

    private static int getRandomLessThanWrongResult(int result, @Difficulty int difficulty) {
        return getRandomNumberLessThen(result, difficulty);
    }

    private static int getRandomDivisorWrongResult(int result, @Difficulty int difficulty) {
        return getRandomDivisor(getNumberDivisors(result), difficulty);
    }

    private static int getRandomWrongResult(@Difficulty int difficulty) {
        return getRandomIntResult(difficulty);
    }

    private static int getRandomWrongResultCloseByLength(int result) {
        int wrongResult = 1;
        int resultLength = String.valueOf(result).length();
        for (int i = 1; i < resultLength; i++) {
            wrongResult *= 10;
        }
        wrongResult *= getRandomSingleMultiplier();
        boolean isPositive = mRandom.nextBoolean();
        if (isPositive) {
            wrongResult += result;
        } else {
            wrongResult -= result;
        }

        return wrongResult;
    }

    private static int getInvertedWrongResult(int result) {
        //we cannot inverse zero
        if (result == 0) {
            boolean isPositive = mRandom.nextBoolean();
            if (isPositive) {
                result++;
            } else {
                result--;
            }
        }
        if (result > 0) {
            result = 0 - result;
        } else {
            result = Math.abs(result);
        }
        return result;
    }

    private static int getRandomWrongIntResultByMethod(int result, @Difficulty int difficulty, int method) {
        int wrongResult;
        switch (method) {
            case RANDOM_DIVISOR_WRONG_RESULT:
                wrongResult = getRandomDivisorWrongResult(result, difficulty);
                break;
            case CLOSE_BY_LENGTH_WRONG_RESULT:
                wrongResult = getRandomWrongResultCloseByLength(result);
                break;
            case RANDOM_WRONG_RESULT:
                wrongResult = getRandomWrongResult(difficulty);
                break;
            case RANDOM_MORE_THAN_WRONG_RESULT:
                wrongResult = getRandomMoreThanWrongResult(result, difficulty);
                break;
            case RANDOM_LESS_THAN_WRONG_RESULT:
                wrongResult = getRandomLessThanWrongResult(result, difficulty);
                break;
            case INVERTED_WRONG_RESULT:
            default:
                wrongResult = getInvertedWrongResult(result);
                break;
        }
        return wrongResult;
    }

    private static int getRandomWrongResultMethod(List<Integer> wrongResultMethods) {
        int min = 0;
        int max = wrongResultMethods.size() - 1;
        int randomMethodIndex = mRandom.nextInt(max + 1 - min) + min;
        return wrongResultMethods.get(randomMethodIndex);
    }

    private static int[] getRandomWrongIntResults(int result, @Difficulty int difficulty) {
        int[] wrongResults = new int[2];
        List<Integer> wrongResultMethods = new ArrayList<>(
                Arrays.asList(INVERTED_WRONG_RESULT,
                        CLOSE_BY_LENGTH_WRONG_RESULT,
                        RANDOM_WRONG_RESULT,
                        RANDOM_MORE_THAN_WRONG_RESULT,
                        RANDOM_LESS_THAN_WRONG_RESULT,
                        RANDOM_DIVISOR_WRONG_RESULT)
        );

        wrongResults[0] = getRandomWrongIntResultByMethod(result, difficulty, getRandomWrongResultMethod(wrongResultMethods));
        wrongResults[1] = getRandomWrongIntResultByMethod(result, difficulty, getRandomWrongResultMethod(wrongResultMethods));
        while (wrongResults[0] == result) {
            wrongResults[0] = getRandomWrongIntResultByMethod(result, difficulty, getRandomWrongResultMethod(wrongResultMethods));
        }
        while (wrongResults[1] == result || wrongResults[0] == wrongResults[1]) {
            wrongResults[1] = getRandomWrongIntResultByMethod(result, difficulty, getRandomWrongResultMethod(wrongResultMethods));
        }
        return wrongResults;
    }

    private static int getRandomIntResult(@Difficulty int difficulty) {
        int min = minInteger;
        int max = maxInteger;

        min = applyCoefficient(min, difficulty);
        max = applyCoefficient(max, difficulty);

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
                if (max > 4) {
                    max = 4;
                }
                break;
            case DIFFICULTY_HARD:
            case DIFFICULTY_MEDIUM:
                if (max > 3) {
                    max = 3;
                }
                break;
            case DIFFICULTY_EASY:
            default:
                if (max > 2) {
                    max = 2;
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
        private final int result;
        private final int firstWrongResult;
        private final int secondWrongResult;
        private final List<Node> leafs = new ArrayList<>();
        private Equation(@Difficulty int difficulty) {
            this.result = getRandomIntResult(difficulty);
            this.difficulty = difficulty;
            Expression rootExpression = generateExpression(this.result, this.difficulty);
            int[] wrongResults = getRandomWrongIntResults(this.result, difficulty);
            this.firstWrongResult = wrongResults[0];
            this.secondWrongResult = wrongResults[1];
            this.root = new Node(rootExpression.getOperation(), Node.TYPE_OPERATION, null);
            this.root.children.add(new Node(rootExpression.getFirstOperandAsString(), Node.TYPE_OPERAND, this.root));
            this.root.children.add(new Node(rootExpression.getSecondOperandAsString(), Node.TYPE_OPERAND, this.root));
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
                StringBuilder floppedData = new StringBuilder();
                floppedData.append(firstOperandString);
                floppedData.append(" ");
                floppedData.append(operation);
                floppedData.append(" ");
                floppedData.append(secondOperandString);
                if (node.getParent() != null) {
                    floppedData.append(")");
                    floppedData.reverse();
                    floppedData.append("(");
                    floppedData.reverse();
                }
                node.setData(floppedData.toString());
                node.setChildren(new ArrayList<Node>());
            }
        }

        int getResult() {
            return result;
        }

        int getFirstWrongResult() {
            return firstWrongResult;
        }

        int getSecondWrongResult() {
            return secondWrongResult;
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
            children.add(new Node(expression.getFirstOperandAsString(), Node.TYPE_OPERAND, node));
            children.add(new Node(expression.getSecondOperandAsString(), Node.TYPE_OPERAND, node));
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
            private Node parent;

            private Node(String data, int type, @Nullable Node parent) {
                this.data = data;
                this.type = type;
                this.parent = parent;
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

            public Node getParent() {
                return parent;
            }

            public void setParent(Node parent) {
                this.parent = parent;
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