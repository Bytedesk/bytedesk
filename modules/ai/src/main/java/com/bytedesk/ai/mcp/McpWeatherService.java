package com.bytedesk.ai.mcp;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class McpWeatherService {
    
    @Tool(description = "Get current temperature for a location")
    public String getTemperature(
            @ToolParam(description = "City name", required = true) String city) {
        return String.format("Current temperature in %s: 22°C", city);
    }
}
