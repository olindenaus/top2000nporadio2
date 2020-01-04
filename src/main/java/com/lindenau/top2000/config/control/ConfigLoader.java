package com.lindenau.top2000.config.control;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private String excelPath;
    private String clientId;
    private String clientSecret;
    private String oauthToken;
    private String userId;
    InputStream inputStream;

    public String getExcelPath() {
        return excelPath;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public String getUserId() {
        return userId;
    }

    public ConfigLoader() throws IOException {
        try {
            Properties prop = new Properties();
            String configFileName = "config.properties";

            inputStream = getClass().getClassLoader().getResourceAsStream(configFileName);
            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("config file '" + configFileName + "'  not found in the classpath");
            }

            excelPath = prop.getProperty("excel_path");
            clientId = prop.getProperty("client_id");
            clientSecret = prop.getProperty("client_secret");
            oauthToken = prop.getProperty("oauth_token");
            userId = prop.getProperty("user_id");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }
}
