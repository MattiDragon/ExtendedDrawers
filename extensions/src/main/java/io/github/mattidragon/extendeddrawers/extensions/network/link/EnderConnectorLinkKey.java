package io.github.mattidragon.extendeddrawers.extensions.network.link;

import com.kneelawk.graphlib.api.graph.LinkHolder;
import com.kneelawk.graphlib.api.graph.user.LinkKey;
import com.kneelawk.graphlib.api.graph.user.LinkKeyType;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import static io.github.mattidragon.extendeddrawers.extensions.ExtendedDrawersExtensions.id;

public class EnderConnectorLinkKey implements LinkKey {
    public static final Identifier ID = id("ender_connector");
    public static final EnderConnectorLinkKey INSTANCE = new EnderConnectorLinkKey();
    public static final LinkKeyType TYPE = LinkKeyType.of(ID, () -> INSTANCE);

    private EnderConnectorLinkKey() {
    }

    @Override
    public @NotNull LinkKeyType getType() {
        return TYPE;
    }

    @Override
    public boolean isAutomaticRemoval(@NotNull LinkHolder<LinkKey> holder) {
        return false;
    }
}
