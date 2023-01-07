package de.drachir000.survival.replenishenchantment;

import de.drachir000.survival.replenishenchantment.config.LanguageConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageBuilder {

    private final LanguageConfiguration config;

    public MessageBuilder(LanguageConfiguration languageConfiguration) {
        this.config = languageConfiguration;
    }

    /**
     * Builds the Message
     *
     * @param message the message to build
     *
     * @return The built message as a Component
     * @since 0.0.8
     * */
    public Component build(Message message) {
        return LegacyComponentSerializer.legacySection().deserialize(config.getConfig().getString(message.configPath));
    }

    /**
     * Builds the Message using the given placeholders
     *
     * @param message the message to build
     * @param placeholder the placeholders to replace in order
     *                    first given placeholder replaces "{0}",
     *                    second replaces "{1}" and so on
     *
     * @return The built message as a Component
     * @since 0.0.8
     * */
    public Component build(Message message, String... placeholder) {
        String rawMessage = config.getConfig().getString(message.configPath);
        for (int i = 0; i < placeholder.length; i++) {
            rawMessage = rawMessage.replace("{" + i + "}", placeholder[i]);
        }
        return LegacyComponentSerializer.legacySection().deserialize(rawMessage);
    }

    /**
     * Builds the Message using the given placeholders
     *
     * @param message the message to build
     * @param placeholder the placeholders to replace in order
     *                    first given placeholder replaces "{0}",
     *                    second replaces "{1}" and so on
     *
     * @return The built message as a Component
     * @since 0.0.11
     * */
    public Component build(Message message, Component... placeholder) {
        String rawMessage = config.getConfig().getString(message.configPath);
        List<Integer> placeholderSequence = new ArrayList<>();
        String regex;
        if(placeholder.length > 1) {
            regex = "\\{(0|[1-" + (placeholder.length-1) + "])\\}";
        } else {
            regex = "\\{(0)\\}";
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rawMessage);
        while (matcher.find()) {
            String s = matcher.group(1);
            int index = Integer.parseInt(s);
            placeholderSequence.add(index);
        }

        Component res = Component.text("");

        String[] parts = rawMessage.split(regex);
        Component last = res;
        for (int i = 0; i < parts.length; i++) {
            res = res.append(LegacyComponentSerializer.legacySection().deserialize(parts[i]).applyFallbackStyle(last.style()));
            last = lastChildren(LegacyComponentSerializer.legacySection().deserialize(parts[i]).applyFallbackStyle(last.style()));
            if (placeholderSequence.size() > i) {
                res = res.append(placeholder[placeholderSequence.get(i)].applyFallbackStyle(last.style()));
                last = lastChildren(placeholder[placeholderSequence.get(i)].applyFallbackStyle(last.style()));
            }
        }

        return res;
    }

    private Component lastChildren(Component component) {
        if (component.children().isEmpty())
            return component;
        return component.children().get(component.children().size()-1);
    }

    /**
     * Serializes the message Component to a String
     *
     * @param message the message as a Component to serialize
     *
     * @return The message as a String
     * @since 0.0.8
     * */
    public String serialize(Component message) {
        return LegacyComponentSerializer.legacySection().serialize(message);
    }

    public enum Message {

        PLAYER_ONLY_COMMAND("player-only-command"),
        NO_PERMISSION("no-permission"),
        GET_BOOK_SUCCESS("get-book-success"),
        GET_BOOK_INV_FULL("get-book-inv-full"),
        GIVE_BOOK_PLAYER_NOT_FOUND("give-book-player-not-found"),
        GIVE_BOOK_PLAYER_OFFLINE("give-book-player-offline"),
        GIVE_BOOK_INV_FULL("give-book-inv-full"),
        GIVE_BOOK_SUCCESS("give-book-success"),
        GET_HOE_USAGE("get-hoe-usage"),
        GET_HOE_INVALID_MATERIAL("get-hoe-invalid-material"),
        GET_HOE_INV_FULL("get-hoe-inv-full"),
        GET_HOE_SUCCESS("get-hoe-success"),
        GIVE_HOE_USAGE("give-hoe-usage"),
        GIVE_HOE_PLAYER_NOT_FOUND("give-hoe-player-not-found"),
        GIVE_HOE_PLAYER_OFFLINE("give-hoe-player-offline"),
        GIVE_HOE_INVALID_MATERIAL("give-hoe-invalid-material"),
        GIVE_HOE_INV_FULL("give-hoe-inv-full"),
        GIVE_HOE_SUCCESS("give-hoe-success"),
        BOOK_LORE("book-lore");

        private final String configPath;

        Message(String configPath) {
            this.configPath = configPath;
        }

    }

}
