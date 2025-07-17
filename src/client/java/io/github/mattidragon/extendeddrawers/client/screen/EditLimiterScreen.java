package io.github.mattidragon.extendeddrawers.client.screen;

import io.github.mattidragon.extendeddrawers.ExtendedDrawers;
import io.github.mattidragon.extendeddrawers.networking.SetLimiterLimitPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class EditLimiterScreen extends Screen {
    private static final Identifier TEXTURE = ExtendedDrawers.id("textures/gui/limiter.png");
    private final int slot;
    private final Long previous;
    private TextFieldWidget textField;
    private ButtonWidget doneButton;
    private ButtonWidget clearButton;

    public EditLimiterScreen(Text title, int slot, Long previous) {
        super(title);
        this.slot = slot;
        this.previous = previous;
    }

    @Override
    protected void init() {
        if (client == null) return;

        doneButton = addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
                    try {
                        var limit = Long.parseLong(textField.getText());
                        if (limit <= 0) throw new NumberFormatException();
                        ClientPlayNetworking.send(new SetLimiterLimitPayload(slot, limit));
                    } catch (NumberFormatException ignored) {}
                    close();
                })
                .position(width / 2 - 58, height / 2 + 6)
                .width(38)
                .build());

        clearButton = addDrawableChild(ButtonWidget.builder(Text.translatable("item.extended_drawers.limiter.clear"), button -> {
                    ClientPlayNetworking.send(new SetLimiterLimitPayload(slot, -1));
                    close();
                })
                .position(width / 2 - 19, height / 2 + 6)
                .width(38)
                .build());

        addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> close())
                .position(width / 2 + 20, height / 2 + 6)
                .width(38)
                .build());
        
        textField = addDrawableChild(new TextFieldWidget(client.textRenderer, width / 2 - 58, height / 2 - 16, 116, 20, Text.literal("")));
        textField.setRenderTextProvider((text, index) -> { // Render invalid text as red
            var style = isValid(text) ? Style.EMPTY : Style.EMPTY.withColor(Formatting.RED);
            return OrderedText.styledForwardsVisitedString(text, style);
        });
        textField.setChangedListener(value -> doneButton.active = isValid(textField.getText()));
        if (previous != null) textField.setText(String.valueOf(previous));
        setFocused(textField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if (isValid(textField.getText())) {
                doneButton.onPress();
            } else if (textField.getText().isBlank()) {
                clearButton.onPress();
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
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
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        renderInGameBackground(context);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, width / 2 - 64, height / 2 - 32, 0, 0, 128, 64, 128, 64);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        if (client == null) return;
        context.drawText(client.textRenderer, getTitle(), width / 2 - 58, height / 2 - 16 - 10, 0xff404040, false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
