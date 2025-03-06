package cbpg.demo.plugin.auth.session;

public class SessionExpiredException extends RuntimeException {

    private SessionExpiredReason reason;

    public SessionExpiredException(SessionExpiredReason reason) {
        this.reason = reason;
    }

    public SessionExpiredReason getReason() {
        return reason;
    }
}
