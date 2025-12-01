package pl.tenfajnybartek.funnyaddons.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration facade for bossbar-related config and messages.
 */
public class BossBarConfig {

    private final FileConfiguration cfg;

    public BossBarConfig(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    // ---------- BossBar Settings ----------

    /**
     * Gets the display mode (BOSS_BAR or ACTION_BAR).
     */
    public String getMode() {
        return cfg.getString("bossbar.mode", "BOSS_BAR");
    }

    /**
     * Gets the runnable interval in ticks.
     */
    public int getRunnableTime() {
        return cfg.getInt("bossbar.runnable-time", 15);
    }

    /**
     * Checks if progress should be based on distance.
     */
    public boolean isProgressBasedOnDistance() {
        return cfg.getBoolean("bossbar.progress-based-on-distance", true);
    }

    /**
     * Gets the reload permission.
     */
    public String getReloadPermission() {
        return cfg.getString("bossbar.reload-permission", "tguildterrainbar.reload");
    }

    // ---------- BossBar Messages ----------

    /**
     * Gets a BossBarMessage for a specific relation type.
     */
    public BossBarMessage getBossBarMessage(String relation) {
        ConfigurationSection section = cfg.getConfigurationSection("bossbar.messages." + relation);
        if (section == null) return null;
        return new BossBarMessage(
                section.getString("message", ""),
                section.getString("color", ""),
                section.getString("style", "")
        );
    }

    /**
     * Gets all BossBar messages as a map keyed by relation type.
     */
    public Map<String, BossBarMessage> getAllBossBarMessages() {
        Map<String, BossBarMessage> map = new HashMap<>();
        ConfigurationSection messages = cfg.getConfigurationSection("bossbar.messages");
        if (messages != null) {
            for (String key : messages.getKeys(false)) {
                BossBarMessage bbMsg = getBossBarMessage(key);
                if (bbMsg != null) map.put(key, bbMsg);
            }
        }
        return map;
    }

    // ---------- ActionBar Messages ----------

    /**
     * Gets an ActionBar message for a specific relation type.
     */
    public String getActionBarMessage(String relation) {
        return cfg.getString("actionbar.messages." + relation, "");
    }

    /**
     * Gets all ActionBar messages as a map keyed by relation type.
     */
    public Map<String, String> getAllActionBarMessages() {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection messages = cfg.getConfigurationSection("actionbar.messages");
        if (messages != null) {
            for (String key : messages.getKeys(false)) {
                map.put(key, messages.getString(key, ""));
            }
        }
        return map;
    }

    /**
     * Represents a BossBar message configuration with message, color, and style.
     */
    public static class BossBarMessage {
        private final String message;
        private final String color;
        private final String style;

        public BossBarMessage(String message, String color, String style) {
            this.message = message;
            this.color = color;
            this.style = style;
        }

        public String getMessage() {
            return message;
        }

        public String getColor() {
            return color;
        }

        public String getStyle() {
            return style;
        }
    }
}
