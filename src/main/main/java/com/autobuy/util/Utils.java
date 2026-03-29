package com.autobuy.util;

import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

public final class Utils {
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public static String getSeller(ItemStack stack) {
      if (mc.player == null) {
         return "";
      } else {
         try {
            List<Text> itemTooltip = stack.getTooltip(mc.player, (TooltipContext)null);
            Iterator var2 = itemTooltip.iterator();

            while(var2.hasNext()) {
               Text line = (Text)var2.next();
               String lineStr = line.getString();
               if (lineStr.contains("☤ Продавец:")) {
                  return lineStr.replace("☤ Продавец: ", "").replaceAll("\\$", "").replaceAll(" ", "");
               }
            }
         } catch (Exception var5) {
         }

         return "";
      }
   }

   public static int getPrice(ItemStack stack) {
      if (mc.player == null) {
         return -1;
      } else {
         try {
            List<Text> itemTooltip = stack.getTooltip(mc.player, (TooltipContext)null);
            Iterator var2 = itemTooltip.iterator();

            while(true) {
               String lineStr;
               do {
                  do {
                     if (!var2.hasNext()) {
                        return -1;
                     }

                     Text line = (Text)var2.next();
                     lineStr = line.getString();
                     if (lineStr.contains("$ Цена $") || lineStr.contains("$ Цена: $")) {
                        String priceStr = lineStr.replace("$ Цена $", "").replace("$ Цена: $", "").replaceAll("\\$", "").replaceAll(" ", "").replaceAll(",", "");
                        if (!priceStr.isEmpty()) {
                           return Integer.parseInt(priceStr);
                        }
                     }
                  } while(!lineStr.contains("$"));
               } while(!lineStr.contains("Цена"));

               String[] parts = lineStr.split("\\$");
               String[] var6 = parts;
               int var7 = parts.length;

               for(int var8 = 0; var8 < var7; ++var8) {
                  String part = var6[var8];
                  String cleaned = part.replaceAll("[^0-9]", "");
                  if (!cleaned.isEmpty()) {
                     return Integer.parseInt(cleaned);
                  }
               }
            }
         } catch (Exception var11) {
            return -1;
         }
      }
   }

   private Utils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
