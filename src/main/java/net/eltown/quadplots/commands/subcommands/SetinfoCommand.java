package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.forms.custom.CustomForm;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;

public class SetinfoCommand extends PlotCommand {

    public SetinfoCommand(final QuadPlots plugin) {
        super(plugin, "setinfo", "Ändere deine Plot Informationen.", "/p setinfo", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());
            if (plot != null) {
                if (QuadPlots.getApi().isManager(player.getName()) || plot.isOwner(player.getName())) {
                    new CustomForm.Builder("Plot Info")
                            .addElement(new ElementLabel("Hier kannst du die Informationen deines Plots verändern."))
                            .addElement(new ElementInput("Name", plot.getName(), plot.getName()))
                            .addElement(new ElementInput("Description", plot.getDescription(), plot.getDescription()))
                            .onSubmit((p, f) -> {
                                plot.setName(f.getInputResponse(1));
                                plot.setDescription(f.getInputResponse(2));
                                p.sendMessage(Language.get("information.updated"));
                                plot.update();
                                if (QuadPlots.getApi().isManager(player.getName())) PluginsCommand.broadcastCommandMessage(player, "Changed info of Plot " + plot.getX() + "|" + plot.getZ(), false);
                            }).build().send(player);
                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));
        }

        return;
    }
}
