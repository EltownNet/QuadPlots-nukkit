package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
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
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            if (plot != null) {
                // TODO: limit etc.
                if (plot.isClaimed()) {
                    player.sendMessage(Language.get("plot.already.claimed", plot.getOwner()));
                } else {
                    player.sendMessage(Language.get("plot.claim"));
                    plot.claim(player.getName());
                    player.teleport(QuadPlots.getApi().getPlotPosition(plot.getX(), (int) player.getY() + 2, plot.getZ()));
                    Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(BlockID.SLABS, 2), player.getLevel()));
                }
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }
}
