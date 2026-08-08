package com.bytedesk.ai.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntentRecognitionResult {

    private Boolean shouldCallTool;

    private List<String> matchedTools;

    private String reason;
}