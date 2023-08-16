package Events;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Main.*;

public class BotReady extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        List<Role> roles = Objects.requireNonNull(event.getJDA().getGuildById("1132678375660585061")).getRoles();

        List<Long> roleIds = new ArrayList<>();

        for (Role r : roles) {

            roleIds.add(Long.parseLong(r.getId()));

        }

        Main.staffRoles.entrySet().removeIf(entry ->
                entry.getValue().removeIf(roleId -> !roleIds.contains(roleId))
        );

    }

}
