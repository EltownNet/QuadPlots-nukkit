package net.eltown.quadplots.components.tasks;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;

import java.util.concurrent.CompletableFuture;

public class MergePlotTask extends Task {

    private final Plot p1, p2;
    private final boolean northMerge;

    // NORTH -> SOUTH
    // EAST -> WEST
    public MergePlotTask(final Plot p1, final Plot p2, boolean northMerge) {
        this.p1 = p1;
        this.p2 = p2;
        this.northMerge = northMerge;
    }

    @Override
    public void onRun(int i) {
        CompletableFuture.runAsync(() -> {
            final Level level = p1.getPosition().getLevel();
            final PlotGeneratorInfo gen = QuadPlots.getApi().getProvider().getGeneratorInfo();

            // NORTH -> SOUTH
            if (northMerge) {
                final Vector3 northPos = QuadPlots.getApi().getPosition(p1.getX(), p1.getZ());
                final Vector3 southPos = QuadPlots.getApi().getPosition(p2.getX(), p2.getZ()).add(0, 0, gen.getPlotSize() - 1);// this.zMax = plotBeginPos.z + gen.getPlotSize() - 1;

                // Border
                for (int t = 0; t < gen.getRoadWidth() + 1; t++) {
                    level.setBlock(southPos.add(0, 1, t), Block.get(BlockID.SLAB, 1));
                    level.setBlock(southPos.add(gen.getPlotSize() - 1, 1, t), Block.get(BlockID.SLAB, 1));
                }

                for (int x = (int) southPos.x + 1; x < southPos.x + gen.getPlotSize() - 1; x++) {
                    for (int z = 0; z < gen.getRoadWidth() + 2; z++) {
                        level.setBlock(new Vector3(x, gen.getHeight(), (int) southPos.z + z), Block.get(2));
                        for (int y = gen.getHeight() - 1; y > 0; y--) {
                            level.setBlock(new Vector3(x, y, (int) southPos.z + z), Block.get(1));
                        }
                    }
                }
            } else {
                final Vector3 westPos = QuadPlots.getApi().getPosition(p2.getX(), p2.getZ());

                // Border
                for (int t = 0; t < gen.getRoadWidth() + 1; t++) {
                    level.setBlock(westPos.add(-t, 1, 0), Block.get(BlockID.SLAB, 1));
                    level.setBlock(westPos.add(-t, 1, gen.getPlotSize() - 1), Block.get(BlockID.SLAB, 1));
                }

                for (int z = (int) westPos.z + 1; z < westPos.z + gen.getPlotSize() - 1; z++) {
                    for (int x = 0; x < gen.getRoadWidth() + 2; x++) {
                        level.setBlock(new Vector3((int) westPos.x - x, gen.getHeight(), z), Block.get(2));
                        for (int y = gen.getHeight() - 1; y > 0; y--) {
                            level.setBlock(new Vector3(westPos.x - x, y, z), Block.get(1));
                        }
                    }
                }
            }
        });
    }
}
