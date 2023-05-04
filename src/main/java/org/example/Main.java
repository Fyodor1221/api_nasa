package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;

public class Main {
    public static String REMOTE_SERVICE_URL = "https://api.nasa.gov/planetary/apod?api_key=aKEpyALNU3sAh1HJbUUaEXeUpDeFjL7DWrUC0cKf";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();
        HttpGet request = new HttpGet(REMOTE_SERVICE_URL);
        CloseableHttpResponse response = httpClient.execute(request);

        ServerResponse serverResponse = mapper.readValue(response.getEntity().getContent(), new TypeReference<ServerResponse>() {});
        System.out.println(serverResponse);

        HttpGet requestUrl = new HttpGet(serverResponse.getUrl());
        CloseableHttpResponse responseUrl = httpClient.execute(requestUrl);

        System.out.println(responseUrl.getEntity().getContentType());
        byte[] bytes = responseUrl.getEntity().getContent().readAllBytes();

        String[] str = serverResponse.getUrl().split("/");
        String fileName = str[str.length - 1];

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(fileName))) {
            bos.write(bytes, 0, bytes.length);
            System.out.println("Download complete");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}