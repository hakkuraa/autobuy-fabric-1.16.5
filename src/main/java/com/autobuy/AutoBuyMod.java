package com.autobuy;

import com.autobuy.config.AutoBuyConfig;
import com.autobuy.gui.AutoBuyScreen;
import com.autobuy.util.DebugLogger;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

public class AutoBuyMod implements ClientModInitializer {
   public static final AutoBuyConfig CONFIG = new AutoBuyConfig();
   public static boolean autoBuyEnabled = false;
   public static KeyBinding menuKey;
   private static final long CREDIT_MESSAGE_INTERVAL_MS = 1800000L;
   private static final String CREDIT_URL = "https://funpay.com/users/12524555/";
   private static long lastCreditMessageTime;
   private static boolean menuKeyWasDown;

   public void onInitializeClient() {
      menuKey = new KeyBinding("key.autobuy.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "AutoBuy");
      lastCreditMessageTime = System.currentTimeMillis();
      DebugLogger.log("AutoBuy initialized. Debug log path: mods/debug/debug.txt");
   }

   public static void onClientTick(MinecraftClient client) {
      try {
         if (client == null || client.getWindow() == null) {
            return;
         }

         boolean menuKeyDown = InputUtil.isKeyPressed(client.getWindow().getHandle(), GLFW.GLFW_KEY_M);
         if (menuKeyDown && !menuKeyWasDown && client.currentScreen == null) {
            client.openScreen(new AutoBuyScreen());
         }

         menuKeyWasDown = menuKeyDown;
         if (System.currentTimeMillis() - lastCreditMessageTime >= CREDIT_MESSAGE_INTERVAL_MS) {
            lastCreditMessageTime = System.currentTimeMillis();
            showCreditMessage();
         }

         if (autoBuyEnabled) {
            AutoBuyLogic.tick(client);
         }
      } catch (Exception var2) {
         DebugLogger.logException("AutoBuy client tick error", var2);
      }
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
