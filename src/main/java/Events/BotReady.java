package Events;

import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.PunishmentLogManagement;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class BotReady extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {

        System.out.println("Received ready event....initializing...");

        StringBuilder sb = new StringBuilder();

        event.getJDA().getGuilds().forEach(g -> {

            g.getRoles().forEach(r -> sb.append(r.getId()).append(","));

            ActiveDirectoryManagement.verifyRoles(sb.toString());

            ConfigurationSettings.verifyGuildSetting(g.getId());

            sb.delete(0, sb.length());

        });

        startSchedule(event);

        System.out.println("Schedule for timed commands has been initiated.");

    }

    private void startSchedule(ReadyEvent event) {

        Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                PunishmentLogManagement.filterEndedPunishments(event);
            }
        };

        // Schedule the task to run every second (1000 milliseconds)
        timer.scheduleAtFixedRate(task, 0, 1000);

    }



}
