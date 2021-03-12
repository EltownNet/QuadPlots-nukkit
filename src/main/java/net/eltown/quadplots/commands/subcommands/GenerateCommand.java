package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;

import java.util.Collections;

public class GenerateCommand extends PlotCommand {

    public GenerateCommand(QuadPlots plugin) {
        super(plugin, "generate", "wurst", "wurst", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        this.getPlugin().generate();
    }
}
