package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;

import java.util.Collections;

public class UnmergeCommand extends PlotCommand {

    public UnmergeCommand(final QuadPlots plugin) {
        super(plugin, "unmerge", "Trenne ein zusammengefügtes Plot", "/p unmerge", Collections.emptyList(), true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isOp() && sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition(), false);
            if (plot.isMerged()) {
                plot.unmerge();
                sender.sendMessage("§aPlot wurde getrennt.");
            } else sender.sendMessage("§cDieses Plot ist mit keinem anderem Plot zusammengefügt.");
        } else {
            sender.sendMessage("§cDieser Befehl ist zurzeit nur von Admins ausführbar. Möchtest du deine Plots voneinander trennen? Dann öffne ein Support-Ticket mit dem Betreff \"Unmerge Anfrage\".");
        }
    }
}
