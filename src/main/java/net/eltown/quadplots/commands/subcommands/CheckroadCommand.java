package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.Road;
import net.eltown.quadplots.components.math.Direction;

import java.util.Collections;

@Deprecated
public class CheckroadCommand extends PlotCommand {

    public CheckroadCommand(final QuadPlots plugin) {
        super(plugin, "checkroad", "Straßenstatus überprüfen", "/p checkroad", Collections.emptyList(), true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) return; // Deprecated
        if (sender.isPlayer()) {
            final Player player = (Player) sender;

            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            sender.sendMessage("Du guckst in Richtung " + player.getDirection().name());

            if (plot == null) {
                final Road find = QuadPlots.getApi().getRoad(player.getPosition());

                if (find != null) {

                    sender.sendMessage(find.getPlot().getPosition().toString());

                    sender.sendMessage("Diese Straße gehört zu dem Plot " + find.getPlot().getX() + "|" + find.getPlot().getZ());
                    sender.sendMessage("Flags: " + String.join(", ", find.getPlot().getFlags()));
                } else sender.sendMessage("Dies ist eine Kreuzung.");
            } else sender.sendMessage("§cDieser Befehl funktioniert nur auf Straßen!");
        }
    }
}
