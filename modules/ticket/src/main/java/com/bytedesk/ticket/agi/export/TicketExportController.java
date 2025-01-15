package com.bytedesk.ticket.agi.export;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tickets/export")
public class TicketExportController {

    @Autowired
    private TicketExportService exportService;

    @PostMapping
    public ResponseEntity<String> startExport(@RequestBody ExportConfig config) {
        String exportId = exportService.startAsyncExport(config);
        return ResponseEntity.ok(exportId);
    }

    @GetMapping("/{exportId}/progress")
    public ResponseEntity<ExportProgress> getProgress(@PathVariable String exportId) {
        return ResponseEntity.ok(exportService.getExportProgress(exportId));
    }

    @GetMapping("/{exportId}/download")
    public ResponseEntity<byte[]> download(@PathVariable String exportId) {
        byte[] data = exportService.getExportResult(exportId);
        if (data == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=tickets.zip")
            .body(data);
    }

    @DeleteMapping("/{exportId}")
    public ResponseEntity<Void> cancelExport(@PathVariable String exportId) {
        exportService.cancelExport(exportId);
        return ResponseEntity.ok().build();
    }
} 