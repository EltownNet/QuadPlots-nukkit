package net.eltown.quadplots.components.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.eltown.quadplots.components.math.Direction;

@RequiredArgsConstructor
public class Road {

    @Getter
    private final Plot plot;
    private final Direction requiredMerge;

    public boolean isMerged() {
        return plot.hasMergeInDirection(requiredMerge);
    }

}
