package com.bytedesk.core.workflow.result;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {
    private final ResultService resultService;

    @PostMapping
    public ResponseEntity<Result> createResult(@RequestBody Result result) {
        return ResponseEntity.ok(resultService.createResult(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> updateResult(
        @PathVariable String id,
        @RequestBody Result result
    ) {
        return ResponseEntity.ok(resultService.updateResult(id, result));
    }

    @GetMapping("/bot/{botId}")
    public ResponseEntity<List<Result>> getBotResults(@PathVariable String botId) {
        return ResponseEntity.ok(resultService.getBotResults(botId));
    }

    @DeleteMapping("/bot/{botId}")
    public ResponseEntity<Void> deleteResults(@PathVariable String botId) {
        resultService.deleteResults(botId);
        return ResponseEntity.ok().build();
    }
}
