package pl.tenfajnybartek.funnyaddons.managers;

import org.bukkit.plugin.java.JavaPlugin;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Permission API manager for guild member permissions.
 * <p>
 * This class provides the business logic for permission checks, toggling, and setting.
 * All IO operations are delegated to {@link PermissionsStorage}.
 * <p>
 * Features:
 * <ul>
 *   <li>In-memory caching for fast permission lookups</li>
 *   <li>Typed API using {@link PermissionType} enum</li>
 *   <li>Unified API: hasPermission, setPermission, togglePermission, getPermissions</li>
 *   <li>Thread-safe cache implementation</li>
 * </ul>
 */
public class PermissionsManager {

    private final PermissionsStorage storage;

    /**
     * In-memory cache for permissions.
     * Key format: "guildTag:memberUUID"
     * Value: Set of PermissionTypes granted to the member
     */
    private final Map<String, Set<PermissionType>> cache = new ConcurrentHashMap<>();

    /**
     * Creates a new PermissionsManager instance.
     *
     * @param plugin The plugin instance for storage initialization
     */
    public PermissionsManager(JavaPlugin plugin) {
        this.storage = new PermissionsStorage(plugin);
    }

    /**
     * Creates a new PermissionsManager with a custom storage instance.
     * Useful for testing or custom storage implementations.
     *
     * @param storage The storage instance to use
     */
    public PermissionsManager(PermissionsStorage storage) {
        this.storage = storage;
    }

    /**
     * Reloads permissions from storage and clears the cache.
     * Should be called when the configuration file is externally modified.
     */
    public void reload() {
        cache.clear();
        storage.reload();
    }

    /**
     * Saves any pending changes to storage.
     * Note: Individual permission changes are automatically saved.
     */
    public void save() {
        storage.save();
    }

    /**
     * Gets the storage layer instance.
     * Use for advanced operations not covered by the manager API.
     *
     * @return The PermissionsStorage instance
     */
    public PermissionsStorage getStorage() {
        return storage;
    }

    // ==================== Unified Permission API ====================

    /**
     * Gets all permissions for a specific guild member.
     * Uses cache for fast lookup, falls back to storage on cache miss.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     * @return A set of permissions granted to the member (mutable EnumSet)
     */
    public Set<PermissionType> getPermissions(String guildTag, UUID member) {
        String cacheKey = buildCacheKey(guildTag, member);
        return cache.computeIfAbsent(cacheKey, k -> {
            Set<PermissionType> stored = storage.readPermissions(guildTag, member);
            // Return a mutable copy for internal use, wrapped in EnumSet for efficiency
            return stored.isEmpty() ? EnumSet.noneOf(PermissionType.class) : EnumSet.copyOf(stored);
        });
    }

    /**
     * Checks if a guild member has a specific permission.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     * @param type     The permission type to check
     * @return true if the member has the permission
     */
    public boolean hasPermission(String guildTag, UUID member, PermissionType type) {
        return getPermissions(guildTag, member).contains(type);
    }

    /**
     * Sets a specific permission for a guild member.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     * @param type     The permission type to set
     * @param value    true to grant, false to revoke
     */
    public void setPermission(String guildTag, UUID member, PermissionType type, boolean value) {
        Set<PermissionType> permissions = getMutablePermissions(guildTag, member);
        if (value) {
            permissions.add(type);
        } else {
            permissions.remove(type);
        }
        persistPermissions(guildTag, member, permissions);
    }

    /**
     * Toggles a specific permission for a guild member.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     * @param type     The permission type to toggle
     */
    public void togglePermission(String guildTag, UUID member, PermissionType type) {
        Set<PermissionType> permissions = getMutablePermissions(guildTag, member);
        if (permissions.contains(type)) {
            permissions.remove(type);
        } else {
            permissions.add(type);
        }
        persistPermissions(guildTag, member, permissions);
    }

    /**
     * Grants multiple permissions to a guild member.
     *
     * @param guildTag    The guild tag identifier
     * @param member      The UUID of the guild member
     * @param permissions The permissions to grant
     */
    public void grantPermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        Set<PermissionType> current = getMutablePermissions(guildTag, member);
        current.addAll(permissions);
        persistPermissions(guildTag, member, current);
    }

    /**
     * Revokes multiple permissions from a guild member.
     *
     * @param guildTag    The guild tag identifier
     * @param member      The UUID of the guild member
     * @param permissions The permissions to revoke
     */
    public void revokePermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        Set<PermissionType> current = getMutablePermissions(guildTag, member);
        current.removeAll(permissions);
        persistPermissions(guildTag, member, current);
    }

    /**
     * Clears all permissions for a guild member.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     */
    public void clearPermissions(String guildTag, UUID member) {
        String cacheKey = buildCacheKey(guildTag, member);
        cache.remove(cacheKey);
        storage.clearMemberPermissions(guildTag, member);
    }

    /**
     * Gets all members with stored permissions for a guild.
     *
     * @param guildTag The guild tag identifier
     * @return A set of member UUIDs
     */
    public Set<UUID> getGuildMembers(String guildTag) {
        return storage.readGuildMembers(guildTag);
    }

    /**
     * Gets all permissions for all members of a guild.
     *
     * @param guildTag The guild tag identifier
     * @return A map of member UUIDs to their permission sets
     */
    public Map<UUID, Set<PermissionType>> getAllGuildPermissions(String guildTag) {
        return storage.readAllGuildPermissions(guildTag);
    }

    /**
     * Invalidates the cache entry for a specific member.
     * Next permission check will load from storage.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     */
    public void invalidateCache(String guildTag, UUID member) {
        String cacheKey = buildCacheKey(guildTag, member);
        cache.remove(cacheKey);
    }

    /**
     * Invalidates all cache entries for a guild.
     *
     * @param guildTag The guild tag identifier
     */
    public void invalidateGuildCache(String guildTag) {
        String prefix = guildTag + ":";
        // Collect keys to remove to avoid ConcurrentModificationException
        List<String> keysToRemove = new ArrayList<>();
        for (String key : cache.keySet()) {
            if (key.startsWith(prefix)) {
                keysToRemove.add(key);
            }
        }
        keysToRemove.forEach(cache::remove);
    }

    /**
     * Clears the entire permission cache.
     */
    public void clearCache() {
        cache.clear();
    }

    // ==================== Private Helper Methods ====================

    /**
     * Gets a mutable copy of the permissions for modification.
     */
    private Set<PermissionType> getMutablePermissions(String guildTag, UUID member) {
        Set<PermissionType> original = getPermissions(guildTag, member);
        // Return a mutable EnumSet copy
        return original.isEmpty() ? EnumSet.noneOf(PermissionType.class) : EnumSet.copyOf(original);
    }

    /**
     * Persists permissions to storage and updates cache.
     */
    private void persistPermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        String cacheKey = buildCacheKey(guildTag, member);
        // Update cache with a copy to prevent external modification
        cache.put(cacheKey, permissions.isEmpty() ? EnumSet.noneOf(PermissionType.class) : EnumSet.copyOf(permissions));
        // Persist to storage
        storage.writePermissions(guildTag, member, permissions);
    }

    /**
     * Builds the cache key for a guild member.
     */
    private String buildCacheKey(String guildTag, UUID member) {
        return guildTag + ":" + member.toString();
    }
}