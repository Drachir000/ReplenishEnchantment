package de.drachir000.survival.replenishenchantment;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemWatcher implements Listener {

    private final Component loreLine;

    private final ReplenishEnchantment inst;

    public ItemWatcher(ReplenishEnchantment inst) {
        this.inst = inst;
        this.loreLine = Component.text(inst.getMainConfiguration().getEnchantmentName()).color(TextColor.color(170, 170, 170))
                .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.OBFUSCATED, false)
                .decoration(TextDecoration.STRIKETHROUGH, false).decoration(TextDecoration.UNDERLINED, false);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item.getType().isAir())
            return;
        if (!item.hasItemMeta())
            return;
        if (item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))
            return;
        if ((item.getType() == Material.WOODEN_HOE ||
                item.getType() == Material.STONE_HOE ||
                item.getType() == Material.IRON_HOE ||
                item.getType() == Material.GOLDEN_HOE ||
                item.getType() == Material.DIAMOND_HOE ||
                item.getType() == Material.NETHERITE_HOE) &&
                item.getItemMeta().hasEnchant(inst.getEnchantment()))
            if (!item.getItemMeta().hasLore()) {
                ItemMeta itemMeta = item.getItemMeta();
                List<Component> lore = List.of(loreLine);
                itemMeta.lore(lore);
                item.setItemMeta(itemMeta);
                e.setCurrentItem(item);
                return;
            } else {
                for (Component comp : item.getItemMeta().lore())
                    if (comp.equals(loreLine))
                        return;
                ItemMeta itemMeta = item.getItemMeta();
                List<Component> lore = itemMeta.lore();
                lore.add(0, loreLine);
                itemMeta.lore(lore);
                item.setItemMeta(itemMeta);
                e.setCurrentItem(item);
            }
    }

}
