package pl.tenfajnybartek.funnyaddons.config;

/**
 * Enum defining configuration keys for the permissions system.
 * <p>
 * Using this enum instead of raw String literals provides:
 * <ul>
 *   <li>Type safety - compile-time checks for valid keys</li>
 *   <li>IDE auto-completion and refactoring support</li>
 *   <li>Single source of truth for configuration paths</li>
 *   <li>Easier maintenance when adding/changing keys</li>
 * </ul>
 */
public enum PermissionsConfigKey {

    // ==================== GUI Settings ====================
    
    /**
     * Inventory size for member permissions GUI.
     * Path: permissions.gui.member-permissions-size
     */
    GUI_MEMBER_PERMISSIONS_SIZE("permissions.gui.member-permissions-size", 27),

    /**
     * Maximum length for inventory titles (Minecraft client limitation).
     * Path: permissions.gui.title-max-length
     */
    GUI_TITLE_MAX_LENGTH("permissions.gui.title-max-length", 32),

    /**
     * Title pattern for members list GUI.
     * Path: permissions.gui.members-title
     */
    GUI_MEMBERS_TITLE("permissions.gui.members-title", "&cGildia: &e{GUILD} - członkowie"),

    /**
     * Title pattern for member permissions GUI.
     * Path: permissions.gui.member-perms-title
     */
    GUI_MEMBER_PERMS_TITLE("permissions.gui.member-perms-title", "&cUprawnienia: &e{GUILD} - {NAME}"),

    // ==================== Slots ====================

    /**
     * Slot for break permission toggle.
     * Path: permissions.gui.slots.break
     */
    SLOT_BREAK("permissions.gui.slots.break", 10),

    /**
     * Slot for place permission toggle.
     * Path: permissions.gui.slots.place
     */
    SLOT_PLACE("permissions.gui.slots.place", 11),

    /**
     * Slot for interact block permission toggle.
     * Path: permissions.gui.slots.interact_block
     */
    SLOT_INTERACT_BLOCK("permissions.gui.slots.interact_block", 12),

    /**
     * Slot for open chest permission toggle.
     * Path: permissions.gui.slots.open_chest
     */
    SLOT_OPEN_CHEST("permissions.gui.slots.open_chest", 14),

    /**
     * Slot for open ender chest permission toggle.
     * Path: permissions.gui.slots.open_ender_chest
     */
    SLOT_OPEN_ENDER_CHEST("permissions.gui.slots.open_ender_chest", 15),

    /**
     * Slot for use buckets permission toggle.
     * Path: permissions.gui.slots.use_buckets
     */
    SLOT_USE_BUCKETS("permissions.gui.slots.use_buckets", 16),

    /**
     * Slot for use fire permission toggle.
     * Path: permissions.gui.slots.use_fire
     */
    SLOT_USE_FIRE("permissions.gui.slots.use_fire", 19),

    /**
     * Slot for friendly fire permission toggle.
     * Path: permissions.gui.slots.friendly_fire
     */
    SLOT_FRIENDLY_FIRE("permissions.gui.slots.friendly_fire", 21),

    /**
     * Slot for back button.
     * Path: permissions.gui.slots.back
     */
    SLOT_BACK("permissions.gui.slots.back", 26),

    /**
     * Slot for info/player head.
     * Path: permissions.gui.slots.info
     */
    SLOT_INFO("permissions.gui.slots.info", 13),

    // ==================== Display Names ====================

    /**
     * Display name for break permission.
     * Path: permissions.gui.names.break
     */
    NAME_BREAK("permissions.gui.names.break", "&aNiszczenie bloków"),

    /**
     * Display name for place permission.
     * Path: permissions.gui.names.place
     */
    NAME_PLACE("permissions.gui.names.place", "&aStawianie bloków"),

    /**
     * Display name for interact block permission.
     * Path: permissions.gui.names.interact_block
     */
    NAME_INTERACT_BLOCK("permissions.gui.names.interact_block", "&aInterakcja z blokami"),

    /**
     * Display name for open chest permission.
     * Path: permissions.gui.names.open_chest
     */
    NAME_OPEN_CHEST("permissions.gui.names.open_chest", "&aOtwieranie skrzyń"),

    /**
     * Display name for open ender chest permission.
     * Path: permissions.gui.names.open_ender_chest
     */
    NAME_OPEN_ENDER_CHEST("permissions.gui.names.open_ender_chest", "&aOtwieranie ender chestów"),

    /**
     * Display name for use buckets permission.
     * Path: permissions.gui.names.use_buckets
     */
    NAME_USE_BUCKETS("permissions.gui.names.use_buckets", "&aUżywanie kubełków"),

    /**
     * Display name for use fire permission.
     * Path: permissions.gui.names.use_fire
     */
    NAME_USE_FIRE("permissions.gui.names.use_fire", "&aUżywanie flint & steel"),

    /**
     * Display name for friendly fire permission.
     * Path: permissions.gui.names.friendly_fire
     */
    NAME_FRIENDLY_FIRE("permissions.gui.names.friendly_fire", "&aFriendly fire"),

