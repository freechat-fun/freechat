package fun.freechat.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import fun.freechat.annotation.Secret;
import fun.freechat.annotation.Trace;
import fun.freechat.access.user.SysUserDetails;
import fun.freechat.service.cache.LongPeriodCache;
import fun.freechat.service.cache.LongPeriodCacheEvict;
import fun.freechat.service.cache.MiddlePeriodCache;
import fun.freechat.service.cache.ShortPeriodCache;
import fun.freechat.service.account.SysAuthorityService;
import fun.freechat.model.User;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/public/test")
@Validated
@SuppressWarnings("unused")
public class TestController {
    private static final String TEST_PAGE = "test";
    private static final String RESULT = "result";

    private ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .findAndRegisterModules()
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setTimeZone(TimeZone.getTimeZone("GMT"))
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    /*==== trace ====*/
    @Autowired
    TestComponent testComponent;

    @Value("${app.logging.home}")
    String loggingHome;

    @GetMapping("/trace")
    public String trace(Model model) {
        Map<String, Double> info = new HashMap<>(2);
        info.put("a", 1.0);
        info.put("b", 2.0);
        testComponent.shouldBeTracedMethod(null, true, 1, 1.0, "a b",
                new double[]{1.0, 2.0}, List.of(1.0, 2.0), info, new TestPojo(),
                "Should not see me.");
        testComponent.shouldBeTracedMethodWithoutArgsAndReturn(null, true, 1, 1.0, "a b",
                new double[]{1.0, 2.0}, List.of(1.0, 2.0), info, new TestPojo(),
                "Should not see me.");
        String perfLogPath = System.getProperty("user.dir") + File.separator +
                loggingHome +File.separator + "common-perf.log";
        Path perfLog = Paths.get(perfLogPath);
        try {
            List<String> lines = Files.readAllLines(perfLog, StandardCharsets.UTF_8);
            ArrayList<String> latestLines = new ArrayList<>(2);
            int index = lines.size() - 2;
            String line = index >= 0 ? lines.get(index) : "?";
            latestLines.add(0, line);
            ++index;
            line = index >= 0 ? lines.get(index) : "?";
            latestLines.add(1, line);
            model.addAttribute(RESULT, String.join("\n", latestLines));
        } catch (IOException e) {
            model.addAttribute(RESULT, e.getMessage());
        }
        return TEST_PAGE;
    }

    @SuppressWarnings("unused")
    public static class TestPojo implements Serializable {
        private String a = "a";
        public String b = "b";

        @Override
        public String toString() {
            return "TestPojo{" +
                    "a='" + a + '\'' +
                    ", b='" + b + '\'' +
                    '}';
        }
    }

    @Component
    public static class TestComponent {
        @Trace
        @SuppressWarnings("UnusedReturnValue")
        public String shouldBeTracedMethod(Object arg0,
                                           boolean arg1,
                                           int arg2,
                                           double arg3,
                                           String arg4,
                                           double[] arg5,
                                           List<Double> arg6,
                                           Map<String, Double> arg7,
                                           TestPojo arg8,
                                           @Secret String arg9) {
            return "OK";
        }

        @Trace(ignoreArgs = true, ignoreReturn = true, extInfo = "'Hello ' + #p4")
        @SuppressWarnings("UnusedReturnValue")
        public String shouldBeTracedMethodWithoutArgsAndReturn(Object arg0,
                                                               boolean arg1,
                                                               int arg2,
                                                               double arg3,
                                                               String arg4,
                                                               double[] arg5,
                                                               List<Double> arg6,
                                                               Map<String, Double> arg7,
                                                               TestPojo arg8,
                                                               @Secret String arg9) {
            return "OK";
        }

        @ShortPeriodCache(keyBy = "#targetClass + '::' + #methodName + '_' + #p0")
        public String shortPeriodValue(String key, String value) {
            return value;
        }

        @MiddlePeriodCache(keyBy = "#targetClass + '::' + #methodName + '_' + #p0")
        public String middlePeriodValue(String key, String value) {
            return value;
        }

        @LongPeriodCache(keyBy = "'LongPeriodCache::test_' + #p0")
        public String longPeriodValue(String key, String value) {
            return value;
        }

        @LongPeriodCache(keyBy = "'LongPeriodCache::testObj_' + #p0")
        public TestPojo longPeriodObjectValue(String key, String value) {
            TestPojo pojo = new TestPojo();
            pojo.a = key;
            pojo.b = value;
            return pojo;
        }
    }

    /*==== cache ====*/
    @GetMapping("/cache/short/{key}/{value}")
    public String shortPeriod(@PathVariable("key") String key,
                              @PathVariable("value") String value,
                              Model model) {
        model.addAttribute(RESULT, testComponent.shortPeriodValue(key, value));
        return TEST_PAGE;
    }

