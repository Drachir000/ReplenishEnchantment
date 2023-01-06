package de.drachir000.survival.replenishenchantment.api;

import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class REAPI {

    private final ReplenishEnchantment plugin;
    private final ItemUtils itemUtils;
    private final AnvilUtils anvilUtils;
    private static REAPI inst;

    public REAPI(ReplenishEnchantment plugin, ItemUtils itemUtils, AnvilUtils anvilUtils) {
        this.plugin = plugin;
        this.itemUtils = itemUtils;
        this.anvilUtils = anvilUtils;
        inst = this;
    }

    /**
     * @return the API instance
     * @since 0.0.15
     * */
    public static REAPI getAPI() {
        return inst;
    }

    /**
     * @return the Replenish-Enchantment instance
     * @since 0.0.15
     * */
    public Enchantment getEnchantment() {
        return plugin.getEnchantment();
    }

    /**
     * Checks if the given Item is enchanted with the Replenish-Enchantment
     * @param item the item to check
     * @return true - if the item has the Replenish-Enchantment, false otherwise
     * @since 0.0.15
     * */
    public boolean isEnchanted(ItemStack item) {
        return itemUtils.isEnchanted(item);
    }

    /**
     * Checks if a enchantmentStorage (like an Enchanted Book) has the Replenish-Enchantment stored
     * @param enchantmentStorage the enchantmentStorage (like an Enchanted Book) to check
     * @return true - if the enchantmentStorage contains the Replenish-Enchantment, false if the given item is no enchantmentStorage or doesn't contain the Replenish-Enchantment
     * @since 0.0.15
     * */
    public boolean hasStoredEnchant(ItemStack enchantmentStorage) {
        return itemUtils.hasStoredEnchant(enchantmentStorage);
    }

    /**
     * Checks if an item is enchanted with the Replenish-Enchantment
     * This does not check if the item should be able to be enchanted with the Replenish-Enchantment!
     * @param item the item to check
     * @return true - if the item is enchanted with the Replenish-Enchantment, false otherwise
     * @since 0.0.15
     * */
    public boolean isHoe(ItemStack item) {
        return itemUtils.isHoe(item);
    }

    /**
     * Applies the Replenish-Enchantment to the given item
     * @param item The item to apply the Replenish-Enchantment to
     * @return The same ItemStack, the Replenish-Enchantment was applied to
     * @throws IllegalArgumentException if the given item is no hoe
     * @since 0.0.15
     */
    public ItemStack applyEnchantment(ItemStack item) {
        if (!isHoe(item))
            throw new IllegalArgumentException("The given ItemType is no hoe: " + item.getType().toString());
        return itemUtils.applyEnchantment(item);
    }

    /**
     * Adds the Replenish-Enchantment to the Enchantment-Storage of the given item
     * @param item The item to have the Replenish-Enchantment added to the Enchantment-Storage
     * @return The same ItemStack, with the Replenish-Enchantment added to the storage ore just the same ItemStack, if the given ItemStack has no storage.
     * @since 0.0.17
     */
    public ItemStack addStoredEnchant(ItemStack item) {
        return itemUtils.addStoredEnchant(item);
    }

    /**
     * Updates the lore of the given item.
     * Applies and deletes the Replenish-Enchantment lore line from enchanted and not enchanted items
     * @param item the item whose lore is to be updated
     * @return The same ItemStack, with the updated lore
     * @since 0.0.15
     * */
    public ItemStack updateLore(ItemStack item) {
        return itemUtils.updateLore(item);
    }

    /**
     * Builds an Enchanted Book with the Replenish-Enchantment and the lore already applied
     * @return The built Enchanted Book
     * @since 0.0.15
     * */
    public ItemStack buildBook() {
        return itemUtils.buildBook();
    }

    /**
     * Builds a hoe with the Replenish-Enchantment and the lore already applied
     * @param material the Material of the hoe to be built
     * @param fullEnchanted whether the hoe shall be fully enchanted, this will add
     *                      Efficiency 5,
     *                      Fortune 3,
     *                      Durability 3
     *                      and Mending to the resulting hoe
     * @return The built Hoe
     * @throws IllegalArgumentException if the given Material is no hoe material
     * @since 0.0.15
     * */
    public ItemStack buildHoe(Material material, boolean fullEnchanted) {
        if (!isHoe(new ItemStack(material)))
            throw new IllegalArgumentException("Material \"" + material.toString() + "\" is no hoe!");
        return itemUtils.buildHoe(material, fullEnchanted);
    }

    /**
     * Registers an external Enchantment with the item and book level cost modifiers for the anvil
     * @param key The key of the enchantment ( Enchantment.getKey().getKey() ) to register
     * @param itemModifier the modifier for the item level cost multiplier (used when the right item of the anvil is a non-enchanted-book item)
     * @param bookModifier the modifier for the book level cost multiplier (used when the right item of the anvil is an enchanted-book)
     * @return true - if the enchantment was already registered, false otherwise
     * @since 0.0.18
     * */
    public boolean registerEnchantment(String key, int itemModifier, int bookModifier) {
        return anvilUtils.registerEnchantment(key, itemModifier, bookModifier);
    }

    /**
     * Registers an external Enchantment with the item and book level cost modifiers for the anvil
     * @param enchantment The enchantment to register
     * @param itemModifier the modifier for the item level cost multiplier (used when the right item of the anvil is a non-enchanted-book item)
     * @param bookModifier the modifier for the book level cost multiplier (used when the right item of the anvil is an enchanted-book)
     * @return true - if the enchantment was already registered, false otherwise
     * @since 0.0.18
     * */
    public boolean registerEnchantment(Enchantment enchantment, int itemModifier, int bookModifier) {
        return anvilUtils.registerEnchantment(enchantment.getKey().getKey(), itemModifier, bookModifier);
    }

}
