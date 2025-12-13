package com.bytedesk.ai.utils.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class MathTools {

    @Tool(description = "Add two numbers")
    public double add(
            @ToolParam(description = "First addend") double a,
            @ToolParam(description = "Second addend") double b) {
        return a + b;
    }

    @Tool(description = "Subtract the second number from the first")
    public double subtract(
            @ToolParam(description = "Minuend") double a,
            @ToolParam(description = "Subtrahend") double b) {
        return a - b;
    }

    @Tool(description = "Multiply two numbers")
    public double multiply(
            @ToolParam(description = "First factor") double a,
            @ToolParam(description = "Second factor") double b) {
        return a * b;
    }

    @Tool(description = "Divide the first number by the second")
    public double divide(
            @ToolParam(description = "Dividend") double a,
            @ToolParam(description = "Divisor") double b) {
        if (b == 0.0d) {
            throw new IllegalArgumentException("Divisor cannot be zero");
        }
        return a / b;
    }

    @Tool(description = "Raise the first number to the power of the second")
    public double pow(
            @ToolParam(description = "Base value") double base,
            @ToolParam(description = "Exponent") double exponent) {
        return Math.pow(base, exponent);
    }

    @Tool(description = "Calculate the square root of a number")
    public double sqrt(@ToolParam(description = "Value to square root") double value) {
        if (value < 0.0d) {
            throw new IllegalArgumentException("Cannot take the square root of a negative number");
        }
        return Math.sqrt(value);
    }

    @Tool(description = "Compute the average of the provided numbers")
    public double average(@ToolParam(description = "Numbers to average") double[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("Provide at least one number to average");
        }
        double sum = 0.0d;
        for (double number : numbers) {
            sum += number;
        }
        return sum / numbers.length;
    }

    @Tool(description = "Calculate the percentage value")
    public double percentage(
            @ToolParam(description = "Base value") double base,
            @ToolParam(description = "Percentage to apply (e.g. 15 for 15%)") double percent) {
        return base * (percent / 100.0d);
    }
}
