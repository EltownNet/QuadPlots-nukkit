package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;
import sun.net.www.URLConnection;

import java.util.Collections;

public class SetownerCommand extends PlotCommand {

    public SetownerCommand(final QuadPlots plugin) {
        super(plugin, "setowner", "Übertrage dein Plot an §leinen §rSpieler.", "/p setowner <spieler>", Collections.emptyList());
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
                            player.sendMessage(Language.get("plot.setowner.online"));
                            return;
                        }

                        final int maxPlots = QuadPlots.getApi().getMaxPlots(target);
                        final int currentPlots = QuadPlots.getApi().getProvider().getPlotAmount(target.getName());

                        if (maxPlots <= currentPlots) {
                            player.sendMessage(Language.get("plot.setowner.max"));
                            return;
                        }

                        if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
                            plot.getOwners().clear();
                            plot.getOwners().add(target.getName());
                            plot.update();
                            player.sendMessage(Language.get("plot.setowner", target.getName()));
                            target.sendMessage(Language.get("plot.setowner.ping", player.getName(), plot.getName(), plot.getX(), plot.getZ()));
                            if (QuadPlots.getApi().isManager(player.getName())) PluginsCommand.broadcastCommandMessage(player, "Changed owner of " + plot.getX() + "|" + plot.getZ() + " to " + target, false);
                        } else sender.sendMessage(Language.get("plot.setowner.really", target.getName()));


                    } else sender.sendMessage(Language.get("no.plot.permission"));
                } else sender.sendMessage(Language.get("not.in.a.plot"));
            } else sender.sendMessage(Language.get("usage", this.getUsage()));
        }
    }
}

