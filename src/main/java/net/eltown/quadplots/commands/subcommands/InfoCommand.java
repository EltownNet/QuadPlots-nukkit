package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;

public class InfoCommand extends PlotCommand {

    public InfoCommand(QuadPlots plugin) {
        super(plugin, "info", "Führe den Command auf einem Plot aus, um mehr Informationen zu erhalten.", "/p info", Collections.singletonList("i"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            player.sendMessage(plot != null ?
                    Language.getNP("info.command",
                            plot.getName(), plot.getX(), plot.getZ(),
                            plot.getDescription(),
                            plot.getOwners().size(), String.join(", ", plot.getOwners()),
                            plot.getTrusted().size(), String.join(", ", plot.getTrusted()),
                            plot.getHelpers().size(), String.join(", ", plot.getHelpers())
                    ) : Language.get("not.in.a.plot"));
        }
    }
}
