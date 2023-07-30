package Commands.Punishments;

import CustomerFunctions.ConfigurationSQLFunctions;
import CustomerFunctions.PunishmentSQLFunctions;
import CustomerFunctions.functions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.TimeUnit;

public class Ban {

    public void banUser(MessageReceivedEvent event, String[] args) {

        // Verify text is provided in the arguments
        if (args.length < 3) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"ban [user id | user mention] [duration (5m)] [reason]").queue();

        } else {

            // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
            String userId = args[1];
            userId = userId.replace("<@", "").replace(">", "");

            // Now that we have an ID, verify it's a guild member
            Member member = event.getGuild().retrieveMemberById(userId).complete();

            if (member.isOwner()) {

                event.getChannel().sendMessage("I cannot ban the owner of the server.").queue();

            } else {

                // Convert the duration argument into ms.
                String duration = args[2];
                long timeInMs = functions.timeToMilliseconds(duration);

                // convert the rest of the arguments into a string
                StringBuilder reason = new StringBuilder();
                for (int i = 3; i < args.length; i++) {

                    reason.append(args[i]).append(" ");

                }
                String finalReason = reason.toString();

                PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                pc.sendMessage("```\nBANNED FROM " + member.getGuild().getName() + "\nModerator: " +event.getAuthor().getName() + "\nDuration: " +duration + "\nReason: " + finalReason + "\n```" ).queue(
                        success -> event.getChannel().sendMessage("Sent user a private message").queue(),
                        error -> event.getChannel().sendMessage("Failed to send private message: " + error.getMessage()).queue()
                );

                event.getGuild().ban(member.getUser(), 0, TimeUnit.SECONDS).reason(finalReason).queue(
                        success -> event.getChannel().sendMessage("Successfully banned " + member.getEffectiveName() + " `[" + member.getId() + "]`").queue(),
                        error -> event.getChannel().sendMessage("Failed to ban " + member.getEffectiveName() + " `[" + member.getId() + "]`. Reason: " + error.getMessage() ).queue()
                );

                PunishmentSQLFunctions.insertPunishment("ban", member.getId(), event.getAuthor().getId(), String.valueOf(timeInMs), finalReason);

                TextChannel tc = event.getGuild().getTextChannelById(ConfigurationSQLFunctions.getSetting("PunishmentLogId"));
                if (tc != null && tc.canTalk()) {

                    tc.sendMessage("```\nUSER BANNED " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\nDuration: " +duration + "\nReason: " + finalReason + "\n```" ).queue();

                } else {

                    event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();
                }

            }

        }

    }


}


