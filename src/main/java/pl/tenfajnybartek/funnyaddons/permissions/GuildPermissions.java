package pl.tenfajnybartek.funnyaddons.permissions;

import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

/**
 * Data object representing permissions for a specific guild member.
 * <p>
 * This class holds an in-memory representation of a member's permissions
 * within a guild. It can be used as a transfer object between the
 * {@link pl.tenfajnybartek.funnyaddons.managers.PermissionsManager PermissionsManager}
 * and GUI or command layers.
 * <p>
 * Note: Changes to this object are not automatically persisted.
 * Use PermissionsManager methods to save changes.
 */
public class GuildPermissions {
    private final String guildTag;
    private final UUID member;
    private final Set<PermissionType> permissions;

    /**
     * Creates a new GuildPermissions instance with no permissions.
     *
     * @param guildTag The guild tag identifier
     * @param member   The UUID of the guild member
     */
    public GuildPermissions(String guildTag, UUID member) {
        this.guildTag = guildTag;
        this.member = member;
        this.permissions = EnumSet.noneOf(PermissionType.class);
    }

    /**
     * Creates a new GuildPermissions instance with existing permissions.
     *
     * @param guildTag    The guild tag identifier
     * @param member      The UUID of the guild member
     * @param permissions The initial set of permissions
     */
    public GuildPermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        this.guildTag = guildTag;
        this.member = member;
        this.permissions = permissions.isEmpty() ? EnumSet.noneOf(PermissionType.class) : EnumSet.copyOf(permissions);
    }

    /**
     * Gets the guild tag.
     *
     * @return The guild tag identifier
     */
    public String getGuildTag() {
        return guildTag;
    }

    /**
     * Gets the member UUID.
     *
     * @return The member's UUID
     */
    public UUID getMember() {
        return member;
    }

    /**
     * Gets all permissions for this member.
     * <p>
     * Note: This returns the internal set. Use grant/revoke methods to modify
     * permissions to ensure consistent behavior.
     *
     * @return The set of permissions
     */
    public Set<PermissionType> getPermissions() {
        return permissions;
    }

    /**
     * Checks if this member has a specific permission.
     *
     * @param type The permission type to check
     * @return true if the member has the permission
     */
    public boolean has(PermissionType type) {
        return permissions.contains(type);
    }

    /**
     * Grants a permission to this member.
     *
     * @param type The permission type to grant
     */
    public void grant(PermissionType type) {
        permissions.add(type);
    }

    /**
     * Revokes a permission from this member.
     *
     * @param type The permission type to revoke
     */
    public void revoke(PermissionType type) {
        permissions.remove(type);
    }

    /**
     * Toggles a permission for this member.
     *
     * @param type The permission type to toggle
     */
    public void toggle(PermissionType type) {
        if (has(type)) {
            revoke(type);
        } else {
            grant(type);
        }
    }
}