package cz.snappyapps.snappyrpc.client.transporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;

import static cz.snappyapps.snappyrpc.client.transporter.Util.close;
import static cz.snappyapps.snappyrpc.client.transporter.Util.readResponse;

public class SimpleConnectionTransporter implements Transporter {

    protected URI uri;

    public void setUri(URI uri) {
        this.uri = uri;
    }

    protected HttpURLConnection getConnection() throws IOException {
        return (HttpURLConnection) uri.toURL().openConnection();
    }

    @Override
    public void sendAndForget(CharSequence data) {
        sendAndReceive(data);
    }

    protected void modifyRequest(HttpURLConnection connection) {
    }

    @Override
    public String sendAndReceive(final CharSequence data) {
        InputStream in = null;
        try {
            HttpURLConnection connection = getConnection();
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(data.length());
            connection.setRequestProperty("Content-Type", "application/json-snappyrpc");
            modifyRequest(connection);
            OutputStream out = connection.getOutputStream();
            out.write(data.toString().getBytes("UTF-8"));
            close(out);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Unexpected HTTP response: "
                        + connection.getResponseCode() + ' ' + connection.getResponseMessage());
            }

            in = connection.getInputStream();
            return new String(readResponse(in), "UTF-8");
        } catch (MalformedURLException e) {
            throw new TransportError(e);
        } catch (IOException e) {
            throw new TransportError(e);
        } finally {
            close(in);
        }
    }
}
