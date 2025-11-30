package pl.tenfajnybartek.funnyaddons.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.io.File;
import java.util.*;

public class PermissionsManager {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration cfg;

    public PermissionsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "guild-permissions.yml");
        reload();
    }

    public void reload() {
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                plugin.getLogger().severe("Nie można utworzyć guild-permissions.yml: " + e.getMessage());
            }
        }
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe("Nie można zapisać guild-permissions.yml: " + e.getMessage());
        }
    }

    public Set<PermissionType> getPermissions(String guildTag, UUID member) {
        String path = guildPath(guildTag) + "." + member.toString();
        List<String> list = cfg.getStringList(path);
        Set<PermissionType> set = EnumSet.noneOf(PermissionType.class);
        for (String s : list) {
            try {
                set.add(PermissionType.valueOf(s));
            } catch (Exception ignored) {}
        }
        return set;
    }

    public boolean hasPermission(String guildTag, UUID member, PermissionType type) {
        return getPermissions(guildTag, member).contains(type);
    }

    public void setPermission(String guildTag, UUID member, PermissionType type, boolean value) {
        Set<PermissionType> set = getPermissions(guildTag, member);
        if (value) set.add(type); else set.remove(type);
        saveSet(guildTag, member, set);
    }

    public void togglePermission(String guildTag, UUID member, PermissionType type) {
        Set<PermissionType> set = getPermissions(guildTag, member);
        if (set.contains(type)) set.remove(type); else set.add(type);
        saveSet(guildTag, member, set);
    }

    private void saveSet(String guildTag, UUID member, Set<PermissionType> set) {
        String path = guildPath(guildTag) + "." + member.toString();
        List<String> list = new ArrayList<>();
        for (PermissionType t : set) list.add(t.name());
        cfg.set(path, list);
        save();
    }

    private String guildPath(String guildTag) {
        return "guilds." + guildTag;
    }
}