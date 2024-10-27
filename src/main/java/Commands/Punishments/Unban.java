package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import static Handlers.SQLHandlers.PunishmentLogManagement.insertPunishmentLog;
import static Handlers.SQLHandlers.PunishmentLogManagement.markPunishmentAsServed;

public class Unban extends BaseCommand {

    public Unban() {
        super("Punishment",
                "unban", new String[] {}, "unban {userId}", "Unbans a user from a server.", "Used when a user is wrongfully banned, started acting right, or other reasons", Permission.BAN_MEMBERS);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args)  {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            User user;

            try {

                user = event.getJDA().retrieveUserById(args[0]).complete();

                if (user == null) {

                    event.getChannel().sendMessage("I could not find that user.").queue();

                } else {

                    boolean exists = false;

                    for (Guild.Ban b: event.getGuild().retrieveBanList().complete()) {

                        if (b.getUser().getId().equals(user.getId())) {

                            exists = true;
                            break;

                        }

                    }

                    if (exists) {

                        event.getGuild().unban(user).queue(

                                success -> {
                                    event.getChannel().sendMessage("Successfully unbanned " + user.getName()).queue();
                                    insertPunishmentLog(event.getGuild().getId(), user.getId(), event.getAuthor().getId(), SQLFunctions.Punishments.UNBAN, "0", "Manual unban performed by staff.");
                                    int pun_log_id = PunishmentLogManagement.getPunishmentLogId(event.getGuild().getId(), user.getId(), SQLFunctions.Punishments.UNBAN);
                                    markPunishmentAsServed(event.getGuild().getId(), pun_log_id+"");
                                },
                                error -> event.getChannel().sendMessage("Failed to unban user: " + error.getMessage()).queue()

                        );



                        String punishmentLogChannelId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PUNISHMENT_LOG_ID);

                        if (punishmentLogChannelId != null) {

                            TextChannel tc = event.getGuild().getTextChannelById(punishmentLogChannelId);
                            if (tc != null && tc.canTalk()) {

                                tc.sendMessage("```\nPUNISHMENT EXECUTED: UNBAN\nUser: " + user.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "```" ).queue();

                            } else {

                                event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();

                            }

                        }


                    } else {

                        event.getChannel().sendMessage("That user is not banned.").queue();

                    }

                }

            } catch (ErrorResponseException e) {

                if (e.getErrorCode() == 10013) {

                    event.getChannel().sendMessage("I could not find the member specified.").queue();

                } else {

                    e.printStackTrace();

                }

            }

        }

    }

}
