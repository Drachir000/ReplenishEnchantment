package de.drachir000.survival.replenishenchantment.api;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;

public class AnvilUtils {

    private final JSONObject multipliers = new JSONObject();
    private final Plugin plugin;


    public AnvilUtils(Plugin plugin, int itemValue, int bookValue) {
        loadMultipliers(itemValue, bookValue);
        this.plugin = plugin;
    }

    public boolean registerEnchantment(String key, int itemValue, int bookValue) {
        boolean wasRegistered = multipliers.has(key);
        multipliers.put(key,
                new JSONObject()
                        .put("item", itemValue)
                        .put("book", bookValue)
        );
        return wasRegistered;
    }

    private void loadMultipliers(int itemValue, int bookValue) {
        for (Enchantment enchantment : Enchantment.values()) {
            String key = enchantment.getKey().getKey();
            int item = 0;
            int book = 0;
            switch (key) {
                case "impaling", "fire_protection", "smite", "unbreaking", "feather_falling", "knockback", "projectile_protection", "bane_of_arthropods", "quick_charge" -> {
                    item = 2;
                    book = 1;
                }
                case "thorns", "swift_sneak", "channeling", "soul_speed", "silk_touch", "binding_curse", "vanishing_curse", "infinity" -> {
                    item = 8;
                    book = 4;
                }
                case "piercing", "sharpness", "power", "efficiency", "loyalty" -> {
                    item = 1;
                    book = 1;
                }
                case "mending", "protection", "respiration", "fire_aspect", "luck_of_the_sea", "lure", "punch", "frost_walker", "riptide", "fortune", "looting", "aqua_affinity", "multishot", "depth_strider", "flame", "blast_protection", "sweeping" -> {
                    item = 4;
                    book = 2;
                }
                case "replenish" -> {
                    item = itemValue;
                    book = bookValue;
                }
                default -> {
                        plugin.getLogger().log(Level.WARNING, "Could not identify Enchantment \"" + key + "\"! Setting level cost to 0...");
                        plugin.getLogger().log(Level.WARNING, "Please add the multipliers for this Enchantment to the config.yml!");
                }
            }
            registerEnchantment(key, item, book);
        }
    }

    private boolean isApplicable(@NotNull Enchantment enchantment, ItemStack item) {
        return (item.getType() == Material.ENCHANTED_BOOK || enchantment.canEnchantItem(item));
    }

    private @NotNull List<Enchantment> getConflicts(Enchantment enchantment, @NotNull ItemStack item) {
        List<Enchantment> conflicts = new ArrayList<>();
        for (Enchantment ench : getEnchantments(item).keySet()) {
            if (!enchantment.equals(ench) && enchantment.conflictsWith(ench))
                conflicts.add(ench);
        }
        return conflicts;
    }

    private @NotNull List<Enchantment> getCompatible(@NotNull List<Enchantment> enchantments, ItemStack target) {
        List<Enchantment> result = new ArrayList<>();

        for (Enchantment enchantment : enchantments) {
            if (isApplicable(enchantment, target)) {
                boolean compatible = true;
                for (Enchantment targetEnchantment : getEnchantments(target).keySet()) {
                    if (targetEnchantment.equals(enchantment))
                        continue;
                    if (enchantment.conflictsWith(targetEnchantment)) {
                        compatible = false;
                        break;
                    }
                }
                if (compatible)
                    result.add(enchantment);
            }
        }

        return result;
    }

    private int getMultiplier(@NotNull Enchantment enchantment, boolean book) {
        JSONObject obj = multipliers.getJSONObject(enchantment.getKey().getKey());
        try {
            return (book ? obj.getInt("book") : obj.getInt("item"));
        } catch (JSONException e) {
            plugin.getLogger().log(Level.WARNING, "Could not identify Enchantment \"" + enchantment.getKey().getKey() + "\"! Setting level cost to 0...");
            plugin.getLogger().log(Level.WARNING, "Please add the multipliers for this Enchantment to the config.yml!");
            return 0;
        }
    }

