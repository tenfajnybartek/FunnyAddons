package pl.tenfajnybartek.funnyaddons.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.file.FileConfiguration;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

/**
 * Configuration facade for all textual messages and Adventure Component wrappers.
 * <p>
 * Supports dynamic message retrieval using {@link PermissionType} enum metadata,
 * enabling config-driven message handling without hardcoded keys.
 */
public class MessagesConfig {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final FileConfiguration cfg;

    public MessagesConfig(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    /**
     * Retrieves a message string from config.
     * First tries "messages.{key}", then falls back to "{key}" directly.
     */
    public String getMessage(String key, String defaultValue) {
        if (key == null) return defaultValue;
        String val;
        if (key.startsWith("messages.")) {
            val = cfg.getString(key, defaultValue);
        } else {
            val = cfg.getString("messages." + key, null);
            if (val == null) val = cfg.getString(key, defaultValue);
        }
        return val != null ? val : defaultValue;
    }

    public String getMessage(String key) {
        return getMessage(key, "");
    }

    /**
     * Converts a legacy-colored string (using &) to Adventure Component.
     */
    public Component toComponent(String text) {
        if (text == null) return Component.empty();
        return LEGACY.deserialize(text);
    }

    /**
     * Retrieves a message and converts it to Adventure Component.
     */
    public Component getMessageAsComponent(String key) {
        return toComponent(getMessage(key, ""));
    }

    public Component getMessageAsComponent(String key, String defaultValue) {
        return toComponent(getMessage(key, defaultValue));
    }

    // ---------- Convenience getters for common messages ----------

    public String getInGuildMessage() {
        return getMessage("in-guild-message", "&cNie możesz użyć tej komendy, będąc w gildii!");
    }

    public String getNoPermissionMessage() {
        return getMessage("no-permission", "&cNie masz uprawnień do tej akcji!");
    }

    public String getNoRegionMessage() {
        return getMessage("no-region", "&cGildia/region nie odnaleziony!");
    }

    public String getFreeSpaceMessage() {
        return getMessage("free-space-message", "Lista wolnych lokalizacji na gildie");
    }

    public String getLocationListMessage() {
        String v = getMessage("location-list-message", "");
        if (v.isEmpty()) v = getMessage("locationList", "x: {X}, z: {Z} - odleglosc {DISTANCE} metrow.");
        return v;
    }

    // ---------- Generalized permissions messages ----------

    /**
     * Retrieves a permission-related message using the "perms-{key}" pattern.
     *
     * @param key the permission key suffix (e.g., "no-break" for "perms-no-break")
     * @param defaultValue the default value if not found
     * @return the message string
     */
    public String getPermsMessage(String key, String defaultValue) {
        if (key == null || key.isEmpty()) {
            return defaultValue;
        }
        return getMessage("perms-" + key, defaultValue);
    }

    /**
     * Retrieves a permission-related message as Component.
     *
     * @param key the permission key suffix (e.g., "no-break" for "perms-no-break")
     * @param defaultValue the default value if not found
     * @return the message as Component
     */
    public Component getPermsComponent(String key, String defaultValue) {
        return toComponent(getPermsMessage(key, defaultValue));
    }

    // ---------- PermissionType-based Dynamic Message Lookup ----------

    /**
     * Retrieves the permission denial message for a specific PermissionType.
     * <p>
     * Uses the permission type's message key to look up the message in config.
     * Falls back to a generic default message if not configured.
     *
     * @param type The permission type to get the denial message for
     * @return The configured denial message for the permission type
     */
    public String getPermsMessageFor(PermissionType type) {
        return getPermsMessage(type.getMessageKey(), getDefaultDenialMessage(type));
    }

    /**
     * Retrieves the permission denial message as a Component for a specific PermissionType.
     * <p>
     * Uses the permission type's message key to look up the message in config.
     *
     * @param type The permission type to get the denial message for
     * @return The configured denial message as a Component
     */
    public Component getPermsComponentFor(PermissionType type) {
        return toComponent(getPermsMessageFor(type));
    }

    /**
     * Gets a default denial message for a permission type.
     * <p>
     * This provides a fallback message based on the permission type's display name.
     *
     * @param type The permission type
     * @return A default denial message
     */
    private String getDefaultDenialMessage(PermissionType type) {
        return switch (type) {
            case BREAK -> "&cNie masz uprawnień do niszczenia na terenie tej gildii!";
            case PLACE -> "&cNie masz uprawnień do stawiania bloków na terenie tej gildii!";
            case OPEN_CHEST -> "&cNie masz uprawnień do otwierania skrzyń na terenie tej gildii!";
            case OPEN_ENDER_CHEST -> "&cNie masz uprawnień do otwierania ender chesta na terenie tej gildii!";
            case INTERACT_BLOCK -> "&cNie masz uprawnień do używania przycisków/dźwigni/drzwi na terenie tej gildii!";
            case USE_BUCKETS -> "&cNie masz uprawnień do używania kubełków na terenie tej gildii!";
            case USE_FIRE -> "&cNie masz uprawnień do używania flinta i stali (odpalenie) na terenie tej gildii!";
            case FRIENDLY_FIRE -> "&cNie możesz obrażać członków swojej gildii!";
        };
    }

    // Permission denial messages
    public String getPermsNoBreakMessage() {
        return getPermsMessage("no-break", "&cNie masz uprawnień do niszczenia na terenie tej gildii!");
    }

    public String getPermsNoPlaceMessage() {
        return getPermsMessage("no-place", "&cNie masz uprawnień do stawiania bloków na terenie tej gildii!");
    }

    public String getPermsNoOpenChestMessage() {
        return getPermsMessage("no-open-chest", "&cNie masz uprawnień do otwierania skrzyń na terenie tej gildii!");
    }

    public String getPermsNoOpenEnderMessage() {
        return getPermsMessage("no-open-ender", "&cNie masz uprawnień do otwierania ender chesta na terenie tej gildii!");
    }

    public String getPermsNoInteractMessage() {
        return getPermsMessage("no-interact", "&cNie masz uprawnień do używania przycisków/dźwigni/drzwi na terenie tej gildii!");
    }

    public String getPermsNoBucketsMessage() {
        return getPermsMessage("no-buckets", "&cNie masz uprawnień do używania kubełków na terenie tej gildii!");
    }

    public String getPermsNoFireMessage() {
        return getPermsMessage("no-fire", "&cNie masz uprawnień do używania flinta i stali (odpalenie) na terenie tej gildii!");
    }

    public String getPermsNoFriendlyFireMessage() {
        return getPermsMessage("no-friendly-fire", "&cNie możesz obrażać członków swojej gildii!");
    }

    // Permission denial messages as Components
    public Component getPermsNoBreakComponent() {
        return toComponent(getPermsNoBreakMessage());
    }

    public Component getPermsNoPlaceComponent() {
        return toComponent(getPermsNoPlaceMessage());
    }

    public Component getPermsNoOpenChestComponent() {
        return toComponent(getPermsNoOpenChestMessage());
    }

    public Component getPermsNoOpenEnderComponent() {
        return toComponent(getPermsNoOpenEnderMessage());
    }

    public Component getPermsNoInteractComponent() {
        return toComponent(getPermsNoInteractMessage());
    }

    public Component getPermsNoBucketsComponent() {
        return toComponent(getPermsNoBucketsMessage());
    }

    public Component getPermsNoFireComponent() {
        return toComponent(getPermsNoFireMessage());
    }

    public Component getPermsNoFriendlyFireComponent() {
        return toComponent(getPermsNoFriendlyFireMessage());
    }
}
