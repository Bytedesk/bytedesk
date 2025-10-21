package com.bytedesk.call.xml_curl;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CallcenterOptions {
    // ODBC DSN for callcenter (recommended)
    private String odbcDsn; // e.g., pgsql://hostaddr=127.0.0.1 dbname=cc user=cc pass=cc

    // Client address for cc client
    private String clientAddress; // e.g., 127.0.0.1

    // Queue CDR log directory
    private String cdrLogDir; // e.g., /var/log/freeswitch

    // Debug level (0/1)
    private String debug; // e.g., "0" or "1"

    // Whether to create DB tables automatically (if supported)
    private String createTables; // e.g., "true" or "false"
}
