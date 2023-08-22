package Events;

import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Handlers.SQLHandlers.TimedPunishmentsSQLFunctions;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class MemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if(TimedPunishmentsSQLFunctions.isCurrentlyMuted(event.getMember().getId())) {

            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(ConfigurationSQLFunctions.getSetting("MuteRoleId")))).complete();

        }

    }

}
