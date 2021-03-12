package net.eltown.quadplots.components.language;

import cn.nukkit.utils.Config;
import net.eltown.quadplots.QuadPlots;

import java.util.HashMap;
import java.util.Map;

public class Language {

    private static Map<String, String> messages = new HashMap<>();
    private static String prefix;

    public static void init(QuadPlots plugin) {
        messages.clear();
        plugin.saveResource("messages.yml");
        Config m = new Config(plugin.getDataFolder() + "/messages.yml");
        for (Map.Entry<String, Object> map : m.getAll().entrySet()) {
            String key = map.getKey();
            if (map.getValue() instanceof String) {
                String val = (String) map.getValue();
                messages.put(key, val);
            }
        }
        prefix = m.getString("prefix");
    }

    public static String get(String key, Object... replacements) {
        String message = prefix.replace("&", "§") + messages.getOrDefault(key, "null").replace("&", "§");

        int i = 0;
        for (Object replacement : replacements) {
            message = message.replace("[" + i + "]", String.valueOf(replacement));
            i++;
        }

        return message;
    }

    public static String getNP(String key, Object... replacements) {
        String message = messages.getOrDefault(key, "null").replace("&", "§");

        int i = 0;
        for (Object replacement : replacements) {
            message = message.replace("[" + i + "]", String.valueOf(replacement));
            i++;
        }

        return message;
    }

}
