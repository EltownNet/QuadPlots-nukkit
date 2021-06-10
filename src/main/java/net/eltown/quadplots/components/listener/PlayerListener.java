package net.eltown.quadplots.components.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.ItemEdible;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.data.Plot;

public class PlayerListener implements Listener {

    @EventHandler
    public void on(final PlayerJoinEvent event) {
        event.getPlayer().setCheckMovement(false);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final BlockBreakEvent event) {
        if (event.getPlayer().isOp()) return;
        if (QuadPlots.getApi().getProvider().getGeneratorInfo().getLevel().equalsIgnoreCase(event.getPlayer().getLevel().getName())) {
            final Plot plot = QuadPlots.getApi().getPlotByPosition(event.getBlock().getLocation());
            if (plot != null) {
                if (!plot.canBuild(event.getPlayer().getName())) event.setCancelled(true);
            } else event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final BlockPlaceEvent event) {
        if (event.getPlayer().isOp()) return;
        if (QuadPlots.getApi().getProvider().getGeneratorInfo().getLevel().equalsIgnoreCase(event.getPlayer().getLevel().getName())) {
            final Plot plot = QuadPlots.getApi().getPlotByPosition(event.getBlock().getLocation());
            if (plot != null) {
                if (!plot.canBuild(event.getPlayer().getName())) event.setCancelled(true);
            } else event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final PlayerInteractEvent event) {
        if (event.getPlayer().isOp()) return;
        if (event.getItem() instanceof ItemEdible && event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) return;
        if (QuadPlots.getApi().getProvider().getGeneratorInfo().getLevel().equalsIgnoreCase(event.getPlayer().getLevel().getName())) {
            final Plot plot = QuadPlots.getApi().getPlotByPosition(event.getBlock() != null ? event.getBlock().getLocation() : event.getPlayer().getPosition());
            if (plot != null) {
                if (!plot.canBuild(event.getPlayer().getName())) event.setCancelled(true);
            } else event.setCancelled(true);
        }
    }

}
