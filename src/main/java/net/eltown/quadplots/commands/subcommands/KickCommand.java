package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;

public class KickCommand extends PlotCommand {

    public KickCommand(final QuadPlots plugin) {
        super(plugin, "kick", "Kicke einen Spieler von deinem Plot.", "/p kick <spieler>", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;

            if (args.length > 0) {
                final Player target = Server.getInstance().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(Language.get("player.not.found"));
                    return;
                }

                final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
                if (plot != null) {
                    if (QuadPlots.getApi().isManager(player.getName()) || plot.isOwner(player.getName())) {

                            final Plot prePlot = QuadPlots.getApi().getPlotByPosition(target.getPosition());
                            if (prePlot != null && prePlot.getStringId().equals(plot.getStringId())) {
                                target.teleport(target.getLevel().getSpawnLocation());
                                player.sendMessage(Language.get("plot.kick", target.getName()));
                                if (QuadPlots.getApi().isManager(player.getName())) PluginsCommand.broadcastCommandMessage(player, "Kicked " + target + " from Plot " + plot.getX() + "|" + plot.getZ(), false);
                            } else player.sendMessage(Language.get("player.cant.kick"));

                    } else player.sendMessage(Language.get("no.plot.permission"));
                } else player.sendMessage(Language.get("not.in.a.plot"));

            } else player.sendMessage(Language.get("usage", this.getUsage()));
        }
    }
}
