package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.forms.custom.CustomForm;
import net.eltown.quadplots.components.forms.simple.SimpleForm;
import net.eltown.quadplots.components.language.Language;

import java.util.Arrays;
import java.util.Collections;

public class SettingsCommand extends PlotCommand {

    public SettingsCommand(final QuadPlots plugin) {
        super(plugin, "settings", "Verändere die Einstellungen deines Plots.", "/p settings", Arrays.asList("menu", "einstellungen"), false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

            if (plot != null) {
                if (QuadPlots.getApi().isManager(player.getName()) || plot.isOwner(player.getName())) {

                    final SimpleForm.Builder main = new SimpleForm.Builder("Plot Einstellungen", "Hier kannst du alles für dein Plot einstellen.")
                            .addButton(new ElementButton("§8» §fPlot-Info bearbeiten"), (p) -> Server.getInstance().dispatchCommand(player, "p setinfo"))
                            .addButton(new ElementButton("§8» §fGenerelle Einstellungen"))
                            .addButton(new ElementButton("§8» §fMitglieder verwalten"), (p) -> {
                                new CustomForm.Builder("Mitglieder verwalten")
                                        .addElement(new ElementLabel("Hier kannst du die Mitglieder deines Plots bearbeiten."))
                                        .addElement(new ElementInput("Mitglieder", "BeispielUser1,"));
                            })
                            .addButton(new ElementButton("§8» §fPlot-Rand ändern"), (p) -> Server.getInstance().dispatchCommand(player, "p rand"))
                            .addButton(new ElementButton("§8» §fPlot-Wand ändern"), (p) -> Server.getInstance().dispatchCommand(player, "p wand"));

                    main.build().send(player);

                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }
    }
}
