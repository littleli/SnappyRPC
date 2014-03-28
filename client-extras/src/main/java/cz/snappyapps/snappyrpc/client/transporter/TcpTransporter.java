package cz.snappyapps.snappyrpc.client.transporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.*;
import java.net.Socket;
import java.net.URI;

public final class TcpTransporter implements Transporter, Closeable {

    private static final Logger logger = LoggerFactory.getLogger(TcpTransporter.class);

    private final Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    TcpTransporter(Socket socket) {
        this.socket = socket;
    }

    public static TcpTransporter connect(URI uri) {
        if ("tcp".equals(uri.getScheme())) {
            return connect(uri.getHost(), uri.getPort());
        } else {
            throw new IllegalArgumentException("Incorrect schema");
        }
    }

    public static TcpTransporter connect(String host, int port) {
        try {
            Socket socket = new Socket(host, port);
            socket.setKeepAlive(true);
            TcpTransporter tcpClient = new TcpTransporter(socket);
            tcpClient.prepareStreams();
            return tcpClient;
        } catch (IOException io) {
            throw new TransportError(io);
        }

    }

    private void prepareStreams() throws IOException {
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void sendAndForget(CharSequence data) {
        try {
            writer.write(data.toString());
            writer.newLine();
        } catch (IOException e) {
            throw new TransportError(e);
        }
    }

    @Override
    public String sendAndReceive(CharSequence data) {
        sendAndForget(data);
        try {
            return reader.readLine();
        } catch (IOException e) {
            throw new TransportError(e);
        }
    }

    private static void closeSocket(Socket socket) {
        if (socket == null) return;
        try {
            socket.close();
        } catch (IOException ignored) {
            logger.error(Marker.ANY_MARKER, ignored);
        }
    }


    @Override
    public void close() throws IOException {
        Util.close(writer);
        Util.close(reader);
        closeSocket(socket);
    }
}
