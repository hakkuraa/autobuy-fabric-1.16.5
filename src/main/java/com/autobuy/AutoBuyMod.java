package com.autobuy;

import com.autobuy.config.AutoBuyConfig;
import com.autobuy.gui.AutoBuyScreen;
import com.autobuy.util.DebugLogger;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.class_124;
import net.minecraft.class_2583;
import net.minecraft.class_2585;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_2558;
import net.minecraft.class_3675.class_307;

public class AutoBuyMod implements ClientModInitializer {
   private static final long MAIN_LOOP_DELAY_MS = 10L;
   public static final AutoBuyConfig CONFIG = new AutoBuyConfig();
   public static boolean autoBuyEnabled = false;
   public static class_304 menuKey;
   private static final long CREDIT_MESSAGE_INTERVAL_MS = 1800000L;
   private static final String CREDIT_URL = "https://funpay.com/users/12524555/";
   private static long lastCreditMessageTime;

   public void onInitializeClient() {
      menuKey = new class_304("key.autobuy.menu", class_307.field_1668, 77, "AutoBuy");
      lastCreditMessageTime = System.currentTimeMillis();
      DebugLogger.log("AutoBuy initialized. Debug log path: mods/debug/debug.txt");
      (new Thread(() -> {
         while(true) {
            try {
               Thread.sleep(MAIN_LOOP_DELAY_MS);
               class_310 client = class_310.method_1551();
               if (menuKey.method_1436()) {
                  client.execute(() -> {
                     if (client.field_1755 == null) {
                        client.method_1507(new AutoBuyScreen());
                     }

                  });
               }

               if (System.currentTimeMillis() - lastCreditMessageTime >= CREDIT_MESSAGE_INTERVAL_MS) {
                  lastCreditMessageTime = System.currentTimeMillis();
                  client.execute(AutoBuyMod::showCreditMessage);
               }
            } catch (Exception var2) {
               DebugLogger.logException("AutoBuy main thread loop error", var2);
            }
         }
      })).start();
   }

   private static void showCreditMessage() {
      class_310 client = class_310.method_1551();
      if (client.field_1705 != null && client.field_1705.method_1743() != null) {
         class_2585 message = new class_2585("remade by yukkiraa - ");
         class_2585 link = new class_2585("funpay.com/users/12524555/");
         message.method_10862(class_2583.field_24360.method_10977(class_124.method_533("aqua")));
         link.method_10862(class_2583.field_24360.method_10977(class_124.method_533("aqua")).method_10958(new class_2558(class_2558.class_2559.field_11749, CREDIT_URL)).method_10982(true));
         message.method_10852(link);
         client.field_1705.method_1743().method_1812(message);
      }
   }
}
