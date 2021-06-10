package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;
import net.eltown.quadplots.components.tasks.ChangeWallTask;
import net.eltown.quadplots.components.tasks.ClearTask;

import java.util.Collections;

public class DisposeCommand extends PlotCommand {

    public DisposeCommand(final QuadPlots plugin) {
        super(plugin, "dispose", "Gebe dein Plot zum claimen frei.", "/p dispose", Collections.singletonList("unclaim"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            if (plot != null) {

                if (args.length > 0) {
                    if (!args[0].equalsIgnoreCase("confirm")) {
                        player.sendMessage(Language.get("plot.dispose.really"));
                        return;
                    }
                } else {
                    player.sendMessage(Language.get("plot.dispose.really"));
                    return;
                }

                if (plot.isOwner(player.getName())) {
                    player.sendMessage(Language.get("plot.disposing"));
                    plot.unclaim();
                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }
}
