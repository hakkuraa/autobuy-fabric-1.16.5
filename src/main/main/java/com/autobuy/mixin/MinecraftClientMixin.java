package com.autobuy.mixin;

import com.autobuy.AutoBuyMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({MinecraftClient.class})
public abstract class MinecraftClientMixin {
   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void autobuy$onTick(CallbackInfo ci) {
      AutoBuyMod.onClientTick((MinecraftClient)(Object)this);
   }
}
