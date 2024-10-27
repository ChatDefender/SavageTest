package Commands.PunishmentManagement;
import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.PunishmentManagement;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

public class Punish extends BaseCommand {

    public Punish() {
        super("punish", new String[] {"pu"}, "pu [@User | user id] [punishment_name | punishment_id]", "Punishes a user based on their current tier.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        if (args.length != 2) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {
            // validate punishment
            String guildId = event.getGuild().getId();

            String user = functions.verifyMember(event, args[0]);

            Member member = event.getGuild().retrieveMemberById(user).complete();
            String punishmentName = args[1].toUpperCase();

            if (PunishmentManagement.checkPunishmentExistence(guildId, punishmentName) > 0) {

                if (functions.hasPunishPerms(event.getMember(), guildId, punishmentName)) {

                    PunishmentManagement.addActivePunishmentLog(guildId, user, punishmentName);

                    String punishmentType = PunishmentManagement.getPunishmentTypeFromActive(guildId, user);
                    String duration = PunishmentManagement.getDurationFromActive(guildId, user);


                    System.out.println(punishmentType);

                    SQLFunctions.Punishments punishment = SQLFunctions.Punishments.valueOf(punishmentType);

                    functions.punishUser(punishment, event, member, punishmentName);

                    PunishmentLogManagement.insertPunishmentLog(guildId, user, event.getMember().getId(), punishment, duration, punishmentName);

                    event.getChannel().sendMessage("Successfully punished user.").queue();

                } else {

                    event.getChannel().sendMessage("You do not have permission to perform this punishment.").queue();

                }

            } else {

                event.getChannel().sendMessage("This punishment does not exist.").queue();

            }



        }

    }

}
