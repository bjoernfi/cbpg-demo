package cbpg.demo.plugin.progdata.pseudonym;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

public class PseudonymContext {
    private List<String> initialHashes = new ArrayList<>();
    private List<String> pseudonyms = new ArrayList<>();

    public List<String> getPseudonyms() {
        return pseudonyms;
    }

    public List<String> getInitialHashes() {
        return initialHashes;
    }

    @JsonIgnore
    public String getNewestPseudonym() {
        return !pseudonyms.isEmpty() ? pseudonyms.get(pseudonyms.size() - 1) : null;
    }
}
