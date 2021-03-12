package net.eltown.quadplots.components.generator;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import lombok.SneakyThrows;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;

import java.util.HashMap;
import java.util.Map;

public class PlotGenerator extends Generator {

    private final String NAME = "Plots";
    private ChunkManager chunkManager;
    private PlotGeneratorInfo genInfo;

    public PlotGenerator(Map<String, Object> options) {
        //this.level = (String) options.get("level");
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void init(ChunkManager chunkManager, NukkitRandom nukkitRandom) {
        this.chunkManager = chunkManager;
        this.genInfo = QuadPlots.getApi().getProvider().getGeneratorInfo();
    }

    @SneakyThrows
    @Override
    public void generateChunk(int chX, int chZ) {
        final BaseFullChunk chunk = getChunkManager().getChunk(chX, chZ);


        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunk.setBiomeId(x, z, EnumBiome.PLAINS.id);

                final int blockX = chX << 4 | x;
                final int blockZ = chZ << 4 | z;

                final double difX = blockX >= 0 ? blockX % this.genInfo.getTotalSize() : Math.abs((blockX - this.genInfo.getPlotSize() + 1) % this.genInfo.getTotalSize());
                final double difZ = blockZ >= 0 ? blockZ % this.genInfo.getTotalSize() : Math.abs((blockZ - this.genInfo.getPlotSize() + 1) % this.genInfo.getTotalSize());

                chunk.setBlockAt(blockX, 0, blockZ, BlockID.BEDROCK);

                int[] ground = this.genInfo.getFill();

                if (difX > this.genInfo.getPlotSize() - 1 || difZ > this.genInfo.getPlotSize() - 1) {
                    chunk.setBlockAt(blockX, this.genInfo.getHeight(), blockZ, this.genInfo.getRoad()[0], this.genInfo.getRoad()[1]);
                    ground = this.genInfo.getRoad();
                } else if (difX == this.genInfo.getPlotSize() - 1 || difZ == this.genInfo.getPlotSize() - 1 || difZ == 0 || difX == 0) {
                    chunk.setBlockAt(blockX, this.genInfo.getHeight() + 1, blockZ, this.genInfo.getBorder()[0], this.genInfo.getBorder()[1]);
                    chunk.setBlockAt(blockX, this.genInfo.getHeight(), blockZ, this.genInfo.getRoad()[0], this.genInfo.getRoad()[1]);
                    ground = this.genInfo.getRoad();
                } else {
                    chunk.setBlockAt(blockX, this.genInfo.getHeight(), blockZ, this.genInfo.getGround()[0], this.genInfo.getGround()[1]);
                }

                for (int y = 1; y < 64; y++) chunk.setBlockAt(blockX, y, blockZ, ground[0], ground[1]);
            }
        }

    }

    @Override
    public void populateChunk(int i, int i1) {
        /* empty */
    }

    @Override
    public Map<String, Object> getSettings() {
        return new HashMap<>();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, this.genInfo.getHeight() + 2, 0);
    }

    @Override
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

}
