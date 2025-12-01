package pl.tenfajnybartek.funnyaddons.managers;

import net.dzikoysk.funnyguilds.guild.Guild;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PlayerPositionManager {

    private final Map<UUID, Guild> positions;

    public PlayerPositionManager() {
        this.positions = new HashMap<>();
    }

    public Guild find(UUID uniqueId) {
        return this.positions.get(uniqueId);
    }

    public void add(UUID uniqueId, Guild guild) {
        this.positions.put(uniqueId, guild);
    }

    public void remove(UUID uniqueId) {
        this.positions.remove(uniqueId);
    }

    /**
     * Removes all player position entries that point to the specified guild.
     * Returns the set of UUIDs that were removed.
     *
     * @param guild the guild to remove entries for
     * @return set of player UUIDs that were removed
     */
    public Set<UUID> removeByGuild(Guild guild) {
        Set<UUID> removed = new HashSet<>();
        if (guild == null) {
            return removed;
        }
        java.util.Iterator<Map.Entry<UUID, Guild>> iterator = positions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Guild> entry = iterator.next();
            if (guild.equals(entry.getValue())) {
                removed.add(entry.getKey());
                iterator.remove();
            }
        }
        return removed;
    }
}
