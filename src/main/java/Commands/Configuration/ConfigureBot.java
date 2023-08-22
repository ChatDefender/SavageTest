package Commands.Configuration;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ConfigureBot extends BaseCommand {


    public ConfigureBot() {
        super("configurebot", new String[] {"config", "configbot"}, "config [setting] [value]", "Current Available Settings: prefix, punishmentlogs, mutedrole", "");
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length < 2) {

            event.getChannel().sendMessage("Command Layout: "+ConfigurationSQLFunctions.getSetting("Prefix")+"config [setting] [value]").queue();

        } else {

            String setting = args[1].toLowerCase();

            switch (setting) {

                case "prefix":

                    ConfigurationSQLFunctions.setSetting("Prefix", args[2]);
                    event.getChannel().sendMessage("Successfully updated the prefix to `" + args[2] + "`").queue();
                    break;
                case "pl":
                case "punlogs":
                case "punishmentlogs":

                    String textChannelId = args[2];
                    textChannelId = textChannelId.replace("<#", "").replace(">", "");

                    if (event.getGuild().getTextChannelById(Long.parseLong(textChannelId)) != null) {

                        String finalTextChannelId = textChannelId;
                        event.getGuild().getTextChannelById(Long.parseLong(textChannelId)).sendMessage("This is a test message").queue(

                                success -> {

                                    ConfigurationSQLFunctions.setSetting("PunishmentLogId", finalTextChannelId);
                                    event.getChannel().sendMessage("Successfully updated the punishment logs to " + event.getGuild().getTextChannelById(Long.parseLong(finalTextChannelId)).getName() + " `["+finalTextChannelId+"]`").queue();

                                },

                                failure -> event.getChannel().sendMessage("I do not have access to send messages to that channel.").queue()

                        );

                    } else {

                        event.getChannel().sendMessage("I could not find that channel. Either it does not exist or I do not have access to it.").queue();

                    }


                    break;

                case "mr":
                case "muterole":
                case "mutedrole":

                    String roleId = args[2];
                    roleId = roleId.replace("<@&", "").replace(">", "");

                    Role r = event.getGuild().getRoleById(Long.parseLong(roleId));

                    if (r == null) {

                        event.getChannel().sendMessage("I cannot find the Muted role.").queue();

                    } else {

                        ConfigurationSQLFunctions.setSetting("MuteRoleId", args[2]);
                        event.getChannel().sendMessage("Successfully updated the mute role to `" + r.getName() + " ["+r.getId()+"]`").queue();

                    }
                    break;



            }

        }

    }

}
