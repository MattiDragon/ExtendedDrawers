package io.github.mattidragon.extendeddrawers.client.mixin;

import com.mojang.authlib.GameProfile;
import io.github.mattidragon.extendeddrawers.client.screen.EditLimiterScreen;
import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends Player {
    @Shadow @Final protected Minecraft minecraft;

    public ClientPlayerEntityMixin(Level world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "openItemGui", at = @At("HEAD"))
    private void extended_drawers$hackLimiterIntoBookCode(ItemStack stack, InteractionHand hand, CallbackInfo ci) {
        if (stack.is(ModItems.LIMITER)) {
            var limit = Optional.ofNullable(stack.get(ModDataComponents.LIMITER_LIMIT))
                    .map(LimiterLimitComponent::limit)
                    .orElse(null);

            minecraft.setScreen(new EditLimiterScreen(stack.getHoverName(), hand == InteractionHand.MAIN_HAND ? getInventory().getSelectedSlot() : 40, limit));
        }
    }
}
