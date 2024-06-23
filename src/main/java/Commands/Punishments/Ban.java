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

import java.util.concurrent.TimeUnit;

public class Ban extends BaseCommand {

    public Ban() {
        super("ban", new String[]{"banish", "banana", "bananahammer"}, "ban {@user | userId} {duration} {reason}", "Bans a user from the discord server", "Available times are in (m)inutes, (h)ours, (d)ays, (mon)ths, (y)ear, or 0 for permanent.", Permission.BAN_MEMBERS);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length < 3) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            SQLFunctions.Punishments pun = SQLFunctions.Punishments.BAN;

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

                    // Convert the duration argument into ms.
                    String duration = args[1];
                    long timeInMs = functions.timeToMilliseconds(duration);

                    if (timeInMs == -1) {

                        event.getChannel().sendMessage("You provided an invalid time. Available times are as follows:  \n#m - minutes, \n#h - hours, \n#d - days, \n#mon - months, \n#y - year, \nor 0 for permanent.").queue();

                    } else {

                        // convert the rest of the arguments into a string
                        StringBuilder reason = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {

                            reason.append(args[i]).append(" ");

                        }
                        String finalReason = reason.toString();

                        PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                        if (pc.canTalk()) {

                            pc.sendMessage("```\nPUNISHMENT EXECUTED IN  " + member.getGuild().getName() + "\nPUNISHMENT TYPE: " + pun + "\nModerator: " + event.getAuthor().getName() + "\nDuration: " + duration + "\nReason: " + finalReason + "\n```").queue();

                        } else {

                            event.getChannel().sendMessage("Failed to send user a private message: User is not accepting private messages").queue();

                        }
                        // execute actual punishment here
                        event.getGuild().ban(member.getUser(), 0, TimeUnit.SECONDS).reason(finalReason).queue(
                                success -> event.getChannel().sendMessage("Successfully banned " + member.getEffectiveName() + " `[" + member.getId() + "]`").queue(),
                                error -> event.getChannel().sendMessage("Failed to ban " + member.getEffectiveName() + " `[" + member.getId() + "]`. Reason: " + error.getMessage() ).queue()
                        );


                        int id = PunishmentManagement.insertPunishment(event.getGuild().getId(), userId, event.getAuthor().getId(), pun, String.valueOf(timeInMs), finalReason );

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

}

