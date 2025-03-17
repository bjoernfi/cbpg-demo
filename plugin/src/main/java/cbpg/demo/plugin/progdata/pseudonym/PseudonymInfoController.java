package cbpg.demo.plugin.progdata.pseudonym;

import cbpg.demo.plugin.auth.consent.ConsentStore;

public class PseudonymInfoController {

    public void showPseudonymInfo() {
        var pseudonymService = PseudonymService.getInstance();
        var consentStore = ConsentStore.getInstance();

        var consentContext = consentStore.getContext();
        var pseudonymContext = pseudonymService.getPseudonymContext(consentContext.loginName());

        var dialog = new PseudonymInfoDialog(pseudonymContext);
        dialog.show();
    }
}