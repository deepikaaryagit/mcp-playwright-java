package mcp;

public class AIUtils {

    private static final MCPClient client = new MCPClient();

    public static String getSmartLocator(String htmlSnippet) {
        MCPToolResponse res = client.callTool("suggestLocator", new Object() {
            final String html = htmlSnippet;
        });

        if (res == null || res.output == null) {
            // fallback: if MCP not running, just return a simple locator
            return "#email";
        }

        return res.output;
    }
}
