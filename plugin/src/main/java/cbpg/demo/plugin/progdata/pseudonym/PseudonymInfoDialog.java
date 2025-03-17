package cbpg.demo.plugin.progdata.pseudonym;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import cbpg.demo.plugin.common.LogMessage;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import org.jetbrains.annotations.Nullable;

public class PseudonymInfoDialog extends DialogWrapper {

    private static final Logger LOG = Logger.getInstance(PseudonymInfoDialog.class);

    private JPanel contentPanel;
    private JTextArea pseudonymsTextArea;

    public PseudonymInfoDialog(PseudonymContext pseudonymContext) {
        super(true);

        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        var labelComment = new JBLabel("Pseudonyms:");
        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        contentPanel.add(labelComment, gridBagConstraints);

        pseudonymsTextArea = new JBTextArea(formatPseudonyms(pseudonymContext));
        pseudonymsTextArea.setRows(5);
        pseudonymsTextArea.setWrapStyleWord(true);
        pseudonymsTextArea.setLineWrap(true);
        pseudonymsTextArea.setEditable(false);
        var pseudonymsScrollPane = ScrollPaneFactory.createScrollPane(pseudonymsTextArea);
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = JBUI.insetsTop(5);
        contentPanel.add(pseudonymsScrollPane, gridBagConstraints);

        var infoLabel = new JBLabel(
            "<html>These are the pseudonyms under which your data has been stored.<br>Please send them to us if you wish to have your data deleted.</html>");
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = JBUI.insetsTop(10);
        contentPanel.add(infoLabel, gridBagConstraints);

        setTitle("Your Pseudonyms");
        setOKButtonText("Copy");
        setCancelButtonText("Close");
        setResizable(true);
        init();
    }

    private String formatPseudonyms(PseudonymContext pseudonymContext) {
        StringBuilder result = new StringBuilder();
        for (String pseudonym : pseudonymContext.getPseudonyms()) {
            result.append("-").append(pseudonym).append("\n");
        }
        return result.toString();
    }

    @Override
    protected void doOKAction() {
        try {
            StringSelection selection = new StringSelection(pseudonymsTextArea.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            setOKButtonText(" Copied ");
        } catch (Exception e) {
            LOG.error(LogMessage.from("Failed to copy pseudonyms to clipboard"), e);
        }
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return pseudonymsTextArea;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPanel;
    }

}
