package pl.tenfajnybartek.funnyaddons.utils;

/**
 * Enum representing all available permission types for guild members.
 * <p>
 * These permissions control what actions a guild member can perform
 * within their guild's territory. The permission system uses typed enums
 * instead of strings for compile-time safety.
 * <p>
 * Usage examples:
 * <pre>
 * // Check permission
 * boolean canBreak = permissionsManager.hasPermission(guildTag, memberUuid, PermissionType.BREAK);
 *
 * // Toggle permission
 * permissionsManager.togglePermission(guildTag, memberUuid, PermissionType.PLACE);
 * </pre>
 */
public enum PermissionType {
    /**
     * Permission to break blocks in guild territory.
     */
    BREAK,

    /**
     * Permission to place blocks in guild territory.
     */
    PLACE,

    /**
     * Permission to open chests and other container blocks.
     */
    OPEN_CHEST,

    /**
     * Permission to open ender chests in guild territory.
     */
    OPEN_ENDER_CHEST,

    /**
     * Permission to enable friendly fire (damage guild members).
     */
    FRIENDLY_FIRE,

    /**
     * Permission to interact with buttons, levers, doors, etc.
     */
    INTERACT_BLOCK,

    /**
     * Permission to use buckets (water, lava, etc.).
     */
    USE_BUCKETS,

    /**
     * Permission to use flint and steel (fire).
     */
    USE_FIRE
}
