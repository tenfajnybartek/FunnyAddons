package pl.tenfajnybartek.funnyaddons.utils;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public enum PermissionType {

    BREAK("no-break", "&aNiszczenie bloków", "break", Material.DIAMOND_PICKAXE, 10,
            "&cNie masz uprawnień do niszczenia na terenie tej gildii!"),

    PLACE("no-place", "&aStawianie bloków", "place", Material.OAK_PLANKS, 11,
            "&cNie masz uprawnień do stawiania bloków na terenie tej gildii!"),

    OPEN_CHEST("no-open-chest", "&aOtwieranie skrzyń", "open_chest", Material.CHEST, 14,
            "&cNie masz uprawnień do otwierania skrzyń na terenie tej gildii!"),

    OPEN_ENDER_CHEST("no-open-ender", "&aOtwieranie ender chestów", "open_ender_chest", Material.ENDER_CHEST, 15,
            "&cNie masz uprawnień do otwierania ender chesta na terenie tej gildii!"),

    FRIENDLY_FIRE("no-friendly-fire", "&aFriendly fire", "friendly_fire", Material.TIPPED_ARROW, 21,
            "&cNie możesz obrażać członków swojej gildii!"),

    INTERACT_BLOCK("no-interact", "&aInterakcja z blokami", "interact_block", Material.LEVER, 12,
            "&cNie masz uprawnień do używania przycisków/dźwigni/drzwi na terenie tej gildii!"),

    USE_BUCKETS("no-buckets", "&aUżywanie kubełków", "use_buckets", Material.WATER_BUCKET, 16,
            "&cNie masz uprawnień do używania kubełków na terenie tej gildii!"),

    USE_FIRE("no-fire", "&aUżywanie flint & steel", "use_fire", Material.FLINT_AND_STEEL, 19,
            "&cNie masz uprawnień do używania flinta i stali (odpalenie) na terenie tej gildii!");

    private final String messageKey;
    private final String defaultDisplayName;
    private final String configKey;
    private final Material defaultIcon;
    private final int defaultSlot;
    private final String defaultDenialMessage;

    PermissionType(String messageKey, String defaultDisplayName, String configKey, Material defaultIcon, int defaultSlot, String defaultDenialMessage) {
        this.messageKey = messageKey;
        this.defaultDisplayName = defaultDisplayName;
        this.configKey = configKey;
        this.defaultIcon = defaultIcon;
        this.defaultSlot = defaultSlot;
        this.defaultDenialMessage = defaultDenialMessage;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getDefaultDisplayName() {
        return defaultDisplayName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public Material getDefaultIcon() {
        return defaultIcon;
    }

    public int getDefaultSlot() {
        return defaultSlot;
    }

    public String getDefaultDenialMessage() {
        return defaultDenialMessage;
    }

    private static final Map<String, PermissionType> CONFIG_KEY_MAP = new HashMap<>();
    private static final Map<String, PermissionType> MESSAGE_KEY_MAP = new HashMap<>();

    static {
        for (PermissionType type : values()) {
            CONFIG_KEY_MAP.put(type.configKey.toLowerCase(), type);
            MESSAGE_KEY_MAP.put(type.messageKey.toLowerCase(), type);
        }
    }

    public static PermissionType fromConfigKey(String configKey) {
        if (configKey == null) return null;
        return CONFIG_KEY_MAP.get(configKey.toLowerCase());
    }

    public static PermissionType fromMessageKey(String messageKey) {
        if (messageKey == null) return null;
        return MESSAGE_KEY_MAP.get(messageKey.toLowerCase());
    }
}
