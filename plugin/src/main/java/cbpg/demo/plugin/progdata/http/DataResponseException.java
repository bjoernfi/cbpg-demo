package cbpg.demo.plugin.progdata.http;

public class DataResponseException extends RuntimeException {

    private String url;
    private int statusCode;
    private String reason;

    public DataResponseException(String url, int statusCode, String reason) {
        super();
        this.url = url;
        this.statusCode = statusCode;
        this.reason = reason;
    }

    @Override
    public String getMessage() {
        return String.format("Request for url %s failed with status %s (%s)", url, statusCode, reason);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
