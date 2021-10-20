package net.eltown.quadplots.components.data;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.math.Direction;

import java.util.*;

@Getter
@Setter
public class Plot {

    private final int x, z;
    private boolean claimed;
    private Set<String> owners = new HashSet<>();
    private Set<String> trusted = new HashSet<>(), helpers = new HashSet<>(), flags = new HashSet<>(), banned = new HashSet<>();

    public Plot(int x, int z, boolean claimed) {
        this.claimed = claimed;
        this.x = x;
        this.z = z;
    }

    public Plot(int x, int z, boolean claimed, List<String> owners, List<String> trusted, List<String> helpers, List<String> flags, List<String> banned) {
        this.claimed = claimed;
        this.x = x;
        this.z = z;
        this.owners.addAll(owners);
        this.trusted.addAll(trusted);
        this.helpers.addAll(helpers);
        this.flags.addAll(flags);
        this.banned.addAll(banned);
    }

    public Plot getSide(final Direction side) {
        switch (side) {
            case NORTH:
                return QuadPlots.getApi().getPlot(this.x, this.z - 1);
            case EAST:
                return QuadPlots.getApi().getPlot(this.x + 1, this.z);
            case SOUTH:
                return QuadPlots.getApi().getPlot(this.x, this.z + 1);
            case WEST:
                return QuadPlots.getApi().getPlot(this.x - 1, this.z);
            default:
                return null;
        }
    }

    public boolean canBuild(final String player) {
        if (this.isOwner(player)) return true;
        else if (this.trusted.contains(player)) return true;
        else {
            if (this.helpers.contains(player)) {
                for (final String owner : this.getOwners()) {
                    if (this.getOwners().contains(owner)) return true;
                }
            }
        }
        return false;
    }

    public void update() {
        QuadPlots.getApi().getProvider().updatePlot(this);
    }

    public void unclaim() {
        QuadPlots.getApi().getProvider().unclaimPlot(this);
    }

    public void claim(final String owner) {
        this.owners = new HashSet<>(Collections.singletonList(owner));
        this.claimed = true;

        QuadPlots.getApi().getProvider().updatePlot(this);
    }

    public void setName(final String name) {
        this.removeFlag("name");
        this.addFlag("name;" + name);
    }

    public void setDescription(final String name) {
        this.removeFlag("description");
        this.addFlag("description;" + name);
    }

    public String getName() {
        for (final String flag : flags) {
            if (flag.startsWith("name;")) {
                return flag.split(";")[1];
            }
        }

        return "Unbenannt";
    }

    public String getDescription() {
        for (final String flag : flags) {
            if (flag.startsWith("description;")) {
                return flag.split(";")[1];
            }
        }

        return "Keine Beschreibung angegeben.";
    }

    public Position getPosition() {
        final Level level = Server.getInstance().getLevelByName(QuadPlots.getApi().getProvider().getGeneratorInfo().getLevel());
        for (final String str : this.flags) {
            if (str.startsWith("home;")) {
                final String[] split = str.split(";");
                final double x = Double.parseDouble(split[1]), y = Double.parseDouble(split[2]), z = Double.parseDouble(split[3]);
                return new Position(x, y, z, level);
            }
        }

        final Vector3 pos = QuadPlots.getApi().getPlotPosition(this.x, this.z);
        return new Position(pos.getX(), pos.getY() + 2, pos.getZ(), level);
    }

    public void addFlag(final String str) {
        this.flags.add(str);
    }

    public void removeFlag(final String str) {
        this.flags.removeIf(flag -> flag.startsWith(str) || flag.equalsIgnoreCase(str));
    }

    public void clear() {
        // todo: unclaim
    }

    public boolean isOwner(final String player) {
        return this.owners.contains(player);
    }

    public String getStringId() {
        return x + ";" + z;
    }

}
