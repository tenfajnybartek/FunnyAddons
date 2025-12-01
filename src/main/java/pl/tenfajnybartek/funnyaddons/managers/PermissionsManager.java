package pl.tenfajnybartek.funnyaddons.managers;

import org.bukkit.plugin.java.JavaPlugin;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionsManager {

    private final PermissionsStorage storage;

    private final Map<String, Set<PermissionType>> cache = new ConcurrentHashMap<>();

    public PermissionsManager(JavaPlugin plugin) {
        this.storage = new PermissionsStorage(plugin);
    }

    public PermissionsManager(PermissionsStorage storage) {
        this.storage = storage;
    }

    public void reload() {
        cache.clear();
        storage.reload();
    }

    public void save() {
        storage.save();
    }

    public PermissionsStorage getStorage() {
        return storage;
    }

    public Set<PermissionType> getPermissions(String guildTag, UUID member) {
        String cacheKey = buildCacheKey(guildTag, member);
        return cache.computeIfAbsent(cacheKey, k -> {
            Set<PermissionType> stored = storage.readPermissions(guildTag, member);
            // Return a mutable copy for internal use, wrapped in EnumSet for efficiency
            return stored.isEmpty() ? EnumSet.noneOf(PermissionType.class) : EnumSet.copyOf(stored);
        });
    }

    public boolean hasPermission(String guildTag, UUID member, PermissionType type) {
        return getPermissions(guildTag, member).contains(type);
    }

    public void setPermission(String guildTag, UUID member, PermissionType type, boolean value) {
        Set<PermissionType> permissions = getMutablePermissions(guildTag, member);
        if (value) {
            permissions.add(type);
        } else {
            permissions.remove(type);
        }
        persistPermissions(guildTag, member, permissions);
    }

    public void togglePermission(String guildTag, UUID member, PermissionType type) {
        Set<PermissionType> permissions = getMutablePermissions(guildTag, member);
        if (permissions.contains(type)) {
            permissions.remove(type);
        } else {
            permissions.add(type);
        }
        persistPermissions(guildTag, member, permissions);
    }

    public void grantPermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        Set<PermissionType> current = getMutablePermissions(guildTag, member);
        current.addAll(permissions);
        persistPermissions(guildTag, member, current);
    }

    public void revokePermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        Set<PermissionType> current = getMutablePermissions(guildTag, member);
        current.removeAll(permissions);
        persistPermissions(guildTag, member, current);
    }

    public void clearPermissions(String guildTag, UUID member) {
        String cacheKey = buildCacheKey(guildTag, member);
        cache.remove(cacheKey);
        storage.clearMemberPermissions(guildTag, member);
    }

    public Set<UUID> getGuildMembers(String guildTag) {
        return storage.readGuildMembers(guildTag);
    }

    public Map<UUID, Set<PermissionType>> getAllGuildPermissions(String guildTag) {
        return storage.readAllGuildPermissions(guildTag);
    }

    public void invalidateCache(String guildTag, UUID member) {
        String cacheKey = buildCacheKey(guildTag, member);
        cache.remove(cacheKey);
    }

    public void invalidateGuildCache(String guildTag) {
        String prefix = guildTag + ":";
        List<String> keysToRemove = new ArrayList<>();
        for (String key : cache.keySet()) {
            if (key.startsWith(prefix)) {
                keysToRemove.add(key);
            }
        }
        keysToRemove.forEach(cache::remove);
    }

    public void clearCache() {
        cache.clear();
    }

    private Set<PermissionType> getMutablePermissions(String guildTag, UUID member) {
        Set<PermissionType> original = getPermissions(guildTag, member);
        return original.isEmpty() ? EnumSet.noneOf(PermissionType.class) : EnumSet.copyOf(original);
    }

    private void persistPermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        String cacheKey = buildCacheKey(guildTag, member);
        cache.put(cacheKey, permissions.isEmpty() ? EnumSet.noneOf(PermissionType.class) : EnumSet.copyOf(permissions));
        storage.writePermissions(guildTag, member, permissions);
    }

    private String buildCacheKey(String guildTag, UUID member) {
        return guildTag + ":" + member.toString();
    }
}