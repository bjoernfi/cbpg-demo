package cbpg.demo.plugin.auth.provider;


import com.intellij.openapi.application.ApplicationManager;

public interface AuthProvider {

    static AuthProvider getInstance() {
        return ApplicationManager.getApplication().getService(AuthProvider.class);
    }

    AuthContext authenticate(String username, char[] password);
    boolean refreshRequired(AuthContext ctx);
    AuthContext refresh(AuthContext ctx);
}
