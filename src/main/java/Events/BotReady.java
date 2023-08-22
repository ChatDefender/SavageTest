package Events;

import Handlers.MongoDBHandler.MongoDBHandler;
import Handlers.SQLHandlers.TimedPunishmentsSQLFunctions;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.util.*;

public class BotReady extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        List<Role> roles = Objects.requireNonNull(event.getJDA().getGuildById("1132678375660585061")).getRoles();

        List<String> roleIds = new ArrayList<>();

        for (Role r : roles) {

            roleIds.add(r.getId());

        }

        MongoDBHandler.getGroups().forEach(g -> {

            MongoDBHandler.getArrValues("Roles", g).forEach(role -> {

                if (!roleIds.contains(role)) {

                    MongoDBHandler.removeFromActiveDirectory("Roles", g, role);

                }

            });

        });

        startSchedule(event);

    }

    private void startSchedule(ReadyEvent event) {

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                TimedPunishmentsSQLFunctions.filter(event);
            }
        };

        // Schedule the task to run every second (1000 milliseconds)
        timer.scheduleAtFixedRate(task, 0, 1000);

    }

}
