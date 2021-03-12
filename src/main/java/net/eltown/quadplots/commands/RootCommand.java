package net.eltown.quadplots.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import net.eltown.quadplots.QuadPlots;

public class RootCommand extends PluginCommand<QuadPlots> {

    public RootCommand(final QuadPlots plots) {
        super("plot", plots);
        this.setAliases(new String[]{"p"});
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        QuadPlots.getApi().getCommandHandler().handle(sender, args.length != 0 ? args : new String[]{"help"});
        return false;
    }
}
