package Commands.User;

import Commands.BaseCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Aboose extends BaseCommand {

    public Aboose() {
        super("abuse", new String[] {"aboos", "aboose", "rose", "queenrose"}, "abuse", "Abuse", "", 0);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        event.getChannel().sendMessage("May :cucumber: rest in pieces, as <@899376117746630666> had slaughtered him in ways that were extremely violent and not PG-13.").queue();


    }
}
