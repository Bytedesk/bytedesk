package com.bytedesk.ai.tool.test;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class CalculatorTools {

    @McpTool(name = "add", description = "Add two double-precision numbers and return their sum. " +
            "Use this tool whenever you need to compute the result of adding two numeric values together.")
    public double add(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a + b;
    }

    @McpTool(name = "subtract", description = "Subtract the second double-precision number from the first and return the difference. " +
            "Use this tool whenever you need to compute the result of subtracting one numeric value from another.")
    public double subtract(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a - b;
    }

    @McpTool(name = "multiply", description = "Multiply two double-precision numbers and return their product. " +
            "Use this tool whenever you need to compute the result of multiplying two numeric values together.")
    public double multiply(
            @McpToolParam(description = "First number", required = true) double a,
            @McpToolParam(description = "Second number", required = true) double b) {
        return a * b;
    }

    @McpTool(name = "divide", description = "Divide the dividend by the divisor and return the quotient as a double-precision number. " +
            "Throws an IllegalArgumentException if the divisor is zero. " +
            "Use this tool whenever you need to compute the result of dividing one numeric value by another.")
    public double divide(
            @McpToolParam(description = "Dividend", required = true) double dividend,
            @McpToolParam(description = "Divisor", required = true) double divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Division by zero");
        }
        return dividend / divisor;
    }

    // @McpTool(name = "calculate-expression",
    //          description = "Calculate a complex mathematical expression")
    // public CallToolResult calculateExpression(
    //         CallToolRequest request,
    //         McpSyncRequestContext context) {

    //     Map<String, Object> args = request.arguments();
    //     String expression = (String) args.get("expression");

    //     // Use convenient logging method
    //     context.info("Calculating: " + expression);

    //     try {
    //         double result = evaluateExpression(expression);
    //         return CallToolResult.builder()
    //             .addTextContent("Result: " + result)
    //             .build();
    //     } catch (Exception e) {
    //         return CallToolResult.builder()
    //             .isError(true)
    //             .addTextContent("Error: " + e.getMessage())
    //             .build();
    //     }
    // }
}