package Commands.Log;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import Main.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ActiveDirectory {



    public void activeDirectory(MessageReceivedEvent event, String[] args) {

        if (args.length < 2) {

            event.getChannel().sendMessage("Command Layout: s!ad [tm | m | hm | a | man | d] [ar | rr | ac | rc] [{role-id | @role} | {command name}]").queue();

        } else {

            String action = args[2].replace("-", "").toLowerCase();
            String staff = args[1].toLowerCase();

            boolean shouldContinue = true;

            switch (staff) {
                case "tm":
                    staff = "TrialMod";
                    break;
                case "m":
                    staff = "Moderator";
                    break;
                case "hm":
                    staff = "HeadMod";
                    break;
                case "a":
                    staff = "Admin";
                    break;
                case "man":
                    staff = "Manager";
                    break;
                case "d":
                    staff = "Developer";
                    break;
                default:
                    shouldContinue = false;
                    event.getChannel().sendMessage("That is not a valid tag for staff. The valid tags are as follows: \ntm - TrialMod\nm - Moderator\nhm - HeadMod\na - Admin\nman - Manager\nd - Developer").queue();
            }


            if (shouldContinue) {

                String roleId;
                List<Long> oldIds;

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

                                String group = doesExistInOtherGroup(roleId, staff);
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
                                String group = doesExistInOtherGroup(roleId, staff);
                                if (!group.isEmpty()) {

                                    event.getChannel().sendMessage("That role is currently a part of " + group + " group. Please specify the " + group + " group to remove it. You referenced the " + staff + " group").queue();

                                } else {

                                    event.getChannel().sendMessage("Could not find " + event.getGuild().getRoleById(roleId).getName() + " in the " + staff + " group.").queue();

                                }

                            }

                        }

                        break;
                    case "ac":

                        break;
                    case "rc":

                        break;
                    default:
                }

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

    private String doesExistInOtherGroup(String roleId, String staff) {

        AtomicReference<String> group = new AtomicReference<>("");

        Main.staffRoles.forEach((key, value) -> {

                    value.forEach(l -> {

                        if (Long.parseLong(roleId) == l && !staff.equals(key)) {
                            group.set(key);
                        }

                    });

                }


        );

        return group.toString();

    }

}
