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
import net.eltown.quadplots.components.tasks.ClearTask;

import java.util.Collections;

public class ClearCommand extends PlotCommand {

    public ClearCommand(final QuadPlots plugin) {
        super(plugin, "clear", "Leere ein Plot.", "/p clear", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            if (plot != null) {
                // TODO: limit etc.
                if (plot.getOwner().equalsIgnoreCase(player.getName())) {
                    Server.getInstance().getScheduler().scheduleTask(new ClearTask(plot, player.getLevel()));
                    player.sendMessage("clearing...");
                }
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }

}
