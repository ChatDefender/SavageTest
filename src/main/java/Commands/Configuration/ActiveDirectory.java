package Commands.Configuration;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Handlers.MongoDBHandler.MongoDBHandler;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.stream.Collectors;

import static Handlers.CommandHandler.doesCommandExist;

public class ActiveDirectory extends BaseCommand {

    public ActiveDirectory() {
        super("activedirectory", new String[] {"activedir", "dir", "ad"}, "ad [action] [group] [role/command]", "Actions: addGroup, removeGroup, addCommand, removeCommand, addRole, removeRole\nGroups: "+ String.join(", ", MongoDBHandler.getGroups()) +"\nCommands:"+ CommandHandler.getCommands().stream().map(command -> "`" + command + "`").collect(Collectors.joining(", ")), "", 8);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (!(args.length > 3)) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"ad [action] [group] [role/command]\nActions: addGroup, removeGroup, addCommand, removeCommand, addRole, removeRole\nGroups: "+ String.join(", ", MongoDBHandler.getGroups()) +"\nCommands:"+CommandHandler.getCommands().stream().map(command -> "`" + command + "`").collect(Collectors.joining(", "))+"Note: If adding a group, role/command should be the default permission level (cannot be greater than 10 or less than zero)").queue();

        } else {

            // switch based on the action
            String action = args[1].toLowerCase();

            String groupName = args[2].toLowerCase();

            switch (action) {

                case "ag":
                case "addgroup": {

                    // first we want to make sure the group does not already exist:
                    if (MongoDBHandler.doesGroupExist("Roles", groupName)) {

                        event.getChannel().sendMessage("That group already exists!").queue();

                    } else {

                        // now we have to do some quality control for the supplied permission level
                        int permLevel = Integer.parseInt(args[3]);

                        // first, check if it is between 1 and 10

                        String otherGroupWithPermLevel = MongoDBHandler.doesGroupHaveSamePermissionLevel("Roles", permLevel);

                        if (otherGroupWithPermLevel != null) {

                            event.getChannel().sendMessage(otherGroupWithPermLevel + " currently has permission level " + permLevel + ". Please change the permission level of the newly created group").queue();

                        } else if (permLevel >= 0 && permLevel <= 10) {

                            // If it is, add it to the database
                            MongoDBHandler.addGroup(groupName, permLevel);
                            event.getChannel().sendMessage("Successfully added " + groupName + " to the active directory database with permission level " + permLevel).queue();

                        } else {

                            // if it's not, yell at rose
                            event.getChannel().sendMessage("The permission level cannot be less than zero and greater than ten!").queue();

                        }

                    }

                    }
                    break;
                case "rg":
                case "removegroup": {
                    // first we want to make sure the group does not already exist:
                    if (!MongoDBHandler.doesGroupExist("Roles", groupName)) {

                        event.getChannel().sendMessage("That group does not exist!").queue();

                    } else {

                        // If the group exists, we can remove it
                        MongoDBHandler.removeGroup(groupName);
                        event.getChannel().sendMessage("Successfully removed " + groupName + " from the active directory.").queue();

                    }
                    }
                    break;
                case "ac":
                case "addcommand": {

                    // convert the command to lower case
                    String cmd = args[3].toLowerCase();

                    boolean groupHasCommand = MongoDBHandler.getArrValues("Commands", groupName).contains(cmd);
                    String otherGroup = MongoDBHandler.doesExistInOtherGroup("Commands", cmd);

                    // check if the command exists
                    if (!doesCommandExist(cmd)) {

                        event.getChannel().sendMessage("The command provided was an invalid command. The available commands are " + CommandHandler.getCommands().stream().map(command -> "`" + command + "`").collect(Collectors.joining(", "))).queue();

                    } else if (otherGroup != null) {

                        event.getChannel().sendMessage("That command is currently in the " + otherGroup + " group. Please remove it from this group to add it to " + groupName).queue();

                    } else if (!groupHasCommand) {

                        MongoDBHandler.insertIntoActiveDirectory("Commands", groupName, cmd);
                        event.getChannel().sendMessage("Successfully added the " + cmd + " command to the " + groupName + " group.").queue();

                    }  else {

                        event.getChannel().sendMessage("That command was already in "+groupName+" group.").queue();

                    }

                    }
                    break;
                case "rc":
                case "removecommand": {

                    // convert the command to lower case
                    String cmd = args[3].toLowerCase();
                    boolean groupHasCommand = MongoDBHandler.getArrValues("Commands", groupName).contains(cmd);

                    // check if the command exists
                    if (!doesCommandExist(cmd)) {

                        event.getChannel().sendMessage("The command provided was an invalid command. The available commands are " + CommandHandler.getCommands().stream().map(command -> "`" + command + "`").collect(Collectors.joining(", "))).queue();

                    } else if (groupHasCommand) {

                        MongoDBHandler.removeFromActiveDirectory("Commands", groupName, cmd);
                        event.getChannel().sendMessage("Successfully removed the " + cmd + " command from the " + groupName + " group.").queue();

                    } else if (MongoDBHandler.doesExistInOtherGroup("Commands", cmd) != null) {

                        event.getChannel().sendMessage("That command is currently in the " + MongoDBHandler.doesExistInOtherGroup("Commands", cmd) + " group. Please remove it from this group to add it to " + groupName).queue();

                    } else {

                        event.getChannel().sendMessage("That command was not in any group.").queue();

                    }
                    }
                    break;
                case "ar":
                case "addrole": {

                    // convert the command to lower case
                    String roleId = getRoleId(event, args[3]);

                    // check if the command exists
                    if (roleId == null) {

                        event.getChannel().sendMessage("The role supplied was an invalid role.").queue();

                    } else if (!MongoDBHandler.getArrValues("Roles", groupName).contains(roleId)) {

                        MongoDBHandler.insertIntoActiveDirectory("Roles", groupName, roleId);
                        event.getChannel().sendMessage("Successfully add the " + event.getGuild().getRoleById(roleId).getName() + " role to the " + groupName + " group.").queue();

                    } else {

                        event.getChannel().sendMessage("That role currently exists in the " + MongoDBHandler.doesExistInOtherGroup("Roles", roleId) + " group. Please remove it from this group to add it to " + groupName + " group." ).queue();
                    }
                    }
                    break;
                case "rr":
                case "removerole": {
                    // convert the command to lower case
                    String roleId = getRoleId(event, args[3]);

                    // check if the command exists
                    if (roleId == null) {

                        event.getChannel().sendMessage("The role supplied was an invalid role.").queue();

                    } else if (MongoDBHandler.getArrValues("Roles", groupName).contains(roleId)) {

                        MongoDBHandler.removeFromActiveDirectory("Roles", groupName, roleId);
                        event.getChannel().sendMessage("Successfully removed the " + event.getGuild().getRoleById(roleId).getName() + " role from the " + groupName + " group.").queue();

                    } else {

                        event.getChannel().sendMessage("That role already exists in the " + MongoDBHandler.doesExistInOtherGroup("Roles", roleId) + " group. Please specify that group to remove it appropriately." ).queue();
                    }
                    }
                    break;
                case "view": {

                    // TODO create view that shows Group | Default Permission Level | Role Names or Commands

                    }
                    break;
                default:
                    event.getChannel().sendMessage("The provided action is invalid. The only actions available are as follows: addGroup, removeGroup, addCommand, removeCommand, addRole, removeRole").queue();

            }

        }

    }

    private String getRoleId(MessageReceivedEvent event, String role) {

        // If the role was mentioned, we know it exists, so we do not need to further check.
        if (role.startsWith("<@&")) {

            role = role.replace("<@&", "").replace(">", "");

            return role;

        } else {

            if (event.getGuild().getRoleById(role) == null) {

                role = null;

            } else {

                return role;

            }

        }

        return role;
    }
}