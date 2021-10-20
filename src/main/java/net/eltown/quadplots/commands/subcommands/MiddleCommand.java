package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;

public class MiddleCommand extends PlotCommand {

    public MiddleCommand(final QuadPlots plugin) {
        super(plugin, "middle", "Teleportiere dich in die Mitte eines Plots.", "/p middle", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            if (plot != null) {
                player.teleport(QuadPlots.getApi().getMiddle(plot.getX(), plot.getZ()));
                player.sendMessage(Language.get("plot.middle"));
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }
}
