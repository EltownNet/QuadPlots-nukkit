package net.eltown.quadplots.components.data;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import net.eltown.quadplots.QuadPlots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class Plot {

    private final int x, z;
    private boolean claimed;
    private List<String> owners = new ArrayList<>();
    private List<String> trusted = new ArrayList<>(), helpers = new ArrayList<>(), flags = new ArrayList<>(), banned = new ArrayList<>();

    public Plot(int x, int z, boolean claimed) {
        this.claimed = claimed;
        this.x = x;
        this.z = z;
    }

    public Plot(int x, int z, boolean claimed, List<String> owners, List<String> trusted, List<String> helpers, List<String> flags, List<String> banned) {
        this.claimed = claimed;
        this.x = x;
        this.z = z;
        this.owners = owners;
        this.trusted = trusted;
        this.helpers = helpers;
        this.flags = flags;
        this.banned = banned;
    }

    public boolean canBuild(final String player) {
        if (this.isOwner(player)) return true;
        else if (this.trusted.contains(player)) return true;
        else return this.helpers.contains(player) && Server.getInstance().getPlayer(player) != null;
    }

    public void update() {
        QuadPlots.getApi().getProvider().updatePlot(this);
    }

    public void unclaim() {
        QuadPlots.getApi().getProvider().unclaimPlot(this);
    }

    public void claim(final String owner) {
        this.owners = Collections.singletonList(owner);
        this.claimed = true;

        QuadPlots.getApi().getProvider().updatePlot(this);
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
