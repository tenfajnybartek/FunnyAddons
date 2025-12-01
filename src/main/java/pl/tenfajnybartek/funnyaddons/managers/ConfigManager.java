package pl.tenfajnybartek.funnyaddons.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tenfajnybartek.funnyaddons.config.BossBarConfig;
import pl.tenfajnybartek.funnyaddons.config.MessagesConfig;
import pl.tenfajnybartek.funnyaddons.config.PermissionsConfig;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration cfg;
    private final File configFile;

    // Config sub-classes for organized access
    private MessagesConfig messagesConfig;
    private PermissionsConfig permissionsConfig;
    private BossBarConfig bossBarConfig;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.configFile);
        initConfigFacades();
    }

    private void initConfigFacades() {
        this.messagesConfig = new MessagesConfig(this.cfg);
        this.permissionsConfig = new PermissionsConfig(this.cfg);
        this.bossBarConfig = new BossBarConfig(this.cfg);
    }

    // ---------- Factory methods for config sub-classes ----------

    /**
     * Gets the MessagesConfig facade for message-related configuration.
     */
    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    /**
     * Gets the PermissionsConfig facade for permissions-related configuration.
     */
    public PermissionsConfig getPermissionsConfig() {
        return permissionsConfig;
    }

    /**
     * Gets the BossBarConfig facade for bossbar-related configuration.
     */
    public BossBarConfig getBossBarConfig() {
        return bossBarConfig;
    }

    public FileConfiguration getConfig() {
        return cfg;
    }

    public String getMessage(String key) {
        return messagesConfig.getMessage(key);
    }

    public String getMessage(String key, String defaultValue) {
        return messagesConfig.getMessage(key, defaultValue);
    }

    public void reload() {
        plugin.reloadConfig();
        this.cfg = YamlConfiguration.loadConfiguration(this.configFile);
        initConfigFacades();
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    // ---------- Convenience getters dla często używanych wiadomości ----------
    // (Delegating to MessagesConfig for backward compatibility)
    public String getInGuildMessage() {
        return messagesConfig.getInGuildMessage();
    }

    public String getNoPermissionMessage() {
        return messagesConfig.getNoPermissionMessage();
    }

    public String getNoRegionMessage() {
        return messagesConfig.getNoRegionMessage();
    }

    public String getFreeSpaceMessage() {
        return messagesConfig.getFreeSpaceMessage();
    }

    public String getLocationListMessage() {
        return messagesConfig.getLocationListMessage();
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

    // ---------- BossBar methods (delegating to BossBarConfig for backward compatibility) ----------
    public BossBarMessage getBossBarMessage(String relation) {
        BossBarConfig.BossBarMessage msg = bossBarConfig.getBossBarMessage(relation);
        if (msg == null) return null;
        return new BossBarMessage(msg.getMessage(), msg.getColor(), msg.getStyle());
    }

    public String getActionBarMessage(String relation) {
        return bossBarConfig.getActionBarMessage(relation);
    }

    public Map<String, BossBarMessage> getBossBarMessages() {
        Map<String, BossBarMessage> map = new HashMap<>();
        Map<String, BossBarConfig.BossBarMessage> srcMap = bossBarConfig.getAllBossBarMessages();
        for (Map.Entry<String, BossBarConfig.BossBarMessage> entry : srcMap.entrySet()) {
            BossBarConfig.BossBarMessage msg = entry.getValue();
            map.put(entry.getKey(), new BossBarMessage(msg.getMessage(), msg.getColor(), msg.getStyle()));
        }
        return map;
    }

    public Map<String, String> getActionBarMessages() {
        return bossBarConfig.getAllActionBarMessages();
    }

    public String getBossBarMode() {
        return bossBarConfig.getMode();
    }

    public String getBossBarPermission() {
        return bossBarConfig.getReloadPermission();
    }

    public int getBossBarRunnableTime() {
        return bossBarConfig.getRunnableTime();
    }

    public boolean isBossBarProgressBasedOnDistance() {
        return bossBarConfig.isProgressBasedOnDistance();
    }

    /**
     * @deprecated Use BossBarConfig.BossBarMessage from getBossBarConfig() instead.
     */
    @Deprecated
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

    // ---------- Permissions methods (delegating to PermissionsConfig for backward compatibility) ----------

    // Rozmiar GUI dla MemberPermissionsGUI (domyślnie 27)
    public int getMemberPermissionsSize() {
        return permissionsConfig.getMemberPermissionsSize();
    }

    // Maksymalna długość tytułu inventory (klient) - domyślnie 32
    public int getTitleMaxLength() {
        return permissionsConfig.getTitleMaxLength();
    }

    // Pobierz Material z config.permissions.icons.{key} z fallbackiem
    public Material getIcon(String key, Material fallback) {
        return permissionsConfig.getIcon(key, fallback);
    }

    // Domyślne uprawnienia dla roli (member/officer/owner) - lista Stringów
    public List<String> getDefaultPerms(String role) {
        return permissionsConfig.getDefaultPerms(role);
    }

    // Czy relacje mają być włączone (relation.enable)
    public boolean isRelationEnabled() {
        return permissionsConfig.isRelationEnabled();
    }

    // domyślne zachowanie relacji (follow_fg / follow_addon)
    public String getRelationDefaultBehavior() {
        return permissionsConfig.getRelationDefaultBehavior();
    }

    // ---------- Permission messages (delegating to MessagesConfig for backward compatibility) ----------

    public String getPermsNoBreakMessage() { return messagesConfig.getPermsNoBreakMessage(); }
    public String getPermsNoPlaceMessage() { return messagesConfig.getPermsNoPlaceMessage(); }
    public String getPermsNoOpenChestMessage() { return messagesConfig.getPermsNoOpenChestMessage(); }
    public String getPermsNoOpenEnderMessage() { return messagesConfig.getPermsNoOpenEnderMessage(); }
    public String getPermsNoInteractMessage() { return messagesConfig.getPermsNoInteractMessage(); }
    public String getPermsNoBucketsMessage() { return messagesConfig.getPermsNoBucketsMessage(); }
    public String getPermsNoFireMessage() { return messagesConfig.getPermsNoFireMessage(); }
    public String getPermsNoFriendlyFireMessage() { return messagesConfig.getPermsNoFriendlyFireMessage(); }

    // Permission messages as Components
    public Component getPermsNoBreakComponent() { return messagesConfig.getPermsNoBreakComponent(); }
    public Component getPermsNoPlaceComponent() { return messagesConfig.getPermsNoPlaceComponent(); }
    public Component getPermsNoOpenChestComponent() { return messagesConfig.getPermsNoOpenChestComponent(); }
    public Component getPermsNoOpenEnderComponent() { return messagesConfig.getPermsNoOpenEnderComponent(); }
    public Component getPermsNoInteractComponent() { return messagesConfig.getPermsNoInteractComponent(); }
    public Component getPermsNoBucketsComponent() { return messagesConfig.getPermsNoBucketsComponent(); }
    public Component getPermsNoFireComponent() { return messagesConfig.getPermsNoFireComponent(); }
    public Component getPermsNoFriendlyFireComponent() { return messagesConfig.getPermsNoFriendlyFireComponent(); }

    // ---------- Component utilities (delegating to MessagesConfig) ----------

    // Parsowanie legacy-colored string -> Component
    public Component toComponent(String text) {
        return messagesConfig.toComponent(text);
    }

    // Pobierz komunikat jako Component (z "messages." prefix)
    public Component messageAsComponent(String key) {
        return messagesConfig.getMessageAsComponent(key);
    }
}