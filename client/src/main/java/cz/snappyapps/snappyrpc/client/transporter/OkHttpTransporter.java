package cz.snappyapps.snappyrpc.client.transporter;

import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;

public class OkHttpTransporter extends SimpleConnectionTransporter {

    private final OkHttpClient http;

    public OkHttpTransporter(OkHttpClient http) {
        this.http = http;
    }

    @Override
    protected HttpURLConnection getConnection() throws IOException {
        return http.open(uri.toURL());
    }
}
