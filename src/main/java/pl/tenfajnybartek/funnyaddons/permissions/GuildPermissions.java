package pl.tenfajnybartek.funnyaddons.permissions;

import pl.tenfajnybartek.funnyaddons.utils.PermissionType;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class GuildPermissions {
    private final String guildTag;
    private final UUID member;
    private final Set<PermissionType> permissions;

    public GuildPermissions(String guildTag, UUID member) {
        this.guildTag = guildTag;
        this.member = member;
        this.permissions = EnumSet.noneOf(PermissionType.class);
    }

    public GuildPermissions(String guildTag, UUID member, Set<PermissionType> permissions) {
        this.guildTag = guildTag;
        this.member = member;
        this.permissions = permissions.isEmpty() ? EnumSet.noneOf(PermissionType.class) : EnumSet.copyOf(permissions);
    }

    public String getGuildTag() {
        return guildTag;
    }

    public UUID getMember() {
        return member;
    }

    public Set<PermissionType> getPermissions() {
        return permissions;
    }

    public boolean has(PermissionType type) {
        return permissions.contains(type);
    }

    public void grant(PermissionType type) {
        permissions.add(type);
    }

    public void revoke(PermissionType type) {
        permissions.remove(type);
    }

    public void toggle(PermissionType type) {
        if (has(type)) {
            revoke(type);
        } else {
            grant(type);
        }
    }
}