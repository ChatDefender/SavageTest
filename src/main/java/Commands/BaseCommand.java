package Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BaseCommand {

    String name;
    String[] alias;
    String usage;
    String smallDescription;
    String longDescription;
    Permission permissionlevel;
    String category;

    public BaseCommand(String c, String n, String[] a, String u, String sd, String ld, Permission permissionlevel) {

        this.category = c;
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

    public Permission getPermissionLevel() {

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

    public String getCategory() {

        return this.category;

    }
}
