package Events;

import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.PunishmentManagement;
import Handlers.SQLHandlers.SQLFunctions;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class MemberJoin extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {

        if(PunishmentManagement.isCurrentlyMuted(event.getGuild().getId(), event.getMember().getId())) {

            event.getGuild().addRoleToMember(event.getMember(), Objects.requireNonNull(event.getGuild().getRoleById(ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.MUTEDROLEID)))).complete();

        }

    }

}
