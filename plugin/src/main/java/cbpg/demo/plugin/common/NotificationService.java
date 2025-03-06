package cbpg.demo.plugin.common;

import cbpg.demo.plugin.auth.provider.AuthContext;
import cbpg.demo.plugin.progdata.upload.UploadResult;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

@Service
public final class NotificationService {
    public static final String DEMO_NOTIFICATIONS_GROUP_ID = "Demo Notifications";

    public static NotificationService getInstance() {
        return ApplicationManager.getApplication().getService(NotificationService.class);
    }

    public void notifyLogout() {
        show(NotificationGroupManager.getInstance()
                .getNotificationGroup(DEMO_NOTIFICATIONS_GROUP_ID)
                .createNotification("Successfully logged out",
                        NotificationType.INFORMATION)
        );
    }

    public void notifyLogin(AuthContext context) {
        show(NotificationGroupManager.getInstance()
                .getNotificationGroup(DEMO_NOTIFICATIONS_GROUP_ID)
                .createNotification("Successfully logged in",
                        "Hi %s! :)".formatted(context.getName()),
                        NotificationType.INFORMATION)
        );
    }

    public void notifyUploadCompleted(UploadResult result) {
        show(NotificationGroupManager.getInstance()
                .getNotificationGroup(DEMO_NOTIFICATIONS_GROUP_ID)
                .createNotification("Upload completed",
                        "%s events uploaded.".formatted(result.eventsUploaded()),
                        NotificationType.INFORMATION)
        );
    }

    public void notifyError(String msg) {
        show(NotificationGroupManager.getInstance()
            .getNotificationGroup(DEMO_NOTIFICATIONS_GROUP_ID)
            .createNotification(msg,
                NotificationType.ERROR)
        );
    }

    private void show(Notification n) {
        show(n, null);
    }

    /**
     * should be used to show all notifications because of a bug in intellij? see
     * https://intellij-support.jetbrains.com/hc/en-us/community/posts/206766265-Notification-Balloon-not-shown-weird-scenario
     */
    private void show(Notification n, Project project) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> n.notify(project));
    }
}
