package pl.tenfajnybartek.funnyaddons.managers;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration cfg;
    private final File configFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.cfg = plugin.getConfig();
    }

    /**
     * Zwraca obiekt FileConfiguration (aktualny konfig).
     */
    public FileConfiguration getConfig() {
        return cfg;
    }

    /**
     * Bezpieczne pobranie wiadomości.
     * Jeżeli podasz klucz "key" to najpierw próbuje "messages.key", potem "key".
     * Jeśli nic nie znajdzie zwraca pusty string.
     */
    public String getMessage(String key) {
        return getMessage(key, "");
    }

    /**
     * Jak wyżej, ale z wartością domyślną.
     */
    public String getMessage(String key, String defaultValue) {
        if (key == null) return defaultValue;
        // Jeżeli użytkownik podał już pełny path "messages.xxx" - używamy bez zmian
        String val;
        if (key.startsWith("messages.")) {
            val = cfg.getString(key, defaultValue);
        } else {
            val = cfg.getString("messages." + key, null);
            if (val == null) val = cfg.getString(key, defaultValue);
        }
        return val != null ? val : defaultValue;
    }

    /**
     * Przeładowanie configu z dysku.
     */
    public void reload() {
        plugin.reloadConfig();
        // ponownie wczytaj do lokalnego pola (dla bezpieczeństwa)
        this.cfg = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    // ---------- Convenience getters dla często używanych wiadomości ----------
    public String getInGuildMessage() {
        return getMessage("in-guild-message", "&cNie możesz użyć tej komendy, będąc w gildii!");
    }

    public String getNoPermissionMessage() {
        return getMessage("no-permission", "&cNie masz uprawnień do tej akcji!");
    }

    public String getNoRegionMessage() {
        return getMessage("no-region", "&cGildia/region nie odnaleziony!");
    }

    public String getFreeSpaceMessage() {
        return getMessage("free-space-message", "Lista wolnych lokalizacji na gildie");
    }

    public String getLocationListMessage() {
        // zachowujemy zgodność z poprzednimi nazwami kluczy
        String v = getMessage("location-list-message", "");
        if (v.isEmpty()) v = getMessage("locationList", "x: {X}, z: {Z} - odleglosc {DISTANCE} metrow.");
        return v;
    }

    // ---------- Pozostałe getters (Twoje dotychczasowe) ----------
    public int getMinBound() {
        return getConfig().getInt("bound.min");
    }

    public int getMaxBound() {
        return getConfig().getInt("bound.max");
    }

    public int getGenerationDelay() {
        return getConfig().getInt("generationTime", 30);
    }

    public int getListSize() {
        return getConfig().getInt("listSize", 20);
    }

    public BossBarMessage getBossBarMessage(String relation) {
        ConfigurationSection section = getConfig().getConfigurationSection("bossbar.messages." + relation);
        if (section == null) return null;
        return new BossBarMessage(
                section.getString("message", ""),
                section.getString("color", ""),
                section.getString("style", "")
        );
    }

    public String getActionBarMessage(String relation) {
        return getConfig().getString("actionbar.messages." + relation, "");
    }

    public Map<String, BossBarMessage> getBossBarMessages() {
        Map<String, BossBarMessage> map = new HashMap<>();
        ConfigurationSection messages = getConfig().getConfigurationSection("bossbar.messages");
        if (messages != null) {
            for (String key : messages.getKeys(false)) {
                BossBarMessage bbMsg = getBossBarMessage(key);
                if (bbMsg != null) map.put(key, bbMsg);
            }
        }
        return map;
    }

    public Map<String, String> getActionBarMessages() {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection messages = getConfig().getConfigurationSection("actionbar.messages");
        if (messages != null) {
            for (String key : messages.getKeys(false)) {
                map.put(key, messages.getString(key, ""));
            }
        }
        return map;
    }

    public String getBossBarMode() {
        return getConfig().getString("bossbar.mode", "BOSS_BAR");
    }

    public String getBossBarPermission() {
        return getConfig().getString("bossbar.reload-permission", "tguildterrainbar.reload");
    }

    public int getBossBarRunnableTime() {
        return getConfig().getInt("bossbar.runnable-time", 15);
    }

    public boolean isBossBarProgressBasedOnDistance() {
        return getConfig().getBoolean("bossbar.progress-based-on-distance", true);
    }

    public static class BossBarMessage {
        public final String message;
        public final String color;
        public final String style;

        public BossBarMessage(String message, String color, String style) {
            this.message = message;
            this.color = color;
            this.style = style;
        }
    }
}