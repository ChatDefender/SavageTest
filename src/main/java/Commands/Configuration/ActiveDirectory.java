package Commands.Configuration;

import CustomerFunctions.ConfigurationSQLFunctions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import Main.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ActiveDirectory {

    public void activeDirectory(MessageReceivedEvent event, String[] args) {

        String staff = args[1].toLowerCase();

        if (args.length < 3 && !staff.equalsIgnoreCase("view")) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"ad [tm | m | hm | a | man | d] [ar | rr | ac | rc] [{role-id | @role} | {command name}]").queue();

        } else {

            String action = args[2].replace("-", "").toLowerCase();

            boolean shouldContinue = true;

            switch (staff) {
                case "tm":
                case "trialmod":
                case "trialmoderator":
                    staff = "TrialMod";
                    break;
                case "m":
                case "mod":
                case "moderator":
                    staff = "Moderator";
                    break;
                case "hm":
                case "headmod":
                case"headmoderator":
                    staff = "HeadMod";
                    break;
                case "a":
                case "admin":
                case "administrator":
                    staff = "Admin";
                    break;
                case "man":
                case "manager":
                    staff = "Manager";
                    break;
                case "d":
                case "dev":
                case "developer":
                    staff = "Developer";
                    break;
                default:
                    shouldContinue = false;
                    event.getChannel().sendMessage("That is not a valid tag for staff. The valid tags are as follows: \ntm - TrialMod\nm - Moderator\nhm - HeadMod\na - Admin\nman - Manager\nd - Developer").queue();
            }


            if (shouldContinue) {

                String roleId;
                List<Long> oldIds;

                String command;
                List<String> oldCommands;

                switch(action) {
                    case "ar":

                        oldIds = Main.staffRoles.get(staff);

                        roleId = getRoleId(event, args[3]);

                        if (roleId == null) {

                            event.getChannel().sendMessage("I could not find that role in this server.").queue();

                        } else {

                            boolean isAdded = false;

                            for (long id : oldIds) {

                                if (id == Long.parseLong(roleId)) {

                                    isAdded = true;
                                    break;

                                }

                            }

                            if (isAdded) {

                                event.getChannel().sendMessage("That role already exists in the " + staff + " group.").queue();

                            } else {

                                String group = doesExistInOtherGroup( Main.staffRoles, Long.parseLong(roleId), staff);
                                if (!group.isEmpty()) {

                                    event.getChannel().sendMessage("That role is currently a part of " + group + " Group. Please remove it from the " + group + " group to add it to the " + staff + " group.").queue();

                                } else {

                                    oldIds.add(Long.parseLong(roleId));
                                    Main.staffRoles.put(staff, oldIds);
                                    event.getChannel().sendMessage("Successfully added " + event.getGuild().getRoleById(roleId).getName() + " to the " + staff + " group.").queue();

                                }

                            }

                        }

                        break;
                    case "rr":

                        roleId = getRoleId(event, args[3]);

                        if (roleId == null) {

                            event.getChannel().sendMessage("I could not find that role in this server.").queue();

                        } else {

                            boolean exists = false;

                            oldIds = Main.staffRoles.get(staff);

                            for (long id : oldIds) {

                                if (id == Long.parseLong(roleId)) {

                                    exists = true;
                                    break;

                                }

                            }

                            if (exists) {

                                oldIds.remove(Long.parseLong(roleId));
                                Main.staffRoles.put(staff, oldIds);
                                event.getChannel().sendMessage("Successfully removed " + event.getGuild().getRoleById(roleId).getName() + " to the " + staff + " group.").queue();

                            } else {
                                String group = doesExistInOtherGroup(Main.staffRoles, Long.parseLong(roleId), staff);
                                if (!group.isEmpty()) {

                                    event.getChannel().sendMessage("That role is currently a part of " + group + " group. Please specify the " + group + " group to remove it. You referenced the " + staff + " group").queue();

                                } else {

                                    event.getChannel().sendMessage("Could not find " + event.getGuild().getRoleById(roleId).getName() + " in the " + staff + " group.").queue();

                                }

                            }

                        }

                        break;
                    case "ac":
                        oldCommands = Main.staffCommands.get(staff);

                        command = args[3].toLowerCase();

                        if (!doesCommandExist(command)) {

                            StringBuilder sb = new StringBuilder();

                            for (String s : Main.commands) {

                                sb.append(s).append(", ");

                            }

                            sb.deleteCharAt(sb.length());

                            event.getChannel().sendMessage("The command does not exists.\nThe available commands are as follows: " + sb).queue();

                        } else {

                            boolean isAdded = false;

                            for (String c : oldCommands) {

                                if (c.equalsIgnoreCase(command)) {

                                    isAdded = true;
                                    break;

                                }

                            }

                            if (isAdded) {

                                event.getChannel().sendMessage("That command already exists in the " + staff + " group.").queue();

                            } else {

                                String group = doesExistInOtherGroup(Main.staffCommands, command, staff);
                                if (!group.isEmpty()) {

                                    event.getChannel().sendMessage("That command is currently a part of " + group + " Group. Please remove it from the " + group + " group to add it to the " + staff + " group.").queue();

                                } else {

                                    oldCommands.add(command);
                                    Main.staffCommands.put(staff, oldCommands);
                                    event.getChannel().sendMessage("Successfully added `" + command + "` to the " + staff + " group.").queue();

                                }

                            }

                        }
                        break;
                    case "rc":
                        oldCommands = Main.staffCommands.get(staff);

                        command = args[3].toLowerCase();

                        if (!doesCommandExist(command)) {

                            StringBuilder sb = new StringBuilder();

                            for (String s : Main.commands) {

                                sb.append(s).append(", ");

                            }

                            sb.deleteCharAt(sb.length());

                            event.getChannel().sendMessage("The command does not exists.\nThe available commands are as follows: " + sb).queue();

                        } else {

                            boolean exists = false;

                            for (String c : oldCommands) {

                                if (c.equalsIgnoreCase(command)) {

                                    exists = true;
                                    break;

                                }

                            }

                            if (exists) {

                                oldCommands.remove(command);
                                Main.staffCommands.put(staff, oldCommands);
                                event.getChannel().sendMessage("Successfully removed `" + command + "` from the " + staff + " group.").queue();

                            } else {
                                String group = doesExistInOtherGroup(Main.staffCommands, command, staff);
                                if (!group.isEmpty()) {

                                    event.getChannel().sendMessage("That command is currently a part of " + group + " group. Please specify the " + group + " group to remove it. You referenced the " + staff + " group").queue();

                                } else {

                                    event.getChannel().sendMessage("Could not find `" + command + "` in the " + staff + " group.").queue();

                                }

                            }

                        }
                        break;
                    default:
                }

            }

        }

    }

    private boolean doesCommandExist(String command) {

        return Main.commands.contains(command);

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

    private String doesExistInOtherGroup(HashMap<String, List<Long>> hm, Long roleId, String staff) {

        AtomicReference<String> group = new AtomicReference<>("");

        hm.forEach((key, value) -> {

            value.forEach(l -> {

                    if (Objects.equals(l, roleId) && !staff.equals(key)) {
                        group.set(key);
                    }

                });

            }

        );

        return group.toString();

    }

    private String doesExistInOtherGroup(HashMap<String, List<String>> hm, String command, String staff) {

        AtomicReference<String> group = new AtomicReference<>("");

        hm.forEach((key, value) -> {

                    value.forEach(l -> {

                        if (command.equalsIgnoreCase(l) && !staff.equals(key)) {
                            group.set(key);
                        }

                    });

                }

        );

        return group.toString();

    }

}
