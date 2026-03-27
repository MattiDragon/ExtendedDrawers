package io.github.mattidragon.extendeddrawers.client.config.render;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.config.ConfigData;
import io.github.mattidragon.extendeddrawers.config.category.ClientCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import org.joml.Matrix3x2f;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;

@SuppressWarnings("NotNullFieldNotInitialized")
public class LayoutPreviewImageRenderer implements ImageRenderer {
    private Option<Float> smallItemScale;
    private Option<Float> largeItemScale;
    private Option<Float> smallTextScale;
    private Option<Float> largeTextScale;
    private Option<Float> textOffset;
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
    public int render(GuiGraphics context, int x, int y, int renderWidth, float tickDelta) {
        if (!initialized) return 0;

        @SuppressWarnings("deprecation")
        var atlas = TextureAtlas.LOCATION_BLOCKS;
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

        var matrices = context.pose();

        context.blitSprite(RenderPipelines.GUI_TEXTURED, context.getSprite(new Material(atlas, id("block/single_drawer"))), x, y, size, size);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, context.getSprite(new Material(atlas, id("block/quad_drawer"))), x + size, y, size, size);
        context.blitSprite(RenderPipelines.GUI_TEXTURED, context.getSprite(new Material(atlas, id("block/compacting_drawer"))), x + 2 * size, y, size, size);

        context.guiRenderState.submitPicturesInPictureState(new LayoutPreviewRenderState(
                newConfig, size, new Matrix3x2f(matrices), x, y, x + renderWidth, y + size, 1, context.scissorStack.peek()
        ));

        return size;
    }

    @Override
    public void close() {

    }
}
