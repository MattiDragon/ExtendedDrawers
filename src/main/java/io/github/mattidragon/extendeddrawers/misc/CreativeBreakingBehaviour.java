package io.github.mattidragon.extendeddrawers.misc;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

@SuppressWarnings("unused") // Accessed by values()
public enum CreativeBreakingBehaviour implements StringIdentifiable {
    BREAK, MINE, NO_BREAK;

    public static final Codec<CreativeBreakingBehaviour> CODEC = StringIdentifiable.createCodec(CreativeBreakingBehaviour::values);

    public Text getDisplayName() {
        return Text.translatable("config.extended_drawers.creativeBreakingBehaviour." + asString());
    }

    @Override
    public String asString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
