package com.bytedesk.ai.mcp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bytedesk.ai.mcp.tools")
public class BytedeskMcpToolProperties {

    /**
     * Whether to expose Bytedesk Spring AI @Tool beans to the MCP server.
     */
    private boolean enabled = true;

    /**
     * Keep the first MCP exposure conservative; write tools can be enabled later by policy.
     */
    private boolean readOnly = true;

    /**
     * Bean class package prefixes that are allowed to be scanned for @Tool methods.
     */
    private List<String> includePackages = new ArrayList<>(List.of("com.bytedesk"));

        /**
         * Explicit tool name allowlist. Defaults to the first-phase external tools only.
         * Empty means all matched read-only tools are allowed.
         */
        private List<String> allowNames = new ArrayList<>(List.of(
            "bytedeskKnowledgeSearch",
            "bytedeskTicketCreate"));

    /**
     * Explicit tool name denylist, evaluated before pattern based exposure.
     */
    private List<String> denyNames = new ArrayList<>();

    /**
     * Explicit write tool allowlist when readOnly is true.
     */
    private List<String> writeAllowNames = new ArrayList<>();

    /**
     * Tool method names allowed when readOnly is true.
     */
    private String readOnlyIncludePattern = ".*(Query|Search|Find|Get|List|Count).*";

    /**
     * Tool method names that must not be exposed by the default MCP registry.
     */
    private String excludePattern = ".*(Create|Update|Delete|Remove|Cancel|Change|Optimize|Reset|Score|Set|Send).*";
}
