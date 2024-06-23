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

public class Mute extends BaseCommand {

    public Mute() {
        super("mute", new String[] {"silence", "bequiet", "shush"}, "mute {@user | userId} {duration} {reason}", "Mutes a specified user for a specified duration", "\nAvailable times are in (m)inutes, (h)ours, (d)ays, (mon)ths, (y)ear, or 0 for indefinite.\nUsed when someone is being naughty in the chatty, or tickles rose on the wrong day", Permission.MANAGE_ROLES);    }

    public void run(MessageReceivedEvent event, String[] args) {


        // Verify text is provided in the arguments
        if (args.length <= 3) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String muteRoleId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.MUTEDROLEID);

            if (muteRoleId.isEmpty()) {

                event.getChannel().sendMessage("The mute role needs to be configured. Run `"+ ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PREFIX)+"config muteroleid [@mutedRole | mute role id]`").queue();

            } else if (event.getGuild().getRoleById(muteRoleId) == null) {

                event.getChannel().sendMessage("I could not find the mute role. Please reset it using the setmutedrole command").queue();

            } else {


                SQLFunctions.Punishments pun = SQLFunctions.Punishments.MUTE;

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

                            if (member.getRoles().contains(event.getGuild().getRoleById(muteRoleId))) {

                                event.getChannel().sendMessage("User " + member.getNickname() + " `["+member.getId()+"]` is already muted.").queue();

                            } else {

                                event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(muteRoleId)).queue(

                                        success -> event.getChannel().sendMessage("Successfully muted "+ member.getNickname() + " `["+member.getId()+"]`").queue(),
                                        error -> event.getChannel().sendMessage("There was an error in muting "+ member.getNickname() + " `["+member.getId()+"]`\nError: " + error.getMessage()).queue()

                                );

                                PrivateChannel pc = member.getUser().openPrivateChannel().complete();

                                if (pc.canTalk()) {

                                    pc.sendMessage("```\nPUNISHMENT EXECUTED IN  " + member.getGuild().getName() + "\nPUNISHMENT TYPE: " + pun + "\nModerator: " + event.getAuthor().getName() + "\nDuration: " + duration + "\nReason: " + finalReason + "\n```").queue();

                                } else {

                                    event.getChannel().sendMessage("Failed to send user a private message: User is not accepting private messages").queue();

                                }

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

    }

}
