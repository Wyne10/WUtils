package me.wyne.wutils.i18n.language.interpretation;

import me.wyne.wutils.i18n.language.validation.StringValidator;

import java.util.function.Function;

public enum ComponentInterpreters {
    LEGACY(LegacyInterpreter::new),
    ENHANCED_LEGACY(EnhancedLegacyInterpreter::new),
    MINI_MESSAGE(MiniMessageInterpreter::new),
    ITEM_LEGACY(ItemLegacyInterpreter::new),
    ITEM_ENHANCED_LEGACY(ItemEnhancedLegacyInterpreter::new),
    ITEM_MINI_MESSAGE(ItemMiniMessageInterpreter::new);

    private final Function<StringValidator, ComponentInterpreter> factory;

    ComponentInterpreters(Function<StringValidator, ComponentInterpreter> factory) {
        this.factory = factory;
    }

    public ComponentInterpreter get(StringValidator validator) {
        return factory.apply(validator);
    }
}
