package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;

public class SethomeCommand extends PlotCommand {

    public SethomeCommand(QuadPlots plugin) {
        super(plugin, "sethome", "Setze das Home deines Plots.", "/p sethome", Collections.singletonList("setspawn"), false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

            if (plot != null) {
                if (QuadPlots.getApi().isManager(player.getName()) || plot.isOwner(player.getName())) {
                    plot.removeFlag("home");
                    plot.addFlag("home;" + player.getX() + ";" + player.getY() + ";" + player.getZ());
                    plot.update();
                    player.sendMessage(Language.get("home.set"));
                    if (QuadPlots.getApi().isManager(player.getName())) PluginsCommand.broadcastCommandMessage(player, "Changed home of Plot " + plot.getX() + "|" + plot.getZ(), false);
                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }
}
