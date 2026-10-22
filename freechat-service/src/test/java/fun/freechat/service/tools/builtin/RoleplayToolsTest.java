package fun.freechat.service.tools.builtin;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import fun.freechat.service.tools.BuiltinTool;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import fun.freechat.service.tools.ToolDefinition;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(10)
class RoleplayToolsTest {
    private static final Instant INSTANT = Instant.parse("2024-02-29T23:15:30.123456789Z");
    private static final Clock CLOCK = Clock.fixed(INSTANT, ZoneId.of("Asia/Tokyo"));
    private final List<BuiltinTool> tools = RoleplayTools.create(CLOCK, new Random(12345));

    @Test
    void exposesOnlyCanonicalInProcessReadToolsAndStrictSchemas() {
        List<String> names = List.of("get_current_time", "calculate", "roll_dice");
        assertEquals(names, tools.stream().map(tool -> tool.definition().name()).toList());
        assertEquals(
                names,
                RoleplayTools.create().stream()
                        .map(tool -> tool.definition().name())
                        .toList());
        assertThrows(UnsupportedOperationException.class, () -> tools.add(tools.getFirst()));
        assertThrows(NullPointerException.class, () -> RoleplayTools.create(null, new Random(1)));
        assertThrows(NullPointerException.class, () -> RoleplayTools.create(CLOCK, null));
        for (BuiltinTool tool : tools) {
            var definition = tool.definition();
            assertEquals(ToolDefinition.Boundary.IN_PROCESS, definition.boundary());
            assertFalse(definition.sideEffects());
            assertEquals(
                    definition.name().equals("roll_dice")
                            ? Set.of("roleplay", "utility", "read", "random")
                            : Set.of("roleplay", "utility", "read"),
                    definition.tags());
            var schema = definition.specification().parameters();
            assertEquals(Boolean.FALSE, schema.additionalProperties());
            assertFalse(definition.specification().description().isBlank());
            switch (definition.name()) {
                case "get_current_time" -> {
                    assertEquals(Set.of("timezone"), schema.properties().keySet());
                    assertInstanceOf(JsonStringSchema.class, schema.properties().get("timezone"));
                    assertTrue(schema.required().isEmpty());
                }
                case "calculate" -> {
                    assertEquals(
                            Set.of("expression", "precision"),
                            schema.properties().keySet());
                    assertInstanceOf(JsonStringSchema.class, schema.properties().get("expression"));
                    assertInstanceOf(
                            JsonIntegerSchema.class, schema.properties().get("precision"));
                    assertEquals(List.of("expression"), schema.required());
                }
                case "roll_dice" -> {
                    assertEquals(
                            Set.of("count", "sides", "modifier"),
                            schema.properties().keySet());
                    schema.properties().values().forEach(value -> assertInstanceOf(JsonIntegerSchema.class, value));
                    assertTrue(schema.required().isEmpty());
                }
                default -> fail("Unexpected tool");
            }
        }
    }

    @Test
    void currentTimeDefaultsToUtcRegardlessOfClockZone() throws Exception {
        JsonNode result = execute("get_current_time", "{}");
        assertEquals(ToolArguments.JSON.readTree("""
                        {
                          "datetime": "2024-02-29T23:15:30.123456789Z",
                          "date": "2024-02-29",
                          "day_of_week": "THURSDAY",
                          "iso_day_of_week": 4,
                          "timezone": "UTC",
                          "utc_offset": "Z",
                          "epoch_seconds": 1709248530,
                          "epoch_millis": 1709248530123
                        }
                        """), result);
        assertEquals(result, execute("get_current_time", "{\"timezone\":\"UTC\"}"));
    }

    @Test
    void currentTimeHandlesFractionalOffsetAndDateRolloverWithSameSchema() throws Exception {
        JsonNode result = execute("get_current_time", "{\"timezone\":\"Asia/Kathmandu\"}");
        assertEquals(
                "2024-03-01T05:00:30.123456789+05:45", result.path("datetime").asText());
        assertEquals("2024-03-01", result.path("date").asText());
        assertEquals("FRIDAY", result.path("day_of_week").asText());
        assertEquals(5, result.path("iso_day_of_week").asInt());
        assertEquals("+05:45", result.path("utc_offset").asText());
        assertEquals("Asia/Kathmandu", result.path("timezone").asText());
        assertEquals(INSTANT.getEpochSecond(), result.path("epoch_seconds").asLong());
        assertEquals(INSTANT.toEpochMilli(), result.path("epoch_millis").asLong());
    }

