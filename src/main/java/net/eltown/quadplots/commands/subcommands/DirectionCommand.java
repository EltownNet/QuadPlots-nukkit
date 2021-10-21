package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.math.Direction;

import java.util.Collections;

@Deprecated
public class DirectionCommand extends PlotCommand {

    public DirectionCommand(final QuadPlots plugin) {
        super(plugin, "direction", "Gehe zu dem Plot in der angegebenen Himmelsrichtung.", "/p direction <north|east|south|west>", Collections.emptyList(), true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) return; // Deprecated
        if (sender.isPlayer()) {
            final Player player = (Player) sender;

            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            if (plot != null) sender.sendMessage(String.join(", ", plot.getFlags()));


            if (args.length > 0) {
                if (plot != null) {
                    final Plot destination;

                    switch (args[0].toLowerCase()) {
                        case "north":
                            destination = plot.getSide(Direction.NORTH);
                            break;
                        case "east":
                            destination = plot.getSide(Direction.EAST);
                            break;
                        case "south":
                            destination = plot.getSide(Direction.SOUTH);
                            break;
                        case "west":
                            destination = plot.getSide(Direction.WEST);
                            break;
                        default:
                            sender.sendMessage(this.getUsage());
                            return;
                    }
                    sender.sendMessage("Plot: " + destination.getX() + "|" + destination.getZ());

                } else sender.sendMessage("§cBitte stehe auf einem Plot um diesen Befehl ausführen zu können.");
            } else sender.sendMessage(this.getUsage());
        }
    }
}
