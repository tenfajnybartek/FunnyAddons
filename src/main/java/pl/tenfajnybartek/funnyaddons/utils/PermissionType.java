package pl.tenfajnybartek.funnyaddons.utils;

import org.bukkit.Material;

/**
 * Enum representing all available permission types for guild members.
 * <p>
 * These permissions control what actions a guild member can perform
 * within their guild's territory. The permission system uses typed enums
 * instead of strings for compile-time safety.
 * <p>
 * Each permission type holds metadata including:
 * <ul>
 *   <li>Message key for permission-related messages (e.g., perms-no-break)</li>
 *   <li>Display name for GUI representation</li>
 *   <li>Config key for icon/slot/name lookup in configuration</li>
 *   <li>Default icon material</li>
 *   <li>Default slot position in GUI</li>
 * </ul>
 * <p>
 * Usage examples:
 * <pre>
 * // Check permission
 * boolean canBreak = permissionsManager.hasPermission(guildTag, memberUuid, PermissionType.BREAK);
 *
 * // Toggle permission
 * permissionsManager.togglePermission(guildTag, memberUuid, PermissionType.PLACE);
 *
 * // Get message key for denial message
 * String messageKey = PermissionType.BREAK.getMessageKey(); // returns "no-break"
 *
 * // Get config key for icon lookup
 * String configKey = PermissionType.BREAK.getConfigKey(); // returns "break"
 * </pre>
 */
public enum PermissionType {
    /**
     * Permission to break blocks in guild territory.
     */
    BREAK("no-break", "&aNiszczenie bloków", "break", Material.DIAMOND_PICKAXE, 10,
            "&cNie masz uprawnień do niszczenia na terenie tej gildii!"),

    /**
     * Permission to place blocks in guild territory.
     */
    PLACE("no-place", "&aStawianie bloków", "place", Material.OAK_PLANKS, 11,
            "&cNie masz uprawnień do stawiania bloków na terenie tej gildii!"),

    /**
     * Permission to open chests and other container blocks.
     */
    OPEN_CHEST("no-open-chest", "&aOtwieranie skrzyń", "open_chest", Material.CHEST, 14,
            "&cNie masz uprawnień do otwierania skrzyń na terenie tej gildii!"),

    /**
     * Permission to open ender chests in guild territory.
     */
    OPEN_ENDER_CHEST("no-open-ender", "&aOtwieranie ender chestów", "open_ender_chest", Material.ENDER_CHEST, 15,
            "&cNie masz uprawnień do otwierania ender chesta na terenie tej gildii!"),

    /**
     * Permission to enable friendly fire (damage guild members).
     */
    FRIENDLY_FIRE("no-friendly-fire", "&aFriendly fire", "friendly_fire", Material.TIPPED_ARROW, 21,
            "&cNie możesz obrażać członków swojej gildii!"),

    /**
     * Permission to interact with buttons, levers, doors, etc.
     */
    INTERACT_BLOCK("no-interact", "&aInterakcja z blokami", "interact_block", Material.LEVER, 12,
            "&cNie masz uprawnień do używania przycisków/dźwigni/drzwi na terenie tej gildii!"),

    /**
     * Permission to use buckets (water, lava, etc.).
     */
    USE_BUCKETS("no-buckets", "&aUżywanie kubełków", "use_buckets", Material.WATER_BUCKET, 16,
            "&cNie masz uprawnień do używania kubełków na terenie tej gildii!"),

    /**
     * Permission to use flint and steel (fire).
     */
    USE_FIRE("no-fire", "&aUżywanie flint & steel", "use_fire", Material.FLINT_AND_STEEL, 19,
            "&cNie masz uprawnień do używania flinta i stali (odpalenie) na terenie tej gildii!");

    private final String messageKey;
    private final String defaultDisplayName;
    private final String configKey;
    private final Material defaultIcon;
    private final int defaultSlot;
    private final String defaultDenialMessage;

    /**
     * Creates a new PermissionType with full metadata.
     *
     * @param messageKey            The message key suffix for permission denial messages (e.g., "no-break")
     * @param defaultDisplayName    The default display name for GUI representation
     * @param configKey             The config key for looking up icon/slot/name in config
     * @param defaultIcon           The default Material icon for this permission
     * @param defaultSlot           The default slot position in the permissions GUI
     * @param defaultDenialMessage  The default denial message when permission is not granted
     */
    PermissionType(String messageKey, String defaultDisplayName, String configKey, Material defaultIcon, int defaultSlot, String defaultDenialMessage) {
        this.messageKey = messageKey;
        this.defaultDisplayName = defaultDisplayName;
        this.configKey = configKey;
        this.defaultIcon = defaultIcon;
        this.defaultSlot = defaultSlot;
        this.defaultDenialMessage = defaultDenialMessage;
    }

    /**
     * Gets the message key suffix for permission-related messages.
     * <p>
     * This key is used with the "perms-" prefix in the messages config.
     * For example, for BREAK permission, messageKey is "no-break",
     * which corresponds to "messages.perms-no-break" in config.
     *
     * @return The message key suffix (e.g., "no-break")
     */
    public String getMessageKey() {
        return messageKey;
    }

    /**
     * Gets the default display name for this permission.
     * <p>
     * This is the fallback name used in GUI when no config override exists.
     *
     * @return The default display name with color codes (e.g., "&aNiszczenie bloków")
     */
    public String getDefaultDisplayName() {
        return defaultDisplayName;
    }

    /**
     * Gets the config key for this permission type.
     * <p>
     * This key is used to look up icons, slots, and names in the configuration.
     * For example, for BREAK permission, configKey is "break",
     * which corresponds to paths like:
     * <ul>
     *   <li>permissions.gui.slots.break</li>
     *   <li>permissions.gui.names.break</li>
     *   <li>permissions.icons.break</li>
     * </ul>
     *
     * @return The config key (e.g., "break", "open_chest")
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Gets the default icon Material for this permission.
     * <p>
     * This is the fallback icon used in GUI when no config override exists.
     *
     * @return The default Material icon
     */
    public Material getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * Gets the default slot position for this permission in the GUI.
     * <p>
     * This is the fallback slot used when no config override exists.
     *
     * @return The default slot index (0-based)
     */
    public int getDefaultSlot() {
        return defaultSlot;
    }

    /**
     * Gets the default denial message for this permission.
     * <p>
     * This is the fallback message shown when a player lacks this permission
     * and no config override exists.
     *
     * @return The default denial message with color codes
     */
    public String getDefaultDenialMessage() {
        return defaultDenialMessage;
    }

    /**
     * Finds a PermissionType by its config key.
     *
     * @param configKey The config key to search for (case-insensitive)
     * @return The matching PermissionType, or null if not found
     */
    public static PermissionType fromConfigKey(String configKey) {
        if (configKey == null) return null;
        for (PermissionType type : values()) {
            if (type.configKey.equalsIgnoreCase(configKey)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Finds a PermissionType by its message key.
     *
     * @param messageKey The message key to search for (case-insensitive)
     * @return The matching PermissionType, or null if not found
     */
    public static PermissionType fromMessageKey(String messageKey) {
        if (messageKey == null) return null;
        for (PermissionType type : values()) {
            if (type.messageKey.equalsIgnoreCase(messageKey)) {
                return type;
            }
        }
        return null;
    }
}