    @ParameterizedTest
    @CsvSource({
        "2024-11-03T05:30:00Z, 2024-11-03T01:30:00-04:00, -04:00",
        "2024-11-03T06:30:00Z, 2024-11-03T01:30:00-05:00, -05:00"
    })
    void currentTimeUsesIanaDaylightSavingRules(String instant, String datetime, String offset) throws Exception {
        BuiltinTool tool = find(
                RoleplayTools.create(Clock.fixed(Instant.parse(instant), ZoneOffset.UTC), new Random(1)),
                "get_current_time");
        JsonNode result = output(tool, ToolArguments.parse("{\"timezone\":\"America/New_York\"}"), context());
        assertEquals(datetime, result.path("datetime").asText());
        assertEquals(offset, result.path("utc_offset").asText());
        assertEquals("SUNDAY", result.path("day_of_week").asText());
        assertEquals(7, result.path("iso_day_of_week").asInt());
    }

    @ParameterizedTest
    @CsvSource({
        "2 + 3 * 4, 14",
        "(2 + 3) * 4, 20",
        "10 - 3 - 2, 5",
        "20 / 2 / 5, 2",
        "17 % 5 * 2, 4",
        "-7 % 3, -1",
        "7 % -3, 1",
        "5.5 % 2, 1.5",
        "-(-2.50 + +.5) / 4, 0.5",
        "2 * -3 + 8 / +2, -2",
        "1--2, 3",
        "1. + .25, 1.25",
        "0001.2500 + .7500, 2",
        "0.1 + 0.2, 0.3",
        "-0.000, 0"
    })
    void calculatesDecimalArithmeticGoldenPaths(String expression, String expected) throws Exception {
        JsonNode result = calculate(expression, null);
        assertEquals(
                ToolArguments.JSON.valueToTree(Map.of(
                        "expression", expression, "result", expected, "precision", 34, "rounding_mode", "HALF_EVEN")),
                result);
    }

    @Test
    void precisionIsBoundedAndHalfEvenRoundingIsExplicit() throws Exception {
        assertEquals(
                "0.3333333333333333333333333333333333",
                calculate("1/3", null).path("result").asText());
        assertEquals(
                "0." + "3".repeat(100), calculate("1/3", 100).path("result").asText());
        JsonNode rounded = calculate("1/8", 2);
        assertEquals("0.12", rounded.path("result").asText());
        assertEquals(2, rounded.path("precision").asInt());
        assertEquals("0.38", calculate("3/8", 2).path("result").asText());
        assertEquals("0.3", calculate("1/3", 1).path("result").asText());
        assertEquals("1", calculate("100%3", 1).path("result").asText());
        assertEquals("4", calculate("\t 1 +\r\n 3 ", null).path("result").asText());
    }

