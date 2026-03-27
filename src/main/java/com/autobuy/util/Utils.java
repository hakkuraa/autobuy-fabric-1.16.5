package com.autobuy.util;

import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_1836;
import net.minecraft.class_2561;
import net.minecraft.class_310;

public final class Utils {
   private static final class_310 mc = class_310.method_1551();

   public static String getSeller(class_1799 stack) {
      if (mc.field_1724 == null) {
         return "";
      } else {
         try {
            List<class_2561> itemTooltip = stack.method_7950(mc.field_1724, (class_1836)null);
            Iterator var2 = itemTooltip.iterator();

            while(var2.hasNext()) {
               class_2561 line = (class_2561)var2.next();
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

   public static int getPrice(class_1799 stack) {
      if (mc.field_1724 == null) {
         return -1;
      } else {
         try {
            List<class_2561> itemTooltip = stack.method_7950(mc.field_1724, (class_1836)null);
            Iterator var2 = itemTooltip.iterator();

            while(true) {
               String lineStr;
               do {
                  do {
                     if (!var2.hasNext()) {
                        return -1;
                     }

                     class_2561 line = (class_2561)var2.next();
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
