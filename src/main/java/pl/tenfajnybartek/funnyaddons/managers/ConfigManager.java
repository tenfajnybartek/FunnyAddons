package pl.tenfajnybartek.funnyaddons.managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tenfajnybartek.funnyaddons.config.BossBarConfig;
import pl.tenfajnybartek.funnyaddons.config.MessagesConfig;
import pl.tenfajnybartek.funnyaddons.config.PanelConfig;
import pl.tenfajnybartek.funnyaddons.config.PermissionsConfig;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration cfg;
    private FileConfiguration panelCfg;
    private final File configFile;
    private final File panelFile;

    // Config sub-classes for organized access
    private MessagesConfig messagesConfig;
    private PermissionsConfig permissionsConfig;
    private BossBarConfig bossBarConfig;
    private PanelConfig panelConfig;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.panelFile = new File(plugin.getDataFolder(), "panel.yml");
        this.cfg = YamlConfiguration.loadConfiguration(this.configFile);

        // Save and load panel.yml
        saveDefaultPanelConfig();
        this.panelCfg = YamlConfiguration.loadConfiguration(this.panelFile);

        initConfigFacades();
    }

    private void saveDefaultPanelConfig() {
        if (!panelFile.exists()) {
            try (InputStream in = plugin.getResource("panel.yml")) {
                if (in != null) {
                    Files.copy(in, panelFile.toPath());
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not save default panel.yml: " + e.getMessage());
            }
        }
    }

    private void initConfigFacades() {
        this.messagesConfig = new MessagesConfig(this.cfg);
        this.permissionsConfig = new PermissionsConfig(this.cfg);
        this.bossBarConfig = new BossBarConfig(this.cfg);
        this.panelConfig = new PanelConfig(this.panelCfg);
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

    /**
     * Gets the PanelConfig facade for guild panel-related configuration.
     */
    public PanelConfig getPanelConfig() {
        return panelConfig;
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
        this.panelCfg = YamlConfiguration.loadConfiguration(this.panelFile);
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
     * @deprecated Since version 1.0.0-SNAPSHOT. Use {@link BossBarConfig.BossBarMessage} from
     * {@link #getBossBarConfig()} instead.
     * <p>Example migration:</p>
     * <pre>
     * // Old way:
     * ConfigManager.BossBarMessage msg = configManager.getBossBarMessage("MEMBER");
     * String text = msg.message;
     *
     * // New way:
     * BossBarConfig.BossBarMessage msg = configManager.getBossBarConfig().getBossBarMessage("MEMBER");
     * String text = msg.getMessage();
     * </pre>
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

    // ---------- PermissionType-based Dynamic Lookups ----------

    /**
     * Gets the permission denial message for a specific PermissionType.
     * <p>
     * Uses the permission type's message key to look up the message in config.
     *
     * @param type The permission type to get the denial message for
     * @return The configured denial message for the permission type
     */
    public String getPermsMessageFor(PermissionType type) {
        return messagesConfig.getPermsMessageFor(type);
    }

    /**
     * Gets the permission denial message as a Component for a specific PermissionType.
     *
     * @param type The permission type to get the denial message for
     * @return The configured denial message as a Component
     */
    public Component getPermsComponentFor(PermissionType type) {
        return messagesConfig.getPermsComponentFor(type);
    }

    /**
     * Gets the slot for a permission type from config.
     *
     * @param type The permission type
     * @return The configured slot number, or the default slot from the enum
     */
    public int getSlotFor(PermissionType type) {
        return permissionsConfig.getSlotFor(type);
    }

    /**
     * Gets the display name for a permission type from config.
     *
     * @param type The permission type
     * @return The configured display name, or the default name from the enum
     */
    public String getDisplayNameFor(PermissionType type) {
        return permissionsConfig.getDisplayNameFor(type);
    }

    /**
     * Gets the icon Material for a permission type from config.
     *
     * @param type The permission type
     * @return The configured Material icon, or the default icon from the enum
     */
    public Material getIconFor(PermissionType type) {
        return permissionsConfig.getIconFor(type);
    }

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