package de.drachir000.survival.replenishenchantment.command;

import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandHandler implements CommandExecutor {

    private ReplenishEnchantment inst;

    public CommandHandler(ReplenishEnchantment inst) {
        this.inst = inst;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Only-Player Command!").color(TextColor.color(Color.RED.asRGB())));
            return false;
        }
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(inst.getEnchantment(), 1, false);
        List<Component> lore = List.of(
                inst.getEnchantment().displayName(1).color(TextColor.color(170, 170, 170))
                .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.OBFUSCATED, false)
                .decoration(TextDecoration.STRIKETHROUGH, false).decoration(TextDecoration.UNDERLINED, false)
        );
        meta.lore(lore);
        book.setItemMeta(meta);
        ((Player) sender).getPlayer().getInventory().addItem(book);
        return true;
    }

}
