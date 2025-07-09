package cbpg.demo.plugin.progdata.pseudonym;

import cbpg.demo.plugin.common.KeyValueStore;
import cbpg.demo.plugin.common.PropertyKeyGenerator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public final class PseudonymService {
    private final long ITERATIONS = (long) Math.ceil(Math.pow(2, 26.921));

    public static PseudonymService getInstance() {
        return ApplicationManager.getApplication().getService(PseudonymService.class);
    }

    public PseudonymContext getPseudonymContext(String username) {
        var store = KeyValueStore.getInstance();
        return store.getValue(getStoreKey(username), PseudonymContext.class);
    }

    private String getStoreKey(String username) {
        return "%s.%s".formatted(PropertyKeyGenerator.prefix("pseudonyms"), username);
    }

    /**
     * generates a pseudonym based on the input credentials and stores it in the history for the
     * given user. this behavior enables scenarios in which more than 1 user is using the same
     * device. however, we assume that the account username never changes.
     */
    public void generateAndStore(String username, char[] password) {
        var pseudonymContext = getPseudonymContext(username);
        if (pseudonymContext == null) {
            pseudonymContext = new PseudonymContext();
        }

        try {
            var digest = MessageDigest.getInstance("SHA-256");

            byte[] initialHash = createInitialHash(username, password, digest);
            var initialHashHex = bytesToHex(initialHash);

            var shouldGeneratePseudonym = !pseudonymContext.getInitialHashes().contains(initialHashHex);
            if (shouldGeneratePseudonym) {
                var hash = initialHash;

                // we skip the first one as we already have the initial hash
                for(var i = 0; i < ITERATIONS - 1; i++) {
                    hash = digest.digest(hash);
                }

                var pseudonymHex = bytesToHex(hash);
                pseudonymContext.getPseudonyms().add(pseudonymHex);
                pseudonymContext.getInitialHashes().add(initialHashHex);

                var store = KeyValueStore.getInstance();
                store.setValue(getStoreKey(username), pseudonymContext);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        var hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * Creates the input and hashes it using the provided digest
     */
    private byte[] createInitialHash(String username, char[] password, MessageDigest digest) {
        var usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        var passwordBytes = toBytes(password, StandardCharsets.UTF_8);

        var plain = new byte[usernameBytes.length + passwordBytes.length];

        for (int i = 0; i < passwordBytes.length; i++) {
            plain[i] = passwordBytes[i];
        }

        for (int i = 0; i < usernameBytes.length; i++) {
            plain[passwordBytes.length + i] = usernameBytes[i];
        }

        return digest.digest(plain);
    }

    private byte[] toBytes(char[] chars, Charset charset) {
        var byteBuffer = charset.encode(CharBuffer.wrap(chars));
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(),
            byteBuffer.limit());

        // clear sensitive data
        Arrays.fill(byteBuffer.array(), (byte) 0);
        return bytes;
    }
}
