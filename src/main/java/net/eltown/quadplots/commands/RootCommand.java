package net.eltown.quadplots.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import net.eltown.quadplots.QuadPlots;

public class RootCommand extends PluginCommand<QuadPlots> {

    public RootCommand(final QuadPlots plots) {
        super("plot", plots);
        this.setAliases(new String[]{"p"});
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("subcommand", new String[]{
                        "add", "addowner", "auto", "ban", "rand", "claim", "clear",
                        "dispose", "help", "home", "info", "kick", "list", "merge",
                        "remove", "reset", "sethome", "setinfo", "setowner", "trust",
                        "unban", "unmerge", "untrust", "wand", "warp"
                }),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        QuadPlots.getApi().getCommandHandler().handle(sender, args.length != 0 ? args : new String[]{"help"});
        return false;
    }
}
