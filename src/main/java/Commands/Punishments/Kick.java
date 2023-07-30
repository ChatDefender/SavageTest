package Commands.Punishments;

import CustomerFunctions.ConfigurationSQLFunctions;
import CustomerFunctions.PunishmentSQLFunctions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Kick {
    public void kickUser(MessageReceivedEvent event, String[] args) {

        // Verify text is provided in the arguments
        if (args.length < 2) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"kick [user id | user mention] [reason]").queue();

        } else {

            // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
            String userId = args[1];
            userId = userId.replace("<@", "").replace(">", "");

            // Now that we have an ID, verify it's a guild member
            Member member = event.getGuild().retrieveMemberById(userId).complete();

            if (member.isOwner()) {

                event.getChannel().sendMessage("I cannot kick the owner of the server.").queue();

            } else {

                // convert the rest of the arguments into a string
                StringBuilder reason = new StringBuilder();
                for (int i = 3; i < args.length; i++) {

                    reason.append(args[i]).append(" ");

                }
                String finalReason = reason.toString();

                PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                pc.sendMessage("```\nKICKED FROM " + member.getGuild().getName() + "\nModerator: " +event.getAuthor().getName() + "\nReason: " + finalReason + "\n```" ).queue(
                        success -> event.getChannel().sendMessage("Sent user a private message").queue(),
                        error -> event.getChannel().sendMessage("Failed to send private message: " + error.getMessage()).queue()
                );

                event.getGuild().kick(UserSnowflake.fromId(userId)).reason(finalReason).queue(
                        success -> event.getChannel().sendMessage("Successfully kicked " + member.getEffectiveName() + " `[" + member.getId() + "]`").queue(),
                        error -> event.getChannel().sendMessage("Failed to kick " + member.getEffectiveName() + " `[" + member.getId() + "]`. Reason: " + error.getMessage() ).queue()
                );

                PunishmentSQLFunctions.insertPunishment("kick", member.getId(), event.getAuthor().getId(), String.valueOf(0), finalReason);

                TextChannel tc = event.getGuild().getTextChannelById(ConfigurationSQLFunctions.getSetting("PunishmentLogId"));

                if (tc != null && tc.canTalk()) {

                    tc.sendMessage("```\nUSER KICKED " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\nReason: " + finalReason + "\n```" ).queue();

                } else {

                    event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();

                }

            }

        }

    }

}