package cz.snappyapps.snappyrpc.client.transporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StubTransporter implements Transporter {

    private static final Logger logger = LoggerFactory.getLogger(StubTransporter.class);

    private volatile String stringToReturn;

    public void setStringToReturn(String stringToReturn) {
        this.stringToReturn = stringToReturn;
    }

    @Override
    public void sendAndForget(CharSequence data) {
        logger.info("--> {}", data);
    }

    @Override
    public String sendAndReceive(CharSequence data) {
        logger.info("--> {}", data);
        logger.info("<-- {}", stringToReturn);
        return stringToReturn;
    }
}
