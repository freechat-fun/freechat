package fun.freechat.langchain4j.model.input;


import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

public class FStringPromptTemplateTest {
    @ParameterizedTest
    @MethodSource
    public void testFString(String expected, String template, Map<String, Object> variables) {
        assertEquals(expected, FStringPromptTemplate.from(template).apply(variables).text());
        System.out.println(FStringTemplateVariablesExtractor.extractTemplateVariables(template));
        assertIterableEquals(List.of("1", "{2}", "3", "{4", "", "{}", "5}", "6"),
                FStringTemplateVariablesExtractor.extractTemplateVariables(template));
    }

    static Stream<Arguments> testFString() {
        return Stream.of(
                Arguments.of("一 and  and and and  and  and  and ",
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
