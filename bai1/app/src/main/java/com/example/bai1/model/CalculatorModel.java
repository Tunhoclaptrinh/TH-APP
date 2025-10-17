package com.example.bai1.model;

public class CalculatorModel {
    private double currentValue = 0;
    private double lastValue = 0;
    private String operator = "";
    private boolean isNewInput = true;
    private String expression = "";

    public String getExpression() {
        if (operator.isEmpty()) return format(currentValue);
        return format(lastValue) + " " + operator + " " + format(currentValue);
    }

    public String input(String value) {
        switch (value) {
            case "+": case "-": case "×": case "÷":
                operator = value;
                lastValue = currentValue;
                expression = format(currentValue) + " " + operator;
                isNewInput = true;
                return format(currentValue);

            case "=":
                String result = calculate();
                expression = "";
                return result;

            case "C":
                reset();
                return "0";

            default:
                if (isNewInput) {
                    currentValue = 0;
                    isNewInput = false;
                }
                String currentStr = format(currentValue);
                if (currentStr.equals("0") && !value.equals(".")) {
                    currentStr = "";
                }
                currentStr += value;
                try {
                    currentValue = Double.parseDouble(currentStr);
                } catch (NumberFormatException ignored) {}
                return currentStr;
        }
    }

    private String calculate() {
        double result = currentValue;
        switch (operator) {
            case "+": result = lastValue + currentValue; break;
            case "-": result = lastValue - currentValue; break;
            case "×": result = lastValue * currentValue; break;
            case "÷":
                if (currentValue == 0) return "Error";
                result = lastValue / currentValue;
                break;
        }
        currentValue = result;
        operator = "";
        isNewInput = true;
        return format(result);
    }

    private void reset() {
        currentValue = 0;
        lastValue = 0;
        operator = "";
        isNewInput = true;
        expression = "";
    }

    private String format(double val) {
        if (val == (long) val)
            return String.format("%d", (long) val);
        else
            return String.format("%s", val);
    }


}
