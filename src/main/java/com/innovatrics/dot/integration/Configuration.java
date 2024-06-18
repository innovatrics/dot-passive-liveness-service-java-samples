package com.innovatrics.dot.integration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    public final String DOT_AUTHENTICATION_TOKEN;
    public final Double LIVENESS_THRESHOLD_SCORE;
    public final String DOT_PASSIVE_LIVENESS_SERVICE_URL;
    public final String EXAMPLE_BASE64_ENCODED_IMAGE_STRING;
    public final String DOT_AUTH0_CLIENT_ID;
    public final String DOT_AUTH0_CLIENT_SECRET;
    public final String DOT_AUTH0_TOKEN_ENDPOINT;
    public final String DOT_AUTH0_AUDIENCE;

    public Configuration() throws IOException {
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(Configuration.class.getClassLoader().getResource("application.properties").getPath()));
        DOT_PASSIVE_LIVENESS_SERVICE_URL = appProps.getProperty("dot-passive-liveness-service-url");
        EXAMPLE_BASE64_ENCODED_IMAGE_STRING = appProps.getProperty("example-base64-encoded-image");
        LIVENESS_THRESHOLD_SCORE = Double.parseDouble(appProps.getProperty("threshold-score"));
        DOT_AUTHENTICATION_TOKEN = appProps.getProperty("dot-authentication-token");
        DOT_AUTH0_CLIENT_ID = appProps.getProperty("dot-auth0-client-id");
        DOT_AUTH0_CLIENT_SECRET = appProps.getProperty("dot-auth0-client-secret");
        DOT_AUTH0_TOKEN_ENDPOINT = appProps.getProperty("dot-auth0-token-endpoint");
        DOT_AUTH0_AUDIENCE = appProps.getProperty("dot-auth0-audience");
    }
}
