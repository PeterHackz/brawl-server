package com.brawl.logic.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HTTPSConnection {

    public static byte[] downloadFile(String search) throws IOException, URISyntaxException {

        URI uri = new URI(search);
        URL url = uri.toURL();

        String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        con.setRequestProperty("User-Agent", USER_AGENT);

        con.setRequestMethod("GET");

        con.connect();

        return con.getInputStream().readAllBytes();
    }

}
