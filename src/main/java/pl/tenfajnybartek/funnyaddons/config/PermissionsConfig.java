package pl.tenfajnybartek.funnyaddons.config;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.util.List;


public class PermissionsConfig {

    private static final String RELATION_SECTION = "RELATION";

    private final FileConfiguration cfg;

    public PermissionsConfig(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    public int getSlotFor(PermissionType type) {
        String path = "permissions.gui.slots." + type.getConfigKey();
        return cfg.getInt(path, type.getDefaultSlot());
    }

    public String getDisplayNameFor(PermissionType type) {
        String path = "permissions.gui.names." + type.getConfigKey();
        return cfg.getString(path, type.getDefaultDisplayName());
    }

    public Material getIconFor(PermissionType type) {
        String path = "permissions.icons." + type.getConfigKey();
        String mat = cfg.getString(path, null);
        if (mat == null || mat.isBlank()) return type.getDefaultIcon();
        Material m = Material.matchMaterial(mat);
        return m != null ? m : type.getDefaultIcon();
    }

    public PermissionType getPermissionTypeBySlot(int slot) {
        for (PermissionType type : PermissionType.values()) {
            if (getSlotFor(type) == slot) {
                return type;
            }
        }
        return null;
    }

    public int getMemberPermissionsSize() {
        return cfg.getInt(
                PermissionsConfigKey.GUI_MEMBER_PERMISSIONS_SIZE.getPath(),
                PermissionsConfigKey.GUI_MEMBER_PERMISSIONS_SIZE.getDefaultInt()
        );
    }

    public int getTitleMaxLength() {
        return cfg.getInt(
                PermissionsConfigKey.GUI_TITLE_MAX_LENGTH.getPath(),
                PermissionsConfigKey.GUI_TITLE_MAX_LENGTH.getDefaultInt()
        );
    }

    public String getMembersTitle() {
        return cfg.getString(
                PermissionsConfigKey.GUI_MEMBERS_TITLE.getPath(),
                PermissionsConfigKey.GUI_MEMBERS_TITLE.getDefaultString()
        );
    }

    public String getMemberPermsTitle() {
        return cfg.getString(
                PermissionsConfigKey.GUI_MEMBER_PERMS_TITLE.getPath(),
                PermissionsConfigKey.GUI_MEMBER_PERMS_TITLE.getDefaultString()
        );
    }

    public int getSlot(PermissionsConfigKey key) {
        return cfg.getInt(key.getPath(), key.getDefaultInt());
    }

    public int getBreakSlot() {
        return getSlot(PermissionsConfigKey.SLOT_BREAK);
    }

    public int getPlaceSlot() {
        return getSlot(PermissionsConfigKey.SLOT_PLACE);
    }

    public int getInteractBlockSlot() {
        return getSlot(PermissionsConfigKey.SLOT_INTERACT_BLOCK);
    }

    public int getOpenChestSlot() {
        return getSlot(PermissionsConfigKey.SLOT_OPEN_CHEST);
    }

    public int getOpenEnderChestSlot() {
        return getSlot(PermissionsConfigKey.SLOT_OPEN_ENDER_CHEST);
    }

    public int getUseBucketsSlot() {
        return getSlot(PermissionsConfigKey.SLOT_USE_BUCKETS);
    }

    public int getUseFireSlot() {
        return getSlot(PermissionsConfigKey.SLOT_USE_FIRE);
    }

    public int getFriendlyFireSlot() {
        return getSlot(PermissionsConfigKey.SLOT_FRIENDLY_FIRE);
    }

    public int getBackSlot() {
        return getSlot(PermissionsConfigKey.SLOT_BACK);
    }

    public int getInfoSlot() {
        return getSlot(PermissionsConfigKey.SLOT_INFO);
    }

    public String getName(PermissionsConfigKey key) {
        return cfg.getString(key.getPath(), key.getDefaultString());
    }

    public String getBreakName() {
        return getName(PermissionsConfigKey.NAME_BREAK);
    }

    public String getPlaceName() {
        return getName(PermissionsConfigKey.NAME_PLACE);
    }

    public String getInteractBlockName() {
        return getName(PermissionsConfigKey.NAME_INTERACT_BLOCK);
    }

    public String getOpenChestName() {
        return getName(PermissionsConfigKey.NAME_OPEN_CHEST);
    }

    public String getOpenEnderChestName() {
        return getName(PermissionsConfigKey.NAME_OPEN_ENDER_CHEST);
    }

    public String getUseBucketsName() {
        return getName(PermissionsConfigKey.NAME_USE_BUCKETS);
    }

    public String getUseFireName() {
        return getName(PermissionsConfigKey.NAME_USE_FIRE);
    }

    public String getFriendlyFireName() {
        return getName(PermissionsConfigKey.NAME_FRIENDLY_FIRE);
    }

    public String getBackName() {
        return getName(PermissionsConfigKey.NAME_BACK);
    }

    public String getInfoName() {
        return getName(PermissionsConfigKey.NAME_INFO);
    }

    public String getToggleLore() {
        return cfg.getString(
                PermissionsConfigKey.LORE_TOGGLE.getPath(),
                PermissionsConfigKey.LORE_TOGGLE.getDefaultString()
        );
    }

    public String getInfoLore() {
        return cfg.getString(
                PermissionsConfigKey.LORE_INFO.getPath(),
                PermissionsConfigKey.LORE_INFO.getDefaultString()
        );
    }

    public String getStateOn() {
        return cfg.getString(
                PermissionsConfigKey.STATE_ON.getPath(),
                PermissionsConfigKey.STATE_ON.getDefaultString()
        );
    }

    public String getStateOff() {
        return cfg.getString(
                PermissionsConfigKey.STATE_OFF.getPath(),
                PermissionsConfigKey.STATE_OFF.getDefaultString()
        );
    }

    public Material getIcon(String key, Material fallback) {
        String path = "permissions.icons." + key;
        String mat = cfg.getString(path, null);
        if (mat == null || mat.isBlank()) return fallback;
        Material m = Material.matchMaterial(mat);
        return m != null ? m : fallback;
    }

    public Material getIcon(PermissionsConfigKey key, Material fallback) {
        String mat = cfg.getString(key.getPath(), null);
        if (mat == null || mat.isBlank()) return fallback;
        Material m = Material.matchMaterial(mat);
        return m != null ? m : fallback;
    }

    public Material getBreakIcon() {
        return getIcon(PermissionsConfigKey.ICON_BREAK, Material.DIAMOND_PICKAXE);
    }

    public Material getPlaceIcon() {
        return getIcon(PermissionsConfigKey.ICON_PLACE, Material.OAK_PLANKS);
    }

    public Material getInteractBlockIcon() {
        return getIcon(PermissionsConfigKey.ICON_INTERACT_BLOCK, Material.LEVER);
    }

    public Material getOpenChestIcon() {
        return getIcon(PermissionsConfigKey.ICON_OPEN_CHEST, Material.CHEST);
    }

    public Material getOpenEnderChestIcon() {
        return getIcon(PermissionsConfigKey.ICON_OPEN_ENDER_CHEST, Material.ENDER_CHEST);
    }

    public Material getUseBucketsIcon() {
        return getIcon(PermissionsConfigKey.ICON_USE_BUCKETS, Material.WATER_BUCKET);
    }

    public Material getUseFireIcon() {
        return getIcon(PermissionsConfigKey.ICON_USE_FIRE, Material.FLINT_AND_STEEL);
    }

    public Material getFriendlyFireIcon() {
        return getIcon(PermissionsConfigKey.ICON_FRIENDLY_FIRE, Material.TIPPED_ARROW);
    }

    public Material getBackIcon() {
        return getIcon(PermissionsConfigKey.ICON_BACK, Material.BARRIER);
    }

    public Material getInfoIcon() {
        return getIcon(PermissionsConfigKey.ICON_INFO, Material.PLAYER_HEAD);
    }

    public List<String> getDefaultPerms(String role) {
        return cfg.getStringList("permissions.defaults." + role);
    }

    public List<String> getDefaultPerms(PermissionsConfigKey key) {
        return cfg.getStringList(key.getPath());
    }

    public List<String> getMemberDefaultPerms() {
        return getDefaultPerms(PermissionsConfigKey.DEFAULTS_MEMBER);
    }

    public List<String> getOfficerDefaultPerms() {
        return getDefaultPerms(PermissionsConfigKey.DEFAULTS_OFFICER);
    }

    public List<String> getOwnerDefaultPerms() {
        return getDefaultPerms(PermissionsConfigKey.DEFAULTS_OWNER);
    }

    public boolean isRelationEnabled() {
        return cfg.getBoolean(
                PermissionsConfigKey.RELATION_ENABLE.getPath(),
                PermissionsConfigKey.RELATION_ENABLE.getDefaultBoolean()
        );
    }

    public String getRelationDefaultBehavior() {
        return cfg.getString(
                PermissionsConfigKey.RELATION_DEFAULT_BEHAVIOR.getPath(),
                PermissionsConfigKey.RELATION_DEFAULT_BEHAVIOR.getDefaultString()
        );
    }

    public boolean isRelationEnforced(String relationType) {
        return cfg.getBoolean("permissions.relation." + RELATION_SECTION + "." + relationType + ".enforce", false);
    }
}
