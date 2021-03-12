package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;

import java.util.Collections;

public class WarpCommand extends PlotCommand {

    public WarpCommand(QuadPlots plugin) {
        super(plugin, "warp", "Teleportiere dich zu einem Plot.", "/p warp <x> <z>", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        return;
    }
}
