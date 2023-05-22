package io.github.mattidragon.extendeddrawers.misc;

import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

@SuppressWarnings("unused") // Accessed by values()
public enum CreativeExtractionBehaviour implements StringIdentifiable {
    FRONT_MINE(true, true),
    ALL_MINE(false, true),
    FRONT_NO_BREAK(true, false),
    ALL_NO_BREAK(false, false),
    NORMAL(false, false);

    @SuppressWarnings("deprecation")
    public static final Codec<CreativeExtractionBehaviour> CODEC = StringIdentifiable.createCodec(CreativeExtractionBehaviour::values);
    
    private final boolean frontOnly;
    private final boolean allowMine;
    
    CreativeExtractionBehaviour(boolean frontOnly, boolean allowMine) {
        this.frontOnly = frontOnly;
        this.allowMine = allowMine;
    }
    
    public boolean isFrontOnly() {
        return frontOnly;
    }
    
    public boolean isAllowMine() {
        return allowMine;
    }

    @Override
    public String asString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
