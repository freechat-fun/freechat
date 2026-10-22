package fun.freechat.service.tools.builtin;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.model.chat.request.json.JsonObjectSchema;
import fun.freechat.service.tools.BuiltinTool;
import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import fun.freechat.service.tools.ToolDefinition;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.random.RandomGenerator;

public final class RoleplayTools {
    private static final int MAX_EXPRESSION_LENGTH = 1024;
    private static final int MAX_NUMBER_LENGTH = 128;
    private static final int MAX_TOKENS = 256;
    private static final int MAX_DEPTH = 32;
    private static final int MAX_EXPONENT = 256;
    private static final Set<String> TIMEZONES = Set.copyOf(ZoneId.getAvailableZoneIds());
    private static final Set<String> READ_TAGS = Set.of("roleplay", "utility", "read");

    private RoleplayTools() {}

    public static List<BuiltinTool> create() {
        return create(Clock.systemUTC(), new Random());
    }

    public static List<BuiltinTool> create(Clock clock, RandomGenerator random) {
        Objects.requireNonNull(clock);
        Objects.requireNonNull(random);
        return List.of(
                tool(
                        "get_current_time",
                        "Read the current time as ISO datetime/date, ISO weekday, UTC offset and Unix epoch seconds/milliseconds.",
                        JsonObjectSchema.builder()
                                .addStringProperty(
                                        "timezone", "IANA timezone identifier, at most 64 characters; default UTC.")
                                .additionalProperties(false)
                                .build(),
                        READ_TAGS,
                        (arguments, context) -> currentTime(arguments, clock)),
                tool(
                        "calculate",
                        "Evaluate bounded decimal arithmetic. Returns a decimal string with the precision and rounding mode used. Nonzero intermediate results must have a base-10 exponent between -256 and 256.",
                        JsonObjectSchema.builder()
                                .addStringProperty(
                                        "expression",
                                        "Required arithmetic using +, -, *, /, %, parentheses and unary signs. Decimal literals only; no scientific notation or functions. At most 1024 characters, 256 tokens, 32 nested parentheses/unary signs and 128 characters per literal. % is remainder with the dividend's sign.")
                                .addIntegerProperty(
                                        "precision",
                                        "Significant decimal digits, 1 through 100; default 34. HALF_EVEN rounding for literals and each operation.")
                                .required("expression")
                                .additionalProperties(false)
                                .build(),
                        READ_TAGS,
                        RoleplayTools::calculate),
                tool(
                        "roll_dice",
                        "Generate bounded dice rolls for roleplay. Returns the individual results and their sum plus the modifier.",
                        JsonObjectSchema.builder()
                                .addIntegerProperty("count", "Number of dice, 1 through 100; default 1.")
                                .addIntegerProperty("sides", "Sides per die, 2 through 1000000; default 6.")
                                .addIntegerProperty(
                                        "modifier",
                                        "Integer added once to the sum, -1000000 through 1000000; default 0.")
                                .additionalProperties(false)
                                .build(),
                        Set.of("roleplay", "utility", "read", "random"),
                        (arguments, context) -> rollDice(arguments, context, random)));
    }

    private static BuiltinTool tool(
            String name,
            String description,
            JsonObjectSchema parameters,
            Set<String> tags,
            BiFunction<JsonNode, ToolContext, Object> execute) {
        ToolSpecification specification = ToolSpecification.builder()
                .name(name)
                .description(description)
                .parameters(parameters)
                .build();
        return new LocalTool(
                new ToolDefinition(specification, tags, ToolDefinition.Boundary.IN_PROCESS, false), execute);
    }

    private static Object currentTime(JsonNode arguments, Clock clock) {
        ToolArguments.only(arguments, "timezone");
        String timezone = ToolArguments.optionalText(arguments, "timezone", "UTC", 64);
        if (!TIMEZONES.contains(timezone)) {
            throw ToolArguments.invalid();
        }
        var instant = clock.instant();
        var time = instant.atZone(ZoneId.of(timezone));
        return Map.of(
                "datetime", time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                "date", time.toLocalDate().toString(),
                "day_of_week", time.getDayOfWeek().name(),
                "iso_day_of_week", time.getDayOfWeek().getValue(),
                "timezone", timezone,
                "utc_offset", time.getOffset().getId(),
                "epoch_seconds", instant.getEpochSecond(),
                "epoch_millis", instant.toEpochMilli());
    }

    private static Object calculate(JsonNode arguments, ToolContext context) {
        ToolArguments.only(arguments, "expression", "precision");
        String expression = ToolArguments.text(arguments, "expression", MAX_EXPRESSION_LENGTH);
        int precision = ToolArguments.integer(arguments, "precision", 34, 1, 100);
        MathContext mathContext = new MathContext(precision, RoundingMode.HALF_EVEN);
        BigDecimal value;
        try {
            value = new Arithmetic(expression, mathContext, context).parse();
        } catch (ArithmeticException exception) {
            throw ToolArguments.invalid();
        }
        return Map.of(
                "expression",
                expression,
                "result",
                value.toPlainString(),
                "precision",
                precision,
                "rounding_mode",
                mathContext.getRoundingMode().name());
    }

