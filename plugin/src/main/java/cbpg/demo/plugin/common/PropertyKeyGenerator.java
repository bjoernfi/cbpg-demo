package cbpg.demo.plugin.common;

public class PropertyKeyGenerator {

    public static String prefix(String key) {
        return "cbpg.demo.plugin.%s".formatted(key);
    }
}
