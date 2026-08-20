package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.StringValidator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Factory for the available {@link ComponentInterpreter} implementations. {@code ENHANCED_LEGACY} and
 * {@code ITEM_ENHANCED_LEGACY} require the {@code enhancedlegacytext} dependency on the consumer's
 * classpath — this module only declares it {@code compileOnly}.
 */
public enum ComponentInterpreters {
    LEGACY(LegacyInterpreter::new),
    ENHANCED_LEGACY(EnhancedLegacyInterpreter::new),
    MINI_MESSAGE(MiniMessageInterpreter::new),
    ITEM_LEGACY(ItemLegacyInterpreter::new),
    ITEM_ENHANCED_LEGACY(ItemEnhancedLegacyInterpreter::new),
    ITEM_MINI_MESSAGE(ItemMiniMessageInterpreter::new);

    private final Function<StringValidator, ComponentInterpreter> factory;

    ComponentInterpreters(@NotNull Function<StringValidator, ComponentInterpreter> factory) {
        this.factory = factory;
    }

    /**
     * Creates a new interpreter of this kind, validating lookups with {@code validator}.
     */
    public @NotNull ComponentInterpreter get(@NotNull StringValidator validator) {
        return factory.apply(validator);
    }
}
