package fun.freechat.util;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PojoUtilsTest {
    @SuppressWarnings("unused")
    static class Entry {
        private final String v;
        private Demo host;
        private Entry next;

        public Entry(String v) {
            this.v = v;
        }

        public Entry setHost(Demo host) {
            this.host = host;
            return this;
        }

        public Entry setNext(Entry next) {
            this.next = next;
            return this;
        }
    }

    @SuppressWarnings("unused")
    static class Demo {
        private final List<Entry> l = new LinkedList<>();

        public Demo() {
            l.add(new Entry("1").setHost(this)
                    .setNext(new Entry("12")
                            .setNext(new Entry("123")
                                    .setNext(new Entry("1234")
                                            .setNext(new Entry("12345")
                                                    .setNext(new Entry("123456")))))));
        }

        public List<Entry> getL() {
            return l;
        }
    }

    @SuppressWarnings("unused")
    static class MapPojo {
        String str;
        List<String> list;
        Map<String, Integer> map;
        Object value;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public Map<String, Integer> getMap() {
            return map;
        }

        public void setMap(Map<String, Integer> map) {
            this.map = map;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "MapPojo{" +
                    "str='" + str + '\'' +
                    ", list=" + list +
                    ", map=" + map +
                    ", value=" + value +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MapPojo mapPojo)) return false;
            return Objects.equals(getStr(), mapPojo.getStr()) &&
                    Objects.equals(getList(), mapPojo.getList()) &&
                    Objects.equals(getMap(), mapPojo.getMap()) &&
                    Objects.equals(getValue(), mapPojo.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getStr(), getList(), getMap(), getValue());
        }
    }

    private String toPattern(String orig) {
        return orig.replaceAll("\\{", "\\\\{")
                .replaceAll("\\}", "\\\\}")
                .replaceAll("\\[", "\\\\[")
                .replaceAll("\\]", "\\\\]");
    }

    @Test
    public void testCircleAndDepth() {
        Demo demo = new Demo();
        String objInfo = PojoUtils.object2JsonString(demo, false);
        Pattern p = Pattern.compile(toPattern("{\"l\":[{\"v\":\"1\",\"host\":\"Demo@\\w+\",\"next\":{\"v\":\"12\",\"host\":null,\"next\":{\"v\":\"123\",\"host\":null,\"next\":{\"v\":\"1234\",\"host\":null,\"next\":{\"v\":\"12345\",\"host\":null,\"next\":\"Entry@\\w+\"}}}}}]}"));
        Matcher m = p.matcher(objInfo);
        assertTrue(m.matches());
    }

    @Test
    public void testMap() {
        MapPojo source = new MapPojo();
        source.setStr("str");
        source.setList(List.of("item1"));
        source.setMap(Map.of("key1", 1));
        source.setValue(0.1);

        MapPojo target = new MapPojo();
        PojoUtils.mapWhenExists(source::getStr, target::setStr);
        PojoUtils.mapWhenExists(source::getList, target::setList);
        PojoUtils.mapWhenExists(source::getMap, target::setMap);
        PojoUtils.mapWhenExists(source::getValue, target::setValue);

        assertEquals(source, target);
    }
}
