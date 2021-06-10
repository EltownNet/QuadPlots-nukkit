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
import java.util.concurrent.CompletableFuture;

public class AutoCommand extends PlotCommand {

    public AutoCommand(final QuadPlots plugin) {
        super(plugin, "auto", "Finde automatisch ein freies Grundstück.", "/p auto", Collections.singletonList("a"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        CompletableFuture.runAsync(() -> {
            if (sender.isPlayer()) {

                final Player player = (Player) sender;
                player.sendMessage(Language.get("plot.searching"));

                final Plot plot = QuadPlots.getApi().getProvider().findFreePlot(0, 0);
                player.sendMessage(Language.get("plot.claim"));
                final PlotGeneratorInfo gen = QuadPlots.getApi().getProvider().getGeneratorInfo();
                plot.claim(player.getName());
                player.teleport(QuadPlots.getApi().getPlotPosition(plot.getX(), (int) player.getY() + 2, plot.getZ()));
                Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(gen.getBorderClaimed()[0], gen.getBorderClaimed()[1]), player.getLevel()));

            }
        });
    }
}
