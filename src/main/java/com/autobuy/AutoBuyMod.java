package com.autobuy;

import com.autobuy.config.AutoBuyConfig;
import com.autobuy.gui.AutoBuyScreen;
import com.autobuy.util.DebugLogger;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Formatting;
import net.minecraft.text.Style;
import net.minecraft.text.LiteralText;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.client.util.InputUtil.Type;

public class AutoBuyMod implements ClientModInitializer {
   private static final long MAIN_LOOP_DELAY_MS = 10L;
   public static final AutoBuyConfig CONFIG = new AutoBuyConfig();
   public static boolean autoBuyEnabled = false;
   public static KeyBinding menuKey;
   private static final long CREDIT_MESSAGE_INTERVAL_MS = 1800000L;
   private static final String CREDIT_URL = "https://funpay.com/users/12524555/";
   private static long lastCreditMessageTime;

   public void onInitializeClient() {
      menuKey = new KeyBinding("key.autobuy.menu", Type.KEYSYM, 77, "AutoBuy");
      lastCreditMessageTime = System.currentTimeMillis();
      DebugLogger.log("AutoBuy initialized. Debug log path: mods/debug/debug.txt");
      (new Thread(() -> {
         while(true) {
            try {
               Thread.sleep(MAIN_LOOP_DELAY_MS);
               MinecraftClient client = MinecraftClient.getInstance();
               if (menuKey.wasPressed()) {
                  client.execute(() -> {
                     if (client.currentScreen == null) {
                        client.openScreen(new AutoBuyScreen());
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
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.inGameHud != null && client.inGameHud.getChatHud() != null) {
         LiteralText message = new LiteralText("remade by yukkiraa - ");
         LiteralText link = new LiteralText("funpay.com/users/12524555/");
         message.setStyle(Style.EMPTY.withColor(Formatting.byName("aqua")));
         link.setStyle(Style.EMPTY.withColor(Formatting.byName("aqua")).withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, CREDIT_URL)).withBold(true));
         message.append(link);
         client.inGameHud.getChatHud().addMessage(message);
      }
   }
}
