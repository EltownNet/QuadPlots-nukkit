package net.eltown.quadplots.components.api;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.eltown.quadplots.QuadPlots;
import net.eltown.quadplots.commands.SubCommandHandler;
import net.eltown.quadplots.components.data.Plot;
import net.eltown.quadplots.components.data.PlotGeneratorInfo;
import net.eltown.quadplots.components.data.Road;
import net.eltown.quadplots.components.math.Direction;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class Api {

    @Getter
    private final SubCommandHandler commandHandler;
    private final QuadPlots plugin;
    @Getter
    private Provider provider;
    public final int defaultPlots = 2;
    private final Set<String> managers = new HashSet<>();


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

    public boolean toggleManage(final String player) {
        if (managers.contains(player)) {
            managers.remove(player);
            return false;
        } else {
            managers.add(player);
            return true;
        }
    }

    public boolean isManager(final String player) {
        return managers.contains(player);
    }

    public Road getRoad(final Position position) {
        // -x -y

        final Vector3 xSide = new Vector3(position.x, position.y, position.z);
        final Vector3 ySide = new Vector3(position.x, position.y, position.z);

        for (int i = 0; i < 7; i++) {
            final Plot xPlot = this.getPlotByPosition(xSide.add(i), false);
            final Plot yPlot = this.getPlotByPosition(ySide.add(0, 0, i), false);

            if (xPlot != null) return new Road(xPlot, Direction.WEST);
            if (yPlot != null) return new Road(yPlot, Direction.NORTH);
        }

        return null;
    }

    public int getMaxPlots(final Player player) {
        if (player.isOp()) return Integer.MAX_VALUE;
        final AtomicInteger plots = new AtomicInteger(this.defaultPlots);

        player.getEffectivePermissions().keySet().forEach((perm) -> {
            if (perm.startsWith("plots.claim.")) {
                String max = perm.replace("plots.claim.", "");
                if (max.equalsIgnoreCase("unlimited")) {
                    plots.set(Integer.MAX_VALUE);
                } else {
                    try {
                        final int num = Integer.parseInt(max);
                        if (num > plots.get()) plots.set(num);
                    } catch (NumberFormatException ex) {
                    }
                }
            }
        });

        return plots.get();
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
        return getPosition(x, z, 65).add((float) this.getProvider().getGeneratorInfo().getPlotSize() / 2, 0, (float) this.getProvider().getGeneratorInfo().getPlotSize() / 2)/*.add(-2, 0, -2)*/;
    }

    public Vector3 getChunkPosition(int x, int z) {
        return new Vector3(x << 4, 100, z << 4);
    }

    public Plot getPlotByPosition(final Vector3 position) {
        return this.getPlotByPosition(position, true);
    }

    public Plot getPlotByPosition(final Vector3 position, final boolean checkOrigin) {
        int X = position.getX() >= 0 ? (int) Math.floor(position.getX() / this.getProvider().getGeneratorInfo().getTotalSize()) : (int) Math.ceil((position.getX() - this.getProvider().getGeneratorInfo().getPlotSize() + 1) / this.getProvider().getGeneratorInfo().getTotalSize());//(int) position.getX() / this.getProvider().getGeneratorInfo().getTotalSize();
        int Z = position.getZ() >= 0 ? (int) Math.floor(position.getZ() / this.getProvider().getGeneratorInfo().getTotalSize()) : (int) Math.ceil((position.getZ() - this.getProvider().getGeneratorInfo().getPlotSize() + 1) / this.getProvider().getGeneratorInfo().getTotalSize());//(int) position.getX() / this.getProvider().getGeneratorInfo().getTotalSize();

        final double difX = position.getX() >= 0 ? Math.floor(position.getX() % this.getProvider().getGeneratorInfo().getTotalSize()) : Math.abs((position.getX() - this.getProvider().getGeneratorInfo().getPlotSize() + 1) % this.getProvider().getGeneratorInfo().getTotalSize());
        final double difZ = position.getZ() >= 0 ? Math.floor(position.getZ()) % this.getProvider().getGeneratorInfo().getTotalSize() : Math.abs((position.getZ() - this.getProvider().getGeneratorInfo().getPlotSize() + 1) % this.getProvider().getGeneratorInfo().getTotalSize());

        if (difX >= this.getProvider().getGeneratorInfo().getPlotSize() - 1 || difZ >= this.getProvider().getGeneratorInfo().getPlotSize() - 1 || difX == 0 || difZ == 0) {
            return null;
        } else return this.getPlot(X, Z, checkOrigin);

    }

    public Plot getPlot(final int x, final int z) {
        return this.getPlot(x, z, true);
    }

    public Plot getPlot(final int x, final int z, final boolean checkOrigin) {
        return this.getProvider().getPlot(x, z, checkOrigin);
    }

    @RequiredArgsConstructor
    public static class Provider {

        private final QuadPlots plugin;
        private final MongoClientURI uri;
        private final String database, collection;
        private MongoClient client;
        private MongoCollection<Document> coll;
        private Config plotWorld;
        private final LinkedHashMap<String, Plot> plots = new LinkedHashMap<>();

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
                                doc.getList("owners", String.class),
                                doc.getList("trusted", String.class),
                                doc.getList("helpers", String.class),
                                doc.getList("flags", String.class),
                                doc.getList("banned", String.class))
                );
            }
            this.plugin.getLogger().info("§a" + this.plots.size() + " Plots wurden in den Cache geladen.");
        }

        public Plot getPlot(int x, int z, boolean checkOrigin) {
            final String id = x + ";" + z;
            if (plots.containsKey(id)) {
                Plot plot = plots.get(id);
                if (checkOrigin && plot.isMerged() && !plot.isOrigin()) plot = plot.getOrigin();
                return plot;
            } else return new Plot(x, z, false);
            //return plots.containsKey(id) ? plots.get(id) : new Plot(x, z, false);
        }

        public int getPlotAmount(final String player) {
            final AtomicInteger amount = new AtomicInteger(0);
            this.plots.values().forEach((p) -> {
                if (p.isOwner(player)) amount.set(amount.get() + 1);
            });
            return amount.get();
        }

        public LinkedList<Plot> getPlots(String player) {
            final LinkedList<Plot> plots = new LinkedList<>();

            for (final Plot plot : this.plots.values()) {
                if (plot.isOwner(player)) plots.add(plot);
            }

            return plots;
        }

        public LinkedList<Plot> getPlotsFiltered(String player) {
            final LinkedList<Plot> plots = new LinkedList<>(this.getPlots(player));
            plots.removeIf(p -> p.isMerged() && !p.isOrigin());
            return plots;
        }

        public void updatePlot(Plot plot) {
            CompletableFuture.runAsync(() -> {
                final Document doc = this.coll.find(new Document("_id", plot.getStringId())).first();
                if (doc != null) {
                    try {
                        this.coll.updateOne(new Document("_id", plot.getStringId()),
                                new Document("$set",
                                        new Document("owners", new ArrayList<>(plot.getOwners()))
                                                .append("trusted", new ArrayList<>(plot.getTrusted()))
                                                .append("helpers", new ArrayList<>(plot.getHelpers()))
                                                .append("banned", new ArrayList<>(plot.getBanned()))
                                                .append("flags", new ArrayList<>(plot.getFlags()))));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    this.coll.insertOne(
                            new Document("_id", plot.getStringId())
                                    .append("owners", plot.getOwners())
                                    .append("trusted", new ArrayList<String>())
                                    .append("helpers", new ArrayList<String>())
                                    .append("banned", new ArrayList<String>())
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
                    "plots",
                    64,
                    52,
                    4,
                    new int[]{2, 0},
                    new int[]{44, 0},
                    new int[]{155, 0},
                    new int[]{1, 0},
                    new int[]{44, 1}
            );
        }

        public Plot findFreePlot(int amplifierX, int amplifierZ) {
            if (plots.size() == 0) return getPlot(0, 0, false);

            // ToDo: Derzeit geht es nur wenn das erste Plot bei X: 0 und Z: 0 ist.

            int lastX = 0;
            int lastZ = 0;

            for (Plot plot : plots.values()) {

                int x = plot.getX() - lastX;
                int y = plot.getZ() - lastZ;
                int diff = Math.abs(x * y);

                if (diff < 4) {
                    lastX = plot.getX();
                    lastZ = plot.getZ();
                    // - |
                    Plot cb = getPlot(plot.getX() + 1, plot.getZ(), false);
                    if (!cb.isClaimed()) return cb;

                    cb = getPlot(plot.getX(), plot.getZ() + 1, false);
                    if (!cb.isClaimed()) return cb;

                    cb = getPlot(plot.getX() - 1, plot.getZ(), false);
                    if (!cb.isClaimed()) return cb;

                    cb = getPlot(plot.getX(), plot.getZ() - 1, false);
                    if (!cb.isClaimed()) return cb;

                    // / \
                    cb = getPlot(plot.getX() + 1 + amplifierX, plot.getZ() - 1 + amplifierZ, false);
                    if (!cb.isClaimed()) return cb;

                    cb = getPlot(plot.getX() - 1 + amplifierX, plot.getZ() + 1 + amplifierZ, false);
                    if (!cb.isClaimed()) return cb;

                    cb = getPlot(plot.getX() - 1 + amplifierX, plot.getZ() - 1 + amplifierZ, false);
                    if (!cb.isClaimed()) return cb;

                    cb = getPlot(plot.getX() + 1 + amplifierX, plot.getZ() + 1 + amplifierZ, false);
                    if (!cb.isClaimed()) return cb;
                }
            }

            amplifierX += 1;
            amplifierZ -= 1;

            return findFreePlot(amplifierX, amplifierZ);
        }

    }

}
