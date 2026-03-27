package io.github.mattidragon.extendeddrawers.misc;

import com.mojang.serialization.Codec;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

@SuppressWarnings("unused") // Accessed by values()
public enum CreativeBreakingBehaviour implements StringRepresentable {
    BREAK, MINE, NO_BREAK;

    public static final Codec<CreativeBreakingBehaviour> CODEC = StringRepresentable.fromEnum(CreativeBreakingBehaviour::values);

    public Component getDisplayName() {
        return Component.translatable("config.extended_drawers.creativeBreakingBehaviour." + getSerializedName());
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
