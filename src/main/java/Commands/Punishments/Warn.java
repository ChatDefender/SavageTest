package Commands.Punishments;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Warn {

    public void warnUser(MessageReceivedEvent event, String[] args) {

        if (args.length < 2) {

            event.getChannel().sendMessage("Command Layout: s!warn [user id | user mention] [reason]").queue();

        } else {

            // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
            String userId = args[1];
            userId = userId.replace("<@", "").replace(">", "");

            // Now that we have an ID, verify it's a guild member
            Member member = event.getGuild().retrieveMemberById(userId).complete();

            if (member.isOwner()) {

                event.getChannel().sendMessage("I cannot warn the owner of the server.").queue();

            } else {

                // convert the rest of the arguments into a string
                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {

                    reason.append(args[i]).append(" ");

                }
                String finalReason = reason.toString();

                PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                pc.sendMessage("```\nWARNED IN " + member.getGuild().getName() + "\nModerator: " +event.getAuthor().getName() + "\nReason: " + finalReason + "\n```" ).queue(
                        success -> event.getChannel().sendMessage("Sent user a private message").queue(),
                        error -> event.getChannel().sendMessage("Failed to send private message: " + error.getMessage()).queue()
                );

                event.getChannel().sendMessage("Successfully warned " + member.getEffectiveName()).queue();


                TextChannel tc = event.getGuild().getTextChannelById("1132823318337179760");
                if (tc != null && tc.canTalk()) {

                    tc.sendMessage("```\nUSER WARNED " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\nReason: " + finalReason + "\n```" ).queue();

                } else {

                    event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();
                }

            }

        }

    }

}
