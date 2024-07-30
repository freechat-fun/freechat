package fun.freechat.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

@SuppressWarnings("unused")
public class AppMetaUtils {
    private static final Logger log = LoggerFactory.getLogger(AppMetaUtils.class);

    private static final String PROPERTIES_FILE = "application-version.properties";
    private static final String NAME_KEY = "app.name";
    private static final String VERSION_KEY = "app.version";
    private static final String BUILD_TIMESTAMP_KEY = "app.build.timestamp";
    private static final String BUILD_NUMBER_KEY = "app.build.number";
    private static final String SCM_URL_KEY = "app.scm.url";
    private static final String URL_KEY = "app.url";

    private static String appName;
    private static String appVersion;
    private static String appBuildTimestamp;
    private static String appBuildNumber;
    private static String appScmUrl;
    private static String appUrl;

    public static String getName() {
        if (appName == null) {
            synchronized (AppMetaUtils.class) {
                if (appName == null) {
                    appName = getProperty(NAME_KEY);
                }
            }
        }
        return appName;
    }

    public static String getVersion() {
        if (appVersion == null) {
            synchronized (AppMetaUtils.class) {
                if (appVersion == null) {
                    appVersion = getProperty(VERSION_KEY);
                }
            }
        }
        return appVersion;
    }

    public static String getBuildTimestamp() {
        if (appBuildTimestamp == null) {
            synchronized (AppMetaUtils.class) {
                if (appBuildTimestamp == null) {
                    appBuildTimestamp = getProperty(BUILD_TIMESTAMP_KEY);
                }
            }
        }
        return appBuildTimestamp;
    }

    public static String getBuildNumber() {
        if (appBuildNumber == null) {
            synchronized (AppMetaUtils.class) {
                if (appBuildNumber == null) {
                    appBuildNumber = getProperty(BUILD_NUMBER_KEY);
                }
            }
        }
        return appBuildNumber;
    }

    public static String getScmUrl() {
        if (appScmUrl == null) {
            synchronized (AppMetaUtils.class) {
                if (appScmUrl == null) {
                    appScmUrl = getProperty(SCM_URL_KEY);
                }
            }
        }
        return appScmUrl;
    }

    public static String getUrl() {
        if (appUrl == null) {
            synchronized (AppMetaUtils.class) {
                appUrl = getProperty(URL_KEY);
            }
        }
        return appUrl;
    }

    private static String getProperty(String name) {
        Properties properties = new Properties();
        try (InputStream in = AppMetaUtils.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in == null) {
                return null;
            }
            properties.load(in);
            if (!properties.isEmpty()) {
                return properties.getProperty(name);
            }
        } catch (IOException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        return null;
    }

    public static String getRunningEnv() {
        String activeProfile = System.getProperty("spring.profiles.active");
        return StringUtils.isBlank(activeProfile) ? "online" : activeProfile;

    }

    public static boolean isTestEnv() {
        String runningEnv = getRunningEnv();
        return "testing".equalsIgnoreCase(runningEnv) || "local".equalsIgnoreCase(runningEnv);
    }

    public static void main(String[] args) {
        log.info(AppMetaUtils.getName());
        log.info(AppMetaUtils.getVersion());
        log.info(AppMetaUtils.getBuildTimestamp());
        log.info(AppMetaUtils.getBuildNumber());
        log.info(AppMetaUtils.getScmUrl());
        log.info(AppMetaUtils.getUrl());
        log.info(AppMetaUtils.getRunningEnv());
    }
}