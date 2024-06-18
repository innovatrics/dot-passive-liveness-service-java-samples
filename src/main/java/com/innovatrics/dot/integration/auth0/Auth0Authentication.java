package com.innovatrics.dot.integration.auth0;

import com.innovatrics.dot.integration.Configuration;
import com.innovatrics.dot.integrationsamples.api.ApiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

import java.io.IOException;

public class Auth0Authentication {
    private static final Logger LOG = LogManager.getLogger(Auth0Authentication.class);
    public static void main(String[] args) throws IOException {
        final Configuration configuration = new Configuration();
        final String clientId = configuration.DOT_AUTH0_CLIENT_ID;
        final String clientSecret = configuration.DOT_AUTH0_CLIENT_SECRET;
        final String tokenEndpoint = configuration.DOT_AUTH0_TOKEN_ENDPOINT;
        final String audience = configuration.DOT_AUTH0_AUDIENCE;
        final ApiClient client = new ApiClient().setBasePath(configuration.DOT_PASSIVE_LIVENESS_SERVICE_URL);

        //Getting the auth0 token
        String auth0Token = "";
        try {
            URL url = new URL(tokenEndpoint);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            String credentials = clientId + ":" + clientSecret;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

            con.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "grant_type=client_credentials&audience=" + audience;
            con.getOutputStream().write(postData.getBytes());

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // example response string may looks like this:
                // {"access_token":"some access token","expires_in":604800,"token_type":"Bearer"}
                // so we get the index of substring "access token":" and add 16 because
                // that's where the actual token begins
                int startIndex = response.indexOf("\"access_token\":\"") + 16;
                int endIndex = response.indexOf("\"", startIndex);
                auth0Token = response.substring(startIndex, endIndex);
                LOG.info("OAuth token: " + auth0Token);
            } else {
                LOG.error("Error getting Auth0 token. Response code: " + responseCode);
                return;
            }
        } catch (MalformedURLException e) {
            LOG.error("Malformed URL exception.", e);
        } catch (IOException e) {
            LOG.error(e);
        }
        client.setBearerToken(auth0Token);
    }

}

