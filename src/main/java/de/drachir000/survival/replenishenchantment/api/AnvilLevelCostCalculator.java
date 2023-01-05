package de.drachir000.survival.replenishenchantment.api;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class AnvilLevelCostCalculator {

    private final JSONObject multipliers = new JSONObject();
    private final Plugin plugin;


    public AnvilLevelCostCalculator(Plugin plugin) {
        loadMultipliers(multipliers, 1, 1); // TODO add options
        plugin.getLogger().log(Level.INFO, multipliers.toString()); //TODO rem
        plugin.getLogger().log(Level.INFO, "getMultiplier(knockback, true): " + getMultiplier(Enchantment.KNOCKBACK, true)); //TODO rem
        plugin.getLogger().log(Level.INFO, "getMultiplier(knockback, false): " + getMultiplier(Enchantment.KNOCKBACK, false)); //TODO rem
        this.plugin = plugin;
    }

    private void loadMultipliers(JSONObject jsonObject, int itemValue, int bookValue) {
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
                default ->
                        plugin.getLogger().log(Level.WARNING, "Could not identify Enchantment \"" + key + "\"! Setting level cost to 0..."); // TODO add extern enchants to config and add info to this message
            }
            jsonObject.put(key,
                    new JSONObject()
                            .put("item", item)
                            .put("book", book)
            );
        }
    }

    private boolean isApplicable(Enchantment enchantment, ItemStack item) {
        return enchantment.canEnchantItem(item);
    }

    private List<Enchantment> getConflicts(Enchantment enchantment, ItemStack item) {
        List<Enchantment> conflicts = new ArrayList<>();
        for (Enchantment ench : item.getEnchantments().keySet()) {
            if (enchantment.conflictsWith(ench))
                conflicts.add(ench);
        }
        return conflicts;
    }

    private int getMultiplier(Enchantment enchantment, boolean book) {
        JSONObject obj = multipliers.getJSONObject(enchantment.getKey().getKey());
        try {
            return (book ? obj.getInt("book") : obj.getInt("item"));
        } catch (JSONException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.WARNING, "Could not get Enchantment Multiplier for enchantment \"" + enchantment.getKey().getKey() + "\"!");
            return 0;
        }
    }

    private int getPenalty(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return 0;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Repairable) {
            Repairable repairable = (Repairable) meta;
            return (repairable.hasRepairCost() ? repairable.getRepairCost() : 0);
        }
        return 0;
    }

    private boolean isDamaged(ItemStack item) {
        if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable) {
            Damageable damageable = (Damageable) meta;
            return damageable.hasDamage();
        }
        return false;
    }

    public int getCost(ItemStack target, ItemStack sacrifice) {

        // target = slot 0 = left slot
        // sacrifice = slot 1 = right slot (gets sacrificed)

        StringBuilder debugBuilder = new StringBuilder("\n");
        debugBuilder.append("""
                #############################################################################################################################################################################################################################################

                Anvil Level Cost Calculation
                ----------------------------

                target: {MATERIAL}
                    Enchantments:""".replace("{MATERIAL}", target.getType().name()));
        for (Enchantment enchantment : target.getEnchantments().keySet()) {
            debugBuilder.append("\n        - ").append(enchantment.getKey().getKey()).append(":").append(target.getEnchantmentLevel(enchantment)).append("(-> ").append(getMultiplier(enchantment, true)).append("M-true | ").append(getMultiplier(enchantment, false)).append("M-false)");
        }
        debugBuilder.append("\n    Penalty: ").append(getPenalty(target))
                .append("\n\nsacrifice: ").append(sacrifice.getType().name()).append("\n    Enchantments:");
        for (Enchantment enchantment : sacrifice.getEnchantments().keySet()) {
            debugBuilder.append("\n        - ").append(enchantment.getKey().getKey()).append(":").append(target.getEnchantmentLevel(enchantment)).append("(-> ").append(getMultiplier(enchantment, true)).append("M-true | ").append(getMultiplier(enchantment, false)).append("M-false)");
        }
        debugBuilder.append("\n    Penalty: ").append(getPenalty(sacrifice)).append("\n\n--------------------------------------------------------\n\nFOR (enchantments on sacrifice):\n");

        int totalCost = 0;

        // For each enchantment on the sacrifice:
        for (Enchantment enchantment : sacrifice.getEnchantments().keySet()) { // TODO ench storage !!!

            debugBuilder.append("\n    ----------------------------")
                    .append("\n    Enchantment: ").append(enchantment.getKey().getKey())
                    .append("\n    Level on sacrifice: ").append(sacrifice.getEnchantmentLevel(enchantment))
                    .append("\n\n    isApplicable on target ? -> ").append(isApplicable(enchantment, target))
                    .append("\n\n    Conflicts on target:");
            List<Enchantment> conflicts = getConflicts(enchantment, target);
            conflicts.remove(enchantment); //TODO add to main functionality
            for (Enchantment conflict : conflicts) {
                debugBuilder.append("\n        - ").append(conflict.getKey().getKey());
            }
            debugBuilder.append("\n    isConflict on target ? -> ").append(conflicts.size() > 0)
                    .append("\n\n    isSharedEnchantment ? -> ").append(target.getEnchantments().containsKey(enchantment));

            // Ignore any enchantment that cannot be applied to the target (e.g. Protection on a sword).
            if (!isApplicable(enchantment, target)) {
                continue;
            }

            // Add one level for every incompatible enchantment on the target.
            //List<Enchantment> conflicts = getConflicts(enchantment, target); TODO (debug-overwritten) (TODO: remove same enchantment from conflicts)
            if (conflicts.size() > 0) {
                totalCost++;
            }

            //If the enchantment is compatible
            else {

                // If the target has the enchantment as well
                if (target.getEnchantments().containsKey(enchantment)) {

                    debugBuilder.append("\n\n        Level on target: ").append(target.getEnchantmentLevel(enchantment))
                            .append("\n\n        isEqualLevel with target ? -> ").append(target.getEnchantmentLevel(enchantment) == sacrifice.getEnchantmentLevel(enchantment))
                            .append("\n\n        isGreaterLevel than target ? -> ").append(target.getEnchantmentLevel(enchantment) < sacrifice.getEnchantmentLevel(enchantment));

                    //If sacrifice level is equal, the target gains one level, unless it is already at the maximum level for that enchantment.
                    if (target.getEnchantmentLevel(enchantment) == sacrifice.getEnchantmentLevel(enchantment)) {

                        ItemMeta targetMeta = target.getItemMeta();
                        targetMeta.removeEnchant(enchantment);
                        targetMeta.addEnchant(enchantment, Math.min(
                                sacrifice.getEnchantmentLevel(enchantment) + 1,
                                enchantment.getMaxLevel()
                        ), false);
                        target.setItemMeta(targetMeta);

                    }

                    //If sacrifice level is greater, the target is raised to the sacrifice's level
                    else if (target.getEnchantmentLevel(enchantment) < sacrifice.getEnchantmentLevel(enchantment)) {

                        ItemMeta targetMeta = target.getItemMeta();
                        targetMeta.removeEnchant(enchantment);
                        targetMeta.addEnchant(enchantment, sacrifice.getEnchantmentLevel(enchantment), false);
                        target.setItemMeta(targetMeta);

                    }

                }

                //If the target does not have the enchantment, it gains all levels of that enchantment
                else {

                    target.addEnchantment(enchantment, sacrifice.getEnchantmentLevel(enchantment));

                }

                // Add the final level of the enchantment on the resulting item multiplied by the multiplier from the table below.
                int multiplier = getMultiplier(enchantment, sacrifice.getType() == Material.ENCHANTED_BOOK);

                int cost = multiplier * target.getEnchantmentLevel(enchantment);

                debugBuilder.append("\n\n    isEnchantedBook sacrifice ? -> ").append(sacrifice.getType() == Material.ENCHANTED_BOOK)
                        .append("\n    multiplier of loop-Enchantment (").append(enchantment.getKey().getKey()).append("): ").append(getMultiplier(enchantment, sacrifice.getType() == Material.ENCHANTED_BOOK))
                        .append("\n    level of targetEnchantment: ").append(target.getEnchantmentLevel(enchantment))
                        .append("\n    cost(multiplier * level): ").append(cost)
                ;

                totalCost += cost;

            }

        }

        ///Calculate Penalty cost
        double targetPenaltyCost = Math.pow(2, getPenalty(target)) - 1;
        double sacrificePenaltyCost = Math.pow(2, getPenalty(sacrifice)) - 1;
        double penaltyCost = targetPenaltyCost + sacrificePenaltyCost;
        totalCost += penaltyCost;

        //Calculate Repair cost
        if (isDamaged(target) && target.getType() == sacrifice.getType()) {
            //Is it repairable
            totalCost += 2;
        }

        debugBuilder.append("\n    ----------------------------\n\n");
        debugBuilder.append("""
                --------------------------------------------------------
                                
                targetPenaltyCost: {targetPenaltyCost}
                sacrificePenaltyCost: {sacrificePenaltyCost}
                penaltyCost: {penaltyCost}
                                
                --------------------------------------------------------
                                
                isDamaged target ? -> {isDamaged(target)}
                isSameType sacrifice ? -> {target.getType() == sacrifice.getType()}
                                
                --------------------------------------------------------
                                
                totalCost: {totalCost}
                                
                #############################################################################################################################################################################################################################################
                """
                        .replace("{targetPenaltyCost}", "" + targetPenaltyCost)
                        .replace("{sacrificePenaltyCost}", "" + sacrificePenaltyCost)
                        .replace("{penaltyCost}", "" + penaltyCost)
                        .replace("{isDamaged(target)}", "" + isDamaged(target))
                        .replace("{target.getType() == sacrifice.getType()}", "" + (target.getType() == sacrifice.getType()))
                        .replace("{totalCost}", "" + totalCost)
                );
        plugin.getLogger().log(Level.INFO, debugBuilder.toString());

        return totalCost;
    }

}
