package Commands.PunishmentManagement;
import Commands.BaseCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Punish extends BaseCommand {

    public Punish() {
        super("punish", new String[] {"pu"}, "pu [@User | user id] [punishment_name | punishment_id]", "Punishes a user based on their current tier.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        // validate punishment

        // check if they have permissions for this

        // if valid, execute punishment with associated time

        // if not valid, warn that punishment doesn't exist


    }


}
