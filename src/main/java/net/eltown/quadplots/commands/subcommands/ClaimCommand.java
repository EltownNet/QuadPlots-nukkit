package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;
import net.eltown.quadplots.components.language.Language;
import net.eltown.quadplots.components.tasks.ChangeBorderTask;

import java.util.Collections;

public class ClaimCommand extends PlotCommand {

    public ClaimCommand(final QuadPlots plugin) {
        super(plugin, "claim", "Claime ein Plot.", "/p claim", Collections.singletonList("c"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final int maxPlots = QuadPlots.getApi().getMaxPlots(player), currentPlots = QuadPlots.getApi().getProvider().getPlotAmount(player.getName());

            if (currentPlots >= maxPlots) {
                player.sendMessage(Language.get("plots.max"));
                return;
            }

            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            if (plot != null) {
                if (plot.isClaimed()) {
                    player.sendMessage(Language.get("plot.already.claimed", String.join(", ", plot.getOwners())));
                } else {
                    player.sendMessage(Language.get("plot.claim"));
                    final PlotGeneratorInfo gen = QuadPlots.getApi().getProvider().getGeneratorInfo();
                    plot.claim(player.getName());
                    player.teleport(QuadPlots.getApi().getPlotPosition(plot.getX(), (int) player.getY() + 2, plot.getZ()));
                    Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(gen.getBorderClaimed()[0], gen.getBorderClaimed()[1]), player.getLevel()));
                }
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }
}
