package pl.tenfajnybartek.funnyaddons.utils;

import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIContext {

    private static final Map<UUID, String> membersInventory = new HashMap<>();

    private static final Map<UUID, MemberContext> memberPermInv = new HashMap<>();

    // Panel GUI contexts
    private static final Map<UUID, PanelContext> panelMainInv = new HashMap<>();
    private static final Map<UUID, PanelContext> panelTerritoryInv = new HashMap<>();
    private static final Map<UUID, PanelContext> panelEffectsInv = new HashMap<>();

    public static void registerGuildMembersInventory(UUID viewer, String guildTag, PermissionsManager perms) {
        membersInventory.put(viewer, guildTag);
    }

    public static String getGuildTagForMembersInv(UUID viewer) {
        return membersInventory.get(viewer);
    }

    public static void unregisterMembersInventory(UUID viewer) {
        membersInventory.remove(viewer);
    }

    public static void registerMemberPermissionsInventory(UUID viewer, String guildTag, java.util.UUID member, PermissionsManager perms) {
        memberPermInv.put(viewer, new MemberContext(guildTag, member));
    }

    public static MemberContext getMemberContext(UUID viewer) {
        return memberPermInv.get(viewer);
    }

    public static void unregisterMemberPermissionsInventory(UUID viewer) {
        memberPermInv.remove(viewer);
    }

    // ==================== Panel GUI Context Methods ====================

    public static void registerPanelMainInventory(UUID viewer, String guildTag) {
        panelMainInv.put(viewer, new PanelContext(guildTag));
    }

    public static PanelContext getPanelMainContext(UUID viewer) {
        return panelMainInv.get(viewer);
    }

    public static void unregisterPanelMainInventory(UUID viewer) {
        panelMainInv.remove(viewer);
    }

    public static void registerPanelTerritoryInventory(UUID viewer, String guildTag) {
        panelTerritoryInv.put(viewer, new PanelContext(guildTag));
    }

    public static PanelContext getPanelTerritoryContext(UUID viewer) {
        return panelTerritoryInv.get(viewer);
    }

    public static void unregisterPanelTerritoryInventory(UUID viewer) {
        panelTerritoryInv.remove(viewer);
    }

    public static void registerPanelEffectsInventory(UUID viewer, String guildTag) {
        panelEffectsInv.put(viewer, new PanelContext(guildTag));
    }

    public static PanelContext getPanelEffectsContext(UUID viewer) {
        return panelEffectsInv.get(viewer);
    }

    public static void unregisterPanelEffectsInventory(UUID viewer) {
        panelEffectsInv.remove(viewer);
    }

    /**
     * Unregisters all panel-related inventories for a viewer.
     */
    public static void unregisterAllPanelInventories(UUID viewer) {
        panelMainInv.remove(viewer);
        panelTerritoryInv.remove(viewer);
        panelEffectsInv.remove(viewer);
    }

    // ==================== Inner Classes ====================

    public static class MemberContext {
        public final String guildTag;
        public final java.util.UUID member;

        public MemberContext(String guildTag, java.util.UUID member) {
            this.guildTag = guildTag;
            this.member = member;
        }
    }

    /**
     * Context for panel GUI operations.
     */
    public static class PanelContext {
        public final String guildTag;

        public PanelContext(String guildTag) {
            this.guildTag = guildTag;
        }
    }
}
