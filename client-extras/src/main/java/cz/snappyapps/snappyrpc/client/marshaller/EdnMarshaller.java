package cz.snappyapps.snappyrpc.client.marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.bpsm.edn.EdnException;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;
import us.bpsm.edn.printer.Printers;

public class EdnMarshaller implements Marshaller {

    private static final Logger logger = LoggerFactory.getLogger(EdnMarshaller.class);

    private final Parser parser;

    public EdnMarshaller() {
        this(Parsers.defaultConfiguration());
    }

    public EdnMarshaller(Parser.Config config) {
        this.parser = Parsers.newParser(config);
    }

    public Object unmarshall(String representation) {
        try {
            Parseable parseable = Parsers.newParseable(representation);
            return parser.nextValue(parseable);
        } catch (EdnException e) {
            throw new MarshallerError(e);
        }
    }

    @Override
    public <T> T unmarshall(String str, Class<T> aClass) {
        try {
            Parseable parseable = Parsers.newParseable(str);
            return aClass.cast(parser.nextValue(parseable));
        } catch (EdnException e) {
            throw new MarshallerError(e);
        }
    }

    @Override
    public String marshall(Object o) {
        String s = Printers.printString(o);
        logger.debug(s);
        return s;
    }
}
