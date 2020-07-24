package io.github.alloffabric.artis.compat.mixin;

import io.github.alloffabric.artis.compat.rei.ArtisDisplay;
import io.github.alloffabric.artis.inventory.ArtisCraftingController;
import me.shedaniel.rei.api.AutoTransferHandler;
import me.shedaniel.rei.plugin.autocrafting.DefaultCategoryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultCategoryHandler.class)
public class DefaultCategoryHandlerMixin {
    @Inject(method = "handle(Lme/shedaniel/rei/api/AutoTransferHandler$Context;)Lme/shedaniel/rei/api/AutoTransferHandler$Result;", at = @At("HEAD"), cancellable = true)
    private void handle(AutoTransferHandler.Context context, CallbackInfoReturnable<AutoTransferHandler.Result> callbackInfoReturnable) {
        if (context.getRecipe() instanceof ArtisDisplay || context.getContainer() instanceof ArtisCraftingController) {
            callbackInfoReturnable.setReturnValue(AutoTransferHandler.Result.createNotApplicable());
        }
    }
}
