package cbpg.demo.plugin.auth.login;

import java.util.EventListener;

public interface LoginListener extends EventListener {

    void loginRequested();

    void usernameChanged();

    void passwordChanged();
}
