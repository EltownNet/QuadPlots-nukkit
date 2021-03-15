package net.eltown.quadplots.components.tasks;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.Task;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ClearTask extends Task {

    final PlotGeneratorInfo gen;
    final Level level;
    final Vector3 start;
    final int xMax, zMax, startZ;

    public ClearTask(final Plot plot, final Level level) {
        this.gen = QuadPlots.getApi().getProvider().getGeneratorInfo();
        Vector3 position = QuadPlots.getApi().getPosition(plot.getX(), plot.getZ());
        this.start = position.setComponents(position.getX() + 1, 0, position.getZ() + 1);
        this.xMax = (int) (position.getX() + gen.getPlotSize() - 2);
        this.zMax = (int) (position.getZ() + gen.getPlotSize() - 2);
        this.startZ = (int) this.start.getZ();
        this.level = level;
    }

    @Override
    public void onRun(int i) {
        CompletableFuture.runAsync(() -> {
            try {

                final Map<String, BaseFullChunk> chunks = new HashMap<>();

                Block block;

                while (this.start.getX() < this.xMax) {
                    while (this.start.getZ() < this.zMax) {
                        final BaseFullChunk chunk = this.level.getChunk(this.start.getChunkX(), this.start.getChunkZ());
                        if (!chunks.containsKey(chunk.getX() + ";" + chunk.getZ())) chunks.put(chunk.getX() + ";" + chunk.getZ(), chunk);

                        while (this.start.getY() < 256) {
                            if (this.start.getY() == this.gen.getHeight()) {
                                block = Block.get(this.gen.getGround()[0], this.gen.getGround()[1]);
                            } else if (this.start.getY() < this.gen.getHeight()) {
                                block = this.start.getY() != 0 ? Block.get(this.gen.getFill()[0], this.gen.getFill()[1]) : Block.get(BlockID.AIR);
                            } else block = Block.get(BlockID.AIR);
                            chunk.setBlockAt((int) this.start.getX(), (int) this.start.getY(), (int) this.start.getZ(), block.getId(), block.getDamage());
                            //this.level.setBlock(this.start, block);
                            this.start.y++;
                        }
                        this.start.z++;
                        this.start.y = 0;
                    }
                    this.start.z = this.startZ;
                    this.start.x++;
                }

                chunks.values().forEach((ch) -> {
                    ch.setGenerated(true);
                    this.level.setChunk(ch.getX(), ch.getZ(), ch);
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
