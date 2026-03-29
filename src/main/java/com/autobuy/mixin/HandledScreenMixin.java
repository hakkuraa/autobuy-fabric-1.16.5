package com.autobuy.mixin;

import com.autobuy.AuctionHistoryOverlay;
import com.autobuy.AutoBuyMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({HandledScreen.class})
public abstract class HandledScreenMixin {
   private static final String CREDIT_TEXT = "\u00A7l\u00A7nBy medny_byk";
   @Shadow
   protected int x;
   @Shadow
   protected int y;
   @Shadow
   protected int backgroundWidth;

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      HandledScreen<?> screen = (HandledScreen)(Object)this;
      String title = screen.getTitle().getString().toLowerCase();
      if (!title.contains("аукцион") && !title.contains("auction") && !title.contains("ah")) {
         AuctionHistoryOverlay.setVisible(false);
      } else {
         AuctionHistoryOverlay.setVisible(true);
         AuctionHistoryOverlay.render(matrices);
         String tgText = CREDIT_TEXT;
         int tgX = this.x + this.backgroundWidth - 75;
         int tgY = this.y - 28;
         int textWidth = MinecraftClient.getInstance().textRenderer.getWidth(CREDIT_TEXT);
         DrawableHelper.fill(matrices, tgX - 5, tgY - 2, tgX + textWidth + 5, tgY + 12, -2013265920);
         MinecraftClient.getInstance().textRenderer.draw(matrices, tgText, (float)tgX, (float)tgY, -5601025);
         int btnX = this.x + this.backgroundWidth - 62;
         int btnY = this.y - 16;
         int btnW = 60;
         int btnH = 14;
         int bgColor = AutoBuyMod.autoBuyEnabled ? -872393472 : -866844672;
         int borderColor = AutoBuyMod.autoBuyEnabled ? -16711936 : -65536;
         int textColor = AutoBuyMod.autoBuyEnabled ? '\uff00' : 16729156;
         DrawableHelper.fill(matrices, btnX, btnY, btnX + btnW, btnY + btnH, bgColor);
         DrawableHelper.fill(matrices, btnX, btnY, btnX + btnW, btnY + 1, borderColor);
         DrawableHelper.fill(matrices, btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, borderColor);
         DrawableHelper.fill(matrices, btnX, btnY, btnX + 1, btnY + btnH, borderColor);
         DrawableHelper.fill(matrices, btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, borderColor);
         String label = "AutoBuy " + (AutoBuyMod.autoBuyEnabled ? "✔ ON" : "✘ OFF");
         MinecraftClient.getInstance().textRenderer.draw(matrices, label, (float)(btnX + 4), (float)(btnY + 3), textColor);
      }

   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      HandledScreen<?> screen = (HandledScreen)(Object)this;
      String title = screen.getTitle().getString().toLowerCase();
      if (title.contains("аукцион") || title.contains("auction") || title.contains("ah")) {
         int btnX = this.x + this.backgroundWidth - 62;
         int btnY = this.y - 16;
         int btnW = 60;
         int btnH = 14;
         if (mouseX >= (double)btnX && mouseX <= (double)(btnX + btnW) && mouseY >= (double)btnY && mouseY <= (double)(btnY + btnH)) {
            AutoBuyMod.autoBuyEnabled = !AutoBuyMod.autoBuyEnabled;
            cir.setReturnValue(true);
            return;
         }
      }

   }
}
