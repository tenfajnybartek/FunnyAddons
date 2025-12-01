package pl.tenfajnybartek.funnyaddons.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class GUIHolder implements InventoryHolder {

    public enum Kind { MEMBERS_LIST, MEMBER_PERMISSIONS }

    private final Kind kind;
    private final String guildTag;
    private final UUID member;

    public GUIHolder(Kind kind, String guildTag, UUID member) {
        this.kind = kind;
        this.guildTag = guildTag;
        this.member = member;
    }

    public Kind getKind() {
        return kind;
    }

    public String getGuildTag() {
        return guildTag;
    }

    public UUID getMember() {
        return member;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}