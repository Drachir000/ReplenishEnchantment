package de.drachir000.survival.replenishenchantment.command;

import de.drachir000.survival.replenishenchantment.MessageBuilder;
import de.drachir000.survival.replenishenchantment.ReplenishEnchantment;
import de.drachir000.survival.replenishenchantment.api.ItemUtils;
import de.drachir000.survival.replenishenchantment.config.MainConfiguration;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
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

    private final MessageBuilder messageBuilder;
    private final MainConfiguration config;
    private final ItemUtils utils;
    private final BukkitAudiences audiences;
    private final boolean enabled;

    public CommandHandler(ReplenishEnchantment inst, ItemUtils utils, boolean enabled) {
        this.messageBuilder = inst.getMessageBuilder();
        this.config = inst.getMainConfiguration();
        this.utils = utils;
        this.audiences = inst.adventure();
        this.enabled = enabled;
    }

    private String getTranslationItemKey(String key) {
        return "item.minecraft." + key.replace("minecraft:", "");
    }

    private boolean getBook(CommandSender sender) {
        Audience senderAudience = audiences.sender(sender);
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_BOOK)))) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (!(sender instanceof Player)) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
            return true;
        }
        ItemStack item = utils.buildBook();
        if (!((Player) sender).getPlayer().getInventory().addItem(item).isEmpty()) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_INV_FULL, Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        } else {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_SUCCESS, Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        }
        return true;
    }

    private boolean giveBook(CommandSender sender, String[] args) {
        Audience senderAudience = audiences.sender(sender);
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_BOOK)))) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
                return true;
            }
            ItemStack item = utils.buildBook();
            if (!((Player) sender).getPlayer().getInventory().addItem(item).isEmpty()) {
                senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_INV_FULL, Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
            } else {
                senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_BOOK_SUCCESS, Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
            }
        } else {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_BOOK_PLAYER_NOT_FOUND, args[1]));
                return true;
            } else if (!target.isOnline()) {
                senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_BOOK_PLAYER_OFFLINE, target.getName()));
                return true;
            }
            ItemStack item = utils.buildBook();
            if (!target.getInventory().addItem(item).isEmpty()) {
                senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_BOOK_INV_FULL, Component.text(target.getName()), Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
            } else {
                senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_BOOK_SUCCESS, Component.text(target.getName()), Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
            }
        }
        return true;
    }

    private boolean getHoe(CommandSender sender, String[] args) {
        Audience senderAudience = audiences.sender(sender);
        if (!(sender instanceof Player)) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
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
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length < 2) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_HOE_USAGE));
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
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_HOE_INVALID_MATERIAL, args[1].toUpperCase()));
            return true;
        }
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.cmdGetHoeMaterialFromString(args[1].toUpperCase()))))) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        ItemStack item = utils.buildHoe(
                hoeMaterial,
                (args.length > 2 && args[2].equalsIgnoreCase("true") && (sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_FULL_ENCHANT))))
        );
        if (!((Player) sender).getPlayer().getInventory().addItem(item).isEmpty()) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_HOE_INV_FULL, Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        } else {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_HOE_SUCCESS, Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        }
        return true;
    }

    private boolean giveHoe(CommandSender sender, String[] args) {
        Audience senderAudience = audiences.sender(sender);
        if (!(
                sender.isOp()
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_WOOD))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_STONE))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_GOLD))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_IRON))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_DIAMOND))
                || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_NETHERITE))
        )) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length < 3) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_USAGE));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_PLAYER_NOT_FOUND, args[1]));
            return true;
        } else if (!target.isOnline()) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_PLAYER_OFFLINE, target.getName()));
            return true;
        }
        Material hoeMaterial = null;
        switch (args[2].toUpperCase()) {
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
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_INVALID_MATERIAL, target.getName(), args[2].toUpperCase()));
            return true;
        }
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.cmdGiveHoeMaterialFromString(args[2].toUpperCase()))))) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        ItemStack item = utils.buildHoe(
                hoeMaterial,
                (args.length > 3 && args[3].equalsIgnoreCase("true") && (sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_FULL_ENCHANT)))));
        if (!target.getInventory().addItem(item).isEmpty()) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_INV_FULL, Component.text(target.getName()), Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        } else {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_HOE_SUCCESS, Component.text(target.getName()), Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        }
        return true;
    }

    private boolean getAxe(CommandSender sender, String[] args) {
        Audience senderAudience = audiences.sender(sender);
        if (!(sender instanceof Player)) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.PLAYER_ONLY_COMMAND));
            return true;
        }
        if (!(
                sender.isOp()
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_WOOD))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_STONE))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_GOLD))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_IRON))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_DIAMOND))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_NETHERITE))
        )) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length < 2) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_AXE_USAGE));
            return true;
        }
        Material axeMaterial = null;
        switch (args[1].toUpperCase()) {
            case "WOOD" -> axeMaterial = Material.WOODEN_AXE;
            case "STONE" -> axeMaterial = Material.STONE_AXE;
            case "GOLD" -> axeMaterial = Material.GOLDEN_AXE;
            case "IRON" -> axeMaterial = Material.IRON_AXE;
            case "DIAMOND" -> axeMaterial = Material.DIAMOND_AXE;
            case "NETHERITE" -> axeMaterial = Material.NETHERITE_AXE;
            default -> {
            }
        }
        if (axeMaterial == null) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_AXE_INVALID_MATERIAL, args[0].toUpperCase()));
            return true;
        }
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.cmdGetAxeMaterialFromString(args[0].toUpperCase()))))) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        ItemStack item = utils.buildAxe(
                axeMaterial,
                (args.length > 2 && args[2].equalsIgnoreCase("true") && (sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_FULL_ENCHANT))))
        );
        if (!((Player) sender).getPlayer().getInventory().addItem(item).isEmpty()) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_AXE_INV_FULL, Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        } else {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_AXE_SUCCESS, Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        }
        return true;
    }

    private boolean giveAxe(CommandSender sender, String[] args) {
        Audience senderAudience = audiences.sender(sender);
        if (!(
                sender.isOp()
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_WOOD))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_STONE))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_GOLD))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_IRON))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_DIAMOND))
                        || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_NETHERITE))
        )) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        if (args.length < 3) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_AXE_USAGE));
            return true;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_AXE_PLAYER_NOT_FOUND, args[1]));
            return true;
        } else if (!target.isOnline()) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_AXE_PLAYER_OFFLINE, target.getName()));
            return true;
        }
        Material axeMaterial = null;
        switch (args[2].toUpperCase()) {
            case "WOOD" -> axeMaterial = Material.WOODEN_AXE;
            case "STONE" -> axeMaterial = Material.STONE_AXE;
            case "GOLD" -> axeMaterial = Material.GOLDEN_AXE;
            case "IRON" -> axeMaterial = Material.IRON_AXE;
            case "DIAMOND" -> axeMaterial = Material.DIAMOND_AXE;
            case "NETHERITE" -> axeMaterial = Material.NETHERITE_AXE;
            default -> {
            }
        }
        if (axeMaterial == null) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_AXE_INVALID_MATERIAL, target.getName(), args[2].toUpperCase()));
            return true;
        }
        if (!(sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.cmdGiveAxeMaterialFromString(args[2].toUpperCase()))))) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
            return true;
        }
        ItemStack item = utils.buildAxe(
                axeMaterial,
                (args.length > 3 && args[3].equalsIgnoreCase("true") && (sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_FULL_ENCHANT)))));
        if (!target.getInventory().addItem(item).isEmpty()) {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_AXE_INV_FULL, Component.text(target.getName()), Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        } else {
            senderAudience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_AXE_SUCCESS, Component.text(target.getName()), Component.translatable(getTranslationItemKey(item.getType().getKey().toString()))));
        }
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!enabled)
            return true;
        switch (command.getName()) {
            case "replenish-get" -> {
                if (!sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET))) {
                    audiences.sender(sender).sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
                }
            }
            case "replenish-give" -> {
                if (!sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE))) {
                    audiences.sender(sender).sendMessage(messageBuilder.build(MessageBuilder.Message.NO_PERMISSION));
                }
            }
        }
        if (args.length < 1) {
            Audience audience = audiences.sender(sender);
            switch (command.getName()) {
                case "replenish-get" -> audience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_USAGE));
                case "replenish-give" -> audience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_USAGE));
            }
            return true;
        }
        return switch (command.getName()) {
            case "replenish-get" ->
                switch (args[0].toUpperCase()) {
                    case "BOOK" -> getBook(sender);
                    case "HOE" -> getHoe(sender, args);
                    case "AXE" -> getAxe(sender, args);
                    default -> sendGetUsage(sender);
                };
            case "replenish-give" ->
                switch (args[0].toUpperCase()) {
                    case "BOOK" -> giveBook(sender, args);
                    case "HOE" -> giveHoe(sender, args);
                    case "AXE" -> giveAxe(sender, args);
                    default -> sendGiveUsage(sender);
                };
            default -> false;
        };
    }

    private boolean sendGetUsage(CommandSender sender) {
        Audience audience = audiences.sender(sender);
        audience.sendMessage(messageBuilder.build(MessageBuilder.Message.GET_USAGE));
        return true;
    }

    private boolean sendGiveUsage(CommandSender sender) {
        Audience audience = audiences.sender(sender);
        audience.sendMessage(messageBuilder.build(MessageBuilder.Message.GIVE_USAGE));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        if (!enabled)
            return list;
        switch (command.getName()) {
            case "replenish-give" -> {
                if (args.length < 1)
                    return list;
                if (args.length == 1) {
                    if (sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_BOOK)))
                        list.add("BOOK");
                    if (
                            sender.isOp()
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_WOOD))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_STONE))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_GOLD))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_IRON))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_DIAMOND))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_NETHERITE))
                    )
                        list.add("HOE");
                    if (
                            sender.isOp()
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_WOOD))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_STONE))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_GOLD))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_IRON))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_DIAMOND))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_NETHERITE))
                    )
                        list.add("AXE");
                } else if (args.length == 2) {
                    if (sender.isOp() || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_WOOD))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_STONE))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_GOLD))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_IRON))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_DIAMOND))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_NETHERITE))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_WOOD))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_STONE))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_GOLD))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_IRON))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_DIAMOND))
                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_NETHERITE)))
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            list.add(player.getName());
                        }
                } else if (args.length == 3) {
                    switch (args[0].toUpperCase()) {
                        case "HOE" -> {
                            if (!(
                                    sender.isOp()
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_WOOD))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_STONE))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_GOLD))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_IRON))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_DIAMOND))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_NETHERITE))
                            ))
                                return list;
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_WOOD)))
                                list.add("WOOD");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_STONE)))
                                list.add("STONE");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_GOLD)))
                                list.add("GOLD");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_IRON)))
                                list.add("IRON");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_DIAMOND)))
                                list.add("DIAMOND");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_MATERIAL_NETHERITE)))
                                list.add("NETHERITE");
                        }
                        case "AXE" -> {
                            if (!(
                                    sender.isOp()
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_WOOD))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_STONE))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_GOLD))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_IRON))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_DIAMOND))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_NETHERITE))
                            ))
                                return list;
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_WOOD)))
                                list.add("WOOD");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_STONE)))
                                list.add("STONE");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_GOLD)))
                                list.add("GOLD");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_IRON)))
                                list.add("IRON");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_DIAMOND)))
                                list.add("DIAMOND");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_MATERIAL_NETHERITE)))
                                list.add("NETHERITE");
                        }
                    }
                } else if (args.length == 4) {
                    switch (args[0].toUpperCase()) {
                        case "HOE" -> {
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_HOE_FULL_ENCHANT)))
                                list.addAll(List.of("true", "false"));
                        }
                        case "AXE" -> {
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GIVE_AXE_FULL_ENCHANT)))
                                list.addAll(List.of("true", "false"));
                        }
                    }
                }
            }
            case "replenish-get" -> {
                if (args.length < 1)
                    return list;
                if (args.length == 1) {
                    if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_BOOK)))
                        list.add("BOOK");
                    if (
                            sender.isOp()
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_WOOD))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_STONE))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_GOLD))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_IRON))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_DIAMOND))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_NETHERITE))
                    )
                        list.add("HOE");
                    if (
                            sender.isOp()
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_WOOD))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_STONE))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_GOLD))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_IRON))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_DIAMOND))
                                    || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_NETHERITE))
                    )
                        list.add("AXE");
                }
                if (args.length == 2) {
                    switch (args[0].toUpperCase()) {
                        case "BOOK" -> {
                            return list;
                        }
                        case "HOE" -> {
                            if (!(
                                    sender.isOp()
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_WOOD))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_STONE))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_GOLD))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_IRON))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_DIAMOND))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_MATERIAL_NETHERITE))
                            ))
                                return list;
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
                        }
                        case "AXE" -> {
                            if (!(
                                    sender.isOp()
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_WOOD))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_STONE))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_GOLD))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_IRON))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_DIAMOND))
                                            || sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_NETHERITE))
                            ))
                                return list;
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_WOOD)))
                                list.add("WOOD");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_STONE)))
                                list.add("STONE");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_GOLD)))
                                list.add("GOLD");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_IRON)))
                                list.add("IRON");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_DIAMOND)))
                                list.add("DIAMOND");
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_MATERIAL_NETHERITE)))
                                list.add("NETHERITE");
                        }
                    }
                } else if (args.length == 3) {
                    switch (args[0].toUpperCase()) {
                        case "HOE" -> {
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_HOE_FULL_ENCHANT)))
                                list.addAll(List.of("true", "false"));
                        }
                        case "AXE" -> {
                            if (sender.hasPermission(config.getPermission(MainConfiguration.Permission.CMD_GET_AXE_FULL_ENCHANT)))
                                list.addAll(List.of("true", "false"));
                        }
                    }
                }
            }
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

}