    @Test
    void acceptsResourceLimitEdges() throws Exception {
        assertEquals("1", calculate("1" + " ".repeat(1023), null).path("result").asText());
        assertEquals("0", calculate("0".repeat(128), null).path("result").asText());
        assertEquals(
                "1",
                calculate("(".repeat(32) + "1" + ")".repeat(32), null)
                        .path("result")
                        .asText());
        assertEquals("1", calculate("-".repeat(32) + "1", null).path("result").asText());
        assertEquals(
                "128", calculate("1+".repeat(127) + "1", null).path("result").asText());
        assertEquals(
                "128",
                calculate("+" + "1+".repeat(127) + "1", null).path("result").asText());
        String large = "1" + "0".repeat(127);
        assertEquals(
                "1" + "0".repeat(256),
                calculate(large + "*" + large + "*100", 100).path("result").asText());
        String small = "0." + "0".repeat(125) + "1";
        assertEquals(
                "0." + "0".repeat(255) + "1",
                calculate(small + "*" + small + "/10000", 100).path("result").asText());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "",
                " ",
                ".",
                "()",
                "1+",
                "*1",
                "1 2",
                "1..2",
                "1.2.3",
                "(1+2",
                "1+2)",
                "2(3)",
                "1e3",
                "1E-3",
                "0x10",
                "2^100",
                "2**10",
                "NaN",
                "Infinity",
                "1,2",
                "[1]",
                "true",
                "Math.random()",
                "T(java.lang.Runtime).getRuntime()",
                "1; 2",
                "1/*x*/+2",
                "${1+2}",
                "\uFF11+2",
                "1\u00a0+2",
                "1\u0000",
                "1/0",
                "1/-0.00",
                "1/(2-2)",
                "0/0",
                "1%0",
                "1%(2-2)"
            })
    void rejectsMalformedUnsupportedAndUndefinedArithmetic(String expression) {
        assertInvalid(
                find(tools, "calculate"), ToolArguments.JSON.createObjectNode().put("expression", expression));
    }

    @Test
    void rejectsNumericOverflowUnderflowAndParserResourceExhaustion() {
        String large = "1" + "0".repeat(127);
        String small = "0." + "0".repeat(125) + "1";
        for (String expression : List.of(
                "1" + " ".repeat(1024),
                "0".repeat(129),
                "9".repeat(129),
                "(".repeat(33) + "1" + ")".repeat(33),
                "-".repeat(33) + "1",
                "(".repeat(20) + "-".repeat(13) + "1" + ")".repeat(20),
                "1+".repeat(128) + "1",
                large + "*" + large + "*1000",
                large + "*" + large + "*1000*0",
                small + "*" + small + "/100000",
                "(" + large + "*" + large + "*100)/0.1",
                "(" + large + "*" + large + "*100)*10%1")) {
            assertInvalid(
                    find(tools, "calculate"),
                    ToolArguments.JSON.createObjectNode().put("expression", expression));
        }
    }

    @Test
    void diceGoldenPathUsesInjectedRandomAndAppliesModifierOnce() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        int[] values = {0, 5, 2};
        BuiltinTool dice = find(
                RoleplayTools.create(CLOCK, random(bound -> {
                    assertEquals(6, bound);
                    return values[calls.getAndIncrement()];
                })),
                "roll_dice");
        JsonNode result = output(dice, ToolArguments.parse("{\"count\":3,\"modifier\":4}"), context());
        assertEquals(
                ToolArguments.JSON.readTree(
                        "{\"count\":3,\"sides\":6,\"modifier\":4,\"results\":[1,6,3],\"total\":14}"),
                result);
        assertEquals(3, calls.get());
    }

    @Test
    void diceDefaultsAndBothIntegerBoundariesHaveSameResultSchema() throws Exception {
        BuiltinTool dice = find(RoleplayTools.create(CLOCK, random(bound -> bound - 1)), "roll_dice");
        assertEquals(
                ToolArguments.JSON.readTree("{\"count\":1,\"sides\":6,\"modifier\":0,\"results\":[6],\"total\":6}"),
                output(dice, ToolArguments.parse("{}"), context()));
        JsonNode largest =
                output(dice, ToolArguments.parse("{\"count\":100,\"sides\":1000000,\"modifier\":1000000}"), context());
        assertEquals(100, largest.path("results").size());
        largest.path("results").forEach(value -> assertEquals(1_000_000, value.intValue()));
        assertEquals(101_000_000L, largest.path("total").longValue());
        JsonNode smallest =
                output(dice, ToolArguments.parse("{\"count\":1,\"sides\":2,\"modifier\":-1000000}"), context());
        assertEquals(-999_998L, smallest.path("total").longValue());
    }

    @Test
    void seededRandomProducesRepeatableResultsAcrossCalls() throws Exception {
        BuiltinTool first = find(RoleplayTools.create(CLOCK, new Random(42)), "roll_dice");
        BuiltinTool second = find(RoleplayTools.create(CLOCK, new Random(42)), "roll_dice");
        JsonNode arguments = ToolArguments.parse("{\"count\":100,\"sides\":1000000,\"modifier\":-42}");
        for (int index = 0; index < 3; index++) {
            assertEquals(output(first, arguments, context()), output(second, arguments, context()));
        }
    }

    @ParameterizedTest
    @MethodSource("invalidArguments")
    void rejectsWrongUnknownAndOversizedArguments(String name, String arguments) throws Exception {
        assertInvalid(find(tools, name), ToolArguments.JSON.readTree(arguments));
    }

    private static Stream<Arguments> invalidArguments() {
        Stream<Arguments> time = Stream.of(
                        "{\"timezone\":null}",
                        "{\"timezone\":0}",
                        "{\"timezone\":true}",
                        "{\"timezone\":[]}",
                        "{\"timezone\":{}}",
                        "{\"timezone\":\"\"}",
                        "{\"timezone\":\" UTC \"}",
                        "{\"timezone\":\"Not/A_Zone\"}",
                        "{\"timezone\":\"+01:00\"}",
                        "{\"timezone\":\"UTC+01:00\"}",
                        "{\"timezone\":\"Z\"}",
                        "{\"timezone\":\"" + "x".repeat(65) + "\"}")
                .map(value -> Arguments.of("get_current_time", value));
        Stream<Arguments> calculate = Stream.of(
                        "{}",
                        "{\"expression\":null}",
                        "{\"expression\":1}",
                        "{\"expression\":true}",
                        "{\"expression\":[]}",
                        "{\"expression\":{}}",
                        "{\"expression\":\"1\",\"precision\":null}",
                        "{\"expression\":\"1\",\"precision\":0}",
                        "{\"expression\":\"1\",\"precision\":101}",
                        "{\"expression\":\"1\",\"precision\":-1}",
                        "{\"expression\":\"1\",\"precision\":1.0}",
                        "{\"expression\":\"1\",\"precision\":\"34\"}",
                        "{\"expression\":\"1\",\"precision\":true}",
                        "{\"expression\":\"1\",\"precision\":[]}",
                        "{\"expression\":\"1\",\"precision\":{}}",
                        "{\"expression\":\"1\",\"precision\":9223372036854775808}",
                        "{\"expression\":\"1\",\"rounding_mode\":\"DOWN\"}")
                .map(value -> Arguments.of("calculate", value));
        Stream<Arguments> diceBounds = Stream.of(
                        "{\"count\":0}",
                        "{\"count\":101}",
                        "{\"sides\":1}",
                        "{\"sides\":1000001}",
                        "{\"modifier\":-1000001}",
                        "{\"modifier\":1000001}",
                        "{\"notation\":\"2d6\"}")
                .map(value -> Arguments.of("roll_dice", value));
        Stream<Arguments> diceTypes = Stream.of("count", "sides", "modifier")
                .flatMap(name -> Stream.of("null", "\"2\"", "true", "2.0", "[]", "{}", "9223372036854775808")
                        .map(value -> Arguments.of("roll_dice", "{\"" + name + "\":" + value + "}")));
        Stream<Arguments> shared = Stream.of("get_current_time", "calculate", "roll_dice")
                .flatMap(name -> Stream.of("null", "[]", "0", "true", "\"text\"", "{\"unknown\":0}")
                        .map(value -> Arguments.of(name, value)));
        return Stream.of(time, calculate, diceBounds, diceTypes, shared).flatMap(value -> value);
    }

    @Test
    void rejectsNullRootAndMalformedUnicodeWithoutCoercion() {
        tools.forEach(tool -> assertInvalid(tool, null));
        for (String text : List.of("\uD800", "\uDC00", "\uD800x")) {
            assertInvalid(
                    find(tools, "get_current_time"),
                    ToolArguments.JSON.createObjectNode().put("timezone", text));
            assertInvalid(
                    find(tools, "calculate"),
                    ToolArguments.JSON.createObjectNode().put("expression", text));
        }
    }

    @Test
    void invalidDiceArgumentsDoNotConsumeRandomness() {
        BuiltinTool dice = find(
                RoleplayTools.create(CLOCK, random(bound -> {
                    throw new AssertionError("Invalid arguments reached random generator");
                })),
                "roll_dice");
        assertInvalid(dice, ToolArguments.parse("{\"count\":100,\"sides\":6,\"modifier\":1000001}"));
        assertInvalid(dice, ToolArguments.parse("{\"count\":1,\"extra\":true}"));
    }

    @Test
    void everyToolRejectsExpiredDeadlinesBeforeWork() {
        ToolContext expired = new ToolContext("chat", "user", "character", "backend", Duration.ofNanos(1));
        while (true) {
            try {
                expired.check();
            } catch (IllegalStateException expected) {
                break;
            }
        }
        for (BuiltinTool tool : tools) {
            assertThrows(IllegalStateException.class, () -> tool.execute(validArguments(tool), expired));
        }
    }

    @Test
    void everyToolHonorsInterruptionWithoutClearingIt() {
        try {
            Thread.currentThread().interrupt();
            for (BuiltinTool tool : tools) {
                assertThrows(IllegalStateException.class, () -> tool.execute(validArguments(tool), context()));
                assertTrue(Thread.currentThread().isInterrupted());
            }
        } finally {
            Thread.interrupted();
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100})
    void diceRechecksDeadlineDuringAndAfterRandomGeneration(int count) {
        AtomicInteger calls = new AtomicInteger();
        BuiltinTool dice = find(
                RoleplayTools.create(CLOCK, random(bound -> {
                    calls.incrementAndGet();
                    Thread.currentThread().interrupt();
                    return 0;
                })),
                "roll_dice");
        try {
            assertThrows(
                    IllegalStateException.class,
                    () -> dice.execute(ToolArguments.JSON.createObjectNode().put("count", count), context()));
            assertEquals(1, calls.get());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void timeRechecksDeadlineAfterClockRead() {
        Clock interruptingClock = new Clock() {
            @Override
            public ZoneId getZone() {
                return ZoneOffset.UTC;
            }

            @Override
            public Clock withZone(ZoneId zone) {
                return this;
            }

            @Override
            public Instant instant() {
                Thread.currentThread().interrupt();
                return INSTANT;
            }
        };
        BuiltinTool time = find(RoleplayTools.create(interruptingClock, new Random(1)), "get_current_time");
        try {
            assertThrows(IllegalStateException.class, () -> time.execute(ToolArguments.parse("{}"), context()));
        } finally {
            Thread.interrupted();
        }
    }

    private JsonNode calculate(String expression, Integer precision) throws Exception {
        var arguments = ToolArguments.JSON.createObjectNode().put("expression", expression);
        if (precision != null) {
            arguments.put("precision", precision);
        }
        return output(find(tools, "calculate"), arguments, context());
    }

    private JsonNode execute(String name, String arguments) throws Exception {
        return output(find(tools, name), ToolArguments.parse(arguments), context());
    }

    private static BuiltinTool find(List<BuiltinTool> tools, String name) {
        return tools.stream()
                .filter(tool -> tool.definition().name().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private static ToolContext context() {
        return new ToolContext("chat", "user", "character", "backend", Duration.ofSeconds(5));
    }

    private static JsonNode validArguments(BuiltinTool tool) {
        return ToolArguments.parse(tool.definition().name().equals("calculate") ? "{\"expression\":\"1+2\"}" : "{}");
    }

    private static void assertInvalid(BuiltinTool tool, JsonNode arguments) {
        var exception = assertThrows(IllegalArgumentException.class, () -> tool.execute(arguments, context()));
        assertEquals("Invalid tool arguments", exception.getMessage());
    }

    private static RandomGenerator random(IntUnaryOperator nextInt) {
        return new RandomGenerator() {
            @Override
            public long nextLong() {
                throw new AssertionError("Unexpected random operation");
            }

            @Override
            public int nextInt(int bound) {
                return nextInt.applyAsInt(bound);
            }
        };
    }

    private static JsonNode output(BuiltinTool tool, JsonNode arguments, ToolContext context) throws Exception {
        String raw = tool.execute(arguments, context);
        assertTrue(raw.length() < 4096);
        JsonNode result = ToolArguments.JSON.readTree(raw);
        assertTrue(result.isObject());
        Set<String> fields = new HashSet<>();
        result.fieldNames().forEachRemaining(fields::add);
        switch (tool.definition().name()) {
            case "get_current_time" -> {
                assertEquals(
                        Set.of(
                                "datetime",
                                "date",
                                "day_of_week",
                                "iso_day_of_week",
                                "timezone",
                                "utc_offset",
                                "epoch_seconds",
                                "epoch_millis"),
                        fields);
                for (String field : List.of("datetime", "date", "day_of_week", "timezone", "utc_offset")) {
                    assertTrue(result.get(field).isTextual(), field);
                }
                for (String field : List.of("iso_day_of_week", "epoch_seconds", "epoch_millis")) {
                    assertTrue(result.get(field).isIntegralNumber(), field);
                }
            }
            case "calculate" -> {
                assertEquals(Set.of("expression", "result", "precision", "rounding_mode"), fields);
                assertTrue(result.get("expression").isTextual());
                assertTrue(result.get("result").isTextual());
                assertTrue(result.get("result").textValue().matches("-?[0-9]+(?:\\.[0-9]+)?"));
                assertTrue(result.get("precision").isIntegralNumber());
                assertEquals("HALF_EVEN", result.get("rounding_mode").textValue());
            }
            case "roll_dice" -> {
                assertEquals(Set.of("count", "sides", "modifier", "results", "total"), fields);
                for (String field : List.of("count", "sides", "modifier", "total")) {
                    assertTrue(result.get(field).isIntegralNumber(), field);
                }
                assertTrue(result.get("results").isArray());
                assertEquals(
                        result.get("count").intValue(), result.get("results").size());
                long total = result.get("modifier").longValue();
                for (JsonNode roll : result.get("results")) {
                    assertTrue(roll.isIntegralNumber());
                    assertTrue(roll.intValue() >= 1
                            && roll.intValue() <= result.get("sides").intValue());
                    total += roll.intValue();
                }
                assertEquals(total, result.get("total").longValue());
            }
            default -> fail("Unexpected tool");
        }
        return result;
    }
}
