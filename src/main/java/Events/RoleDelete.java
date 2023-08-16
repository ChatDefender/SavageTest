package Events;

import Main.*;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleDelete  extends ListenerAdapter {

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {

        Long roleId = Long.parseLong(event.getRole().getId());

        String staff = null;
        boolean delete = false;

        for (String s : Main.staffRoles.keySet()) {

            if (Main.staffRoles.get(s).contains(roleId)) {

                staff = s;
                delete = true;

            }

        }

        if (delete) {

            Main.staffRoles.get(staff).remove(roleId);

        }


    }

}
