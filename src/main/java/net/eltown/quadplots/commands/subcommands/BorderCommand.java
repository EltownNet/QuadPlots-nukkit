package net.eltown.quadplots.commands.subcommands;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
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
import net.eltown.quadplots.components.tasks.ChangeBorderTask;
import net.eltown.quadplots.components.tasks.ChangeWallTask;
import net.eltown.servercore.components.api.ServerCoreAPI;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

public class BorderCommand extends PlotCommand {

    private static final LinkedHashSet<Border> border = new LinkedHashSet<>();

    public BorderCommand(final QuadPlots plugin) {
        super(plugin, "rand", "Ändere den Rand deines Plots.", "/p rand", Collections.singletonList("border"), false);
    }

    static {
        border.addAll(
                Arrays.asList(
                        new Border(Block.get(BlockID.AIR, 0), 299.95),
                        new Border(Block.get(BlockID.SLAB, 0), 0.99),
                        new Border(Block.get(BlockID.SLAB, 1), 0.99),
                        new Border(Block.get(BlockID.SLAB, 5), 129.95),
                        new Border(Block.get(BlockID.WOODEN_SLAB, 0), 99.95),
                        new Border(Block.get(BlockID.WOODEN_SLAB, 1), 99.95),
                        new Border(Block.get(BlockID.WOODEN_SLAB, 2), 99.95),
                        new Border(Block.get(BlockID.WOODEN_SLAB, 3), 99.95),
                        new Border(Block.get(BlockID.WOODEN_SLAB, 4), 99.95),
                        new Border(Block.get(BlockID.WOODEN_SLAB, 5), 99.95)
                )
        );
    }

    private boolean hasBlock(final Player player, final Block block) {
        return player.hasPermission("plot.border." + block.getId() + "-" + block.getDamage());
    }

    private ElementButtonImageData getImage(final Block block) {
        return new ElementButtonImageData("url", "http://45.138.50.23:3000/img/ui/plot/border/" + block.getId() + "-" + block.getDamage() + ".png");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.isPlayer()) {
            final Player player = (Player) sender;

            final Plot plot = QuadPlots.getApi().getPlotByPosition(player.getPosition());

            if (plot != null) {
                if (QuadPlots.getApi().isManager(player.getName()) || plot.isOwner(player.getName())) {
                    final SimpleForm.Builder builder = new SimpleForm.Builder("Plot-Rand", "Hier kannst du den Rand deines Plots ändern.");

                    border.forEach(wall -> {

                        final Block block = wall.getBlock();
                        final double price = wall.getPrice();

                        if (this.hasBlock(player, block)) {
                            builder.addButton(new ElementButton(block.getName() + "\n§2Im Besitz", this.getImage(block)), (p) -> {
                                new ModalForm.Builder("Plot-Rand ändern", "Möchtest du den Rand deines Plots zu §9" + block.getName() + "§r ändern?", "§aJa", "§cZurück")
                                        .onYes((p1) -> {
                                            this.getPlugin().getServer().getScheduler().scheduleTask(new ChangeBorderTask(plot, Block.get(block.getId(), block.getDamage()), player.getLevel()));
                                            player.sendMessage(Language.get("border.change", block.getName()));
                                        })
                                        .onNo((p1) -> {
                                            builder.build().send(player);
                                        })
                                        .build().send(player);
                            });
                        } else {
                            builder.addButton(new ElementButton(block.getName() + "\n§0Kaufen für §a$" + Economy.getAPI().getMoneyFormat().format(price), this.getImage(block)), (p) -> {
                                new ModalForm.Builder("Plot-Rand kaufen", "Möchtest du den Plot-Rand §9" + block.getName() + " §rfür §a$" + Economy.getAPI().getMoneyFormat().format(price) + "§r kaufen?", "§aJa", "§cZurück")
                                        .onYes((p1) -> {
                                            Economy.getAPI().getMoney(player, (money) -> {
                                                if (!(price > money)) {
                                                    ServerCoreAPI.getGroupAPI().addPlayerPermission(player.getName(), "plot.border." + block.getId() + "-" + block.getDamage());
                                                    Economy.getAPI().reduceMoney(player, price);
                                                    player.sendMessage(Language.get("border.bought"));
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
    private static class Border {

        private final Block block;
        private final double price;

    }

}
