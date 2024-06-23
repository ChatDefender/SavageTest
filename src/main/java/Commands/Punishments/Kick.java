package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.PunishmentManagement;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Kick extends BaseCommand {
    public Kick() {
        super("kick", new String[] {"remove", "buhbye"}, "kick {@user | userId} {reason}", "Removes a member from the server.", "", Permission.KICK_MEMBERS);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        // Verify text is provided in the arguments
        if (args.length < 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {


            SQLFunctions.Punishments pun = SQLFunctions.Punishments.KICK;

            // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
            String userId = functions.getUserId(event, args[0]);

            if (userId == null) {

                event.getChannel().sendMessage("I cannot find the mentioned user!").queue();

            } else {

                // Now that we have an ID, verify it's a guild member
                Member member = event.getGuild().retrieveMemberById(userId).complete();

                if (member.isOwner()) {

                    event.getChannel().sendMessage("I cannot punish the owner of the server.").queue();

                } else {

                    // convert the rest of the arguments into a string
                    StringBuilder reason = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {

                        reason.append(args[i]).append(" ");

                    }
                    String finalReason = reason.toString();

                    PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                    if (pc.canTalk()) {

                        pc.sendMessage("```\nPUNISHMENT EXECUTED IN  " + member.getGuild().getName() + "\nPUNISHMENT TYPE: " + pun + "\nModerator: " + event.getAuthor().getName() + "\nReason: " + finalReason + "\n```").queue();

                    } else {

                        event.getChannel().sendMessage("Failed to send user a private message: User is not accepting private messages").queue();

                    }
                    // execute actual punishment here
                    event.getGuild().kick(member).queue(

                            success -> event.getChannel().sendMessage("Successfully kicked " + member.getNickname() + " `[" + member.getId() + "].`").queue(),
                            error -> event.getChannel().sendMessage("There was an error kicking "+ member.getNickname() + " `[" + member.getId() + "].` Error Message" + error.getMessage()).queue()

                    );

                    int id = PunishmentManagement.insertPunishment(event.getGuild().getId(), userId, event.getAuthor().getId(), pun, "0", finalReason );

                    String punishmentLogChannelId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PUNISHMENTLOGID);

                    if (punishmentLogChannelId != null) {

                        TextChannel tc = event.getGuild().getTextChannelById(punishmentLogChannelId);
                        if (tc != null && tc.canTalk()) {

                            tc.sendMessage("```\nPUNISHMENT EXECUTED: "+ pun + " " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\nReason: " + finalReason + "\nPunishmentId: "+id+"```" ).queue();

                        } else {

                            event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();

                        }

                    }

                }

            }

        }

    }

}