package de.drachir000.survival.replenishenchantment;

import de.drachir000.survival.replenishenchantment.config.LanguageConfiguration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class MessageBuilder {

    private final LanguageConfiguration config;
    private final ReplenishEnchantment inst;

    public MessageBuilder(ReplenishEnchantment inst, LanguageConfiguration languageConfiguration) {
        this.config = languageConfiguration;
        this.inst = inst;
    }

    /**
     * Builds the Message
     *
     * @param message the message to build
     *
     * @return The built message as a Component
     * @since 0.0.8
     * */
    public Component build(@NotNull Message message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(config.getConfig().getString(message.configPath));
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
    public Component build(@NotNull Message message, String... placeholder) {
        String s = config.getConfig().getString(message.configPath);
        for (int i = 0; i < placeholder.length; i++) {
            s = s.replace("{" + i + "}", placeholder[i]);
        }
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
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
        return LegacyComponentSerializer.legacyAmpersand().serialize(message);
    }

    public enum Message {

        PLAYER_ONLY_COMMAND("player-only-command"),
        NO_PERMISSION("no-permission"),
        GET_BOOK_SUCCESS("get-book-success"),
        GET_BOOK_INV_FULL("get-book-inv-full");

        private String configPath;

        Message(String configPath) {
            this.configPath = configPath;
        }

    }

}