    @GetMapping("/cache/middle/{key}/{value}")
    public String middlePeriod(@PathVariable("key") String key,
                               @PathVariable("value") String value,
                               Model model) {
        model.addAttribute(RESULT, testComponent.middlePeriodValue(key, value));
        return TEST_PAGE;
    }

    @GetMapping("/cache/long/{key}/{value}")
    public String longPeriod(@PathVariable("key") String key,
                             @PathVariable("value") String value,
                             Model model) {
        model.addAttribute(RESULT, testComponent.longPeriodValue(key, value));
        return TEST_PAGE;
    }

    @GetMapping("/cache/long/obj/{key}/{value}")
    public String longPeriodObject(@PathVariable("key") String key,
                                     @PathVariable("value") String value,
                                     Model model) {
        model.addAttribute(RESULT, testComponent.longPeriodObjectValue(key, value));
        return TEST_PAGE;
    }

    @GetMapping("/cache/long/evict/{key}")
    @LongPeriodCacheEvict(keyBy = "'LongPeriodCache::test_' + #p0")
    public String longPeriodEvict(@PathVariable("key") String key,
                                  Model model) {
        model.addAttribute(RESULT, "Cleaned up LongPeriodCache::test_" + key);
        return TEST_PAGE;
    }

    /*==== session ====*/
    @GetMapping("/session/{key}/{value}")
    public String session(@PathVariable("key") String key,
                          @PathVariable("value") String value,
                          HttpServletRequest request,
                          HttpSession session,
                          Model model) {
        StringBuilder resp = new StringBuilder();
        Object sessionValue = session.getAttribute(key);
        if (Objects.isNull(sessionValue)) {
            resp.append("Session doesn't exist, setting ").append(key).append("=").append(value);
            session.setAttribute(key, value);
        } else {
            resp.append("Session exists, which ").append(key).append("=").append(sessionValue);
        }

        Cookie[] cookies = request.getCookies();
        if (Objects.nonNull(cookies) && cookies.length > 0) {
            resp.append("\n").append("Cookies:\n");
            for (Cookie cookie : cookies) {
                resp.append(cookie.getName()).append(" : ").append(cookie.getValue()).append("\n");
            }
        }

        model.addAttribute(RESULT, resp.toString());
        return TEST_PAGE;
    }

    @GetMapping("/session/obj/{key}/{value}")
    public String sessionObj(@PathVariable("key") String key,
                             @PathVariable("value") String value,
                             HttpServletRequest request,
                             HttpSession session,
                             Model model) {
        Object sessionValue = session.getAttribute(key);
        TestPojo pojo;
        if (Objects.isNull(sessionValue) || !(sessionValue instanceof TestPojo)) {
            pojo = new TestPojo();
            pojo.a = key;
            pojo.b = value;
            session.setAttribute(key, pojo);
        } else {
            pojo = (TestPojo) sessionValue;
        }
        model.addAttribute(RESULT, pojo);
        return TEST_PAGE;
    }

    /*==== accounts ====*/
    @Autowired
    private SysAuthorityService authorityService;
    @Autowired
    private UserDetailsManager userDetailsManager;

