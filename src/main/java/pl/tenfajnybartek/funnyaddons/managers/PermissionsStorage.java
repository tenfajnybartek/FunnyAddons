package pl.tenfajnybartek.funnyaddons.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Storage layer for guild permissions - handles all YAML file IO operations.
 * <p>
 * This class is responsible for:
 * <ul>
 *   <li>Loading and saving permission data to guild-permissions.yml</li>
 *   <li>File management (creation, reload)</li>
 *   <li>Converting between storage format and typed PermissionType enums</li>
 * </ul>
 * <p>
 * The PermissionsManager uses this class internally for persistence,
 * separating IO concerns from business logic.
 */
public class PermissionsStorage {

    /**
     * Configuration key prefix for guilds section.
     */
    public static final String GUILDS_PATH = "guilds";

    private final JavaPlugin plugin;
    private final File file;
    private final Logger logger;
    private FileConfiguration cfg;

    /**
     * Creates a new PermissionsStorage instance.
     *
     * @param plugin The plugin instance for file path resolution and logging
     */
    public PermissionsStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.file = new File(plugin.getDataFolder(), "guild-permissions.yml");
        load();
    }

    /**
     * Loads the permissions configuration from file.
     * Creates the file if it doesn't exist.
     */
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

    /**
     * Reloads the permissions configuration from file.
     * This clears any unsaved changes.
     */
    public void reload() {
        load();
    }

    /**
     * Saves the current configuration to file.
     */
    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            logger.severe("Nie można zapisać guild-permissions.yml: " + e.getMessage());
        }
    }

    /**
     * Reads all permissions for a specific guild member from storage.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     * @return A set of PermissionType values stored for the member
     */
    public Set<PermissionType> readPermissions(String guildTag, UUID member) {
        String path = buildMemberPath(guildTag, member);
        List<String> list = cfg.getStringList(path);
        Set<PermissionType> set = EnumSet.noneOf(PermissionType.class);
        for (String s : list) {
            try {
                set.add(PermissionType.valueOf(s));
            } catch (IllegalArgumentException ignored) {
                // Skip invalid permission types
            }
        }
        return set;
    }

    /**
     * Writes all permissions for a specific guild member to storage.
     *
     * @param guildTag    The guild tag identifier
     * @param member      The UUID of the guild member
     * @param permissions The set of permissions to store
     */
    public void writePermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        String path = buildMemberPath(guildTag, member);
        List<String> list = new ArrayList<>();
        for (PermissionType t : permissions) {
            list.add(t.name());
        }
        cfg.set(path, list);
        save();
    }

    /**
     * Reads all guild members with stored permissions for a specific guild.
     *
     * @param guildTag The guild tag identifier
     * @return A set of member UUIDs that have stored permissions
     */
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
                // Skip invalid UUIDs
            }
        }
        return members;
    }

    /**
     * Reads all stored permissions for all members of a guild.
     *
     * @param guildTag The guild tag identifier
     * @return A map of member UUIDs to their permission sets
     */
    public Map<UUID, Set<PermissionType>> readAllGuildPermissions(String guildTag) {
        Set<UUID> members = readGuildMembers(guildTag);
        Map<UUID, Set<PermissionType>> result = new HashMap<>();
        for (UUID member : members) {
            result.put(member, readPermissions(guildTag, member));
        }
        return result;
    }

    /**
     * Clears all permissions for a specific guild member.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     */
    public void clearMemberPermissions(String guildTag, UUID member) {
        String path = buildMemberPath(guildTag, member);
        cfg.set(path, null);
        save();
    }

    /**
     * Clears all permissions for an entire guild.
     *
     * @param guildTag The guild tag identifier
     */
    public void clearGuildPermissions(String guildTag) {
        String path = buildGuildPath(guildTag);
        cfg.set(path, null);
        save();
    }

    /**
     * Checks if any permissions are stored for a specific guild member.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     * @return true if the member has any stored permissions
     */
    public boolean hasStoredPermissions(String guildTag, UUID member) {
        String path = buildMemberPath(guildTag, member);
        return cfg.contains(path);
    }

    /**
     * Builds the configuration path for a guild.
     *
     * @param guildTag The guild tag identifier
     * @return The configuration path string
     */
    private String buildGuildPath(String guildTag) {
        return GUILDS_PATH + "." + guildTag;
    }

    /**
     * Builds the configuration path for a specific member within a guild.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     * @return The configuration path string
     */
    private String buildMemberPath(String guildTag, UUID member) {
        return buildGuildPath(guildTag) + "." + member.toString();
    }

    /**
     * Gets the underlying FileConfiguration for advanced operations.
     * Use with caution - prefer the typed methods when possible.
     *
     * @return The FileConfiguration instance
     */
    public FileConfiguration getConfiguration() {
        return cfg;
    }
}
