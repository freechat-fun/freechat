package fun.freechat.api.dto;

import fun.freechat.api.util.AccountUtils;
import fun.freechat.api.util.CommonUtils;
import fun.freechat.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Date;
import java.util.Objects;

@Schema(description = "Account detailed information")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDetailsDTO extends TraceableDTO {
    private String username;
    private String nickname;
    private String givenName;
    private String middleName;
    private String familyName;
    private String preferredUsername;
    private String profile;
    private String picture;
    private String website;
    private String email;
    private String gender;
    private Date birthdate;
    private String zoneinfo;
    private String locale;
    private String phoneNumber;
    private Date updatedAt;
    private String platform;
    private Boolean enabled;
    private Boolean locked;
    private Date expiresAt;
    private Date passwordExpiresAt;
    private String address;

    public static UserDetailsDTO from(User user) {
        if (Objects.isNull(user)) {
            return null;
        }
        UserDetailsDTO userDetailsDTO = CommonUtils.convert(user, UserDetailsDTO.class);
        userDetailsDTO.setLocked(user.getLocked() != (byte)0);
        userDetailsDTO.setEnabled(user.getEnabled() != (byte)0);
        return userDetailsDTO;
    }

    public User toUser() {
        User user = CommonUtils.convert(this, User.class);
        user.setUserId(AccountUtils.userNameToId(getUsername()));
        user.setLocked(BooleanUtils.isTrue(getLocked()) ? (byte)1 : (byte)0);
        user.setEnabled(BooleanUtils.isNotFalse(getEnabled()) ? (byte)1 : (byte)0);
        return user;
    }
}