    @GetMapping("/accounts/user")
    public String user(Model model) {
        Date now = new Date();

        User testUser = new User().withUsername(UUID.randomUUID().toString())
                .withPassword(UUID.randomUUID().toString())
                .withGmtCreate(now)
                .withGmtModified(now);

        SysUserDetails sysUser = SysUserDetails.builder().fromUser(testUser)
                .withAuthorityService(authorityService)
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                sysUser, sysUser.getPassword(), sysUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            ObjectMapper objectMapper = createObjectMapper();
            StringBuilder result = new StringBuilder();
            userDetailsManager.createUser(sysUser);
            UserDetails userDetails = userDetailsManager.loadUserByUsername(testUser.getUsername());
            result.append("Created User:\n").append(objectMapper.writeValueAsString(userDetails)).append("\n");
            Thread.sleep(1000);
            userDetailsManager.changePassword(testUser.getPassword(), testUser.getPassword() + ".ver2");
            userDetails = userDetailsManager.loadUserByUsername(testUser.getUsername());
            result.append("Updated User:\n").append(objectMapper.writeValueAsString(userDetails)).append("\n");
            model.addAttribute(RESULT, result.toString());
        } catch (JsonProcessingException | InterruptedException e) {
            model.addAttribute(RESULT, e.getMessage());
        } finally {
            userDetailsManager.deleteUser(testUser.getUsername());
        }
        return TEST_PAGE;
    }

    @GetMapping("/info/user")
    public String userInfo(HttpServletRequest request, Model model) {
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = new HashMap<>(3);
        try {
            if (Objects.nonNull(authenticated)) {
                userInfo.put("authentication", authenticated);
                Object extraInfo = request.getAttribute("userExtraInfo");
                if (Objects.nonNull(extraInfo)) {
                    //noinspection rawtypes
                    if (extraInfo instanceof Map extraMap) {
                        userInfo.putAll(extraMap);
                    } else {
                        userInfo.put("extra", extraInfo);
                    }
                }
                model.addAttribute(RESULT, createObjectMapper().writeValueAsString(userInfo));
            } else {
                model.addAttribute(RESULT, "anonymous");
            }
        } catch (IOException e) {
            model.addAttribute(RESULT, e.getMessage());
        }

        return TEST_PAGE;
    }

    /*==== request ====*/
    @RequestMapping("/info/request")
    public String requestInfo(HttpServletRequest request, Model model) {
        ObjectMapper objectMapper = createObjectMapper();
        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put("scheme", request.getScheme());
        requestInfo.put("host", request.getServerName());
        requestInfo.put("port", request.getServerPort());
        requestInfo.put("path", request.getServletPath());
        requestInfo.put("query", request.getQueryString());
        requestInfo.put("protocol", request.getProtocol());
        requestInfo.put("method", request.getMethod());
        requestInfo.put("uri", request.getRequestURI());

        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        requestInfo.put("headers", headers);

        Map<String, Object> parameters = new HashMap<>();
        request.getParameterMap().forEach((k, va) -> parameters.put(k, String.join(",", va)));
        requestInfo.put("parameters", parameters);

        try (InputStream bodyStream = request.getInputStream()) {
            String body = IOUtils.toString(bodyStream, StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(body)) {
                try {
                    JsonNode json = objectMapper.readTree(body);
                    requestInfo.put("body", json);
                } catch (JsonProcessingException e) {
                    requestInfo.put("body", body);
                }
            }
        } catch (IOException e) {
            // ignore
        }

        try {
            model.addAttribute(RESULT, objectMapper.writeValueAsString(requestInfo));
        } catch (JsonProcessingException e) {
            model.addAttribute(RESULT, e.getMessage());
        }
        return TEST_PAGE;
    }

    private String tinyJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, false);
        Object obj = objectMapper.readValue(json, Object.class);
        return objectMapper.writeValueAsString(obj);

    }

    @GetMapping("/plugin/demo/.well-known/ai-plugin.json")
    @ResponseBody
    public String pluginManifest() throws JsonProcessingException {
        return tinyJson("""
                {
                    "schema_version": "v1",
                    "name_for_model": "name_for_model",
                    "name_for_human": "name_for_human",
                    "description_for_model": "description_for_model",
                    "description_for_human": "description_for_human",
                    "auth": {
                        "type": "user_http",
                        "authorization_type": "bearer"
                    },
                    "api": {
                        "url": "https://domain.com/api/openapi.yaml",
                        "has_user_authentication": true,
                        "type": "openapi"
                    },
                    "logo_url": "https://domain.com/logo.png",
                    "contact_email": "hello@contact.com",
                    "legal_info_url": "hello@legal.com"
                }
                """);
    }

    @GetMapping("/plugin/demo/.well-known/api-docs.json")
    @ResponseBody
    public String pluginApi() throws JsonProcessingException {
        return tinyJson("""
                {
                    "openapi": "3.0.1",
                    "info":
                    {
                        "title": "Retrieval Plugin API",
                        "description": "A retrieval API.",
                        "version": "1.0.0"
                    },
                    "servers":
                    [
                        {
                            "url": "https://domain.com",
                            "description": "description"
                        }
                    ],
                    "paths":
                    {
                        "/plugins/v1/api":
                        {
                            "post":
                            {
                                "tags":
                                [
                                    "tag1"
                                ],
                                "summary": "summary",
                                "description": "description",
                                "operationId": "operationId",
                                "requestBody":
                                {
                                    "content":
                                    {
                                        "application/json":
                                        {
                                            "schema":
                                            {
                                                "$ref": "#/components/schemas/Request"
                                            }
                                        }
                                    },
                                    "required": true
                                },
                                "responses":
                                {
                                    "200":
                                    {
                                        "description": "OK",
                                        "content":
                                        {
                                            "*/*":
                                            {
                                                "schema":
                                                {
                                                    "$ref": "#/components/schemas/Response"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    "components":
                    {
                        "schemas":
                        {
                            "Request":
                            {
                                "properties":
                                {
                                    "property":
                                    {
                                        "type": "string"
                                    }
                                },
                                "title": "title"
                            },
                            "Response":
                            {
                                "properties":
                                {
                                    "property":
                                    {
                                        "type": "string"
                                    }
                                }
                            }
                        }
                    }
                }
                """);
    }
}
