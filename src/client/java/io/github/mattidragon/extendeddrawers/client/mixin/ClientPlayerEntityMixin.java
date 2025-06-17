package io.github.mattidragon.extendeddrawers.client.mixin;

import com.mojang.authlib.GameProfile;
import io.github.mattidragon.extendeddrawers.client.screen.EditLimiterScreen;
import io.github.mattidragon.extendeddrawers.component.LimiterLimitComponent;
import io.github.mattidragon.extendeddrawers.registry.ModDataComponents;
import io.github.mattidragon.extendeddrawers.registry.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntity {
    @Shadow @Final protected MinecraftClient client;

    public ClientPlayerEntityMixin(World world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "useBook", at = @At("HEAD"))
    private void extended_drawers$hackLimiterIntoBookCode(ItemStack stack, Hand hand, CallbackInfo ci) {
        if (stack.isOf(ModItems.LIMITER)) {
            var limit = Optional.ofNullable(stack.get(ModDataComponents.LIMITER_LIMIT))
                    .map(LimiterLimitComponent::limit)
                    .orElse(null);

            client.setScreen(new EditLimiterScreen(stack.getName(), hand == Hand.MAIN_HAND ? getInventory().getSelectedSlot() : 40, limit));
        }
    }
}
