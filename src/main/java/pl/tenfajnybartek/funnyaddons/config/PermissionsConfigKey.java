package pl.tenfajnybartek.funnyaddons.config;


public enum PermissionsConfigKey {


    GUI_MEMBER_PERMISSIONS_SIZE("permissions.gui.member-permissions-size", 27),

    GUI_TITLE_MAX_LENGTH("permissions.gui.title-max-length", 32),

    GUI_MEMBERS_TITLE("permissions.gui.members-title", "&cGildia: &e{GUILD} - członkowie"),

    GUI_MEMBER_PERMS_TITLE("permissions.gui.member-perms-title", "&cUprawnienia: &e{GUILD} - {NAME}"),

    SLOT_BREAK("permissions.gui.slots.break", 10),

    SLOT_PLACE("permissions.gui.slots.place", 11),

    SLOT_INTERACT_BLOCK("permissions.gui.slots.interact_block", 12),

    SLOT_OPEN_CHEST("permissions.gui.slots.open_chest", 14),

    SLOT_OPEN_ENDER_CHEST("permissions.gui.slots.open_ender_chest", 15),

    SLOT_USE_BUCKETS("permissions.gui.slots.use_buckets", 16),

    SLOT_USE_FIRE("permissions.gui.slots.use_fire", 19),

    SLOT_FRIENDLY_FIRE("permissions.gui.slots.friendly_fire", 21),

    SLOT_BACK("permissions.gui.slots.back", 26),

    SLOT_INFO("permissions.gui.slots.info", 13),

    NAME_BREAK("permissions.gui.names.break", "&aNiszczenie bloków"),

    NAME_PLACE("permissions.gui.names.place", "&aStawianie bloków"),

    NAME_INTERACT_BLOCK("permissions.gui.names.interact_block", "&aInterakcja z blokami"),

    NAME_OPEN_CHEST("permissions.gui.names.open_chest", "&aOtwieranie skrzyń"),

    NAME_OPEN_ENDER_CHEST("permissions.gui.names.open_ender_chest", "&aOtwieranie ender chestów"),

    NAME_USE_BUCKETS("permissions.gui.names.use_buckets", "&aUżywanie kubełków"),

    NAME_USE_FIRE("permissions.gui.names.use_fire", "&aUżywanie flint & steel"),

    NAME_FRIENDLY_FIRE("permissions.gui.names.friendly_fire", "&aFriendly fire"),

    NAME_BACK("permissions.gui.names.back", "&cPowrót"),

    NAME_INFO("permissions.gui.names.info", "&eKliknij aby ustawić uprawnienia"),

    LORE_TOGGLE("permissions.gui.lore.toggle", "&7Kliknij aby przełączyć"),

    LORE_INFO("permissions.gui.lore.info", "&7Kliknij aby ustawić uprawnienia"),

    STATE_ON("permissions.gui.state-on", "&a[ON] "),

    STATE_OFF("permissions.gui.state-off", "&c[OFF] "),

    ICON_BREAK("permissions.icons.break", "DIAMOND_PICKAXE"),

    ICON_PLACE("permissions.icons.place", "OAK_PLANKS"),

    ICON_INTERACT_BLOCK("permissions.icons.interact_block", "LEVER"),

    ICON_OPEN_CHEST("permissions.icons.open_chest", "CHEST"),

    ICON_OPEN_ENDER_CHEST("permissions.icons.open_ender_chest", "ENDER_CHEST"),

    ICON_USE_BUCKETS("permissions.icons.use_buckets", "WATER_BUCKET"),

    ICON_USE_FIRE("permissions.icons.use_fire", "FLINT_AND_STEEL"),

    ICON_FRIENDLY_FIRE("permissions.icons.friendly_fire", "TIPPED_ARROW"),

    ICON_BACK("permissions.icons.back", "BARRIER"),

    ICON_INFO("permissions.icons.info", "PLAYER_HEAD"),

    DEFAULTS_MEMBER("permissions.defaults.member", "[]"),

    DEFAULTS_OFFICER("permissions.defaults.officer", "[]"),

    DEFAULTS_OWNER("permissions.defaults.owner", "[]"),

    RELATION_ENABLE("permissions.relation.enable", false),

    RELATION_DEFAULT_BEHAVIOR("permissions.relation.default-behavior", "follow_fg");

    private final String path;
    private final Object defaultValue;

    PermissionsConfigKey(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }

    public String getPath() {
        return path;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDefaultValueAs(Class<T> type) {
        return (T) defaultValue;
    }

    public int getDefaultInt() {
        return (Integer) defaultValue;
    }

    public boolean getDefaultBoolean() {
        return (Boolean) defaultValue;
    }

    public String getDefaultString() {
        return (String) defaultValue;
    }
}
