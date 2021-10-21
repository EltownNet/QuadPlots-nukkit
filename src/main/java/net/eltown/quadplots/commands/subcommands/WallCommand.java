package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.defaults.PluginsCommand;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.eltown.economy.Economy;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.PlotCommand;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.forms.modal.ModalForm;
import net.eltown.quadplots.components.forms.simple.SimpleForm;
import net.eltown.quadplots.components.language.Language;
import net.eltown.quadplots.components.tasks.ChangeWallTask;
import net.eltown.servercore.ServerCore;
import net.eltown.servercore.components.api.ServerCoreAPI;

import java.util.*;

public class WallCommand extends PlotCommand {

    private static final LinkedHashSet<Wall> walls = new LinkedHashSet<>();

    public WallCommand(final QuadPlots plugin) {
        super(plugin, "wand", "Ändere die Wand deines Plots.", "/p wand", Collections.singletonList("wall"), false);
    }

    static {
        walls.addAll(
                Arrays.asList(
                        new Wall(Block.get(BlockID.QUARTZ_BLOCK), 0.99),
                        new Wall(Block.get(BlockID.STONE), 99.95),
                        new Wall(Block.get(BlockID.DIRT), 29.95)
                )
        );
    }

    private boolean hasBlock(final Player player, final Block block) {
        return player.hasPermission("plot.wall." + block.getId() + "-" + block.getDamage());
    }

    private ElementButtonImageData getImage(final Block block) {
        return new ElementButtonImageData("url", "http://45.138.50.23:3000/img/ui/plot/wall/" + block.getId() + "-" + block.getDamage() + ".png");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {

            final Player player = (Player) sender;

            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

            if (plot != null) {
                if (plot.isMerged()) {
                    sender.sendMessage(Language.get("plot.merge.command"));
                    return;
                }
                if (QuadPlots.getApi().isManager(player.getName()) || plot.isOwner(player.getName())) {
                    final SimpleForm.Builder builder = new SimpleForm.Builder("Plot-Wand", "Hier kannst du die Wände deines Plots ändern.");

                    walls.forEach(wall -> {

                        final Block block = wall.getBlock();
                        final double price = wall.getPrice();

                        if (this.hasBlock(player, block)) {
                            builder.addButton(new ElementButton(block.getName() + "\n§2Im Besitz", this.getImage(block)), (p) -> {
                                new ModalForm.Builder("Plot-Wand Ändern", "Möchtest du die Wand deines Plots zu §9" + block.getName() + "§r ändern?", "§aJa", "§cZurück")
                                        .onYes((p1) -> {
                                            this.getPlugin().getServer().getScheduler().scheduleTask(new ChangeWallTask(plot, Block.get(block.getId(), block.getDamage()), player.getLevel()));
                                            player.sendMessage(Language.get("wall.change", block.getName()));
                                        })
                                        .onNo((p1) -> {
                                            builder.build().send(player);
                                        })
                                        .build().send(player);
                            });
                        } else {
                            builder.addButton(new ElementButton(block.getName() + "\n§0Kaufen für §a$" + Economy.getAPI().getMoneyFormat().format(price), this.getImage(block)), (p) -> {
                                new ModalForm.Builder("Plotwand kaufen", "Möchtest du die Plotwand §9" + block.getName() + " §rfür §a$" + Economy.getAPI().getMoneyFormat().format(price) + "§r kaufen?", "§aJa", "§cZurück")
                                        .onYes((p1) -> {
                                            Economy.getAPI().getMoney(player, (money) -> {
                                                if (!(price > money)) {
                                                    ServerCoreAPI.getGroupAPI().addPlayerPermission(player.getName(), "plot.wall." + block.getId() + "-" + block.getDamage());
                                                    Economy.getAPI().reduceMoney(player, price);
                                                    player.sendMessage(Language.get("wall.bought"));
                                                } else player.sendMessage(Language.get("not.enough.money"));
                                            });
                                        })
                                        .onNo((p1) -> {
                                            builder.build().send(player);
                                        })
                                        .build().send(player);
                            });
                        }
                    });

                    builder.build().send(player);
                } else player.sendMessage(Language.get("no.plot.permission"));
            } else player.sendMessage(Language.get("not.in.a.plot"));


        }
        return;
    }

    @Getter
    @AllArgsConstructor
    private static class Wall {

        private final Block block;
        private final double price;

    }

}
