package cz.snappyapps.snappyrpc.client.marshaller;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EdnMarshallerTest {

    private EdnMarshaller edn;

    @Before
    public void setup() {
        edn = new EdnMarshaller();
    }

    @Test
    public void testEdnUnmarshallPrimitives() {
        assertNull(edn.unmarshall("nil", Void.class));
        assertEquals(Long.valueOf(2), edn.unmarshall("2", Long.class));
        assertEquals(Symbol.newSymbol("greetings"), edn.unmarshall("greetings", Symbol.class));
        assertEquals(Keyword.newKeyword("keyword"), edn.unmarshall(":keyword", Keyword.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEdnListOfLongs() {
        List<Long> aListOfLongs = edn.unmarshall("(1 2 3 4)", List.class);
        assertThat(aListOfLongs, hasSize(4));
        assertThat(aListOfLongs, contains(1L, 2L, 3L, 4L));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEdnListOfObject() {
        List<Object> aList = edn.unmarshall("(:one :two 3 4)", List.class);
        assertThat(aList, Matchers.<Object>contains(Keyword.newKeyword("one"), Keyword.newKeyword("two"), 3L, 4L));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEdnSetOfLong() {
        Set<Long> aSet = edn.unmarshall("#{10 20 30 40}", Set.class);
        assertThat(aSet, hasItems(10L, 20L, 30L, 40L));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEdnmapOfLongs() {
        Map<Long, Long> aMap = edn.unmarshall("{1 2 3 4}", Map.class);
        assertThat(aMap, hasEntry(1L, 2L));
        assertThat(aMap, hasEntry(3L, 4L));
    }
}
