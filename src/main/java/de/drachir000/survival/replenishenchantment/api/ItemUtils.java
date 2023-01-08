package de.drachir000.survival.replenishenchantment.api;

import de.drachir000.survival.replenishenchantment.MessageBuilder;
import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
    private final String lore, bookLore;

    public ItemUtils(ReplenishEnchantment inst) {
        this.enchantment = inst.getEnchantment();
        Component loreLine = Component.text(enchantment.getName()).color(NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.OBFUSCATED, false)
                .decoration(TextDecoration.STRIKETHROUGH, false).decoration(TextDecoration.UNDERLINED, false);
        this.lore = LegacyComponentSerializer.legacySection().serialize(loreLine);
        Component bookLore = inst.getMessageBuilder().build(MessageBuilder.Message.BOOK_LORE);
        this.bookLore = LegacyComponentSerializer.legacySection().serialize(bookLore);
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
        meta.setLore(List.of(lore, bookLore));
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
        meta.setLore(List.of(lore));
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
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(List.of(lore));
                    item.setItemMeta(meta);
                } else {
                    for (String line : item.getItemMeta().getLore())
                        if (line.equals(lore))
                            return item;
                    ItemMeta meta = item.getItemMeta();
                    List<String> iLore = meta.getLore();
                    iLore.add(0, lore);
                    meta.setLore(iLore);
                    item.setItemMeta(meta);
                }
                return item;
            } else if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
                List<String> iLore = new ArrayList<>();
                ItemMeta meta = item.getItemMeta();
                for (String line : meta.getLore()) {
                    if(!line.equals(lore))
                        iLore.add(line);
                }
                meta.setLore(iLore);
                item.setItemMeta(meta);
                return item;
            }
        } else if (hasStoredEnchant(item)) {
            if (!item.getItemMeta().hasLore()) {
                ItemMeta meta = item.getItemMeta();
                meta.setLore(List.of(lore, bookLore));
                item.setItemMeta(meta);
            } else {
                boolean ench = false;
                boolean book = false;
                ItemMeta meta = item.getItemMeta();
                for (String line : meta.getLore()) {
                    if (line.equals(lore))
                        ench = true;
                    if (line.equals(bookLore))
                        book = true;
                }
                if (ench && book)
                    return item;
                List<String> iLore = meta.getLore();
                if (!ench)
                    iLore.add(0, lore);
                if (!book)
                    iLore.add(1, bookLore);
                meta.setLore(iLore);
                item.setItemMeta(meta);
            }
            return item;
        } else if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            List<String> iLore = new ArrayList<>();
            ItemMeta meta = item.getItemMeta();
            for (String line : meta.getLore()) {
                if (!(line.equals(lore) || line.equals(bookLore)))
                    iLore.add(line);
            }
            meta.setLore(iLore);
            item.setItemMeta(meta);
            return item;
        }
        return item;
    }

}
