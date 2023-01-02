package de.drachir000.survival.replenishenchantment.command;

import de.drachir000.survival.replenishenchantment.MessageBuilder;
import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
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

    private final ReplenishEnchantment inst;
    private final MessageBuilder messageBuilder;

    public CommandHandler(ReplenishEnchantment inst) {
        this.inst = inst;
        this.messageBuilder = inst.getMessageBuilder();
    }

    private boolean getBook(CommandSender sender, String[] args) {
        if (sender.isOp()) { //TODO add permission to config.yml
            if (!(sender instanceof Player)) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
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
            if (!((Player) sender).getPlayer().getInventory().addItem(book).isEmpty()) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_INV_FULL));
                return false;
            } else {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_SUCCESS));
                return true;
            }
        } else {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return false;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (command.getName()) {
            case "replenish-getbook":
                return getBook(sender, args);
            default:
                return false;
        }
    }

}
