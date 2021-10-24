package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.plugin.PluginBase;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.Road;
import net.eltown.quadplots.components.forms.custom.CustomForm;

import java.util.Collections;
import java.util.Set;

public class AdminCommand extends PlotCommand {

    public AdminCommand(final QuadPlots plugin) {
        super(plugin, "admin", "Adminpanel", "/p admin", Collections.emptyList(), true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer() && sender.isOp()) {
            final Player player = (Player) sender;
            Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            if (plot == null) {
                Road road = QuadPlots.getApi().getRoad(player.getPosition());
                if (road != null && road.isMerged()) plot = road.getPlot();
            }

            if (plot != null) {
                Plot finalPlot = plot;
                final CustomForm.Builder main = new CustomForm.Builder("Plot - §cAdminpanel")
                        .addElement(new ElementInput("Plot Besitzer", String.join(",", plot.getOwners()), String.join(",", plot.getOwners())))
                        .addElement(new ElementLabel("\n§l§8[§c!§8]§r§c Achtung: Eine fehlerhafte Änderung der Flags kann die Funktion eines Plots stören."))
                        .addElement(new ElementInput("Flags", String.join(",", plot.getFlags()), String.join(",", plot.getFlags())))
                        .onSubmit((p, f) -> {
                            finalPlot.setOwners(f.getInputResponse(0).split(","));
                            finalPlot.setFlags(f.getInputResponse(2).split(","));
                            sender.sendMessage("§aPlot aktualisiert.");

                            QuadPlots.getApi().getProvider().updatePlot(finalPlot);
                        });
                main.build().send(player);
            } else sender.sendMessage("§cBitte stelle dich auf ein Plot.");
        }
    }
}
