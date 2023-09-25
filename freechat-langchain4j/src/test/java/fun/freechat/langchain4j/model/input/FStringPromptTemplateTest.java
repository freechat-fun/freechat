package fun.freechat.langchain4j.model.input;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FStringPromptTemplateTest {
    @ParameterizedTest
    @MethodSource
    public void testFString(String expected, String template, Map<String, Object> variables) {
        assertEquals(expected, FStringPromptTemplate.from(template).apply(variables).text());
    }

    static Stream<Arguments> testFString() {
        return Stream.of(
                Arguments.of("一 and {2} and 3and {4and  and {} and 5} and ",
                        "{1} and {{2}} and {3}and {{4}and {} and {{}} and {5}} and {6}",
                        new HashMap<>() {{
                            put("1", "一");
                            put("2", "二");
                            put("5", "五");
                            put("6", "");
                        }})
        );
    }

}
