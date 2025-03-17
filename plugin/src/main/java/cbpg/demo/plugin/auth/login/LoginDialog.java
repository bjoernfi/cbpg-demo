package cbpg.demo.plugin.auth.login;

import com.google.common.base.Strings;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.EventDispatcher;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.JBUI.Borders;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LoginDialog extends DialogWrapper {

    private final JPasswordField inputPassword;
    private final JTextField inputUsername;
    private final JPanel contentPanel;

    private LoginModel model;

    private final EventDispatcher<LoginListener> eventDispatcher =
        EventDispatcher.create(LoginListener.class);

    public LoginDialog(LoginModel model) {
        super(true);
        setTitle("Login");
        setResizable(false);

        this.model = model;

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gridBagConstraints;

        if (!Strings.isNullOrEmpty(model.getContextMessage())) {
            var labelContext = new JBLabel(model.getContextMessage());
            labelContext.setBorder(Borders.emptyBottom(10)); // 10-pixel margin at the bottom
            gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = 0;
            gridBagConstraints.gridwidth = 2;
            contentPanel.add(labelContext, gridBagConstraints);
        }

        var labelUsername = new JBLabel("Username:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = JBUI.insetsRight(10);
        contentPanel.add(labelUsername, gridBagConstraints);

        var labelPassword = new JBLabel("Password:");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = JBUI.insetsRight(10);
        contentPanel.add(labelPassword, gridBagConstraints);

        inputUsername = new JBTextField(model.getUsername(), 10);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        contentPanel.add(inputUsername, gridBagConstraints);

        inputPassword = new JBPasswordField();
        inputPassword.setColumns(10);
        inputPassword.setText(model.getPassword());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        contentPanel.add(inputPassword, gridBagConstraints);

        init();

        inputUsername.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                eventDispatcher.getMulticaster().usernameChanged();
            }
        });

        inputPassword.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                warn();
            }

            public void removeUpdate(DocumentEvent e) {
                warn();
            }

            public void insertUpdate(DocumentEvent e) {
                warn();
            }

            public void warn() {
                eventDispatcher.getMulticaster().passwordChanged();
            }
        });
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        myOKAction = new LoginAction();
        setCancelButtonText("Cancel");
        return new Action[]{getOKAction(), getCancelAction()};
    }

    @Override
    protected ValidationInfo doValidate() {
        if (model.isLoggingIn()) {
            return new ValidationInfo("");
        }

        if (inputUsername.getText().isEmpty()) {
            return new ValidationInfo("Enter username", inputUsername);
        }

        if (inputPassword.getPassword().length == 0) {
            return new ValidationInfo("Enter password", inputPassword);
        }

        if (model.getLastRemoteError() != null) {
            return new ValidationInfo(model.getLastRemoteError()).withOKEnabled();
        }

        return null;
    }

    public void setLoginModel(LoginModel model) {
        this.model = model;
        var v = doValidateAll();
        updateErrorInfo(v);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPanel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return inputUsername;
    }

    public void addListener(LoginListener listener) {
        eventDispatcher.addListener(listener);
    }

    public JTextField getUsernameInput() {
        return inputUsername;
    }

    public JPasswordField getPasswordInput() {
        return inputPassword;
    }

    private class LoginAction extends DialogWrapperAction {

        protected LoginAction() {
            super("Login");
            putValue(DEFAULT_ACTION, Boolean.TRUE);
        }

        @Override
        protected void doAction(ActionEvent e) {
            List<ValidationInfo> infoList = doValidateAll();
            if (!infoList.isEmpty()) {
                ValidationInfo info = infoList.get(0);
                if (info.component != null && info.component.isVisible()) {
                    IdeFocusManager.getInstance(null).requestFocus(info.component, true);
                }

                updateErrorInfo(infoList);
                startTrackingValidation();
                if (ContainerUtil.exists(infoList, info1 -> !info1.okEnabled)) {
                    return;
                }
            }

            eventDispatcher.getMulticaster().loginRequested();
        }
    }
}
