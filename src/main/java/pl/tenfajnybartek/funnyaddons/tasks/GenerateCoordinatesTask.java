package pl.tenfajnybartek.funnyaddons.tasks;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import pl.tenfajnybartek.funnyaddons.base.FunnyAddons;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.utils.RandomUtils;

public class GenerateCoordinatesTask implements Runnable {
    private final FunnyAddons addon;
    private final ConfigManager config;

    public GenerateCoordinatesTask(FunnyAddons addon) {
        this.addon = addon;
        this.config = addon.getConfigManager();
        // Usunięto scheduler z konstruktora - task jest uruchamiany w FunnyAddons.initAsyncTasks()
    }

    @Override
    public void run() {
        int x = RandomUtils.getRandomInt(config.getMinBound(), config.getMaxBound(), addon.getLogger());
        int z = RandomUtils.getRandomInt(config.getMinBound(), config.getMaxBound(), addon.getLogger());

        Location location = new Location(Bukkit.getWorlds().get(0), x, 128, z);

        RegionManager regionManager = FunnyGuilds.getInstance().getRegionManager();

        if (!regionManager.isInRegion(location) && !regionManager.isNearRegion(location)) {
            if (addon.getLocationList().size() < config.getListSize()) {
                addon.getLocationList().add(location);
            }
        }
    }
}