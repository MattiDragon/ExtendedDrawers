package io.github.mattidragon.extendeddrawers.mixin.patchouli;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.patchouli.xplat.XplatModContainer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.function.Predicate;

@Pseudo
@Mixin(targets = "vazkii.patchouli.common.book.BookRegistry", remap = false)
public abstract class BookRegistryMixin {
    @Shadow
    private static void walk(Path root, Predicate<Path> rootFilter, BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles, int maxDepth) throws IOException {
        throw new IllegalStateException("shadow");
    }

    @Inject(require = 0, method = "findFiles(Lvazkii/patchouli/xplat/XplatModContainer;Ljava/lang/String;Ljava/util/function/Predicate;Ljava/util/function/BiFunction;ZI)V",
        at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lvazkii/patchouli/common/book/BookRegistry;walk(Ljava/nio/file/Path;Ljava/util/function/Predicate;Ljava/util/function/BiFunction;ZI)V"))
    private static void extended_drawers$injectRoots(XplatModContainer mod, String base, Predicate<Path> rootFilter, BiFunction<Path, Path, Boolean> processor, boolean visitAllFiles, int maxDepth, CallbackInfo ci) throws IOException {
        var container = ((FabricXplatModContainerAccess) mod).getContainer();

        var roots = container.getRootPaths();
        if (roots.size() > 1) {
            for (var root : roots) {
                walk(root.resolve(base), rootFilter, processor, visitAllFiles, maxDepth);
            }
        }
    }
}
