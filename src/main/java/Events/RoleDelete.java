package Events;

import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Handlers.SQLHandlers.ConfigurationSettings;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleDelete  extends ListenerAdapter {

//    @Override
//    public void onRoleDelete(RoleDeleteEvent event) {
//
//        ActiveDirectoryManagement.verifyRoles(event.getRole().getId());
//
//        ConfigurationSettings.removeMutedRole(event.getGuild().getId(), event.getRole().getId());
//
//    }

}
