package pl.tenfajnybartek.funnyaddons.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class PermissionsStorage {

    public static final String GUILDS_PATH = "guilds";

    private final JavaPlugin plugin;
    private final File file;
    private final Logger logger;
    private FileConfiguration cfg;

    public PermissionsStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.file = new File(plugin.getDataFolder(), "guild-permissions.yml");
        load();
    }

    public void load() {
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                logger.severe("Nie można utworzyć guild-permissions.yml: " + e.getMessage());
            }
        }
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void reload() {
        load();
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            logger.severe("Nie można zapisać guild-permissions.yml: " + e.getMessage());
        }
    }

    public Set<PermissionType> readPermissions(String guildTag, UUID member) {
        String path = buildMemberPath(guildTag, member);
        List<String> list = cfg.getStringList(path);
        Set<PermissionType> set = EnumSet.noneOf(PermissionType.class);
        for (String s : list) {
            try {
                set.add(PermissionType.valueOf(s));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return set;
    }

    public void writePermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        String path = buildMemberPath(guildTag, member);
        List<String> list = new ArrayList<>();
        for (PermissionType t : permissions) {
            list.add(t.name());
        }
        cfg.set(path, list);
        save();
    }

    public Set<UUID> readGuildMembers(String guildTag) {
        String path = buildGuildPath(guildTag);
        var section = cfg.getConfigurationSection(path);
        if (section == null) {
            return Collections.emptySet();
        }
        Set<UUID> members = new HashSet<>();
        for (String key : section.getKeys(false)) {
            try {
                members.add(UUID.fromString(key));
            } catch (IllegalArgumentException ignored) {
            }
        }
        return members;
    }

    public Map<UUID, Set<PermissionType>> readAllGuildPermissions(String guildTag) {
        Set<UUID> members = readGuildMembers(guildTag);
        Map<UUID, Set<PermissionType>> result = new HashMap<>();
        for (UUID member : members) {
            result.put(member, readPermissions(guildTag, member));
        }
        return result;
    }

    public void clearMemberPermissions(String guildTag, UUID member) {
        String path = buildMemberPath(guildTag, member);
        cfg.set(path, null);
        save();
    }

    public void clearGuildPermissions(String guildTag) {
        String path = buildGuildPath(guildTag);
        cfg.set(path, null);
        save();
    }

    public boolean hasStoredPermissions(String guildTag, UUID member) {
        String path = buildMemberPath(guildTag, member);
        return cfg.contains(path);
    }

    private String buildGuildPath(String guildTag) {
        return GUILDS_PATH + "." + guildTag;
    }

    private String buildMemberPath(String guildTag, UUID member) {
        return buildGuildPath(guildTag) + "." + member.toString();
    }

    public FileConfiguration getConfiguration() {
        return cfg;
    }
}
