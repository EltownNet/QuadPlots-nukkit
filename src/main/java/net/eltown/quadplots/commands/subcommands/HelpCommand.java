package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.forms.modal.ModalForm;
import net.eltown.quadplots.components.forms.simple.SimpleForm;

import java.util.Collections;

public class HelpCommand extends PlotCommand {

    public HelpCommand(QuadPlots plugin) {
        super(plugin, "help", "wurst", "wurst", Collections.emptyList(), true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) showHelpForm((Player) sender);
        return;
    }

    private void showHelpForm(Player player) {
        final SimpleForm.Builder form = new SimpleForm.Builder("§bPlot Commands", "Hier findest du alle Befehle die du für dein Grundstück brauchst.");
        QuadPlots.getApi().getCommandHandler().getCommands().forEach((e) -> {
            if (!e.isHidden()) {
                form.addButton(new ElementButton("/p " + e.getName() + "\n§7" + e.getDescription()), (p) -> {
                    new ModalForm.Builder("/p " + e.getName(), e.getDescription() + "\n\nVerwendung: " + e.getUsage(), "§l§c«", "§cSchließen")
                            .onYes((pp) -> showHelpForm(p))
                            .onNo((pp) -> {})
                            .build().send(p);
                });
            }
        });
        form.build().send(player);

    }
}
