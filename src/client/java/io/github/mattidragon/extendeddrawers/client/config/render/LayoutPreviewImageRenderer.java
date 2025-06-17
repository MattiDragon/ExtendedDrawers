package io.github.mattidragon.extendeddrawers.client.config.render;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.config.ConfigData;
import io.github.mattidragon.extendeddrawers.config.category.ClientCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.texture.SpriteAtlasTexture;
import org.joml.Matrix3x2f;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

public class LayoutPreviewImageRenderer implements ImageRenderer {
    private Option<Float> smallItemScale = null;
    private Option<Float> largeItemScale = null;
    private Option<Float> smallTextScale = null;
    private Option<Float> largeTextScale = null;
    private Option<Float> textOffset = null;
    private boolean initialized = false;

    public void init(Option<Float> smallItemScale, Option<Float> largeItemScale, Option<Float> smallTextScale, Option<Float> largeTextScale, Option<Float> textOffset) {
        this.smallItemScale = smallItemScale;
        this.largeItemScale = largeItemScale;
        this.smallTextScale = smallTextScale;
        this.largeTextScale = largeTextScale;
        this.textOffset = textOffset;
        this.initialized = true;
    }

    @Override
    public int render(DrawContext context, int x, int y, int renderWidth, float tickDelta) {
        if (!initialized) return 0;

        @SuppressWarnings("deprecation")
        var atlas = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
        var size = renderWidth / 3;
        var config = ExtendedDrawers.CONFIG.get();
        var client = config.client();
        var newConfig = new ConfigData(
                new ClientCategory(client.itemRenderDistance(),
                        client.iconRenderDistance(),
                        client.textRenderDistance(),
                        client.displayEmptyCount(),
                        new ClientCategory.LayoutGroup(smallItemScale.pendingValue(),
                                largeItemScale.pendingValue(),
                                smallTextScale.pendingValue(),
                                largeTextScale.pendingValue(),
                                textOffset.pendingValue()),
                        client.icons()),
                config.storage(),
                config.misc());

        var matrices = context.getMatrices();

        context.drawSpriteStretched(RenderPipelines.GUI_TEXTURED, atlas.apply(id("block/single_drawer")), x, y, size, size);
        context.drawSpriteStretched(RenderPipelines.GUI_TEXTURED, atlas.apply(id("block/quad_drawer")), x + size, y, size, size);
        context.drawSpriteStretched(RenderPipelines.GUI_TEXTURED, atlas.apply(id("block/compacting_drawer")), x + 2 * size, y, size, size);

        context.state.addSpecialElement(new LayoutPreviewRenderState(
                newConfig, size, new Matrix3x2f(matrices), x, y, x + renderWidth, y + size, 1, context.scissorStack.peekLast()
        ));

        return size;
    }

    @Override
    public void close() {

    }
}
