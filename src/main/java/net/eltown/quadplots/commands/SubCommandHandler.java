package net.eltown.quadplots.commands;

import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.subcommands.GenerateCommand;
import net.eltown.quadplots.commands.subcommands.InfoCommand;
import net.eltown.quadplots.commands.subcommands.WarpCommand;
import net.eltown.quadplots.components.language.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SubCommandHandler {

    private final List<PlotCommand> commands = new ArrayList<>();

    public void init(final QuadPlots plugin) {
        commands.addAll(Arrays.asList(
                new InfoCommand(plugin),
                new WarpCommand(plugin),
                new GenerateCommand(plugin)
        ));
    }

    public void handle(final CommandSender sender, final String[] toHandle) {
        String cmd = toHandle[0].toLowerCase();

        List<String> rawArgs = new ArrayList<>(Arrays.asList(toHandle));
        rawArgs.remove(0);
        String[] args = rawArgs.toArray(new String[0]);

        final AtomicBoolean executed = new AtomicBoolean(false);

        commands.forEach((command) -> {
            if (command.getName().equalsIgnoreCase(cmd)) {
                command.execute(sender, args);
                executed.set(true);
            } else if (command.getAliases().contains(cmd)) {
                command.execute(sender, args);
                executed.set(true);
            }
        });

        if (!executed.get()) sender.sendMessage(Language.get("command.not.found"));
    }

}
