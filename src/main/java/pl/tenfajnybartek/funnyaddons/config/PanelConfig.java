package pl.tenfajnybartek.funnyaddons.config;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Configuration facade for the guild panel system.
 * <p>
 * Reads configuration from panel.yml and provides typed access to:
 * - Main panel GUI settings
 * - Territory enlargement levels and costs
 * - Guild validity renewal settings
 * - Guild effects configuration
 */
public class PanelConfig {

    private final FileConfiguration cfg;

    public PanelConfig(FileConfiguration cfg) {
        this.cfg = cfg;
    }

    // ==================== Messages ====================

    public String getMessage(String key) {
        return cfg.getString("messages." + key, "&cBrak wiadomości: " + key);
    }

    public String getOnlyPlayerMessage() {
        return getMessage("only-player");
    }

    public String getNotInGuildMessage() {
        return getMessage("not-in-guild");
    }

    public String getNotLeaderMessage() {
        return getMessage("not-leader");
    }

    public String getNoMoneyMessage() {
        return getMessage("no-money");
    }

    public String getTerritoryUpgradedMessage() {
        return getMessage("territory-upgraded");
    }

    public String getTerritoryMaxLevelMessage() {
        return getMessage("territory-max-level");
    }

    public String getTerritoryAlreadyOwnedMessage() {
        return getMessage("territory-already-owned");
    }

    public String getValidityExtendedMessage() {
        return getMessage("validity-extended");
    }

    public String getEffectPurchasedMessage() {
        return getMessage("effect-purchased");
    }

    public String getEffectAppliedMessage() {
        return getMessage("effect-applied");
    }

    // ==================== Main Panel GUI ====================

    public int getMainGuiSize() {
        return cfg.getInt("panel.main.size", 27);
    }

    public String getMainGuiTitle() {
        return cfg.getString("panel.main.title", "&ePanel Gildii");
    }

    public int getMainItemSlot(String itemKey) {
        return cfg.getInt("panel.main.items." + itemKey + ".slot", 0);
    }

    public Material getMainItemMaterial(String itemKey) {
        String mat = cfg.getString("panel.main.items." + itemKey + ".material", "STONE");
        Material m = Material.matchMaterial(mat);
        return m != null ? m : Material.STONE;
    }

    public String getMainItemName(String itemKey) {
        return cfg.getString("panel.main.items." + itemKey + ".name", "&7Item");
    }

    public List<String> getMainItemLore(String itemKey) {
        return cfg.getStringList("panel.main.items." + itemKey + ".lore");
    }

    // ==================== Territory GUI ====================

    public int getTerritoryGuiSize() {
        return cfg.getInt("panel.territory.size", 27);
    }

    public String getTerritoryGuiTitle() {
        return cfg.getString("panel.territory.title", "&aPowiększenie terenu gildii");
    }

