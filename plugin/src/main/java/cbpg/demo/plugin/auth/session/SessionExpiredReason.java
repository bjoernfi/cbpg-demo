package cbpg.demo.plugin.auth.session;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SessionExpiredReason {
    REFRESH_TOKEN_EXPIRED,
    REFRESH_TOKEN_INVALID;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
