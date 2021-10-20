package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.forms.simple.SimpleForm;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;
import java.util.LinkedList;

public class ListCommand extends PlotCommand {

    public ListCommand(final QuadPlots plugin) {
        super(plugin, "list", "Zeige dir eine Liste mit deinen Plots an.", "/p list <optional: spieler>", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            if (args.length > 0) {
                String target = args[0];

                final Player preTarget = Server.getInstance().getPlayer(target);
                if (preTarget != null) target = preTarget.getName();

                final LinkedList<Plot> plots = QuadPlots.getApi().getProvider().getPlots(target);
                if (plots.size() == 0) {
                    sender.sendMessage(Language.get("has.no.plot"));
                    return;
                }

                final SimpleForm.Builder builder = new SimpleForm.Builder("§8» §f" + target + "'s Plots", " ");

                plots.forEach((plot) -> {
                    builder.addButton(new ElementButton("§8» §8[§7" + plot.getX() + "|" + plot.getZ() + "§8] §f" + plot.getName() + "\n§7" + plot.getDescription()), (p) -> {
                        p.teleport(plot.getPosition());
                    });
                });

                builder.build().send(player);
            } else {
                final LinkedList<Plot> plots = QuadPlots.getApi().getProvider().getPlots(sender.getName());

                final SimpleForm.Builder builder = new SimpleForm.Builder("§8» §fDeine Plots", " ");

                plots.forEach((plot) -> {
                    builder.addButton(new ElementButton("§8» §8[§7" + plot.getX() + "|" + plot.getZ() + "§8] §f" + plot.getName() + "\n§7" + plot.getDescription()), (p) -> {
                        p.teleport(plot.getPosition());
                    });
                });

                builder.build().send(player);
            }
        }
    }
}
