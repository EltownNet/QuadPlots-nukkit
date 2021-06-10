package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;

public class SethomeCommand extends PlotCommand {

    public SethomeCommand(QuadPlots plugin) {
        super(plugin, "sethome", "Setze das Home deines Plots.", "/p sethome", Collections.emptyList(), false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

            if (plot != null) {
                if (plot.isOwner(player.getName())) {
                    plot.removeFlag("home");
                    plot.addFlag("home;" + player.getX() + ";" + player.getY() + ";" + player.getZ());
                    plot.update();
                    player.sendMessage(Language.get("home.set"));
                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }
}
