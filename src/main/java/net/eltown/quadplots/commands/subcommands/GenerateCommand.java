package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;

import java.util.Collections;

public class GenerateCommand extends PlotCommand {

    public GenerateCommand(QuadPlots plugin) {
        super(plugin, "generate", "wurst", "wurst", Collections.emptyList(), true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isOp() && Server.getInstance().getLevelByName("plots") == null) {
            this.getPlugin().generate();
            PluginsCommand.broadcastCommandMessage(sender, "Generated Plotworld", true);
        } ;
    }
}
