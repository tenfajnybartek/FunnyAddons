package pl.tenfajnybartek.funnyaddons.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration cfg;
    private final File configFile;
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public FileConfiguration getConfig() {
        return cfg;
    }

    public String getMessage(String key) {
        return getMessage(key, "");
    }

    public String getMessage(String key, String defaultValue) {
        if (key == null) return defaultValue;
        String val;
        if (key.startsWith("messages.")) {
            val = cfg.getString(key, defaultValue);
        } else {
            val = cfg.getString("messages." + key, null);
            if (val == null) val = cfg.getString(key, defaultValue);
        }
        return val != null ? val : defaultValue;
    }

    public void reload() {
        plugin.reloadConfig();
        this.cfg = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

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
        String v = getMessage("location-list-message", "");
        if (v.isEmpty()) v = getMessage("locationList", "x: {X}, z: {Z} - odleglosc {DISTANCE} metrow.");
        return v;
    }

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

    public int getMemberPermissionsSize() {
        return getConfig().getInt("permissions.gui.member-permissions-size", 27);
    }

    public int getTitleMaxLength() {
        return getConfig().getInt("permissions.gui.title-max-length", 32);
    }

    public Material getIcon(String key, Material fallback) {
        String path = "permissions.icons." + key;
        String mat = getConfig().getString(path, null);
        if (mat == null || mat.isBlank()) return fallback;
        Material m = Material.matchMaterial(mat);
        return m != null ? m : fallback;
    }

    public List<String> getDefaultPerms(String role) {
        return getConfig().getStringList("permissions.defaults." + role);
    }

    public boolean isRelationEnabled() {
        return getConfig().getBoolean("permissions.relation.enable", false);
    }

    public String getRelationDefaultBehavior() {
        return getConfig().getString("permissions.relation.default-behavior", "follow_fg");
    }

    public String getPermsNoBreakMessage() { return getMessage("perms-no-break", "&cNie masz uprawnień do niszczenia na terenie tej gildii!"); }
    public String getPermsNoPlaceMessage() { return getMessage("perms-no-place", "&cNie masz uprawnień do stawiania bloków na terenie tej gildii!"); }
    public String getPermsNoOpenChestMessage() { return getMessage("perms-no-open-chest", "&cNie masz uprawnień do otwierania skrzyń na terenie tej gildii!"); }
    public String getPermsNoOpenEnderMessage() { return getMessage("perms-no-open-ender", "&cNie masz uprawnień do otwierania ender chesta na terenie tej gildii!"); }
    public String getPermsNoInteractMessage() { return getMessage("perms-no-interact", "&cNie masz uprawnień do używania przycisków/dźwigni/drzwi na terenie tej gildii!"); }
    public String getPermsNoBucketsMessage() { return getMessage("perms-no-buckets", "&cNie masz uprawnień do używania kubełków na terenie tej gildii!"); }
    public String getPermsNoFireMessage() { return getMessage("perms-no-fire", "&cNie masz uprawnień do używania flinta i stali (odpalenie) na terenie tej gildii!"); }
    public String getPermsNoFriendlyFireMessage() { return getMessage("perms-no-friendly-fire", "&cNie możesz obrażać członków swojej gildii!"); }

    public Component toComponent(String text) {
        if (text == null) return Component.empty();
        return LEGACY.deserialize(text);
    }

    public Component messageAsComponent(String key) {
        String s = getMessage(key, "");
        return LEGACY.deserialize(s);
    }
}