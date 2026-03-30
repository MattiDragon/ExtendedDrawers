package io.github.mattidragon.extendeddrawers.client.config.render;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.gui.image.ImageRenderer;
import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.config.ConfigData;
import io.github.mattidragon.extendeddrawers.config.category.ClientCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;

import static io.github.mattidragon.extendeddrawers.ExtendedDrawers.id;
import static io.github.mattidragon.extendeddrawers.client.config.render.LayoutPreviewRenderer.areComponentsBound;

@SuppressWarnings("NotNullFieldNotInitialized")
public class LayoutPreviewImageRenderer implements ImageRenderer {
    private Option<Float> smallItemScale;
    private Option<Float> largeItemScale;
    private Option<Float> smallTextScale;
    private Option<Float> largeTextScale;
    private Option<Float> smallTextOffset;
    private Option<Float> largeTextOffset;
    private boolean initialized = false;

    public void init(Option<Float> smallItemScale, Option<Float> largeItemScale, Option<Float> smallTextScale, Option<Float> largeTextScale, Option<Float> smallTextOffset, Option<Float> largeTextOffset) {
        this.smallItemScale = smallItemScale;
        this.largeItemScale = largeItemScale;
        this.smallTextScale = smallTextScale;
        this.largeTextScale = largeTextScale;
        this.smallTextOffset = smallTextOffset;
        this.largeTextOffset = largeTextOffset;
        this.initialized = true;
    }

    @Override
    public int render(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta) {
        if (!initialized) return 0;

        if (!areComponentsBound()) {
            return renderPlaceHolder(graphics, x, y, renderWidth, tickDelta);
        }

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
                        client.indicateFullDrawers(),
                        new ClientCategory.LayoutGroup(smallItemScale.pendingValue(),
                                largeItemScale.pendingValue(),
                                smallTextScale.pendingValue(),
                                largeTextScale.pendingValue(),
                                smallTextOffset.pendingValue(),
                                largeTextOffset.pendingValue()),
                        client.icons()),
                config.storage(),
                config.misc());

        var matrices = graphics.pose();

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, graphics.getSprite(new SpriteId(atlas, id("block/single_drawer"))), x, y, size, size);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, graphics.getSprite(new SpriteId(atlas, id("block/quad_drawer"))), x + size, y, size, size);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, graphics.getSprite(new SpriteId(atlas, id("block/compacting_drawer"))), x + 2 * size, y, size, size);

        graphics.guiRenderState.addPicturesInPictureState(new LayoutPreviewRenderState(
                newConfig, size, new Matrix3x2f(matrices), x, y, x + renderWidth, y + size, 1, graphics.scissorStack.peek()
        ));

        return size;
    }

    private int renderPlaceHolder(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta) {
        var font = Minecraft.getInstance().font;
        graphics.centeredText(font, Component.translatable("config.extended_drawers.info.previewNotAvailable").withStyle(ChatFormatting.RED), x + renderWidth / 2, y + 2, 0xffffffff);
        graphics.centeredText(font, Component.translatable("config.extended_drawers.info.previewNotAvailable.reason").withStyle(ChatFormatting.YELLOW), x + renderWidth / 2, y + 12, 0xffffffff);

        return 20;
    }

    @Override
    public void close() {

    }
}
