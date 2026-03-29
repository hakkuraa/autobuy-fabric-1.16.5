package com.autobuy;

import java.util.Iterator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class AuctionHistoryOverlay extends DrawableHelper {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static boolean visible = false;

   public static void render(MatrixStack matrices) {
      if (visible && mc.currentScreen != null) {
         int left = 10;
         int right = 200;
         int top = 10;
         int bottom = mc.currentScreen.height - 10;
         fill(matrices, left, top, right, bottom, -871296734);
         fill(matrices, left, top, right, top + 1, -5601025);
         fill(matrices, left, bottom - 1, right, bottom, -5601025);
         fill(matrices, left, top, left + 1, bottom, -5601025);
         fill(matrices, right - 1, top, right, bottom, -5601025);
         mc.textRenderer.drawWithShadow(matrices, "§l\ud83d\udcdc ИСТОРИЯ ПОКУПОК", (float)(left + 10), (float)(top + 10), 16777215);
         fill(matrices, left + 10, top + 25, right - 10, top + 26, -11184811);
         List<PurchaseHistory.PurchaseEntry> history = PurchaseHistory.getHistory();
         int y = top + 40;
         if (history.isEmpty()) {
            mc.textRenderer.drawWithShadow(matrices, "§7Пока нет покупок", (float)(left + 10), (float)(top + 80), 8947848);
         } else {
            for(Iterator var7 = history.iterator(); var7.hasNext(); y += 20) {
               PurchaseHistory.PurchaseEntry entry = (PurchaseHistory.PurchaseEntry)var7.next();
               if (y + 20 > bottom) {
                  break;
               }

               float age = (float)(System.currentTimeMillis() - entry.time) / 1000.0F;
               int alpha = age < 5.0F ? 255 : 170;
               int color = alpha << 24 | 16777215;
               mc.textRenderer.drawWithShadow(matrices, entry.getDisplayString(), (float)(left + 10), (float)y, color);
            }
         }

      }
   }

   public static void setVisible(boolean v) {
      visible = v;
   }
}
