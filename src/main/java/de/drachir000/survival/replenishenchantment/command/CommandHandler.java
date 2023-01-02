package de.drachir000.survival.replenishenchantment.command;

import de.drachir000.survival.replenishenchantment.MessageBuilder;
import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandHandler implements CommandExecutor {

    private final ReplenishEnchantment inst;
    private final MessageBuilder messageBuilder;

    public CommandHandler(ReplenishEnchantment inst) {
        this.inst = inst;
        this.messageBuilder = inst.getMessageBuilder();
    }

    private ItemStack buildBook() {
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
        return book;
    }

    private ItemStack buildHoe(Material material, boolean fullEnchanted) {
        ItemStack hoe = new ItemStack(material, 1);
        ItemMeta meta = hoe.getItemMeta();
        meta.addEnchant(inst.getEnchantment(), 1, false);
        if (fullEnchanted) {
            meta.addEnchant(Enchantment.DIG_SPEED, 5, false);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, false);
            meta.addEnchant(Enchantment.DURABILITY, 3, false);
            meta.addEnchant(Enchantment.MENDING, 1, false);
        }
        List<Component> lore = List.of(
                inst.getEnchantment().displayName(1).color(TextColor.color(170, 170, 170))
                        .decoration(TextDecoration.ITALIC, false).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.OBFUSCATED, false)
                        .decoration(TextDecoration.STRIKETHROUGH, false).decoration(TextDecoration.UNDERLINED, false)
        );
        meta.lore(lore);
        hoe.setItemMeta(meta);
        return hoe;
    }

    private boolean getBook(CommandSender sender) {
        if (!sender.isOp()) { //TODO add permission to config.yml
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
            return true;
        }
        if (!((Player) sender).getPlayer().getInventory().addItem(buildBook()).isEmpty()) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_INV_FULL));
            return true;
        } else {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_SUCCESS));
            return true;
        }
    }

    private boolean giveBook(CommandSender sender, String[] args) {
        if (!sender.isOp()) { //TODO add permission to config.yml
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
                return true;
            }
            if (!((Player) sender).getPlayer().getInventory().addItem(buildBook()).isEmpty()) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_INV_FULL));
                return true;
            } else {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_SUCCESS));
                return true;
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                // TODO give-book-player-not-found message
                return true;
            } else if (!target.isOnline()) {
                // TODO give-book-player-offline message
                return true;
            }
            if (!target.getInventory().addItem(buildBook()).isEmpty()) {
                // TODO give-book-inv-full message
                return true;
            } else {
                // TODO give-book-success message
                return true;
            }
        }
    }

    private boolean getHoe(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
            return true;
        }
        if (args.length < 1) {
            // TODO get-hoe-usage message
            return true;
        }
        Material hoeMaterial = null;
        switch (args[0].toUpperCase()) { // TODO permission check
            case "WOOD":
                hoeMaterial = Material.WOODEN_HOE;
                break;
            case "STONE":
                hoeMaterial = Material.STONE_HOE;
                break;
            case "GOLD":
                hoeMaterial = Material.GOLDEN_HOE;
                break;
            case "IRON":
                hoeMaterial = Material.IRON_HOE;
                break;
            case "DIAMOND":
                hoeMaterial = Material.DIAMOND_HOE;
                break;
            case "NETHERITE":
                hoeMaterial = Material.NETHERITE_HOE;
                break;
            default:
                break;
        }
        if (hoeMaterial == null) {
            // TODO get-hoe-invalid-material message
            return true;
        }
        // TODO full enchanted permission check
        if (!((Player) sender).getPlayer().getInventory().addItem(buildHoe(hoeMaterial, (args.length > 1 && args[1].equalsIgnoreCase("true")))).isEmpty()) {
            // TODO get-hoe-inv-full message
            return true;
        } else {
            // TODO get-hoe-success message
            return true;
        }
    }

    private boolean giveHoe(CommandSender sender, String[] args) {
        if (args.length < 2) {
            // TODO give-hoe-usage message
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            // TODO give-hoe-player-not-found message
            return true;
        } else if (!target.isOnline()) {
            // TODO give-hoe-player-offline message
            return true;
        }
        Material hoeMaterial = null;
        switch (args[1].toUpperCase()) { // TODO permission check
            case "WOOD":
                hoeMaterial = Material.WOODEN_HOE;
                break;
            case "STONE":
                hoeMaterial = Material.STONE_HOE;
                break;
            case "GOLD":
                hoeMaterial = Material.GOLDEN_HOE;
                break;
            case "IRON":
                hoeMaterial = Material.IRON_HOE;
                break;
            case "DIAMOND":
                hoeMaterial = Material.DIAMOND_HOE;
                break;
            case "NETHERITE":
                hoeMaterial = Material.NETHERITE_HOE;
                break;
            default:
                break;
        }
        if (hoeMaterial == null) {
            // TODO give-hoe-invalid-material message
            return true;
        }
        // TODO full enchanted permission check
        if (!target.getInventory().addItem(buildHoe(hoeMaterial, (args.length > 2 && args[2].equalsIgnoreCase("true")))).isEmpty()) {
            // TODO give-hoe-inv-full message
            return true;
        } else {
            // TODO give-hoe-success message
            return true;
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return switch (command.getName()) {
            case "replenish-getbook" -> getBook(sender);
            case "replenish-givebook" -> giveBook(sender, args);
            case "replenish-gethoe" -> getHoe(sender, args);
            case "replenish-givehoe" -> giveHoe(sender, args);
            default -> false;
        };
    }

    /*
    * TODO List:
    *  - Add messages
    *  - Add Configurable Permissions
    *  - Add TabComplete
    *      => with permissions (also with material based permissions and if no material based = not base perm)
    *  - Add API
    *  - Move Item generating/enchantment-applying methods to API
    *  - Replenishing
    *  - Replenish Event
    *  - Publishing stuff?
    * */

}