    private static Object rollDice(JsonNode arguments, ToolContext context, RandomGenerator random) {
        ToolArguments.only(arguments, "count", "sides", "modifier");
        int count = ToolArguments.integer(arguments, "count", 1, 1, 100);
        int sides = ToolArguments.integer(arguments, "sides", 6, 2, 1_000_000);
        int modifier = ToolArguments.integer(arguments, "modifier", 0, -1_000_000, 1_000_000);
        List<Integer> results = new ArrayList<>(count);
        long total = modifier;
        for (int index = 0; index < count; index++) {
            context.check();
            int roll = random.nextInt(sides) + 1;
            results.add(roll);
            total += roll;
        }
        return Map.of("count", count, "sides", sides, "modifier", modifier, "results", results, "total", total);
    }

    private record LocalTool(ToolDefinition definition, BiFunction<JsonNode, ToolContext, Object> action)
            implements BuiltinTool {
        @Override
        public String execute(JsonNode arguments, ToolContext context) {
            context.check();
            Object value = action.apply(arguments, context);
            context.check();
            String result = ToolArguments.result(value);
            context.check();
            return result;
        }
    }

    private static final class Arithmetic {
        private final String input;
        private final MathContext mathContext;
        private final ToolContext context;
        private int position;
        private int tokens;

        private Arithmetic(String input, MathContext mathContext, ToolContext context) {
            this.input = input;
            this.mathContext = mathContext;
            this.context = context;
        }

        private BigDecimal parse() {
            BigDecimal value = expression(0);
            peek();
            if (position != input.length()) {
                throw ToolArguments.invalid();
            }
            return value;
        }

        private BigDecimal expression(int depth) {
            BigDecimal value = term(depth);
            while (true) {
                char operator = peek();
                if (operator != '+' && operator != '-') {
                    return value;
                }
                consume();
                BigDecimal right = term(depth);
                value = bounded(operator == '+' ? value.add(right, mathContext) : value.subtract(right, mathContext));
            }
        }

        private BigDecimal term(int depth) {
            BigDecimal value = unary(depth);
            while (true) {
                char operator = peek();
                if (operator != '*' && operator != '/' && operator != '%') {
                    return value;
                }
                consume();
                BigDecimal right = unary(depth);
                value = bounded(
                        switch (operator) {
                            case '*' -> value.multiply(right, mathContext);
                            case '/' -> value.divide(right, mathContext);
                            default -> value.remainder(right).round(mathContext);
                        });
            }
        }

        private BigDecimal unary(int depth) {
            context.check();
            if (depth > MAX_DEPTH) {
                throw ToolArguments.invalid();
            }
            char next = peek();
            if (next == '+' || next == '-') {
                consume();
                BigDecimal value = unary(depth + 1);
                return next == '-' ? value.negate() : value;
            }
            if (next == '(') {
                consume();
                BigDecimal value = expression(depth + 1);
                if (peek() != ')') {
                    throw ToolArguments.invalid();
                }
                consume();
                return value;
            }
            return number();
        }

        private BigDecimal number() {
            int start = position;
            int digits = 0;
            while (position < input.length() && isDigit(input.charAt(position))) {
                position++;
                digits++;
            }
            if (position < input.length() && input.charAt(position) == '.') {
                position++;
                while (position < input.length() && isDigit(input.charAt(position))) {
                    position++;
                    digits++;
                }
            }
            if (digits == 0 || position - start > MAX_NUMBER_LENGTH) {
                throw ToolArguments.invalid();
            }
            token();
            return bounded(new BigDecimal(input.substring(start, position), mathContext));
        }

        private BigDecimal bounded(BigDecimal value) {
            context.check();
            if (value.signum() == 0) {
                return BigDecimal.ZERO;
            }
            long exponent = (long) value.precision() - value.scale() - 1;
            if (exponent < -MAX_EXPONENT || exponent > MAX_EXPONENT) {
                throw ToolArguments.invalid();
            }
            return value.stripTrailingZeros();
        }

        private char peek() {
            while (position < input.length()) {
                char next = input.charAt(position);
                if (next != ' ' && next != '\t' && next != '\r' && next != '\n') {
                    return next;
                }
                position++;
            }
            return '\0';
        }

        private void consume() {
            token();
            position++;
        }

        private void token() {
            context.check();
            if (++tokens > MAX_TOKENS) {
                throw ToolArguments.invalid();
            }
        }

        private static boolean isDigit(char value) {
            return value >= '0' && value <= '9';
        }
    }
}
