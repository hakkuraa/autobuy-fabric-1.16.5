package com.autobuy.mixin;

import com.autobuy.AuctionHistoryOverlay;
import com.autobuy.AutoBuyLogic;
import com.autobuy.AutoBuyMod;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_465.class})
public abstract class HandledScreenMixin {
   private static final String CREDIT_TEXT = "\u00A7l\u00A7nBy medny_byk";
   @Shadow
   protected int field_2776;
   @Shadow
   protected int field_2800;
   @Shadow
   protected int field_2792;

   @Inject(
      method = {"render"},
      at = {@At("TAIL")}
   )
   private void onRender(class_4587 matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
      class_465<?> screen = (class_465)(Object)this;
      String title = screen.method_25440().getString().toLowerCase();
      if (!title.contains("аукцион") && !title.contains("auction") && !title.contains("ah")) {
         AuctionHistoryOverlay.setVisible(false);
      } else {
         AuctionHistoryOverlay.setVisible(true);
         AuctionHistoryOverlay.render(matrices);
         String tgText = CREDIT_TEXT;
         int tgX = this.field_2776 + this.field_2792 - 75;
         int tgY = this.field_2800 - 28;
         int textWidth = class_310.method_1551().field_1772.method_1727(CREDIT_TEXT);
         class_332.method_25294(matrices, tgX - 5, tgY - 2, tgX + textWidth + 5, tgY + 12, -2013265920);
         class_310.method_1551().field_1772.method_1729(matrices, tgText, (float)tgX, (float)tgY, -5601025);
         int btnX = this.field_2776 + this.field_2792 - 62;
         int btnY = this.field_2800 - 16;
         int btnW = 60;
         int btnH = 14;
         int bgColor = AutoBuyMod.autoBuyEnabled ? -872393472 : -866844672;
         int borderColor = AutoBuyMod.autoBuyEnabled ? -16711936 : -65536;
         int textColor = AutoBuyMod.autoBuyEnabled ? '\uff00' : 16729156;
         class_332.method_25294(matrices, btnX, btnY, btnX + btnW, btnY + btnH, bgColor);
         class_332.method_25294(matrices, btnX, btnY, btnX + btnW, btnY + 1, borderColor);
         class_332.method_25294(matrices, btnX, btnY + btnH - 1, btnX + btnW, btnY + btnH, borderColor);
         class_332.method_25294(matrices, btnX, btnY, btnX + 1, btnY + btnH, borderColor);
         class_332.method_25294(matrices, btnX + btnW - 1, btnY, btnX + btnW, btnY + btnH, borderColor);
         String label = "AutoBuy " + (AutoBuyMod.autoBuyEnabled ? "✔ ON" : "✘ OFF");
         class_310.method_1551().field_1772.method_1729(matrices, label, (float)(btnX + 4), (float)(btnY + 3), textColor);
         if (AutoBuyMod.autoBuyEnabled) {
            AutoBuyLogic.tick(class_310.method_1551());
         }
      }

   }

   @Inject(
      method = {"mouseClicked"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
      class_465<?> screen = (class_465)(Object)this;
      String title = screen.method_25440().getString().toLowerCase();
      if (title.contains("аукцион") || title.contains("auction") || title.contains("ah")) {
         int btnX = this.field_2776 + this.field_2792 - 62;
         int btnY = this.field_2800 - 16;
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
