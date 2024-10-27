package Events;

import Handlers.SQLHandlers.ConfigurationSettings;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoin extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        ConfigurationSettings.verifyGuildSetting(event.getGuild().getId());

    }

}
