package cbpg.demo.plugin.auth;

import cbpg.demo.plugin.auth.provider.AuthContext;
import com.intellij.util.messages.Topic;

public interface AuthListener {

    Topic<AuthListener> AUTH_TOPIC = Topic.create("AuthTopic", AuthListener.class);

    void afterLogin(AuthContext ctx);
}
