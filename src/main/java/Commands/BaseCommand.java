package Commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BaseCommand {

    String name;
    String[] alias;
    String usage;
    String smallDescription;
    String longDescription;
    int permissionlevel;

    public BaseCommand(String n, String[] a, String u, String sd, String ld, int permissionlevel) {

        this.name = n;
        this.alias = a;
        this.usage = u;
        this.smallDescription = sd;
        this.longDescription = ld;
        this.permissionlevel = permissionlevel;

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

    public int getPermissionLevel() {

        return this.permissionlevel;

    }

    public String getShortDescription() {

        return this.smallDescription;

    }

    public String getUsage() {

        return this.usage;

    }

    public String getLongDescription() {

        return this.longDescription;

    }
}
