package com.lindenau.top2000.domain.control;

import com.lindenau.top2000.config.control.ConfigLoader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.xddf.usermodel.PresetPattern;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

public class HttpClient {

    ConfigLoader configLoader = new ConfigLoader();

    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public HttpClient() throws IOException {
    }

    public String sendGet(String address) {
        HttpGet request = new HttpGet(address);
        request.addHeader(HttpHeaders.ACCEPT, "application/json");
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + configLoader.getOauthToken());

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String sendPost(String uri) {
        HttpPost post = new HttpPost(uri);
        post.addHeader(HttpHeaders.ACCEPT, "application/json");
        post.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        post.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + configLoader.getOauthToken());

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            System.out.println("Problem for uri: " + uri + ", : " + e);
        }
        return "";
    }

    public String sendDelete(URI uri, List<String> tracks) throws UnsupportedEncodingException {
        HttpDeleteWithBody delete = new HttpDeleteWithBody(uri);
        delete.addHeader(HttpHeaders.ACCEPT, "application/json");
        delete.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        delete.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + configLoader.getOauthToken());
        StringBuilder jsonBody = new StringBuilder(getJsonTracksStart());
        for(String trackId : tracks) {
            jsonBody.append(getJsonUri(trackId));
        }
        jsonBody.append(getJsonTracksEnd());
        delete.setEntity(new StringEntity(jsonBody.toString()));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(delete)) {
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            System.out.println("Problem for uri: " + uri.toString() + ", : " + e);
        }
        return "";
    }

    private String getJsonTracksStart() {
        return "{\n  \"tracks\": [\n";
    }

    private String getJsonTracksEnd() {
        return "  ]\n}";
    }

    private String getJsonUri(String songId) {
        return "    {\n" +
                "      \"uri\": \"spotify:track:" + songId + "\"\n" +
                "    }\n"; //"    },\n"
    }
}
