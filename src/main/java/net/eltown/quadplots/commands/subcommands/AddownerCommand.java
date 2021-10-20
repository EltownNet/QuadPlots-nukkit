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

public class AddownerCommand extends PlotCommand {

    public AddownerCommand(final QuadPlots plugin) {
        super(plugin, "addowner", "Füge einen Spieler als Besitzer hinzu.", "/p addowner <spieler>", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;

            if (args.length > 0) {
                final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
                if (plot != null) {
                    if (QuadPlots.getApi().isManager(player.getName()) || plot.getOwners().contains(player.getName())) {
                        final Player target = Server.getInstance().getPlayer(args[0]);

                        if (target == null) {
                            player.sendMessage(Language.get("plot.addowner.online"));
                            return;
                        }

                        final int maxPlots = QuadPlots.getApi().getMaxPlots(target);
                        final int currentPlots = QuadPlots.getApi().getProvider().getPlotAmount(target.getName());

                        if (maxPlots <= currentPlots) {
                            player.sendMessage(Language.get("plot.setowner.max"));
                            return;
                        }

                        if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
                            plot.getOwners().add(target.getName());
                            plot.update();
                            player.sendMessage(Language.get("plot.addowner", target.getName()));
                            target.sendMessage(Language.get("plot.addowner.ping", player.getName(), plot.getName(), plot.getX(), plot.getZ()));
                            if (QuadPlots.getApi().isManager(player.getName())) PluginsCommand.broadcastCommandMessage(player, "Added Owner " + target + " to Plot " + plot.getX() + "|" + plot.getZ(), false);
                        } else sender.sendMessage(Language.get("plot.addowner.really", target.getName()));
                    } else sender.sendMessage(Language.get("no.plot.permission"));
                } else sender.sendMessage(Language.get("not.in.a.plot"));
            } else sender.sendMessage(Language.get("usage", this.getUsage()));
        }
    }

}
