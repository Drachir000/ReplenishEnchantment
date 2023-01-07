package de.drachir000.survival.replenishenchantment.command;

import de.drachir000.survival.replenishenchantment.MessageBuilder;
import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import de.drachir000.survival.replenishenchantment.api.ItemUtils;
import de.drachir000.survival.replenishenchantment.config.MainConfiguration;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final ReplenishEnchantment inst;
    private final MessageBuilder messageBuilder;
    private final MainConfiguration config;
    private final ItemUtils utils;

    public CommandHandler(ReplenishEnchantment inst, ItemUtils utils) {
        this.inst = inst;
        this.messageBuilder = inst.getMessageBuilder();
        this.config = inst.getMainConfiguration();
        this.utils = utils;
    }

    private boolean getBook(CommandSender sender) {
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_BOOK)))) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
            return true;
        }
        ItemStack item = utils.buildBook();
        if (!((Player) sender).getPlayer().getInventory().addItem(item).isEmpty()) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_INV_FULL, Component.translatable(item.translationKey())));
        } else {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_SUCCESS, Component.translatable(item.translationKey())));
        }
        return true;
    }

    private boolean giveBook(CommandSender sender, String[] args) {
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_BOOK)))) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
                return true;
            }
            ItemStack item = utils.buildBook();
            if (!((Player) sender).getPlayer().getInventory().addItem(item).isEmpty()) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_INV_FULL, Component.translatable(item.translationKey())));
            } else {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_SUCCESS, Component.translatable(item.translationKey())));
            }
        } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_BOOK_PLAYER_NOT_FOUND, args[0]));
                return true;
            } else if (!target.isOnline()) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_BOOK_PLAYER_OFFLINE, target.getName()));
                return true;
            }
            ItemStack item = utils.buildBook();
            if (!target.getInventory().addItem(item).isEmpty()) {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_BOOK_INV_FULL, Component.text(target.getName()), Component.translatable(item.translationKey())));
            } else {
                sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_BOOK_SUCCESS, Component.text(target.getName()), Component.translatable(item.translationKey())));
            }
        }
        return true;
    }

    private boolean getHoe(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
            return true;
        }
        if (!(
                sender.isOp()
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_WOOD))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_STONE))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_GOLD))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_IRON))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_DIAMOND))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_NETHERITE))
        )) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_HOE_USAGE));
            return true;
        }
        Material hoeMaterial = null;
        switch (args[0].toUpperCase()) {
            case "WOOD" -> hoeMaterial = Material.WOODEN_HOE;
            case "STONE" -> hoeMaterial = Material.STONE_HOE;
            case "GOLD" -> hoeMaterial = Material.GOLDEN_HOE;
            case "IRON" -> hoeMaterial = Material.IRON_HOE;
            case "DIAMOND" -> hoeMaterial = Material.DIAMOND_HOE;
            case "NETHERITE" -> hoeMaterial = Material.NETHERITE_HOE;
            default -> {
            }
        }
        if (hoeMaterial == null) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_HOE_INVALID_MATERIAL, args[0].toUpperCase()));
            return true;
        }
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.cmdGetHoeMaterialFromString(args[0].toUpperCase()))))) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        ItemStack item = utils.buildHoe(
                hoeMaterial,
                (args.length > 1 && args[1].equalsIgnoreCase("true") && (sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_FULL_ENCHANT))))
        );
        if (!((Player) sender).getPlayer().getInventory().addItem(item).isEmpty()) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_HOE_INV_FULL, Component.translatable(item.translationKey())));
        } else {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_HOE_SUCCESS, Component.translatable(item.translationKey())));
        }
        return true;
    }

    private boolean giveHoe(CommandSender sender, String[] args) {
        if (!(
                sender.isOp()
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_WOOD))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_STONE))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_GOLD))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_IRON))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_DIAMOND))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_NETHERITE))
        )) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_USAGE));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_PLAYER_NOT_FOUND, args[0]));
            return true;
        } else if (!target.isOnline()) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_PLAYER_OFFLINE, target.getName()));
            return true;
        }
        Material hoeMaterial = null;
        switch (args[1].toUpperCase()) {
            case "WOOD" -> hoeMaterial = Material.WOODEN_HOE;
            case "STONE" -> hoeMaterial = Material.STONE_HOE;
            case "GOLD" -> hoeMaterial = Material.GOLDEN_HOE;
            case "IRON" -> hoeMaterial = Material.IRON_HOE;
            case "DIAMOND" -> hoeMaterial = Material.DIAMOND_HOE;
            case "NETHERITE" -> hoeMaterial = Material.NETHERITE_HOE;
            default -> {
            }
        }
        if (hoeMaterial == null) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_INVALID_MATERIAL, target.getName(), args[1].toUpperCase()));
            return true;
        }
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.cmdGiveHoeMaterialFromString(args[1].toUpperCase()))))) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        ItemStack item = utils.buildHoe(
                hoeMaterial,
                (args.length > 2 && args[2].equalsIgnoreCase("true") && (sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_FULL_ENCHANT)))));
        if (!target.getInventory().addItem(item).isEmpty()) {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_INV_FULL, Component.text(target.getName()), Component.translatable(item.translationKey())));
        } else {
            sender.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_SUCCESS, Component.text(target.getName()), Component.translatable(item.translationKey())));
        }
        return true;
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        switch (command.getName()) {
            case "replenish-givebook":
                if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_BOOK))) || args.length != 1)
                    return list;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
                break;
            case "replenish-gethoe":
                if (!(
                        sender.isOp()
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_WOOD))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_STONE))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_GOLD))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_IRON))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_DIAMOND))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_NETHERITE))
                ) || args.length == 0 || args.length > 2)
                    return list;
                if (args.length == 1 ) {
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_WOOD)))
                        list.add("WOOD");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_STONE)))
                        list.add("STONE");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_GOLD)))
                        list.add("GOLD");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_IRON)))
                        list.add("IRON");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_DIAMOND)))
                        list.add("DIAMOND");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_NETHERITE)))
                        list.add("NETHERITE");
                } else if (args.length == 2) {
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_FULL_ENCHANT)))
                        list.addAll(List.of("true", "false"));
                }
                break;
            case "replenish-givehoe":
                if (!(
                        sender.isOp()
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_WOOD))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_STONE))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_GOLD))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_DIAMOND))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_IRON))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_NETHERITE))
                ) || args.length == 0 || args.length > 3)
                    return list;
                if (args.length == 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                } else if (args.length == 2 ) {
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_WOOD)))
                        list.add("WOOD");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_STONE)))
                        list.add("STONE");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_GOLD)))
                        list.add("GOLD");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_IRON)))
                        list.add("IRON");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_DIAMOND)))
                        list.add("DIAMOND");
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_NETHERITE)))
                        list.add("NETHERITE");
                } else if (args.length == 3)
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_FULL_ENCHANT)))
                        list.addAll(List.of("true", "false"));
                break;
            default:
                break;
        }

        ArrayList<String> returnValue = new ArrayList<String>();
        String arg = args[args.length - 1];
        for (String s : list) {
            if (s.toLowerCase().startsWith(arg.toLowerCase())) {
                returnValue.add(s);
            }
        }
        return returnValue;
    }

    /*
    * TODO List:
    *  - Book lore
    *  - Grindstone Rework (completely overwrite the vanilla Grindstone just like the anvil? xD)
    *  - Replenishing
    *  - Replenish Event
    *  - Publishing stuff?
    * */

}
