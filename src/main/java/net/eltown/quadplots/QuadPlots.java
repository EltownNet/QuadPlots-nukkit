package net.eltown.quadplots;

import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import lombok.Getter;
import net.eltown.quadplots.commands.RootCommand;
import net.eltown.quadplots.components.api.Api;
import net.eltown.quadplots.components.forms.FormListener;
import net.eltown.quadplots.components.generator.PlotGenerator;
import net.eltown.quadplots.components.language.Language;
import net.eltown.quadplots.components.listener.PlayerListener;

import java.util.HashMap;

public class QuadPlots extends PluginBase {

    @Getter
    private static Api api;

    @Override
    public void onLoad() {
        this.saveDefaultConfig();
        api = new Api(this);
        api.init();
        Language.init(this);
        Generator.addGenerator(PlotGenerator.class, "Plots", Generator.TYPE_INFINITE);
    }

    @Override
    public void onEnable() {
        this.getServer().getCommandMap().register("plot", new RootCommand(this));
        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new FormListener(), this);
    }

    public void generate() {
        getServer().generateLevel("plots", 0, Generator.getGenerator("Plots"), new HashMap<>());
    }

}
