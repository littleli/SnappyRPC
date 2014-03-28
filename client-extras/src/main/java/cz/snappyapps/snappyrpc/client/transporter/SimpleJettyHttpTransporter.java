package cz.snappyapps.snappyrpc.client.transporter;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SimpleJettyHttpTransporter implements Transporter {

    private HttpClient http;
    private URI uri;

    public void setHttpClient(HttpClient http) {
        this.http = http;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    protected Request createRequest() {
        return http.newRequest(uri).method(HttpMethod.POST).timeout(10, TimeUnit.SECONDS).idleTimeout(10, TimeUnit.SECONDS);
    }

    protected void modifyRequest(Request request) {
    }

    @Override
    public void sendAndForget(CharSequence data) {
        sendAndReceive(data);
    }

    public String sendAndReceive(CharSequence buffer) {
        final Request request = createRequest().content(new StringContentProvider(buffer.toString()), "application/json-snappyrpc");
        modifyRequest(request);
        try {
            ContentResponse contentResponse = request.send();
            return contentResponse.getContentAsString();
        } catch (InterruptedException e) {
            throw new TransportError(e);
        } catch (TimeoutException e) {
            throw new TransportError(e);
        } catch (ExecutionException e) {
            throw new TransportError(e);
        }
    }
}
