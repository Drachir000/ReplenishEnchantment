package de.drachir000.survival.replenishenchantment.api;

import de.drachir000.survival.replenishenchantment.MessageBuilder;
import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtils {

    private final Enchantment enchantment;
    private final Component loreLine, bookLoreLine;
    private final List<Component> lore;

    public ItemUtils(ReplenishEnchantment inst) {
        this.enchantment = inst.getEnchantment();
        this.loreLine = enchantment.displayName(1).color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.OBFUSCATED, false)
                .decoration(TextDecoration.STRIKETHROUGH, false).decoration(TextDecoration.UNDERLINED, false);
        this.lore = List.of(loreLine);
        this.bookLoreLine = inst.getMessageBuilder().build(MessageBuilder.Message.BOOK_LORE);
    }

    public boolean isEnchanted(ItemStack item) {
        if (!item.hasItemMeta() || item.getItemMeta() == null)
            return false;
        return item.getItemMeta().hasEnchant(enchantment);
    }

    public boolean hasStoredEnchant(ItemStack enchantmentStorage) {
        if (enchantmentStorage.getItemMeta() instanceof EnchantmentStorageMeta)
            return  (((EnchantmentStorageMeta) enchantmentStorage.getItemMeta()).hasStoredEnchant(enchantment));
        return false;
    }

    public ItemStack addStoredEnchant(ItemStack enchantmentStorage) {
        if (enchantmentStorage.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta meta = ((EnchantmentStorageMeta) enchantmentStorage.getItemMeta());
            meta.addStoredEnchant(enchantment, 1, false);
            enchantmentStorage.setItemMeta(meta);
            updateLore(enchantmentStorage);
        }
        return enchantmentStorage;
    }

    public ItemStack buildBook() {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(enchantment, 1, false);
        meta.lore(lore);
        book.setItemMeta(meta);
        return book;
    }

    public ItemStack buildHoe(Material material, boolean fullEnchanted) {
        ItemStack hoe = new ItemStack(material, 1);
        ItemMeta meta = hoe.getItemMeta();
        meta.addEnchant(enchantment, 1, false);
        if (fullEnchanted) {
            meta.addEnchant(Enchantment.DIG_SPEED, 5, false);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, false);
            meta.addEnchant(Enchantment.DURABILITY, 3, false);
            meta.addEnchant(Enchantment.MENDING, 1, false);
        }
        meta.lore(lore);
        hoe.setItemMeta(meta);
        return hoe;
    }

    public ItemStack applyEnchantment(ItemStack item) {
        item.addEnchantment(enchantment, 1);
        updateLore(item);
        return item;
    }

    public boolean isHoe(ItemStack item) {
        return (item.getType() == Material.WOODEN_HOE ||
                item.getType() == Material.STONE_HOE ||
                item.getType() == Material.IRON_HOE ||
                item.getType() == Material.GOLDEN_HOE ||
                item.getType() == Material.DIAMOND_HOE ||
                item.getType() == Material.NETHERITE_HOE);
    }

    public ItemStack updateLore(ItemStack item) {
        if (item == null)
            return item;
        if (item.getType().isAir())
            return item;
        if (!item.hasItemMeta())
            return item;
        if (item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))
            return item;
        if (isHoe(item)) {
            if (item.getItemMeta().hasEnchant(enchantment)) {
                if (!item.getItemMeta().hasLore()) {
                    item.lore(lore);
                } else {
                    for (Component comp : item.getItemMeta().lore())
                        if (comp.equals(loreLine))
                            return item;
                    List<Component> iLore = item.lore();
                    iLore.add(0, loreLine);
                    item.lore(iLore);
                }
                return item;
            } else if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                List<Component> iLore = new ArrayList<>();
                for (Component comp : item.lore()) {
                    if(!comp.equals(loreLine))
                        iLore.add(comp);
                }
                item.lore(iLore);
                return item;
            }
        } else if (hasStoredEnchant(item)) {
            if (!item.getItemMeta().hasLore()) {
                item.lore(List.of(lore.iterator().next(), bookLoreLine));
            } else {
                boolean line = false;
                boolean bookLine = false;
                for (Component comp : item.getItemMeta().lore()) {
                    if (comp.equals(loreLine))
                        line = true;
                    if (comp.equals(bookLoreLine))
                        bookLine = true;
                }
                if (line && bookLine)
                    return item;
                List<Component> iLore = item.lore();
                if (!line)
                    iLore.add(0, loreLine);
                if (!bookLine)
                    iLore.add(1, bookLoreLine);
                item.lore(iLore);
            }
            return item;
        } else if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<Component> iLore = new ArrayList<>();
            for (Component comp : item.lore()) {
                if (!(comp.equals(loreLine) || comp.equals(bookLoreLine)))
                    iLore.add(comp);
            }
            item.lore(iLore);
            return item;
        }
        return item;
    }

}
