package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;
import net.eltown.quadplots.components.tasks.ChangeBorderTask;
import net.eltown.quadplots.components.tasks.ChangeWallTask;
import net.eltown.quadplots.components.tasks.ClearTask;

import java.util.Collections;

public class ResetCommand extends PlotCommand {

    public ResetCommand(final QuadPlots plugin) {
        super(plugin, "reset", "Leere dein Plot und gebe es frei.", "/p reset", Collections.emptyList());
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
                        player.sendMessage(Language.get("plot.reset.really"));
                        return;
                    }
                } else {
                    player.sendMessage(Language.get("plot.reset.really"));
                    return;
                }

                if (QuadPlots.getApi().isManager(player.getName()) || plot.isOwner(player.getName())) {
                    player.sendMessage(Language.get("plot.resetting"));
                    Server.getInstance().getScheduler().scheduleTask(new ClearTask(plot, player.getLevel()));
                    final int borderId = QuadPlots.getApi().getProvider().getGeneratorInfo().getBorder()[0];
                    final int wallId = QuadPlots.getApi().getProvider().getGeneratorInfo().getRoad()[0];
                    final int borderDamage = QuadPlots.getApi().getProvider().getGeneratorInfo().getBorder()[1];
                    final int wallDamage = QuadPlots.getApi().getProvider().getGeneratorInfo().getRoad()[1];
                    Server.getInstance().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(borderId, borderDamage), player.getLevel(), true));
                    Server.getInstance().getScheduler().scheduleTask(new ChangeWallTask(plot, Block.get(wallId, wallDamage), player.getLevel()));
                    plot.unclaim();
                    if (QuadPlots.getApi().isManager(player.getName())) PluginsCommand.broadcastCommandMessage(player, "Resetted Plot " + plot.getX() + "|" + plot.getZ(), false);
                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }

}