    /**
     * Gets all configured territory levels.
     *
     * @return Map of level number to TerritoryLevel configuration
     */
    public Map<Integer, TerritoryLevel> getTerritoryLevels() {
        Map<Integer, TerritoryLevel> levels = new LinkedHashMap<>();
        ConfigurationSection section = cfg.getConfigurationSection("panel.territory.levels");
        if (section == null) return levels;

        for (String key : section.getKeys(false)) {
            try {
                int levelNum = Integer.parseInt(key);
                ConfigurationSection levelSection = section.getConfigurationSection(key);
                if (levelSection != null) {
                    levels.put(levelNum, new TerritoryLevel(levelNum, levelSection));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return levels;
    }

    public int getTerritoryBackSlot() {
        return cfg.getInt("panel.territory.back.slot", 26);
    }

    public Material getTerritoryBackMaterial() {
        String mat = cfg.getString("panel.territory.back.material", "ARROW");
        Material m = Material.matchMaterial(mat);
        return m != null ? m : Material.ARROW;
    }

    public String getTerritoryBackName() {
        return cfg.getString("panel.territory.back.name", "&cPowrót");
    }

    public List<String> getTerritoryBackLore() {
        return cfg.getStringList("panel.territory.back.lore");
    }

    // ==================== Renew Settings ====================

    public long getRenewDurationSeconds() {
        return cfg.getLong("panel.renew.duration-seconds", 604800L);
    }

    public Map<Material, Integer> getRenewCost() {
        return parseCostSection("panel.renew.cost");
    }

    // ==================== Effects GUI ====================

    public int getEffectsGuiSize() {
        return cfg.getInt("panel.effects.size", 27);
    }

    public String getEffectsGuiTitle() {
        return cfg.getString("panel.effects.title", "&dEfekty gildii");
    }

    /**
     * Gets all configured effect options.
     *
     * @return Map of effect key to EffectOption configuration
     */
    public Map<String, EffectOption> getEffectOptions() {
        Map<String, EffectOption> options = new LinkedHashMap<>();
        ConfigurationSection section = cfg.getConfigurationSection("panel.effects.options");
        if (section == null) return options;

        for (String key : section.getKeys(false)) {
            ConfigurationSection optSection = section.getConfigurationSection(key);
            if (optSection != null) {
                options.put(key, new EffectOption(key, optSection));
            }
        }
        return options;
    }

    public int getEffectsBackSlot() {
        return cfg.getInt("panel.effects.back.slot", 26);
    }

    public Material getEffectsBackMaterial() {
        String mat = cfg.getString("panel.effects.back.material", "ARROW");
        Material m = Material.matchMaterial(mat);
        return m != null ? m : Material.ARROW;
    }

    public String getEffectsBackName() {
        return cfg.getString("panel.effects.back.name", "&cPowrót");
    }

    public List<String> getEffectsBackLore() {
        return cfg.getStringList("panel.effects.back.lore");
    }

    // ==================== Helper Methods ====================

    private Map<Material, Integer> parseCostSection(String path) {
        Map<Material, Integer> cost = new LinkedHashMap<>();
        ConfigurationSection section = cfg.getConfigurationSection(path);
        if (section == null) return cost;

        for (String key : section.getKeys(false)) {
            Material mat = Material.matchMaterial(key);
            if (mat != null) {
                cost.put(mat, section.getInt(key, 1));
            }
        }
        return cost;
    }

    // ==================== Inner Classes ====================

    /**
     * Represents a territory level configuration.
     */
    public static class TerritoryLevel {
        private final int level;
        private final int bounds;
        private final Material activeMaterial;
        private final Material inactiveMaterial;
        private final String name;
        private final List<String> loreActive;
        private final List<String> loreInactive;
        private final int slot;
        private final Map<Material, Integer> cost;

        public TerritoryLevel(int level, ConfigurationSection section) {
            this.level = level;
            this.bounds = section.getInt("bounds", 50);

            String activeMat = section.getString("active-material", "GREEN_CONCRETE");
            Material am = Material.matchMaterial(activeMat);
            this.activeMaterial = am != null ? am : Material.GREEN_CONCRETE;

            String inactiveMat = section.getString("inactive-material", "RED_CONCRETE");
            Material im = Material.matchMaterial(inactiveMat);
            this.inactiveMaterial = im != null ? im : Material.RED_CONCRETE;

            this.name = section.getString("name", "&aPoziom " + level);
            this.loreActive = section.getStringList("lore-active");
            this.loreInactive = section.getStringList("lore-inactive");
            this.slot = section.getInt("slot", 10 + level - 1);

            this.cost = new LinkedHashMap<>();
            ConfigurationSection costSection = section.getConfigurationSection("cost");
            if (costSection != null) {
                for (String key : costSection.getKeys(false)) {
                    Material mat = Material.matchMaterial(key);
                    if (mat != null) {
                        this.cost.put(mat, costSection.getInt(key, 1));
                    }
                }
            }
        }

        public int getLevel() {
            return level;
        }

        public int getBounds() {
            return bounds;
        }

        public Material getActiveMaterial() {
            return activeMaterial;
        }

        public Material getInactiveMaterial() {
            return inactiveMaterial;
        }

        public String getName() {
            return name;
        }

        public List<String> getLoreActive() {
            return loreActive;
        }

        public List<String> getLoreInactive() {
            return loreInactive;
        }

        public int getSlot() {
            return slot;
        }

        public Map<Material, Integer> getCost() {
            return cost;
        }

        public boolean hasCost() {
            return !cost.isEmpty();
        }
    }

    /**
     * Represents an effect option configuration.
     */
    public static class EffectOption {
        private final String key;
        private final int slot;
        private final Material material;
        private final String name;
        private final List<String> lore;
        private final PotionEffectType effectType;
        private final int amplifier;
        private final int durationSeconds;
        private final Map<Material, Integer> cost;

        public EffectOption(String key, ConfigurationSection section) {
            this.key = key;
            this.slot = section.getInt("slot", 0);

            String mat = section.getString("material", "POTION");
            Material m = Material.matchMaterial(mat);
            this.material = m != null ? m : Material.POTION;

            this.name = section.getString("name", "&7Effect");
            this.lore = section.getStringList("lore");

            String effectName = section.getString("effect", "SPEED");
            this.effectType = parsePotionEffectType(effectName);

            this.amplifier = section.getInt("amplifier", 0);
            this.durationSeconds = section.getInt("duration-seconds", 3600);

            this.cost = new LinkedHashMap<>();
            ConfigurationSection costSection = section.getConfigurationSection("cost");
            if (costSection != null) {
                for (String costKey : costSection.getKeys(false)) {
                    Material costMat = Material.matchMaterial(costKey);
                    if (costMat != null) {
                        this.cost.put(costMat, costSection.getInt(costKey, 1));
                    }
                }
            }
        }

        private PotionEffectType parsePotionEffectType(String name) {
            if (name == null) return PotionEffectType.SPEED;

            // Try direct registry lookup first
            PotionEffectType type = org.bukkit.Registry.POTION_EFFECT_TYPE.get(
                    org.bukkit.NamespacedKey.minecraft(name.toLowerCase())
            );
            if (type != null) return type;

            // Map legacy names to new names
            return switch (name.toUpperCase()) {
                case "STRENGTH", "INCREASE_DAMAGE" -> PotionEffectType.STRENGTH;
                case "SPEED" -> PotionEffectType.SPEED;
                case "HASTE", "FAST_DIGGING" -> PotionEffectType.HASTE;
                case "REGENERATION" -> PotionEffectType.REGENERATION;
                case "RESISTANCE", "DAMAGE_RESISTANCE" -> PotionEffectType.RESISTANCE;
                case "FIRE_RESISTANCE" -> PotionEffectType.FIRE_RESISTANCE;
                case "WATER_BREATHING" -> PotionEffectType.WATER_BREATHING;
                case "INVISIBILITY" -> PotionEffectType.INVISIBILITY;
                case "NIGHT_VISION" -> PotionEffectType.NIGHT_VISION;
                case "JUMP_BOOST", "JUMP" -> PotionEffectType.JUMP_BOOST;
                case "SLOW_FALLING" -> PotionEffectType.SLOW_FALLING;
                case "ABSORPTION" -> PotionEffectType.ABSORPTION;
                default -> PotionEffectType.SPEED;
            };
        }

        public String getKey() {
            return key;
        }

        public int getSlot() {
            return slot;
        }

        public Material getMaterial() {
            return material;
        }

        public String getName() {
            return name;
        }

        public List<String> getLore() {
            return lore;
        }

        public PotionEffectType getEffectType() {
            return effectType;
        }

        public int getAmplifier() {
            return amplifier;
        }

        public int getDurationSeconds() {
            return durationSeconds;
        }

        public int getDurationTicks() {
            return durationSeconds * 20;
        }

        public Map<Material, Integer> getCost() {
            return cost;
        }
    }
}
