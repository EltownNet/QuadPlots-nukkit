package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;

import java.util.Collections;

public class WarpCommand extends PlotCommand {

    public WarpCommand(QuadPlots plugin) {
        super(plugin, "warp", "Teleportiere dich zu einem Plot.", "/p warp <x> <z>", Collections.emptyList());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            if (args.length > 1) {
                try {
                    final int x = Integer.parseInt(args[0]), z = Integer.parseInt(args[1]);
                    final Vector3 v = QuadPlots.getApi().getPlotPosition(x, z);
                    final Plot plot = QuadPlots.getApi().getPlotByPosition(v);
                    final Level level = Server.getInstance().getLevelByName(QuadPlots.getApi().getProvider().getGeneratorInfo().getLevel());
                    final boolean generated = level.getChunk(v.getChunkX(), v.getChunkZ()).isGenerated();
                    if (generated) {
                        player.teleport(plot.getPosition());
                        player.sendMessage(Language.get("teleported"));
                    } else {
                        player.sendMessage(Language.get("plot.not.generated"));
                    }
                } catch (Exception ex) {
                    player.sendMessage(Language.get("invalid-number"));
                }
            } else player.sendMessage(Language.get("usage", this.getUsage()));
        }
        return;
    }
}
