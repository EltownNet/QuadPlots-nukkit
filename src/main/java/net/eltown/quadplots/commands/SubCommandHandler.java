package net.eltown.quadplots.commands;

import cn.nukkit.command.CommandSender;
import lombok.Getter;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.subcommands.*;
import net.eltown.quadplots.components.language.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SubCommandHandler {

    @Getter
    private final List<PlotCommand> commands = new ArrayList<>();

    public void init(final QuadPlots plugin) {
        commands.addAll(Arrays.asList(
                new InfoCommand(plugin),
                new WarpCommand(plugin),
                new GenerateCommand(plugin),
                new ClaimCommand(plugin),
                new ClearCommand(plugin),
                new HomeCommand(plugin),
                new SethomeCommand(plugin),
                new WallCommand(plugin),
                new ResetCommand(plugin),
                new DisposeCommand(plugin),
                new HelpCommand(plugin),
                new AutoCommand(plugin),
                new ListCommand(plugin),
                new SetinfoCommand(plugin),
                new KickCommand(plugin),
                new BanCommand(plugin),
                new UnbanCommand(plugin),
                new MiddleCommand(plugin),
                new AddCommand(plugin),
                new RemoveCommand(plugin),
                new TrustCommand(plugin),
                new UntrustCommand(plugin),
                new SetownerCommand(plugin),
                new AddownerCommand(plugin),
                new ManageCommand(plugin),
                new BorderCommand(plugin),
                new CheckroadCommand(plugin),
                new TestMergeCommand(plugin),
                new DirectionCommand(plugin),
                new MergeCommand(plugin),
                new UnmergeCommand(plugin)
        ));
    }

    public void handle(final CommandSender sender, final String[] toHandle) {
        final String cmd = toHandle[0].toLowerCase();

        final List<String> rawArgs = new ArrayList<>(Arrays.asList(toHandle));
        rawArgs.remove(0);
        final String[] args = rawArgs.toArray(new String[0]);

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
