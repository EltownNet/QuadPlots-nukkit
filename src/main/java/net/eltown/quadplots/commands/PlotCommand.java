package net.eltown.quadplots.commands;

import cn.nukkit.command.CommandSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.eltown.quadplots.QuadPlots;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PlotCommand {

    private final QuadPlots plugin;
    private final String name, description, usage;
    private final List<String> aliases;

    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage("§cDer Command " + this.name + " ist noch nicht implementiert.");
    }

}
