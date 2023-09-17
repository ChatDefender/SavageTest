package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Handlers.SQLHandlers.PunishmentSQLFunctions;
import Main.functions;
import Handlers.SQLHandlers.TimedPunishmentsSQLFunctions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Mute extends BaseCommand {

    public Mute() {
        super("mute", new String[] {}, "mute [@user | userId] [duration] [reason]", "Mutes a specified user for a specified duration", "\nAvailable times are in (m)inutes, (h)ours, (d)ays, (mon)ths, (y)ear, or 0 for indefinite.\nUsed when someone is being naughty in the chatty, or tickles rose on the wrong day", 2);
    }

    public void run(MessageReceivedEvent event, String[] args) {


        // Verify text is provided in the arguments
        if (args.length <= 3) {

            event.getChannel().sendMessage("Command Layout: "+ConfigurationSQLFunctions.getSetting("Prefix")+"mute [user id | user mention] [duration ] [reason]").queue();

        } else {

            String muteRoleId =ConfigurationSQLFunctions.getSetting("MuteRoleId");

            if (muteRoleId.isEmpty()) {

                event.getChannel().sendMessage("The mute role needs to be configured. Run `"+ConfigurationSQLFunctions.getSetting("prefix")+"config muteroleid [@mutedRole | mute role id]`").queue();

            } else {

                Role r = event.getGuild().getRoleById(Long.parseLong(muteRoleId));

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

                    } else if (functions.getMentionedUserPermLevel(event, userId) >= functions.getAuthorPermLevel(event)) {

                        event.getChannel().sendMessage("I cannot mute that user as they have the same permission level as you or greater.").queue();

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

                        event.getGuild().addRoleToMember(member, r).queue(

                                success -> event.getChannel().sendMessage("Successfully muted " + member.getEffectiveName()).queue(),
                                error -> event.getChannel().sendMessage("Failed to mute " + member.getEffectiveName() + " `[" + member.getId() + "]`. Reason: " + error.getMessage()).queue()

                        );

                        int id = PunishmentSQLFunctions.insertPunishment("mute", member.getId(), event.getAuthor().getId(), String.valueOf(timeInMs), finalReason);

                        if (timeInMs != -1) {

                            long endTime = System.currentTimeMillis() + timeInMs;
                            TimedPunishmentsSQLFunctions.insertTime("mute", event.getGuild().getId(), userId, String.valueOf(endTime));

                        }

                        TextChannel tc = event.getGuild().getTextChannelById(ConfigurationSQLFunctions.getSetting("PunishmentLogId"));
                        if (tc != null && tc.canTalk()) {

                            tc.sendMessage("```\nUSER MUTED " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\nDuration: " +duration + "\nReason: " + finalReason + "\nPunishmentId: "+id+"```" ).queue();

                        } else {

                            event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();
                        }

                    }

                }

            }



        }

    }
}