    private int getPenalty(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Repairable repairable) {
            return (repairable.hasRepairCost() ? repairable.getRepairCost() : 0);
        }
        return 0;
    }

    private void setPenalty(ItemStack item, int penalty) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Repairable repairable) {
            repairable.setRepairCost((int) Math.pow(2, penalty) - 1);
            item.setItemMeta((ItemMeta) repairable);
        }
    }

    private void addEnchantment(ItemStack item, Enchantment enchantment, int level) {
        if (item.getItemMeta() instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            if (enchantmentStorageMeta.hasStoredEnchant(enchantment))
                enchantmentStorageMeta.removeEnchant(enchantment);
            enchantmentStorageMeta.addStoredEnchant(enchantment, level, false);
            item.setItemMeta(enchantmentStorageMeta);
        } else {
            if (item.getItemMeta().hasEnchant(enchantment))
                item.removeEnchantment(enchantment);
            item.addEnchantment(enchantment, level);
        }
    }

    private Map<Enchantment, Integer> getEnchantments(ItemStack item) {
        if (item.getItemMeta() instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
            return enchantmentStorageMeta.getStoredEnchants();
        }
        return item.getEnchantments();
    }

    private boolean canRepair(ItemStack a, ItemStack b) {
        if (a.getType() == b.getType())
            return true;
        return switch (a.getType()) {
            case OAK_PLANKS, ACACIA_PLANKS, BIRCH_PLANKS, CRIMSON_PLANKS, DARK_OAK_PLANKS, JUNGLE_PLANKS, SPRUCE_PLANKS, WARPED_PLANKS ->
                switch (b.getType()) {
                    case WOODEN_SWORD, WOODEN_PICKAXE, WOODEN_AXE, WOODEN_SHOVEL, WOODEN_HOE, SHIELD -> true;
                    default -> false;
                };
            case LEATHER ->
                switch (b.getType()) {
                    case LEATHER_BOOTS, LEATHER_CHESTPLATE, LEATHER_HELMET, LEATHER_LEGGINGS -> true;
                    default -> false;
                };
            case COBBLESTONE, COBBLED_DEEPSLATE, BLACKSTONE ->
                switch (b.getType()) {
                    case STONE_SWORD, STONE_PICKAXE, STONE_AXE, STONE_SHOVEL, STONE_HOE -> true;
                    default -> false;
                };
            case IRON_INGOT ->
                switch (b.getType()) {
                    case IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS, CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS,
                            IRON_SWORD, IRON_PICKAXE, IRON_AXE, IRON_SHOVEL, IRON_HOE -> true;
                    default -> false;
                };
            case GOLD_INGOT ->
                switch (b.getType()) {
                    case GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS, GOLDEN_SWORD, GOLDEN_PICKAXE, GOLDEN_AXE, GOLDEN_SHOVEL, GOLDEN_HOE -> true;
                    default -> false;
                };
            case DIAMOND ->
                switch (b.getType()) {
                    case DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS, DIAMOND_SWORD, DIAMOND_PICKAXE, DIAMOND_AXE, DIAMOND_SHOVEL, DIAMOND_HOE -> true;
                    default -> false;
                };
            case NETHERITE_INGOT ->
                switch (b.getType()) {
                    case NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS, NETHERITE_SWORD, NETHERITE_PICKAXE, NETHERITE_AXE, NETHERITE_SHOVEL, NETHERITE_HOE -> true;
                    default -> false;
                };
            case SCUTE -> b.getType() == Material.TURTLE_HELMET;
            case PHANTOM_MEMBRANE -> b.getType() == Material.ELYTRA;
            default -> false;
        };
    }

    public AnvilResult getResult(ItemStack leftItem, ItemStack rightItem, String name) {

        boolean rename = (name != null && !name.equals(""));

        if (leftItem == null || leftItem.getType().isAir())
            return new EmptyAnvilResult();

        if (!rename && (rightItem == null || rightItem.getType().isAir()))
            return new EmptyAnvilResult();

        if (!(rightItem.getType() == Material.ENCHANTED_BOOK || (leftItem.getType() == rightItem.getType())))
            return new EmptyAnvilResult();

        ItemStack resultItem = leftItem.clone();
        if (rename) {
            ItemMeta meta = resultItem.getItemMeta();
            meta.setDisplayName(name);
            resultItem.setItemMeta(meta);
        }

        int cost = getPenalty(leftItem) + getPenalty(rightItem);

        if (rename) {
            cost++;
        }

        if (!(rightItem == null || rightItem.getType().isAir())) {

            Map<Enchantment, Integer> rightEnchantments = getEnchantments(rightItem);

            if (canRepair(rightItem, resultItem) && rightItem.getType() != resultItem.getType()) {

                Damageable resMeta = (Damageable) resultItem.getItemMeta();
                if (resMeta.hasDamage()) {
                    int maxDurability = resultItem.getType().getMaxDurability(),
                        damage = resMeta.getDamage(),
                        unitsNeeded = (int) Math.ceil(damage / ((double) maxDurability / 4));
                    cost += Math.min(unitsNeeded, rightItem.getAmount());
                    if (rightItem.getAmount() < unitsNeeded) {
                        int newDamage = resMeta.getDamage() - (rightItem.getAmount() * (maxDurability / 4));
                        if (newDamage < 0)
                            newDamage = 0;
                        resMeta.setDamage(newDamage);
                    } else {
                        resMeta.setDamage(0);
                    }
                    resultItem.setItemMeta(resMeta);

                    int penalty = Math.max(getPenalty(leftItem), getPenalty(rightItem));
                    int newPenalty = (int) (Math.log(penalty + 1) / Math.log(2)) + 1;
                    if (newPenalty <= 0)
                        newPenalty = 1;
                    setPenalty(resultItem, newPenalty);

                    return new MaterialRepairingAnvilResult(cost, unitsNeeded, resultItem);
                }

            } else {

                if (rightItem.getType() == resultItem.getType() && resultItem.getItemMeta() instanceof Damageable resMeta) {
                    if (resMeta.hasDamage()) {
                        cost += 2;
                        Damageable rightMeta = (Damageable) rightItem.getItemMeta();
                        int resultDamage = resMeta.getDamage(),
                        maxDurability = resultItem.getType().getMaxDurability(),
                        rightDurability = maxDurability - rightMeta.getDamage(),
                        newDamage = resultDamage - rightDurability - (int) Math.floor(0.12 * maxDurability);
                        if (newDamage < 0)
                            newDamage = 0;
                        resMeta.setDamage(newDamage);
                        resultItem.setItemMeta(resMeta);
                    }
                }

                for (Map.Entry<Enchantment, Integer> rightEnchantment : rightEnchantments.entrySet()) {

                    if (isApplicable(rightEnchantment.getKey(), resultItem) && getConflicts(rightEnchantment.getKey(), resultItem).isEmpty()) {

                        int newLevel;
                        if (getEnchantments(resultItem).containsKey(rightEnchantment.getKey())) {
                            if (rightEnchantment.getValue() == resultItem.getEnchantmentLevel(rightEnchantment.getKey())) {
                                newLevel = Math.min(rightEnchantment.getKey().getMaxLevel(), resultItem.getEnchantmentLevel(rightEnchantment.getKey()) + 1);
                            } else {
                                newLevel = Math.min(rightEnchantment.getKey().getMaxLevel(), Math.max(rightEnchantment.getValue(), resultItem.getEnchantmentLevel(rightEnchantment.getKey())));
                            }
                        } else {
                            newLevel = rightEnchantment.getValue();
                        }
                        addEnchantment(resultItem, rightEnchantment.getKey(), newLevel);

                        cost += newLevel * getMultiplier(rightEnchantment.getKey(), rightItem.getType() == Material.ENCHANTED_BOOK);

                    } else {
                        cost++;
                    }

                }

            }

            List<Enchantment> compatible = getCompatible(rightEnchantments.keySet().stream().toList(), leftItem);
            if (!((canRepair(rightItem, leftItem) || rightItem.getType() == leftItem.getType()) && leftItem.getItemMeta() instanceof Damageable damageable && damageable.hasDamage()) && compatible.isEmpty())
                if (rename && cost > 39) {
                    cost = 39;
                    return new AnvilResult(cost, resultItem);
                } else if (rename) {
                    return new AnvilResult(cost, resultItem);
                } else {
                    return new EmptyAnvilResult();
                }
        }

        int penalty = Math.max(getPenalty(leftItem), getPenalty(rightItem));
        int newPenalty = (int) (Math.log(penalty + 1) / Math.log(2)) + 1;
        if (newPenalty <= 0)
            newPenalty = 1;
        setPenalty(resultItem, newPenalty);

        return new AnvilResult(cost, resultItem);

    }

    public static class AnvilResult {

        private final int levelCost;
        private final ItemStack result;

        private AnvilResult(final int levelCost, final ItemStack result) {
            this.levelCost = levelCost;
            this.result = result;
        }

        public int getLevelCost() {
            return levelCost;
        }

        public ItemStack getResult() {
            return result;
        }

    }

    public static class EmptyAnvilResult extends AnvilResult{

        private EmptyAnvilResult() {
            super(0, null);
        }

    }

    public static class MaterialRepairingAnvilResult extends AnvilResult {

        private final int materialCount;

        private MaterialRepairingAnvilResult(final int levelCost, final int materialCount, final ItemStack result) {
            super(levelCost, result);
            this.materialCount = materialCount;
        }

        public int getMaterialCount() {
            return materialCount;
        }

    }

}
