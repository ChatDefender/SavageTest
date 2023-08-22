package Events;

import Handlers.MongoDBHandler.MongoDBHandler;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleDelete  extends ListenerAdapter {

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {

        String roleId = event.getRole().getId();

        MongoDBHandler.getGroups().forEach(g -> {

            if (MongoDBHandler.getArrValues("Roles", g).contains(roleId)) {

                MongoDBHandler.removeFromActiveDirectory("Roles", g, roleId);

            }

        });

    }

}
