package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Warn extends BaseCommand {

    public Warn() {
        super("warn", new String[] {}, "warn {@user | userId} {reason}", "Warns a user who is misbehaving in the discord.", "", Permission.MANAGE_ROLES);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length <= 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            SQLFunctions.Punishments pun = SQLFunctions.Punishments.WARN;

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

                    // execute actual punishment here

                    PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                    if (pc.canTalk()) {

                        pc.sendMessage("```\nPUNISHMENT EXECUTED IN  " + member.getGuild().getName() + "\nPUNISHMENT TYPE: " + pun + "\nModerator: " + event.getAuthor().getName() + "\nReason: " + finalReason + "\n```").queue();

                    } else {

                        event.getChannel().sendMessage("Failed to send user a private message: User is not accepting private messages").queue();

                    }

                    event.getChannel().sendMessage("Successfully warned " + member.getNickname() + "`["+member.getId()+"]`.").queue();

                    int id = PunishmentLogManagement.insertPunishment(event.getGuild().getId(), userId, event.getAuthor().getId(), pun, "0", finalReason );

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
