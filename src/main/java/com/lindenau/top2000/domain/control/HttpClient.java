package com.lindenau.top2000.domain.control;

import com.lindenau.top2000.config.control.ConfigLoader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

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
            System.out.println(response.getStatusLine().toString());
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
}
