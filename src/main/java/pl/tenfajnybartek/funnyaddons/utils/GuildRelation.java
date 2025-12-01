package pl.tenfajnybartek.funnyaddons.utils;

import net.dzikoysk.funnyguilds.guild.Guild;

public enum GuildRelation {

    ENEMY, MEMBER, ALLY, OUTSIDER;

    public static GuildRelation match(Guild guild, Guild targetGuild) {
        // If targetGuild is null, no terrain to display
        if (targetGuild == null) {
            return OUTSIDER;
        }
        // If player has no guild (outsider), return OUTSIDER
        if (guild == null) {
            return OUTSIDER;
        }
        if (guild.equals(targetGuild)) {
            return MEMBER;
        }
        if (guild.isAlly(targetGuild)) {
            return ALLY;
        }
        return ENEMY;
    }
}
