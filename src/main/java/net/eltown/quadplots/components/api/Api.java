package net.eltown.quadplots.components.api;

import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.SubCommandHandler;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
        final Config c = this.plugin.getConfig();

        this.provider = new Provider(this.plugin,
                new MongoClientURI(c.getString("MongoDb.Uri")),
                c.getString("MongoDb.Database"),
                c.getString("MongoDb.Collection")
        );

        this.provider.connect();
    }

    public Vector3 getPosition(final int x, final int z, final int y) {
        return new Vector3(x * this.getProvider().getGeneratorInfo().getTotalSize(), y, z * this.getProvider().getGeneratorInfo().getTotalSize());
    }

    public Vector3 getPosition(final int x, final int z) {
        return new Vector3(x * this.getProvider().getGeneratorInfo().getTotalSize(), this.getProvider().getGeneratorInfo().getHeight(), z * this.getProvider().getGeneratorInfo().getTotalSize());
    }

    public Vector3 getPlotPosition(final int x, final int y, final int z) {
        return new Vector3(x * this.getProvider().getGeneratorInfo().getTotalSize(), y, z * this.getProvider().getGeneratorInfo().getTotalSize()).add(2, 0, 2);
    }

    public Vector3 getPlotPosition(final int x, final int z) {
        return new Vector3(x * this.getProvider().getGeneratorInfo().getTotalSize(), this.getProvider().getGeneratorInfo().getHeight(), z * this.getProvider().getGeneratorInfo().getTotalSize()).add(2, 0, 2);
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
        return this.getProvider().getPlot(x, z);
    }

    @RequiredArgsConstructor
    public static class Provider {

        private final QuadPlots plugin;
        private final MongoClientURI uri;
        private final String database, collection;
        private MongoClient client;
        private MongoCollection<Document> coll;
        private Config plotWorld;
        private final Map<String, Plot> plots = new HashMap<>();

        public void connect() {
            this.plugin.getLogger().info("§eVerbindung zur Datenbank wird hergestellt...");
            this.client = new MongoClient(uri);
            this.coll = this.client.getDatabase(database).getCollection(collection);

            this.plotWorld = new Config(this.plugin.getDataFolder() + "/data/generator.yml");
            this.plotWorld.save();
            this.plotWorld.reload();
            this.plugin.getLogger().info("§aErfolgreich mit der Datenbank verbunden.");

            this.plugin.getLogger().info("§ePlots werden in den Cache geladen...");
            for (Document doc : this.coll.find()) {
                final String id = doc.getString("_id");
                final int x = Integer.parseInt(id.split(";")[0]);
                final int z = Integer.parseInt(id.split(";")[1]);

                this.plots.put(
                        doc.getString("_id"),
                        new Plot(x, z, true,
                                doc.getString("owner"),
                                doc.getList("members", String.class),
                                doc.getList("helpers", String.class),
                                doc.getList("flags", String.class))
                );
            }
            this.plugin.getLogger().info("§a" + this.plots.size() + " Plots wurden in den Cache geladen.");
        }

        public Plot getPlot(int x, int z) {
            final String id = x + ";" + z;
            return plots.containsKey(id) ? plots.get(id) : new Plot(x, z, false);
        }

        public void updatePlot(Plot plot) {
            CompletableFuture.runAsync(() -> {
                final Document doc = this.coll.find(new Document("_id", plot.getStringId())).first();
                if (doc != null) {
                    this.coll.updateOne(new Document("_id", plot.getStringId()),
                            new Document("$set", new Document("owner", plot.getOwner()))
                                    .append("members", plot.getMembers())
                                    .append("helpers", plot.getHelpers())
                                    .append("flags", plot.getFlags())
                    );
                } else {
                    this.coll.insertOne(
                            new Document("_id", plot.getStringId())
                                    .append("owner", plot.getOwner())
                                    .append("members", new ArrayList<String>())
                                    .append("helpers", new ArrayList<String>())
                                    .append("flags", new ArrayList<String>())
                    );
                }
            });
            this.plots.put(plot.getStringId(), plot);
        }

        public void unclaimPlot(Plot plot) {
            CompletableFuture.runAsync(() -> this.coll.deleteOne(new Document("_id", plot.getStringId())));
            this.plots.remove(plot.getStringId());
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
