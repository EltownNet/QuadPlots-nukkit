package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.math.Direction;
import net.eltown.quadplots.components.tasks.ChangeBorderTask;
import net.eltown.quadplots.components.tasks.MergePlotTask;

import java.util.Collections;

@Deprecated
public class TestMergeCommand extends PlotCommand {

    public TestMergeCommand(final QuadPlots plugin) {
        super(plugin, "testmerge", "Test Merge", "/p testmerge ", Collections.emptyList(), true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) return; // Deprecated
        if (sender.isPlayer()) {
            //if (args.length > 0) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

            if (plot != null) {
                if (plot.isOwner(player.getName())) {
                    final Direction direction = Direction.getPlayerDirection(player);

                    switch (direction) {
                        case NORTH:
                            final Plot toMergeN = plot.getSide(Direction.NORTH);
                            if (toMergeN.isOwner(player.getName())) {
                                if (!plot.isMerged()) {
                                    plot.setOrigin(plot);
                                    toMergeN.setOrigin(plot);
                                }

                                plot.addMerge(Direction.NORTH);
                                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(0), plot.getPosition().getLevel(), false, Direction.NORTH));
                                toMergeN.addMerge(Direction.SOUTH);
                                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(toMergeN, Block.get(0), plot.getPosition().getLevel(), false, Direction.SOUTH));
                                Server.getInstance().getScheduler().scheduleTask(new MergePlotTask(plot, toMergeN, true));

                                player.sendMessage("Plots erfolgreich gemergt.");
                            } else {
                                player.sendMessage("§cDas gegenüberliegende Plot muss dir gehören.");
                            }
                            break;
                        case EAST:
                            final Plot toMergeE = plot.getSide(Direction.EAST);
                            if (toMergeE.isOwner(player.getName())) {
                                if (!plot.isMerged()) {
                                    plot.setOrigin(plot);
                                    toMergeE.setOrigin(plot);
                                }

                                plot.addMerge(Direction.EAST);
                                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(0), plot.getPosition().getLevel(), false, Direction.EAST));
                                toMergeE.addMerge(Direction.WEST);
                                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(toMergeE, Block.get(0), plot.getPosition().getLevel(), false, Direction.WEST));
                                Server.getInstance().getScheduler().scheduleTask(new MergePlotTask(plot, toMergeE, false));

                                player.sendMessage("Plots erfolgreich gemergt.");
                            } else {
                                player.sendMessage("§cDas gegenüberliegende Plot muss dir gehören.");
                            }
                            break;
                        case SOUTH:
                            final Plot toMergeS = plot.getSide(Direction.SOUTH);
                            if (toMergeS.isOwner(player.getName())) {
                                if (!plot.isMerged()) {
                                    plot.setOrigin(plot);
                                    toMergeS.setOrigin(plot);
                                }

                                plot.addMerge(Direction.SOUTH);
                                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(0), plot.getPosition().getLevel(), false, Direction.SOUTH));
                                toMergeS.addMerge(Direction.NORTH);
                                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(toMergeS, Block.get(0), plot.getPosition().getLevel(), false, Direction.NORTH));
                                Server.getInstance().getScheduler().scheduleTask(new MergePlotTask(toMergeS, plot, true));

                                player.sendMessage("Plots erfolgreich gemergt.");
                            } else {
                                player.sendMessage("§cDas gegenüberliegende Plot muss dir gehören.");
                            }
                            break;
                        case WEST:
                            final Plot toMergeW = plot.getSide(Direction.WEST);
                            if (toMergeW.isOwner(player.getName())) {
                                if (!plot.isMerged()) {
                                    plot.setOrigin(plot);
                                    toMergeW.setOrigin(plot);
                                }

                                plot.addMerge(Direction.WEST);
                                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(0), plot.getPosition().getLevel(), false, Direction.WEST));
                                toMergeW.addMerge(Direction.EAST);
                                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(toMergeW, Block.get(0), plot.getPosition().getLevel(), false, Direction.EAST));
                                Server.getInstance().getScheduler().scheduleTask(new MergePlotTask(toMergeW, plot, false));


                                player.sendMessage("Plots erfolgreich gemergt.");
                            } else {
                                player.sendMessage("§cDas gegenüberliegende Plot muss dir gehören.");
                            }
                            break;
                    }

                } else player.sendMessage("§cKeine Berechtigung.");
                /*switch (args[0].toLowerCase()) {
                    case "north":
                        plot.addMerge(Direction.NORTH);
                        sender.sendMessage("Ok. NORTH");
                        break;
                    case "west":
                        plot.addMerge(Direction.WEST);
                        sender.sendMessage("Ok. WEST");
                        break;
                    default:
                        sender.sendMessage("Richtungen: NORTH, WEST");
                        break;
                }*/
            } else player.sendMessage("Funzt nur auf Plots. Sorry :(");
            //} else sender.sendMessage("Richtungen: NORTH, WEST");
        }
        return;
    }
}
