package net.eltown.quadplots.components.api;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.SubCommandHandler;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;

public class Api {

    @Getter
    private final SubCommandHandler commandHandler;
    private final QuadPlots plugin;
    @Getter
    private Provider provider;

    public Api(final QuadPlots plugin) {
        this.plugin = plugin;
        this.commandHandler = new SubCommandHandler();
        this.commandHandler.init(plugin);
    }

    public void init() {
        this.provider = new Provider(this.plugin/*, new MongoClientURI(this.plugin.getConfig().getString("uri"))*/);
    }

    public Vector3 getPosition(final int x, final int z, final int y) {
        return new Vector3(x * this.getProvider().getGeneratorInfo().getTotalSize(), y, z * this.getProvider().getGeneratorInfo().getTotalSize());
    }

    public Vector3 getPlotPosition(final int x, final int y, final int z) {
        return new Vector3(x * this.getProvider().getGeneratorInfo().getTotalSize(), y, z * this.getProvider().getGeneratorInfo().getTotalSize()).add(2, 0, 2);
    }

    public Vector3 getMiddle(final int x, final int z) {
        return getPosition(x, z, 65).add((float) this.getProvider().getGeneratorInfo().getPlotSize() / 2, 0, (float) this.getProvider().getGeneratorInfo().getPlotSize() / 2).add(-2, 0, -2);
    }

    public Vector3 getChunkPosition(int x, int z) {
        return new Vector3(x << 4, 100, z << 4);
    }

    public Plot getPlotByPosition(final Vector3 position) {
        int X = position.getX() >= 0 ? (int) Math.floor(position.getX() / this.getProvider().getGeneratorInfo().getTotalSize()) : (int) Math.ceil((position.getX() - this.getProvider().getGeneratorInfo().getPlotSize() + 1) / this.getProvider().getGeneratorInfo().getTotalSize());//(int) position.getX() / this.getProvider().getGeneratorInfo().getTotalSize();
        int Z = position.getZ() >= 0 ? (int) Math.floor(position.getZ() / this.getProvider().getGeneratorInfo().getTotalSize()) : (int) Math.ceil((position.getZ() - this.getProvider().getGeneratorInfo().getPlotSize() + 1) / this.getProvider().getGeneratorInfo().getTotalSize());//(int) position.getX() / this.getProvider().getGeneratorInfo().getTotalSize();

        final double difX = position.getX() >= 0 ? Math.floor(position.getX() % this.getProvider().getGeneratorInfo().getTotalSize()) : Math.abs((position.getX() - this.getProvider().getGeneratorInfo().getPlotSize() + 1) % this.getProvider().getGeneratorInfo().getTotalSize());
        final double difZ = position.getZ() >= 0 ? Math.floor(position.getZ()) % this.getProvider().getGeneratorInfo().getTotalSize() : Math.abs((position.getZ() - this.getProvider().getGeneratorInfo().getPlotSize() + 1) % this.getProvider().getGeneratorInfo().getTotalSize());

        if (difX >= this.getProvider().getGeneratorInfo().getPlotSize() - 1 || difZ >= this.getProvider().getGeneratorInfo().getPlotSize() - 1 || difX == 0 || difZ == 0) {
            return null;
        } else return this.getPlot(X, Z);

    }

    public Plot getPlot(final int x, final int z) {
        // TODO: Plot mit Datenbank verbinden
        return new Plot(x, z, false);
    }

    @RequiredArgsConstructor
    public static class Provider {

        private final QuadPlots plugin;
        //private final MongoClientURI uri;
        private MongoClient client;
        private Config plotWorld;

        public void connect() {
            //this.client = new MongoClient(uri);
            this.plotWorld = new Config(this.plugin.getDataFolder() + "/data/generator.yml");
            this.plotWorld.save();
            this.plotWorld.reload();
        }

        public void updatePlot(Plot plot) {
            // todo
        }

        public void unclaimPlot(Plot plot) {

        }

        public PlotGeneratorInfo getGeneratorInfo() {
            return new PlotGeneratorInfo(
                    "Plots",
                    64,
                    32,
                    4,
                    new int[]{2, 0},
                    new int[]{44, 0},
                    new int[]{155, 0},
                    new int[]{1, 0}
            );
        }

    }

}
