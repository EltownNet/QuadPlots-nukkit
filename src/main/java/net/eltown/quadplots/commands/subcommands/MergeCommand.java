package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;
import net.eltown.quadplots.components.math.Direction;
import net.eltown.quadplots.components.tasks.ChangeBorderTask;
import net.eltown.quadplots.components.tasks.MergePlotTask;

import java.util.Collections;

public class MergeCommand extends PlotCommand {

    public MergeCommand(final QuadPlots plugin) {
        super(plugin, "merge", "Füge deine Plots zusammen.", "/p merge", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("plots.merge")) {
            if (sender.isPlayer()) {
                final Player player = (Player) sender;
                final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

                if (plot != null) {
                    if (plot.isOwner(player.getName())) {
                        // ALPHA!
                        if (plot.isMerged()) sender.sendMessage(Language.get("plot.merge.alpha"));
                        final Direction direction = Direction.getPlayerDirection(player);

                        switch (direction) {
                            case NORTH:
                                final Plot toMergeN = plot.getSide(Direction.NORTH);
                                if (toMergeN.isOwner(player.getName())) {
                                    if (!plot.isMerged()) {
                                        // TODO: Origin für mehr als zwei Plots.
                                        plot.setOrigin(plot);
                                        toMergeN.setOrigin(plot);
                                    }

                                    plot.addMerge(Direction.NORTH);
                                    Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(0), plot.getPosition().getLevel(), false, Direction.NORTH));
                                    toMergeN.addMerge(Direction.SOUTH);
                                    Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(toMergeN, Block.get(0), plot.getPosition().getLevel(), false, Direction.SOUTH));
                                    Server.getInstance().getScheduler().scheduleTask(new MergePlotTask(plot, toMergeN, true));

                                    player.sendMessage(Language.get("plot.merging"));
                                } else {
                                    player.sendMessage(Language.get("plot.merge.sameowner"));
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

                                    player.sendMessage(Language.get("plot.merging"));
                                } else {
                                    player.sendMessage(Language.get("plot.merge.sameowner"));
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

                                    player.sendMessage(Language.get("plot.merging"));
                                } else {
                                    player.sendMessage(Language.get("plot.merge.sameowner"));
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


                                    player.sendMessage(Language.get("plot.merging"));
                                } else {
                                    player.sendMessage(Language.get("plot.merge.sameowner"));
                                }
                                break;
                        }

                    } else player.sendMessage(Language.get("no.permission"));
                } else player.sendMessage(Language.get("plot.merge.road"));
            }
            return;
        } else sender.sendMessage(Language.get("no.permission"));
    }
}
