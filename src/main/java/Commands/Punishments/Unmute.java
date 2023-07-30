package Commands.Punishments;

import CustomerFunctions.ConfigurationSQLFunctions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Unmute {

    public void unmuteUser(MessageReceivedEvent event, String[] args) {

        String muteRoleId = ConfigurationSQLFunctions.getSetting("MuteRoleId");

        // Verify text is provided in the arguments
        if (args.length < 1) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"unmute [user id | user mention]]").queue();

        } else {

            Role r = event.getGuild().getRoleById(muteRoleId);

            if (r == null) {

                event.getChannel().sendMessage("Cannot find the Muted role.").queue();

            } else {

                // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
                String userId = args[1];
                userId = userId.replace("<@", "").replace(">", "");

                // Now that we have an ID, verify it's a guild member
                Member member = event.getGuild().retrieveMemberById(userId).complete();

                PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                pc.sendMessage("```\nUNMUTED IN " + member.getGuild().getName() + "\nModerator: " +event.getAuthor().getName() + "```" ).queue(
                        success -> event.getChannel().sendMessage("Sent user a private message").queue(),
                        error -> event.getChannel().sendMessage("Failed to send private message: " + error.getMessage()).queue()
                );

                event.getGuild().removeRoleFromMember(member, r).queue();
                event.getChannel().sendMessage("Successfully unmuted " + member.getEffectiveName()).queue();

                TextChannel tc = event.getGuild().getTextChannelById(ConfigurationSQLFunctions.getSetting("PunishmentLogId"));
                if (tc != null && tc.canTalk()) {

                    tc.sendMessage("```\nUSER UNMUTED " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\n```" ).queue();

                } else {

                    event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();
                }


            }

        }

    }

}
