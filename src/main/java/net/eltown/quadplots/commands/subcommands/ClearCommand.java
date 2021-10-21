package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
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
                if (plot.isMerged()) {
                    sender.sendMessage(Language.get("plot.merge.command"));
                    return;
                }

                if (args.length > 0) {
                    if (!args[0].equalsIgnoreCase("confirm")) {
                        player.sendMessage(Language.get("plot.clear.really"));
                        return;
                    }
                } else {
                    player.sendMessage(Language.get("plot.clear.really"));
                    return;
                }

                if (QuadPlots.getApi().isManager(player.getName()) || plot.isOwner(player.getName())) {
                    player.sendMessage(Language.get("plot.clearing"));
                    Server.getInstance().getScheduler().scheduleTask(new ClearTask(plot, player.getLevel()));
                    if (QuadPlots.getApi().isManager(player.getName())) PluginsCommand.broadcastCommandMessage(player, "Cleared Plot " + plot.getX() + "|" + plot.getZ(), false);
                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }

}
