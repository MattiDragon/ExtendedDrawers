package io.github.mattidragon.extendeddrawers.client.screen;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.networking.SetLimiterLimitPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class EditLimiterScreen extends Screen {
    private static final Identifier TEXTURE = ExtendedDrawers.id("textures/gui/limiter.png");
    private final int slot;
    private final @Nullable Long previous;

    @SuppressWarnings("NotNullFieldNotInitialized")
    private EditBox textField;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Button doneButton;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Button clearButton;

    public EditLimiterScreen(Component title, int slot, @Nullable Long previous) {
        super(title);
        this.slot = slot;
        this.previous = previous;
    }

    @Override
    protected void init() {
        doneButton = addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> {
                    try {
                        var limit = Long.parseLong(textField.getValue());
                        if (limit <= 0) throw new NumberFormatException();
                        ClientPlayNetworking.send(new SetLimiterLimitPayload(slot, limit));
                    } catch (NumberFormatException ignored) {}
                    onClose();
                })
                .pos(width / 2 - 58, height / 2 + 6)
                .width(38)
                .build());

        clearButton = addRenderableWidget(Button.builder(Component.translatable("item.extended_drawers.limiter.clear"), button -> {
                    ClientPlayNetworking.send(new SetLimiterLimitPayload(slot, -1));
                    onClose();
                })
                .pos(width / 2 - 19, height / 2 + 6)
                .width(38)
                .build());

        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, button -> onClose())
                .pos(width / 2 + 20, height / 2 + 6)
                .width(38)
                .build());
        
        textField = addRenderableWidget(new EditBox(minecraft.font, width / 2 - 58, height / 2 - 16, 116, 20, Component.literal("")));
        textField.addFormatter((text, firstCharacterIndex) -> { // Render invalid text as red
            var style = isValid(text) ? Style.EMPTY : Style.EMPTY.withColor(ChatFormatting.RED);
            return FormattedCharSequence.forward(text, style);
        });
        textField.setResponder(value -> doneButton.active = isValid(textField.getValue()));
        if (previous != null) textField.setValue(String.valueOf(previous));
        setFocused(textField);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.key() == GLFW.GLFW_KEY_ENTER || input.key() == GLFW.GLFW_KEY_KP_ENTER) {
            if (isValid(textField.getValue())) {
                doneButton.onPress(input);
            } else if (textField.getValue().isBlank()) {
                clearButton.onPress(input);
            }
        }

        return super.keyPressed(input);
    }

    private static boolean isValid(String text) {
        try {
            var limit = Long.parseLong(text);
            if (limit <= 0) throw new NumberFormatException();
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderTransparentBackground(context);
        context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, width / 2 - 64, height / 2 - 32, 0, 0, 128, 64, 128, 64);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawString(minecraft.font, getTitle(), width / 2 - 58, height / 2 - 16 - 10, 0xff404040, false);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
