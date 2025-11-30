package pl.tenfajnybartek.funnyaddons.utils;

import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIContext {

    // viewerUUID -> guildTag (for members list)
    private static final Map<UUID, String> membersInventory = new HashMap<>();

    // viewerUUID -> (guildTag, memberUuid) for member-permissions view
    private static final Map<UUID, MemberContext> memberPermInv = new HashMap<>();

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

    public static class MemberContext {
        public final String guildTag;
        public final java.util.UUID member;

        public MemberContext(String guildTag, java.util.UUID member) {
            this.guildTag = guildTag;
            this.member = member;
        }
    }
}
