package cz.snappyapps.snappyrpc.client.transporter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class SimpleApacheHttpTransporter implements Transporter {

    private HttpClient http;
    private URI uri;

    protected static final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        @Override
        public String handleResponse(HttpResponse httpResponse) throws IOException {
            final int status = httpResponse.getStatusLine().getStatusCode();
            if (status == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    return EntityUtils.toString(entity);
                }
            }
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    };

    @Override
    public void sendAndForget(CharSequence data) {
        sendAndReceive(data);
    }

    @Override
    public String sendAndReceive(CharSequence data) {
        HttpPost request = new HttpPost(uri);
        try {
            request.setEntity(new StringEntity(data.toString(), "UTF-8"));
            return http.execute(request, responseHandler);
        } catch (IOException e) {
            throw new TransportError(e);
        }
    }

    public void setHttp(HttpClient http) {
        this.http = http;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
