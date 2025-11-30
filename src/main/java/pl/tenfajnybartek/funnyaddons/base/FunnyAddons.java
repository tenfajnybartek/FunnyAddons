package pl.tenfajnybartek.funnyaddons.base;

import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.guild.GuildManager;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tenfajnybartek.funnyaddons.commands.BuyCoordsCommand;
import pl.tenfajnybartek.funnyaddons.commands.FreeSpaceCommand;
import pl.tenfajnybartek.funnyaddons.commands.PermissionsCommand;
import pl.tenfajnybartek.funnyaddons.commands.ReloadConfigCommand;
import pl.tenfajnybartek.funnyaddons.listeners.GuildTerrainBarJoinListener;
import pl.tenfajnybartek.funnyaddons.listeners.PlayerPositionHandler;
import pl.tenfajnybartek.funnyaddons.listeners.PermissionsEnforceListener;
import pl.tenfajnybartek.funnyaddons.listeners.PermissionsGuiListener;
import pl.tenfajnybartek.funnyaddons.managers.ConfigManager;
import pl.tenfajnybartek.funnyaddons.bossbar.BossBarManager;
import pl.tenfajnybartek.funnyaddons.bossbar.BossBarHandler;
import pl.tenfajnybartek.funnyaddons.managers.PermissionsManager;
import pl.tenfajnybartek.funnyaddons.managers.PlayerPositionManager;
import pl.tenfajnybartek.funnyaddons.tasks.GenerateCoordinatesTask;
import pl.tenfajnybartek.funnyaddons.utils.GuildTerrainBarRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class FunnyAddons extends JavaPlugin {

    private static FunnyAddons instance;

    private ConfigManager configManager;
    private GuildManager guildManager;
    private PermissionsManager permissionsManager;
    private final List<Location> locationList = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        logInfo("Włączanie FunnyAddons...");

        initConfig();
        initPermissions();
        if (!initFunnyGuilds()) return;
        initCommands();
        initAsyncTasks();
        initBossBarAndEvents();
    }

    @Override
    public void onDisable() {
        logInfo("Wyłączanie FunnyAddons...");
    }

    private void initConfig() {
        configManager = new ConfigManager(this);
    }

    private void initPermissions() {
        // inicjalizujemy manager uprawnień i rejestrujemy listenery związane z GUI/egzekwowaniem uprawnień
        permissionsManager = new PermissionsManager(this);

        // rejestrujemy listener obsługujący GUI kliknięcia i listener egzekwujący uprawnienia
        getServer().getPluginManager().registerEvents(new PermissionsGuiListener(permissionsManager), this);
        getServer().getPluginManager().registerEvents(new PermissionsEnforceListener(permissionsManager), this);
    }

    private boolean initFunnyGuilds() {
        FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
        if (funnyGuilds == null) {
            logError("Nie znaleziono FunnyGuilds! Wyłączam plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        logInfo("FunnyGuilds znaleziony!");

        guildManager = funnyGuilds.getGuildManager();
        if (guildManager == null) {
            logError("Nie można pobrać GuildManager z FunnyGuilds! Wyłączam plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        logInfo("GuildManager znaleziony!");
        return true;
    }

    private void initCommands() {
        Objects.requireNonNull(getCommand("wolnemiejsce")).setExecutor(new FreeSpaceCommand(this));
        Objects.requireNonNull(getCommand("kupkordy")).setExecutor(new BuyCoordsCommand(configManager, guildManager));
        Objects.requireNonNull(getCommand("fgaddonsreload")).setExecutor(new ReloadConfigCommand(configManager));
        // PermissionsCommand wymaga instancji pluginu i permissionsManager - upewnij się, że permissionsManager został zainicjalizowany
        Objects.requireNonNull(getCommand("uprawnienia")).setExecutor(new PermissionsCommand(configManager, permissionsManager));
        logInfo("Komendy zarejestrowane.");
    }

    private void initAsyncTasks() {
        getServer().getScheduler().runTaskTimerAsynchronously(
                this,
                new GenerateCoordinatesTask(this),
                0L,
                configManager.getGenerationDelay() * 20L // delay w sekundach
        );
    }

    private void initBossBarAndEvents() {
        FunnyGuilds funnyGuilds = FunnyGuilds.getInstance();
        if (funnyGuilds == null) return;

        if (!funnyGuilds.getPluginConfiguration().regionsEnabled) {
            logError("Regiony w FunnyGuilds są wyłączone – obsługa BossBarów nieaktywna.");
            return;
        }

        PlayerPositionManager playerPositionManager = new PlayerPositionManager();
        BossBarManager bossBarManager = new BossBarManager();

        getServer().getScheduler().runTaskTimerAsynchronously(
                this,
                new GuildTerrainBarRunnable(configManager, playerPositionManager, bossBarManager),
                configManager.getBossBarRunnableTime(),
                configManager.getBossBarRunnableTime()
        );

        getServer().getPluginManager().registerEvents(
                new GuildTerrainBarJoinListener(configManager, playerPositionManager, bossBarManager),
                this
        );
        getServer().getPluginManager().registerEvents(new BossBarHandler(bossBarManager), this);
        getServer().getPluginManager().registerEvents(new PlayerPositionHandler(playerPositionManager), this);

        logInfo("BossBary i obsługa pozycjonowania graczy włączona.");
    }

    private void logInfo(String msg) {
        getLogger().info(msg);
    }

    private void logError(String msg) {
        getLogger().severe(msg);
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    public List<Location> getLocationList() {
        return locationList;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    // statyczny getter przydatny w innych helperach (ViewerUtils itp.)
    public static FunnyAddons getPluginInstance() {
        return instance;
    }
}