    /**
     * Display name for back button.
     * Path: permissions.gui.names.back
     */
    NAME_BACK("permissions.gui.names.back", "&cPowrót"),

    /**
     * Display name for info/player head.
     * Path: permissions.gui.names.info
     */
    NAME_INFO("permissions.gui.names.info", "&eKliknij aby ustawić uprawnienia"),

    // ==================== Lore ====================

    /**
     * Lore for toggle items.
     * Path: permissions.gui.lore.toggle
     */
    LORE_TOGGLE("permissions.gui.lore.toggle", "&7Kliknij aby przełączyć"),

    /**
     * Lore for info/player head items.
     * Path: permissions.gui.lore.info
     */
    LORE_INFO("permissions.gui.lore.info", "&7Kliknij aby ustawić uprawnienia"),

    // ==================== State Prefixes ====================

    /**
     * State prefix for ON state.
     * Path: permissions.gui.state-on
     */
    STATE_ON("permissions.gui.state-on", "&a[ON] "),

    /**
     * State prefix for OFF state.
     * Path: permissions.gui.state-off
     */
    STATE_OFF("permissions.gui.state-off", "&c[OFF] "),

    // ==================== Icons ====================

    /**
     * Icon for break permission.
     * Path: permissions.icons.break
     */
    ICON_BREAK("permissions.icons.break", "DIAMOND_PICKAXE"),

    /**
     * Icon for place permission.
     * Path: permissions.icons.place
     */
    ICON_PLACE("permissions.icons.place", "OAK_PLANKS"),

    /**
     * Icon for interact block permission.
     * Path: permissions.icons.interact_block
     */
    ICON_INTERACT_BLOCK("permissions.icons.interact_block", "LEVER"),

    /**
     * Icon for open chest permission.
     * Path: permissions.icons.open_chest
     */
    ICON_OPEN_CHEST("permissions.icons.open_chest", "CHEST"),

    /**
     * Icon for open ender chest permission.
     * Path: permissions.icons.open_ender_chest
     */
    ICON_OPEN_ENDER_CHEST("permissions.icons.open_ender_chest", "ENDER_CHEST"),

    /**
     * Icon for use buckets permission.
     * Path: permissions.icons.use_buckets
     */
    ICON_USE_BUCKETS("permissions.icons.use_buckets", "WATER_BUCKET"),

    /**
     * Icon for use fire permission.
     * Path: permissions.icons.use_fire
     */
    ICON_USE_FIRE("permissions.icons.use_fire", "FLINT_AND_STEEL"),

    /**
     * Icon for friendly fire permission.
     * Path: permissions.icons.friendly_fire
     */
    ICON_FRIENDLY_FIRE("permissions.icons.friendly_fire", "TIPPED_ARROW"),

    /**
     * Icon for back button.
     * Path: permissions.icons.back
     */
    ICON_BACK("permissions.icons.back", "BARRIER"),

    /**
     * Icon for info/player head.
     * Path: permissions.icons.info
     */
    ICON_INFO("permissions.icons.info", "PLAYER_HEAD"),

    // ==================== Default Permissions ====================

    /**
     * Default permissions for regular members.
     * Path: permissions.defaults.member
     */
    DEFAULTS_MEMBER("permissions.defaults.member", "[]"),

    /**
     * Default permissions for officers.
     * Path: permissions.defaults.officer
     */
    DEFAULTS_OFFICER("permissions.defaults.officer", "[]"),

    /**
     * Default permissions for owners.
     * Path: permissions.defaults.owner
     */
    DEFAULTS_OWNER("permissions.defaults.owner", "[]"),

    // ==================== Relation Settings ====================

    /**
     * Whether relation-based permissions are enabled.
     * Path: permissions.relation.enable
     */
    RELATION_ENABLE("permissions.relation.enable", false),

    /**
     * Default behavior for relations (follow_fg / follow_addon).
     * Path: permissions.relation.default-behavior
     */
    RELATION_DEFAULT_BEHAVIOR("permissions.relation.default-behavior", "follow_fg");

    private final String path;
    private final Object defaultValue;

    PermissionsConfigKey(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the configuration path for this key.
     *
     * @return The full configuration path
     */
    public String getPath() {
        return path;
    }

    /**
     * Gets the default value for this key.
     *
     * @return The default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the default value cast to a specific type.
     *
     * @param <T>  The type to cast to
     * @param type The class of the type
     * @return The default value cast to the specified type
     * @throws ClassCastException if the default value cannot be cast
     */
    @SuppressWarnings("unchecked")
    public <T> T getDefaultValueAs(Class<T> type) {
        return (T) defaultValue;
    }

    /**
     * Gets the default value as an integer.
     *
     * @return The default value as int
     */
    public int getDefaultInt() {
        return (Integer) defaultValue;
    }

    /**
     * Gets the default value as a boolean.
     *
     * @return The default value as boolean
     */
    public boolean getDefaultBoolean() {
        return (Boolean) defaultValue;
    }

    /**
     * Gets the default value as a string.
     *
     * @return The default value as String
     */
    public String getDefaultString() {
        return (String) defaultValue;
    }
}
