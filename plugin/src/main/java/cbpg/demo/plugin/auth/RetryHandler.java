package cbpg.demo.plugin.auth;

public interface RetryHandler {

    boolean shouldRetry();

    boolean canHandle(Exception ex);
}
