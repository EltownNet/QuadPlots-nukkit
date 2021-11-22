package net.eltown.quadplots.components.listener;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPistonChangeEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.LiquidFlowEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.block.BlockPistonEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemPotion;
import cn.nukkit.level.Location;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.Road;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class PlayerListener implements Listener {

    private void cancel(final Player player, final Event event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void on(final PlayerJoinEvent event) {
        event.getPlayer().setCheckMovement(false);
    }

    @EventHandler
    public void on(final PlayerQuitEvent event) {
        if (QuadPlots.getApi().isManager(event.getPlayer().getName()))
            QuadPlots.getApi().toggleManage(event.getPlayer().getName());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            switch (event.getCause()) {
                case FALL:
                    event.setDamage(0f);
                    break;
                case ENTITY_ATTACK:
                    final EntityDamageByEntityEvent ev = (EntityDamageByEntityEvent) event;
                    if (ev.getDamager() instanceof Player) {
                        final Player damager = (Player) ev.getDamager();
                        if (!damager.isOp() || damager.getGamemode() == 0) {
                            ev.setDamage(0f);
                            ev.setCancelled(true);
                        }
                    } else ev.setDamage(0f);
                    break;
                case PROJECTILE:
                    event.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final PlayerDeathEvent event) {
        event.setKeepExperience(true);
        event.setKeepInventory(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final EntityExplodeEvent event) {
        event.setBlockList(new ArrayList<>());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final LiquidFlowEvent event) {
        final Plot plot = QuadPlots.getApi().getPlotByPosition(event.getTo().getLocation());
        if (plot == null) event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final BlockPistonEvent event) {
        event.getBlocks().forEach((b) -> {
            Plot plot = QuadPlots.getApi().getPlotByPosition(b.getLocation());
            if (plot == null) {
                final Road road = QuadPlots.getApi().getRoad(b.getLocation());
                if (road.isMerged()) plot = road.getPlot();
            }

            if (plot == null) event.setCancelled(true);
        });
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final BlockBreakEvent event) {
        if (QuadPlots.getApi().isManager(event.getPlayer().getName())) return;
        if (QuadPlots.getApi().getProvider().getGeneratorInfo().getLevel().equalsIgnoreCase(event.getPlayer().getLevel().getName())) {
            final Plot plot = QuadPlots.getApi().getPlotByPosition(event.getBlock().getLocation());
            if (plot != null) {
                if (!plot.canBuild(event.getPlayer().getName())) this.cancel(event.getPlayer(), event);
            } else {
                final Road road = QuadPlots.getApi().getRoad(event.getBlock().getLocation());
                if (road != null && road.isMerged()) {
                    if (!road.getPlot().canBuild(event.getPlayer().getName())) this.cancel(event.getPlayer(), event);
                } else this.cancel(event.getPlayer(), event);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final BlockPlaceEvent event) {
        if (QuadPlots.getApi().isManager(event.getPlayer().getName())) return;
        if (QuadPlots.getApi().getProvider().getGeneratorInfo().getLevel().equalsIgnoreCase(event.getPlayer().getLevel().getName())) {
            final Plot plot = QuadPlots.getApi().getPlotByPosition(event.getBlock().getLocation());
            if (plot != null) {
                if (!plot.canBuild(event.getPlayer().getName())) this.cancel(event.getPlayer(), event);
            } else {
                final Road road = QuadPlots.getApi().getRoad(event.getBlock().getLocation());
                if (road != null && road.isMerged()) {
                    if (!road.getPlot().canBuild(event.getPlayer().getName())) this.cancel(event.getPlayer(), event);
                } else this.cancel(event.getPlayer(), event);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void on(final PlayerInteractEvent event) {
        if (QuadPlots.getApi().isManager(event.getPlayer().getName())) return;
        if ((event.getItem() instanceof ItemEdible || event.getItem() instanceof ItemPotion) && event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
            return;
        if (QuadPlots.getApi().getProvider().getGeneratorInfo().getLevel().equalsIgnoreCase(event.getPlayer().getLevel().getName())) {
            final Plot plot = QuadPlots.getApi().getPlotByPosition(event.getBlock() != null ? event.getBlock().getLocation() : event.getPlayer().getPosition());
            if (plot != null) {
                if (!plot.canBuild(event.getPlayer().getName())) this.cancel(event.getPlayer(), event);
            }  else {
                final Road road = QuadPlots.getApi().getRoad(event.getBlock().getLocation());
                if (road != null && road.isMerged()) {
                    if (!road.getPlot().canBuild(event.getPlayer().getName())) this.cancel(event.getPlayer(), event);
                } else this.cancel(event.getPlayer(), event);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void on(final PlayerMoveEvent event) {
        CompletableFuture.runAsync(() -> {
            final Player player = event.getPlayer();

            final Location from = event.getFrom();
            final Location to = event.getTo();

            final Plot plotFrom = QuadPlots.getApi().getPlotByPosition(from);
            final Plot plotTo = QuadPlots.getApi().getPlotByPosition(to);

            if (plotTo != null) {
                if (player.getMovementSpeed() > 1) player.setMovementSpeed(0.1f);
                if (plotFrom == null) {
                    if (plotTo.isClaimed()) {
                        player.sendActionBar(
                                plotTo.getName() + "§r - " + plotTo.getX() + "|" + plotTo.getZ() + "\n" +
                                        "Von: " + String.join(", ", plotTo.getOwners())
                        );
                    } else player.sendActionBar(plotTo.getX() + "|" + plotTo.getZ() + "\n§a/p claim");
                }

                if (QuadPlots.getApi().isManager(event.getPlayer().getName())) return;
                if (plotTo.getBanned().contains(player.getName())) player.teleport(from);
                if (plotFrom != null && plotFrom.getBanned().contains(player.getName()))
                    player.teleport(player.getLevel().getSpawnLocation());
            } else if (!player.getAdventureSettings().get(AdventureSettings.Type.ALLOW_FLIGHT) && player.isSneaking() && plotFrom == null) {
                player.setMovementSpeed(2f);
            } else {
                if (!player.isSneaking() && player.getMovementSpeed() > 1) event.getPlayer().setMovementSpeed(0.1f);
            }
        });
    }

}
