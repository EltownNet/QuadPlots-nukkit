package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.language.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class HomeCommand extends PlotCommand {

    public HomeCommand(final QuadPlots plugin) {
        super(plugin, "home", "Teleportiere dich zu deinem oder das Plot von einem anderen.", "/p h <nummer:spieler> <nummer>", Collections.singletonList("h"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;
            if (args.length >= 1) {
                if (args.length > 1) {
                    try {
                        String target = args[0];
                        final int id = Integer.parseInt(args[1]);

                        if (id <= 0) {
                            sender.sendMessage(Language.get("invalid.number"));
                            return;
                        }

                        final Player targetPlayer = Server.getInstance().getPlayer(target);
                        if (targetPlayer != null) target = targetPlayer.getName();

                        final LinkedList<Plot> plots = new LinkedList<>(QuadPlots.getApi().getProvider().getPlotsFiltered(target));

                        // has.no.plot: "§cDieser Spieler hat keine Plots!"
                        //has.no.plot.with.id: "§cDieser Spieler hat kein Plot mit der ID [0]!"

                        if (plots.size() != 0) {
                            if (plots.size() >= id) {
                                player.teleport(plots.get(id - 1).getPosition());
                                player.sendMessage(Language.get("teleported"));
                            } else sender.sendMessage(Language.get("has.no.plot.with.id", id));
                        } else sender.sendMessage(Language.get("has.no.plot"));
                    } catch (final Exception ex) {
                        sender.sendMessage(Language.get("invalid.number"));
                    }
                } else {
                    try {
                        final int id = Integer.parseInt(args[0]);
                        if (id <= 0) {
                            sender.sendMessage(Language.get("invalid.number"));
                            return;
                        }
                        final LinkedList<Plot> plots = QuadPlots.getApi().getProvider().getPlotsFiltered(player.getName());
                        if (plots.size() >= id) {
                            player.teleport(plots.get(id - 1).getPosition());
                            player.sendMessage(Language.get("teleported"));
                        } else sender.sendMessage(Language.get("no.plot.with.id", id));
                    } catch (final Exception ex) {
                        String target = args[0];

                        final Player targetPlayer = Server.getInstance().getPlayer(target);
                        if (targetPlayer != null) target = targetPlayer.getName();

                        final LinkedList<Plot> plots = QuadPlots.getApi().getProvider().getPlotsFiltered(target);

                        if (plots.size() != 0) {
                            player.teleport(plots.get(0).getPosition());
                            player.sendMessage(Language.get("teleported"));
                        } else sender.sendMessage(Language.get("has.no.plot"));
                    }
                }
            } else {
                final LinkedList<Plot> plots = QuadPlots.getApi().getProvider().getPlots(player.getName());
                if (plots.size() != 0) {
                    player.teleport(plots.get(0).getPosition());
                    player.sendMessage(Language.get("teleported"));
                } else sender.sendMessage(Language.get("no.plot.with.id", 1));
            }
        }
    }
}
