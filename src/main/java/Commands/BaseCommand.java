package Commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BaseCommand {

    String name;
    String[] alias;
    String usage;
    String smallDescription;
    String longDescription;

    public BaseCommand(String n, String[] a, String u, String sd, String ld) {

        this.name = n;
        this.alias = a;
        this.usage = u;
        this.smallDescription = sd;
        this.longDescription = ld;

    }

    public void run(MessageReceivedEvent event, String[] args) {

        event.getChannel().sendMessage("This command has not been configured. Stay tuned! :)").queue();

    }

    public String getName() {

        return this.name;

    }

    public String[] getAliases() {

        return this.alias;

    }
}
