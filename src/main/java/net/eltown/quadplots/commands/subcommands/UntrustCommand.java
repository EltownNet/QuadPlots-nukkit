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

public class UntrustCommand extends PlotCommand {

    public UntrustCommand(final QuadPlots plugin) {
        super(plugin, "untrust", "Entferne einen Spieler als Vertrauten.", "/p untrust <spieler>", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            if (args.length > 0) {
                final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

                if (plot != null) {
                    if (QuadPlots.getApi().isManager(player.getName()) || plot.getOwners().contains(player.getName())) {
                        String target = args[0];

                        final Player preTarget = Server.getInstance().getPlayer(target);
                        if (preTarget != null) target = preTarget.getName();

                        plot.getTrusted().remove(target);
                        plot.update();
                        player.sendMessage(Language.get("plot.untrusted", target));
                        if (QuadPlots.getApi().isManager(player.getName())) PluginsCommand.broadcastCommandMessage(player, "Untrusted " + target + " on Plot " + plot.getX() + "|" + plot.getZ(), false);
                    } else player.sendMessage(Language.get("no.plot.permission"));
                } else player.sendMessage(Language.get("not.in.a.plot"));
            } else player.sendMessage(Language.get("usage", this.getUsage()));
        }
    }

}
