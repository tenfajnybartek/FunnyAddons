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

    public MessagesConfig getMessagesConfig() {
        return messagesConfig;
    }

    public PermissionsConfig getPermissionsConfig() {
        return permissionsConfig;
    }

    public BossBarConfig getBossBarConfig() {
        return bossBarConfig;
    }

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


    public int getMemberPermissionsSize() {
        return permissionsConfig.getMemberPermissionsSize();
    }

    public int getTitleMaxLength() {
        return permissionsConfig.getTitleMaxLength();
    }

    public Material getIcon(String key, Material fallback) {
        return permissionsConfig.getIcon(key, fallback);
    }

    public List<String> getDefaultPerms(String role) {
        return permissionsConfig.getDefaultPerms(role);
    }

    public boolean isRelationEnabled() {
        return permissionsConfig.isRelationEnabled();
    }

    public String getRelationDefaultBehavior() {
        return permissionsConfig.getRelationDefaultBehavior();
    }


    public String getPermsNoBreakMessage() { return messagesConfig.getPermsNoBreakMessage(); }
    public String getPermsNoPlaceMessage() { return messagesConfig.getPermsNoPlaceMessage(); }
    public String getPermsNoOpenChestMessage() { return messagesConfig.getPermsNoOpenChestMessage(); }
    public String getPermsNoOpenEnderMessage() { return messagesConfig.getPermsNoOpenEnderMessage(); }
    public String getPermsNoInteractMessage() { return messagesConfig.getPermsNoInteractMessage(); }
    public String getPermsNoBucketsMessage() { return messagesConfig.getPermsNoBucketsMessage(); }
    public String getPermsNoFireMessage() { return messagesConfig.getPermsNoFireMessage(); }
    public String getPermsNoFriendlyFireMessage() { return messagesConfig.getPermsNoFriendlyFireMessage(); }

    public Component getPermsNoBreakComponent() { return messagesConfig.getPermsNoBreakComponent(); }
    public Component getPermsNoPlaceComponent() { return messagesConfig.getPermsNoPlaceComponent(); }
    public Component getPermsNoOpenChestComponent() { return messagesConfig.getPermsNoOpenChestComponent(); }
    public Component getPermsNoOpenEnderComponent() { return messagesConfig.getPermsNoOpenEnderComponent(); }
    public Component getPermsNoInteractComponent() { return messagesConfig.getPermsNoInteractComponent(); }
    public Component getPermsNoBucketsComponent() { return messagesConfig.getPermsNoBucketsComponent(); }
    public Component getPermsNoFireComponent() { return messagesConfig.getPermsNoFireComponent(); }
    public Component getPermsNoFriendlyFireComponent() { return messagesConfig.getPermsNoFriendlyFireComponent(); }

    public String getPermsMessageFor(PermissionType type) {
        return messagesConfig.getPermsMessageFor(type);
    }

    public Component getPermsComponentFor(PermissionType type) {
        return messagesConfig.getPermsComponentFor(type);
    }

    public int getSlotFor(PermissionType type) {
        return permissionsConfig.getSlotFor(type);
    }

    public String getDisplayNameFor(PermissionType type) {
        return permissionsConfig.getDisplayNameFor(type);
    }

    public Material getIconFor(PermissionType type) {
        return permissionsConfig.getIconFor(type);
    }

    public Component toComponent(String text) {
        return messagesConfig.toComponent(text);
    }

    public Component messageAsComponent(String key) {
        return messagesConfig.getMessageAsComponent(key);
    }
}