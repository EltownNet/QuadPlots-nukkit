package net.eltown.quadplots.components.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.eltown.quadplots.QuadPlots;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Plot {

    private final int x, z;
    private final boolean claimed;
    private String owner = "";
    private List<String> members = new ArrayList<>(), helpers = new ArrayList<>(), flags = new ArrayList<>();

    public Plot(int x, int z, boolean claimed) {
        this.claimed = claimed;
        this.x = x;
        this.z = z;
    }

    public Plot(int x, int z, boolean claimed, String owner, List<String> members, List<String> helpers, List<String> flags) {
        this.claimed = claimed;
        this.x = x;
        this.z = z;
        this.owner = owner;
        this.members = members;
        this.helpers = helpers;
        this.flags = flags;
    }

    public void update() {
        QuadPlots.getApi().getProvider().updatePlot(this);
    }

    public void unclaim() {
        QuadPlots.getApi().getProvider().unclaimPlot(this);
    }

    public void clear() {
        // todo: unclaim
    }

}
