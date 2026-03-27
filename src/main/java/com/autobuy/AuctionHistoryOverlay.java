package com.autobuy;

import java.util.Iterator;
import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;

public class AuctionHistoryOverlay extends class_332 {
   private static final class_310 mc = class_310.method_1551();
   private static boolean visible = false;

   public static void render(class_4587 matrices) {
      if (visible && mc.field_1755 != null) {
         int left = 10;
         int right = 200;
         int top = 10;
         int bottom = mc.field_1755.field_22790 - 10;
         method_25294(matrices, left, top, right, bottom, -871296734);
         method_25294(matrices, left, top, right, top + 1, -5601025);
         method_25294(matrices, left, bottom - 1, right, bottom, -5601025);
         method_25294(matrices, left, top, left + 1, bottom, -5601025);
         method_25294(matrices, right - 1, top, right, bottom, -5601025);
         mc.field_1772.method_1720(matrices, "§l\ud83d\udcdc ИСТОРИЯ ПОКУПОК", (float)(left + 10), (float)(top + 10), 16777215);
         method_25294(matrices, left + 10, top + 25, right - 10, top + 26, -11184811);
         List<PurchaseHistory.PurchaseEntry> history = PurchaseHistory.getHistory();
         int y = top + 40;
         if (history.isEmpty()) {
            mc.field_1772.method_1720(matrices, "§7Пока нет покупок", (float)(left + 10), (float)(top + 80), 8947848);
         } else {
            for(Iterator var7 = history.iterator(); var7.hasNext(); y += 20) {
               PurchaseHistory.PurchaseEntry entry = (PurchaseHistory.PurchaseEntry)var7.next();
               if (y + 20 > bottom) {
                  break;
               }

               float age = (float)(System.currentTimeMillis() - entry.time) / 1000.0F;
               int alpha = age < 5.0F ? 255 : 170;
               int color = alpha << 24 | 16777215;
               mc.field_1772.method_1720(matrices, entry.getDisplayString(), (float)(left + 10), (float)y, color);
            }
         }

      }
   }

   public static void setVisible(boolean v) {
      visible = v;
   }
}
