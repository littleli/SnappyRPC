package cz.snappyapps.snappyrpc.client.transporter;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

class Util {

    static byte[] readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        try {
            for (int count; (count = is.read(buffer)) != -1; ) {
                baos.write(buffer, 0, count);
            }
            return baos.toByteArray();
        } finally {
            close(baos);
        }
    }

    static void close(Closeable c) {
        if (c == null) return;
        try {
            c.close();
        } catch (IOException ignored) {
        }
    }
}
