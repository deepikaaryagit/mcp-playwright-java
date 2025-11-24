package mcp;

import com.google.gson.Gson;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.nio.charset.StandardCharsets;

public class MCPClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:9999/tools";
    private final String baseUrl;
    private final Gson gson = new Gson();

    public MCPClient() {
        // allow override via env var (useful for Jenkins)
        String envUrl = System.getenv("MCP_BASE_URL");
        this.baseUrl = (envUrl != null && !envUrl.isEmpty()) ? envUrl : DEFAULT_BASE_URL;
    }

    public MCPToolResponse callTool(String toolName, Object args) {
        String url = baseUrl + "/" + toolName;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");

            String body = gson.toJson(args);
            post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = client.execute(post)) {
                String json = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                return gson.fromJson(json, MCPToolResponse.class);
            }

        } catch (Exception e) {
            MCPToolResponse error = new MCPToolResponse();
            error.error = "Error calling MCP tool " + toolName + ": " + e.getMessage();
            return error;
        }
    }
}
