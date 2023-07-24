package Commands.Punishments;

import CustomerFunctions.functions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Mute {


    public void muteUser(MessageReceivedEvent event, String[] args) {

        String muteRoleId = "1132812396268888116";

        // Verify text is provided in the arguments
        if (args.length < 3) {

            event.getChannel().sendMessage("Command Layout: s!mute [user id | user mention] [duration (5m)] [reason]").queue();

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

                if (member.isOwner()) {

                    event.getChannel().sendMessage("I cannot mute the owner of the server.").queue();

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
                    pc.sendMessage("```\nMUTED IN " + member.getGuild().getName() + "\nModerator: " +event.getAuthor().getName() + "\nDuration: " +duration + "\nReason: " + finalReason + "\n```" ).queue(
                            success -> event.getChannel().sendMessage("Sent user a private message").queue(),
                            error -> event.getChannel().sendMessage("Failed to send private message: " + error.getMessage()).queue()
                    );

                    event.getGuild().addRoleToMember(member, r).queue();
                    event.getChannel().sendMessage("Successfully muted " + member.getEffectiveName()).queue();


                    TextChannel tc = event.getGuild().getTextChannelById("1132823318337179760");
                    if (tc != null && tc.canTalk()) {

                        tc.sendMessage("```\nUSER MUTED " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\nDuration: " +duration + "\nReason: " + finalReason + "\n```" ).queue();

                    } else {

                        event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();
                    }

                }

            }

        }

    }
}
