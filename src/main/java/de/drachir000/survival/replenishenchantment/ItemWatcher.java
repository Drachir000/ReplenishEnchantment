package de.drachir000.survival.replenishenchantment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemWatcher implements Listener {

    private final Component loreLine;

    private final ReplenishEnchantment inst;

    public ItemWatcher(ReplenishEnchantment inst) {
        this.inst = inst;
        this.loreLine = inst.getEnchantment().displayName(1).color(TextColor.color(170, 170, 170))
                .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.OBFUSCATED, false)
                .decoration(TextDecoration.STRIKETHROUGH, false).decoration(TextDecoration.UNDERLINED, false);
    }

    // /give @s netherite_hoe{Enchantments:[{id:"replenishenchantment:replenish",lvl:1}]} 1
    // /give @s enchanted_book{StoredEnchantments:[{id:"replenishenchantment:replenish",lvl:1}]} 1

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        ItemStack clickedItem = e.getCurrentItem();
        updateLore(clickedItem);

        if (e.getCurrentItem() == null || e.getCursor() == null)
            return;

        ItemStack cursor = e.getCursor();

        if (!isHoe(clickedItem))
            return;

        if (e.getInventory() instanceof AnvilInventory) {
            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasEnchant(inst.getEnchantment())) {
                e.setCancelled(true);
                return;
            }
        }

        if (!isThisBook(cursor))
            return;

        if (!e.getWhoClicked().getGameMode().equals(GameMode.SURVIVAL))
            return;

        applyEnchantment(clickedItem);

        e.setCancelled(true);
        e.setCurrentItem(clickedItem);

        e.getWhoClicked().setItemOnCursor(new ItemStack(Material.AIR));

    }

    private ItemStack applyEnchantment(ItemStack item) {
        item.addEnchantment(inst.getEnchantment(), 1);
        updateLore(item);
        return item;
    }

    private boolean isHoe(ItemStack item) {
        return (item.getType() == Material.WOODEN_HOE ||
                item.getType() == Material.STONE_HOE ||
                item.getType() == Material.IRON_HOE ||
                item.getType() == Material.GOLDEN_HOE ||
                item.getType() == Material.DIAMOND_HOE ||
                item.getType() == Material.NETHERITE_HOE);
    }

    private boolean isThisBook(ItemStack item) {
        if (item.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) item.getItemMeta();
            return enchantmentStorageMeta.hasStoredEnchant(inst.getEnchantment());
        }
        return false;
    }

    private ItemStack updateLore(ItemStack item) {
        if (item == null)
            return item;
        if (item.getType().isAir())
            return item;
        if (!item.hasItemMeta())
            return item;
        if (item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))
            return item;
        if (isHoe(item)) {
            if (item.getItemMeta().hasEnchant(inst.getEnchantment())) {
                if (!item.getItemMeta().hasLore()) {
                    List<Component> lore = List.of(loreLine);
                    item.lore(lore);
                } else {
                    for (Component comp : item.getItemMeta().lore())
                        if (comp.equals(loreLine))
                            return item;
                    List<Component> lore = item.lore();
                    lore.add(0, loreLine);
                    item.lore(lore);
                }
                return item;
            } else if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                List<Component> lore = new ArrayList<>();
                for (Component comp : item.lore()) {
                    if(!comp.equals(loreLine))
                        lore.add(comp);
                }
                item.lore(lore);
                return item;
            }
        } else if (isThisBook(item)) {
            if (!item.getItemMeta().hasLore()) {
                List<Component> lore = List.of(loreLine);
                item.lore(lore);
            } else {
                for (Component comp : item.getItemMeta().lore())
                    if (comp.equals(loreLine))
                        return item;
                List<Component> lore = item.lore();
                lore.add(0, loreLine);
                item.lore(lore);
            }
            return item;
        } else if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<Component> lore = new ArrayList<>();
            for (Component comp : item.lore()) {
                if (!comp.equals(loreLine))
                    lore.add(comp);
            }
            item.lore(lore);
            return item;
            }
        return item;
    }

}
