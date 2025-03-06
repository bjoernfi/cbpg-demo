package cbpg.demo.plugin.auth.login;


public class LoginModel {

    private String username;
    private String password;
    private String contextMessage;
    private String lastRemoteError;
    private boolean loggingIn;

    public void setLoggingIn(boolean loggingIn) {
        this.loggingIn = loggingIn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setContextMessage(String contextMessage) {
        this.contextMessage = contextMessage;
    }

    public void setLastRemoteError(String lastRemoteError) {
        this.lastRemoteError = lastRemoteError;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getContextMessage() {
        return contextMessage;
    }

    public String getLastRemoteError() {
        return lastRemoteError;
    }

    public boolean isLoggingIn() {
        return loggingIn;
    }
}
