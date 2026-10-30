package fun.freechat.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;

@Getter
@Setter
public class GuestRegistrationProperties implements InitializingBean {
    private int maxRegistrations = 10;
    private Duration interval = Duration.ofMinutes(10);

    @Override
    public void afterPropertiesSet() {
        require(maxRegistrations > 0, "maxRegistrations must be positive");
        require(
                interval != null
                        && interval.compareTo(Duration.ofMillis(1)) >= 0
                        && interval.compareTo(Duration.ofDays(365)) <= 0,
                "interval must be between one millisecond and one year");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException("auth.guest.registration." + message);
        }
    }
}
