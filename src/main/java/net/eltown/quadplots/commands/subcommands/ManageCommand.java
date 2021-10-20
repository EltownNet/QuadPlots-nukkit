package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;

public class ManageCommand extends PlotCommand {

    public ManageCommand(final QuadPlots plugin) {
        super(plugin, "manage", "wurst", "/p manage", Collections.emptyList(), true);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer() && sender.isOp()) {
            if (QuadPlots.getApi().toggleManage(sender.getName())) {
                sender.sendMessage(Language.get("plot.manage.on"));
                if (QuadPlots.getApi().isManager(sender.getName())) PluginsCommand.broadcastCommandMessage(sender, "Switched to Plot-Manage mode", false);
            } else sender.sendMessage(Language.get("plot.manage.off"));
        }
    }
}
