package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.forms.simple.SimpleForm;
import net.eltown.quadplots.components.language.Language;
import net.eltown.quadplots.components.tasks.ChangeWallTask;

import java.util.Collections;

public class WallCommand extends PlotCommand {

    public WallCommand(final QuadPlots plugin) {
        super(plugin, "wand", "Ändere die Wand deines Plots.", "/p wand", Collections.emptyList(), false);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;

            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

            if (plot != null) {
                if (plot.isOwner(player.getName())) {
                    new SimpleForm.Builder("Wände", "Hier kannst du die Wand deines Plots ändern.")
                            .addButton(new ElementButton("Stein"), (p) -> {
                                this.getPlugin().getServer().getScheduler().scheduleTask(new ChangeWallTask(plot, Block.get(1, 0), player.getLevel()));
                            })
                            .build().send(player);
                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));


        }
        return;
    }
}
