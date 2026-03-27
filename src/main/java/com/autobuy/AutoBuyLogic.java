package com.autobuy;

import com.autobuy.util.DebugLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1320;
import net.minecraft.class_1322;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1836;
import net.minecraft.class_1844;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_465;
import net.minecraft.class_5134;

public class AutoBuyLogic {
   private static final long SCAN_DELAY_MS = 15L;
   private static final long REFRESH_DELAY_MS = 55L;
   private static final long REOPEN_AH_DELAY_MS = 350L;
   private static final long SCREEN_CLOSE_DELAY_MS = 60L;
   private static class_310 mc = class_310.method_1551();
   private static long lastActionTime = 0L;
   private static int stage = 0;
   private static int failCounter = 0;
   private static class_1799 lastClickedStack = null;
   private static int lastClickedSlot = -1;
   private static long lastClickTime = 0L;
   private static class_1799 activeStack = null;
   private static String activeCollectId = null;
   private static final TimerUtility buyTimer = new TimerUtility();
   private static final TimerUtility updateTimer = new TimerUtility();
   private static final int[] REFRESH_SLOTS = new int[]{49, 50, 53, 48, 47, 46, 45};
   private static final int[] CONFIRM_SLOTS = new int[]{11, 13, 14, 16, 22, 29, 33};
   private static final String[] AUCTION_COMMANDS = new String[]{"/ah", "/auc", "/auction", "/market"};
   private static final List<CollectItem> ALL_ITEMS = new ArrayList();
   private static final List<EnchantmentData> DEFAULT_ARMOR_ENCHANTS;

   private static void initItemDatabase() {
      ALL_ITEMS.add(new CollectItem("enchanted_golden_apple", "Зачарованное золотое яблоко", class_1802.field_8367));
      ALL_ITEMS.add(new CollectItem("golden_apple", "Золотое яблоко", class_1802.field_8463));
      ALL_ITEMS.add(new CollectItem("diamond", "Алмаз", class_1802.field_8477));
      ALL_ITEMS.add(new CollectItem("gold_ingot", "Золотой слиток", class_1802.field_8695));
      ALL_ITEMS.add(new CollectItem("netherite_ingot", "Незеритовый слиток", class_1802.field_22020));
      ALL_ITEMS.add(new CollectItem("ender_pearl", "Жемчуг Края", class_1802.field_8634));
      ALL_ITEMS.add(new CollectItem("experience_bottle", "Пузырёк опыта", class_1802.field_8287));
      ALL_ITEMS.add(new CollectItem("totem_of_undying", "Тотем бессмертия", class_1802.field_8288));
      ALL_ITEMS.add(new CollectItem("beacon", "Маяк", class_1802.field_8668));
      ALL_ITEMS.add(new CollectItem("shulker_box", "Шалкер", class_1802.field_8545));
      ALL_ITEMS.add(new CollectItem("elytra", "Элитры", class_1802.field_8833));
      ALL_ITEMS.add(new CollectItem("golden_carrot", "Золотая морковка", class_1802.field_8071));
      ALL_ITEMS.add(new CollectItem("dragon_head", "Голова дракона", class_1802.field_8712));
      ALL_ITEMS.add(new CollectItem("gunpowder", "Порох", class_1802.field_8054));
      ALL_ITEMS.add(new CollectItem("emerald", "Изумруд", class_1802.field_8687));
      ALL_ITEMS.add(new CollectItem("iron_ingot", "Железный слиток", class_1802.field_8620));
      ALL_ITEMS.add(new CollectItem("redstone", "Редстоун", class_1802.field_8725));
      ALL_ITEMS.add(new CollectItem("lapis_lazuli", "Лазурит", class_1802.field_8759));
      ALL_ITEMS.add(new CollectItem("coal", "Уголь", class_1802.field_8713));
      ALL_ITEMS.add(new CollectItem("obsidian", "Обсидиан", class_1802.field_8281));
      ALL_ITEMS.add((new CollectItem("sphere_chaos", "Сфера хаоса", class_1802.field_8575)).addAttribute(class_5134.field_23719, 0.07, class_1322.class_1323.field_6331).addAttribute(class_5134.field_23723, 0.13, class_1322.class_1323.field_6331).addAttribute(class_5134.field_23716, -4.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23724, 2.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23721, 3.0, class_1322.class_1323.field_6328));
      ALL_ITEMS.add((new CollectItem("sphere_ares", "Сфера ареса", class_1802.field_8575)).addAttribute(class_5134.field_23716, -2.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23724, -2.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23721, 6.0, class_1322.class_1323.field_6328));
      ALL_ITEMS.add((new CollectItem("sphere_aftin", "Сфера афины", class_1802.field_8575)).addAttribute(class_5134.field_23719, 0.15, class_1322.class_1323.field_6331).addAttribute(class_5134.field_23723, 0.15, class_1322.class_1323.field_6331).addAttribute(class_5134.field_23716, -2.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23721, 3.0, class_1322.class_1323.field_6328));
      ALL_ITEMS.add((new CollectItem("talisman_crusher", "Талисман крушителя", class_1802.field_8288)).addAttribute(class_5134.field_23716, 4.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23724, 2.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23721, 3.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23725, 2.0, class_1322.class_1323.field_6328));
      ALL_ITEMS.add((new CollectItem("talisman_rage", "Талисман ярости", class_1802.field_8288)).addAttribute(class_5134.field_23716, -4.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23721, 5.0, class_1322.class_1323.field_6328));
      ALL_ITEMS.add((new CollectItem("talisman_punisher", "Талисман карателя", class_1802.field_8288)).addAttribute(class_5134.field_23719, 0.1, class_1322.class_1323.field_6331).addAttribute(class_5134.field_23716, -4.0, class_1322.class_1323.field_6328).addAttribute(class_5134.field_23721, 7.0, class_1322.class_1323.field_6328));
      ALL_ITEMS.add(new CollectItem("spawner", "Спавнер", class_1802.field_8849));
      ALL_ITEMS.add((new EnchantedItem("helmet_crusher", "Шлем крушителя", class_1802.field_22027, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(class_1893.field_9105, 1), new EnchantmentData(class_1893.field_9127, 3)}).addArmorDefaults());
      ALL_ITEMS.add((new EnchantedItem("chestplate_crusher", "Нагрудник крушителя", class_1802.field_22028, 15000000)).addArmorDefaults());
      ALL_ITEMS.add((new EnchantedItem("leggings_crusher", "Поножи крушителя", class_1802.field_22029, 15000000)).addArmorDefaults());
      ALL_ITEMS.add((new EnchantedItem("boots_crusher", "Ботинки крушителя", class_1802.field_22030, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(class_1893.field_9129, 4), new EnchantmentData(class_1893.field_9128, 3), new EnchantmentData(class_1893.field_23071, 3), new EnchantmentData(class_1893.field_9111, 4), new EnchantmentData(class_1893.field_9107, 4), new EnchantmentData(class_1893.field_9095, 4), new EnchantmentData(class_1893.field_9096, 4), new EnchantmentData(class_1893.field_9119, 3), new EnchantmentData(class_1893.field_9101, 1)}));
      ALL_ITEMS.add((new EnchantedItem("sword_crusher", "Меч крушителя", class_1802.field_22022, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(class_1893.field_9112, 7), new EnchantmentData(class_1893.field_9124, 2), new EnchantmentData(class_1893.field_9110, 5), new EnchantmentData(class_1893.field_9101, 1), new EnchantmentData(class_1893.field_9118, 7), new EnchantmentData(class_1893.field_9123, 7), new EnchantmentData(class_1893.field_9115, 3), new EnchantmentData(class_1893.field_9119, 5)}));
      ALL_ITEMS.add((new EnchantedItem("trident_crusher", "Трезубец крушителя", class_1802.field_8547, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(class_1893.field_9117, 1), new EnchantmentData(class_1893.field_9124, 2), new EnchantmentData(class_1893.field_9106, 5), new EnchantmentData(class_1893.field_9120, 3), new EnchantmentData(class_1893.field_9101, 1), new EnchantmentData(class_1893.field_9118, 7), new EnchantmentData(class_1893.field_9119, 5)}));
      ALL_ITEMS.add((new EnchantedItem("pickaxe_crusher", "Кирка крушителя", class_1802.field_22024, 25000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(class_1893.field_9131, 10), new EnchantmentData(class_1893.field_9130, 5), new EnchantmentData(class_1893.field_9101, 1), new EnchantmentData(class_1893.field_9119, 5)}).addCustomEnchants("Бульдозер II", "Авто-Плавка", "Опытный III", "Пингер", "Магнит", "Паутина"));
      ALL_ITEMS.add((new EnchantedItem("crossbow_crusher", "Арбалет крушителя", class_1802.field_8399, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(class_1893.field_9101, 1), new EnchantmentData(class_1893.field_9108, 1), new EnchantmentData(class_1893.field_9132, 4), new EnchantmentData(class_1893.field_9098, 3), new EnchantmentData(class_1893.field_9119, 3)}));
      ALL_ITEMS.add((new EnchantedItem("sharpened_sword", "Заострённый меч", class_1802.field_22022, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(class_1893.field_9118, 7), new EnchantmentData(class_1893.field_9101, 1), new EnchantmentData(class_1893.field_9119, 3)}));
   }

   public static void tick(class_310 client) {
      if (AutoBuyMod.autoBuyEnabled) {
         try {
            if (client.field_1755 == null) {
               handleClosedScreen(client);
               return;
            }

            if (!(client.field_1755 instanceof class_465)) {
               return;
            }

            class_465<?> screen = (class_465)client.field_1755;
            String title = screen.method_25440().getString().toLowerCase();
            if (System.currentTimeMillis() - lastActionTime < 0L) {
               return;
            }

            if (stage == 1) {
               if (!title.contains("подтверждение") && !title.contains("покупка")) {
                  DebugLogger.logThrottled("stage-one-reset-title:" + sanitizeKey(title), 1000L, "Stage 1 reset because screen title did not look like confirmation: " + title);
                  stage = 0;
               } else {
                  DebugLogger.logThrottled("confirmation-screen:" + sanitizeKey(title), 1000L, "Attempting confirmation on screen: " + title);
                  handleConfirmation(screen);
               }

               lastActionTime = System.currentTimeMillis();
               return;
            }

            if (stage == 0 && (title.contains("аукцион") || title.contains("auction") || title.contains("ah"))) {
               if (!buyTimer.hasPassed(SCAN_DELAY_MS)) {
                  return;
               }

               spookyScanAndBuy(screen);
               lastActionTime = System.currentTimeMillis();
            }
         } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
            DebugLogger.logException("AutoBuy tick failed", e);
            resetState();
         }

      }
   }

   private static void spookyScanAndBuy(class_465<?> screen) {
      class_1703 screenHandler = screen.method_17577();
      int syncId = screenHandler.field_7763;

      for(int i = 0; i < screenHandler.field_7761.size(); ++i) {
         class_1735 slot = (class_1735)screenHandler.field_7761.get(i);
         class_1799 stack = slot.method_7677();
         if (!stack.method_7960()) {
            int totalPrice = getPriceFromLore(stack);
            if (totalPrice > 0) {
               int count = stack.method_7947();
               int pricePerOne = totalPrice / count;
               CollectItem collectItem = findMatchingItem(stack);
               if (collectItem != null) {
                  long limitPrice = AutoBuyMod.CONFIG.getPrice(collectItem.getId());
                  if (limitPrice <= 0L) {
                     DebugLogger.logThrottled("no-limit:" + collectItem.getId(), 1500L, "Skipping " + collectItem.getId() + " because no price limit is configured. Stack=" + describeStack(stack) + ", totalPrice=" + totalPrice);
                  } else if (limitPrice < (long)pricePerOne) {
                     DebugLogger.logThrottled("price-high:" + collectItem.getId(), 1500L, "Skipping " + collectItem.getId() + " because price per item is too high. Limit=" + limitPrice + ", actual=" + pricePerOne + ", stack=" + describeStack(stack));
                  } else {
                     long maxTotalPrice = limitPrice * (long)count;
                     if ((long)totalPrice <= maxTotalPrice) {
                        if (lastClickedStack != null && lastClickedSlot == i && areItemStacksEqual(lastClickedStack, stack)) {
                           DebugLogger.logThrottled("same-stack:" + collectItem.getId(), 1000L, "Found the same stack again in slot " + i + ", refreshing auction. Stack=" + describeStack(stack));
                           clickRefreshButton(screenHandler, syncId);
                           return;
                        }

                        System.out.println("✅ BUYING " + collectItem.getName());
                        DebugLogger.log("Buying " + collectItem.getId() + " from slot " + i + ". Total=" + totalPrice + ", count=" + count + ", perItem=" + pricePerOne + ", stack=" + describeStack(stack));
                        activeStack = stack.method_7972();
                        activeCollectId = collectItem.getId();
                        lastClickedStack = stack.method_7972();
                        lastClickedSlot = i;
                        lastClickTime = System.currentTimeMillis();
                        String purchaseName = getPurchaseName(collectItem, stack);
                        if (!PurchaseHistory.wasRecentlyPurchased(purchaseName, (long)totalPrice, count, 5000L)) {
                           PurchaseHistory.addPurchase(purchaseName, (long)totalPrice, count);
                        }

                        mc.field_1761.method_2906(syncId, slot.field_7874, 0, class_1713.field_7794, mc.field_1724);
                        buyTimer.update();
                        stage = 1;
                        return;
                     } else {
                        DebugLogger.logThrottled("total-price-high:" + collectItem.getId(), 1500L, "Skipping " + collectItem.getId() + " because total price is too high. MaxAllowed=" + maxTotalPrice + ", actual=" + totalPrice + ", stack=" + describeStack(stack));
                     }
                  }
               } else if (isDebugCandidate(stack)) {
                  DebugLogger.logThrottled("unmatched:" + sanitizeKey(stack.method_7964().getString()), 1500L, "Found priced head/totem but it did not match any configured item. Price=" + totalPrice + ", stack=" + describeStack(stack));
               }
            }
         }
      }

      if (updateTimer.hasPassed(REFRESH_DELAY_MS)) {
         DebugLogger.logThrottled("auction-refresh-idle", 1000L, "Refreshing auction because no suitable item was bought in this scan.");
         clickRefreshButton(screenHandler, syncId);
         updateTimer.update();
      }

   }

   private static void clickRefreshButton(class_1703 screenHandler, int syncId) {
      int[] possibleRefreshSlots = new int[]{49, 50, 53, 48, 47, 46, 45};
      int[] var3 = possibleRefreshSlots;
      int var4 = possibleRefreshSlots.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int slot = var3[var5];
         if (slot < screenHandler.field_7761.size()) {
            class_1799 stack = ((class_1735)screenHandler.field_7761.get(slot)).method_7677();
            if (!stack.method_7960()) {
               String name = stack.method_7964().getString().toLowerCase();
               if (name.contains("обновить") || name.contains("refresh") || name.contains("⟲") || name.contains("⟳") || name.contains("\ud83d\udd04") || stack.method_7909() == class_1802.field_8137) {
                  DebugLogger.logThrottled("refresh-slot:" + slot, 1000L, "Clicking refresh slot " + slot + " with item " + stack.method_7964().getString());
                  mc.field_1761.method_2906(syncId, slot, 0, class_1713.field_7794, mc.field_1724);
                  return;
               }
            }
         }
      }

      if (49 < screenHandler.field_7761.size()) {
         DebugLogger.logThrottled("refresh-slot-fallback", 1000L, "Falling back to refresh slot 49.");
         mc.field_1761.method_2906(syncId, 49, 0, class_1713.field_7794, mc.field_1724);
      }

   }

   private static boolean areItemStacksEqual(class_1799 a, class_1799 b) {
      if (a != null && b != null) {
         return a.method_7909() == b.method_7909() && a.method_7947() == b.method_7947() && Objects.equals(a.method_7969(), b.method_7969());
      } else {
         return false;
      }
   }

   private static boolean isDebugCandidate(class_1799 stack) {
      return stack.method_7909() == class_1802.field_8575 || stack.method_7909() == class_1802.field_8288;
   }

   private static String describeStack(class_1799 stack) {
      String name = stack.method_7964().getString();
      String nbt = stack.method_7985() ? stack.method_7969().method_10714() : "";
      if (nbt.length() > 220) {
         nbt = nbt.substring(0, 220) + "...";
      }

      return "name=\"" + name + "\", count=" + stack.method_7947() + ", nbt=" + nbt;
   }

   private static String sanitizeKey(String value) {
      return value == null ? "null" : value.replaceAll("[^a-zA-Z0-9_-]", "_");
   }

   private static int getPriceFromLore(class_1799 stack) {
      try {
         if (!stack.method_7985() || !stack.method_7969().method_10545("display")) {
            return -1;
         }

         class_2487 display = stack.method_7969().method_10562("display");
         if (!display.method_10545("Lore")) {
            return -1;
         }

         class_2499 lore = display.method_10554("Lore", 8);

         for(int j = 0; j < lore.size(); ++j) {
            String line = lore.method_10608(j);
            line = line.replaceAll("§.", "");
            Pattern pattern = Pattern.compile("\\$([0-9,]+)");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
               String priceStr = matcher.group(1).replace(",", "");
               return Integer.parseInt(priceStr);
            }
         }
      } catch (Exception var8) {
      }

      return -1;
   }

   private static String getPurchaseName(CollectItem collectItem, class_1799 stack) {
      if ("spawner".equals(collectItem.getId())) {
         String mob = getSpawnerMobFromLore(stack);
         return mob != null && !mob.isEmpty() ? collectItem.getName() + " (" + mob + ")" : collectItem.getName();
      } else {
         return collectItem.getName();
      }
   }

   private static String getSpawnerMobFromLore(class_1799 stack) {
      try {
         if (!stack.method_7985() || !stack.method_7969().method_10545("display")) {
            return "";
         }

         class_2487 display = stack.method_7969().method_10562("display");
         if (!display.method_10545("Lore")) {
            return "";
         }

         class_2499 lore = display.method_10554("Lore", 8);

         for(int i = 0; i < lore.size(); ++i) {
            String line = lore.method_10608(i).replaceAll("§.", "").replaceAll("В§.", "").trim();
            String lower = line.toLowerCase();
            if (lower.startsWith("моб:") || lower.startsWith("mob:")) {
               int idx = line.indexOf(":");
               return idx >= 0 ? line.substring(idx + 1).trim() : "";
            }
         }
      } catch (Exception var6) {
      }

      return "";
   }

   private static CollectItem findMatchingItem(class_1799 stack) {
      Iterator var1 = ALL_ITEMS.iterator();

      CollectItem item;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         item = (CollectItem)var1.next();
      } while(!item.matches(stack));

      return item;
   }

   private static void resetState() {
      DebugLogger.log("Resetting state. stage=" + stage + ", failCounter=" + failCounter + ", activeCollectId=" + activeCollectId + ", lastClickedSlot=" + lastClickedSlot);
      stage = 0;
      failCounter = 0;
      lastActionTime = System.currentTimeMillis();
      activeStack = null;
      activeCollectId = null;
   }

   private static void handleClosedScreen(class_310 client) {
      if (System.currentTimeMillis() - lastActionTime > REOPEN_AH_DELAY_MS) {
         if (client.field_1724 != null) {
            DebugLogger.logThrottled("reopen-ah", 1500L, "Auction screen is closed while AutoBuy is enabled. Sending /ah.");
            client.field_1724.method_3142("/ah");
         }

         lastActionTime = System.currentTimeMillis();
      }

   }

   private static void handleConfirmation(class_465<?> screen) {
      for(int i = 0; i < screen.method_17577().field_7761.size(); ++i) {
         class_1799 stack = ((class_1735)screen.method_17577().field_7761.get(i)).method_7677();
         if (!stack.method_7960() && stack.method_7909() == class_1802.field_8656) {
            DebugLogger.log("Confirmation accepted via emerald button in slot " + i + ". activeCollectId=" + activeCollectId);
            mc.field_1761.method_2906(screen.method_17577().field_7763, i, 0, class_1713.field_7794, mc.field_1724);
            startScreenCloser();
            stage = 0;
            return;
         }
      }

      int[] var5 = CONFIRM_SLOTS;
      int var6 = var5.length;

      for(int var3 = 0; var3 < var6; ++var3) {
         int slot = var5[var3];
         if (slot < screen.method_17577().field_7761.size()) {
            DebugLogger.logThrottled("confirmation-fallback-slot:" + slot, 1000L, "Confirmation accepted via fallback slot " + slot + ". activeCollectId=" + activeCollectId);
            mc.field_1761.method_2906(screen.method_17577().field_7763, slot, 0, class_1713.field_7794, mc.field_1724);
            startScreenCloser();
            stage = 0;
            return;
         }
      }

      ++failCounter;
      DebugLogger.logThrottled("confirmation-failed", 1000L, "Confirmation not found. failCounter=" + failCounter + ", activeCollectId=" + activeCollectId + ", screen=" + screen.method_25440().getString());
      if (failCounter > 5) {
         if (mc.field_1724 != null) {
            DebugLogger.log("Closing screen after repeated confirmation failures. activeCollectId=" + activeCollectId);
            mc.field_1724.method_3137();
         }

         resetState();
      }

   }

   private static void startScreenCloser() {
      (new Thread(() -> {
         try {
            Thread.sleep(SCREEN_CLOSE_DELAY_MS);
         } catch (Exception var1) {
         }

         mc.execute(() -> {
            if (mc.field_1724 != null) {
               mc.field_1724.method_3137();
            }

         });
      })).start();
   }

   static {
      DEFAULT_ARMOR_ENCHANTS = Arrays.asList(new EnchantmentData(class_1893.field_9111, 5), new EnchantmentData(class_1893.field_9107, 5), new EnchantmentData(class_1893.field_9095, 5), new EnchantmentData(class_1893.field_9096, 5), new EnchantmentData(class_1893.field_9101, 1), new EnchantmentData(class_1893.field_9119, 5));
      initItemDatabase();
   }

   private static class CollectItem {
      protected final String id;
      protected final String name;
      protected final class_1792 item;
      protected String tag;
      protected int maxSellPrice = 10000000;
      protected class_1799 customStack;
      protected List<EnchantmentData> requiredEnchants = new ArrayList();
      protected List<String> customEnchants = new ArrayList();
      protected List<AttributeData> attributes = new ArrayList();
      protected List<EffectData> effects = new ArrayList();
      protected boolean addArmorDefaults = false;

      CollectItem(String id, String name, class_1792 item) {
         this.id = id;
         this.name = name;
         this.item = item;
      }

      public String getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public CollectItem setTag(String tag) {
         this.tag = tag;
         return this;
      }

      public CollectItem setMaxSellPrice(int price) {
         this.maxSellPrice = price;
         return this;
      }

      public CollectItem setStack(class_1799 stack) {
         this.customStack = stack;
         return this;
      }

      public CollectItem addEnchantments(EnchantmentData... enchants) {
         this.requiredEnchants.addAll(Arrays.asList(enchants));
         return this;
      }

      public CollectItem addCustomEnchants(String... enchants) {
         this.customEnchants.addAll(Arrays.asList(enchants));
         return this;
      }

      public CollectItem addArmorDefaults() {
         this.addArmorDefaults = true;
         return this;
      }

      public CollectItem addAttribute(class_1320 attribute, double value, class_1322.class_1323 operation) {
         this.attributes.add(AutoBuyLogic.AttributeData.of(attribute, value, operation));
         return this;
      }

      public CollectItem addEffect(class_1291 effect, int seconds, int amplifier) {
         this.effects.add(AutoBuyLogic.EffectData.of(effect, seconds, amplifier));
         return this;
      }

      public boolean matches(class_1799 stack) {
         if (stack.method_7909() != this.item) {
            return false;
         } else {
            if ("spawner".equals(this.id)) {
               String configuredMob = AutoBuyMod.CONFIG.getMobFilter(this.id).toLowerCase();
               if (configuredMob.isEmpty()) {
                  return false;
               }

               return AutoBuyLogic.getSpawnerMobFromLore(stack).toLowerCase().equals(configuredMob);
            }

            String nbt;
            if (this.tag != null && !this.tag.isEmpty()) {
               if (!stack.method_7985()) {
                  return false;
               }

               nbt = stack.method_7969().method_10714();
               if (!nbt.contains(this.tag)) {
                  return false;
               }
            }

            Iterator var3;
            String custom;
            if (!this.attributes.isEmpty()) {
               if (!this.matchesDisplayName(stack) && !this.hasRequiredAttributes(stack) && !this.hasRequiredTooltipAttributes(stack) && !this.hasRequiredAttributeNbtText(stack)) {
                  return false;
               }
            }

            if (!this.requiredEnchants.isEmpty() || this.addArmorDefaults) {
               label161: {
                  Map<class_1887, Integer> stackEnchants = class_1890.method_8222(stack);
                  List<EnchantmentData> allEnchants = new ArrayList(this.requiredEnchants);
                  if (this.addArmorDefaults) {
                     allEnchants.addAll(AutoBuyLogic.DEFAULT_ARMOR_ENCHANTS);
                  }

                  Iterator var15 = allEnchants.iterator();

                  EnchantmentData ench;
                  Integer level;
                  do {
                     if (!var15.hasNext()) {
                        break label161;
                     }

                     ench = (EnchantmentData)var15.next();
                     level = (Integer)stackEnchants.get(ench.getEnchantment());
                  } while(level != null && level >= ench.getLevel());

                  return false;
               }
            }

            if (!this.customEnchants.isEmpty() && stack.method_7985() && stack.method_7969().method_10545("display")) {
               class_2487 display = stack.method_7969().method_10562("display");
               if (display.method_10545("Lore")) {
                  class_2499 lore = display.method_10554("Lore", 8);
                  List<String> loreLines = new ArrayList();

                  for(int i = 0; i < lore.size(); ++i) {
                     loreLines.add(lore.method_10608(i).toLowerCase());
                  }

                  Iterator var21 = this.customEnchants.iterator();

                  while(var21.hasNext()) {
                     custom = (String)var21.next();
                     boolean found = false;
                     Iterator var8 = loreLines.iterator();

                     while(var8.hasNext()) {
                        String line = (String)var8.next();
                        if (line.contains(custom.toLowerCase())) {
                           found = true;
                           break;
                        }
                     }

                     if (!found) {
                        return false;
                     }
                  }
               }
            }

            if (!this.effects.isEmpty()) {
               List<class_1293> potionEffects = class_1844.method_8067(stack);
               if (potionEffects.size() != this.effects.size()) {
                  return false;
               }

               var3 = this.effects.iterator();

               while(var3.hasNext()) {
                  EffectData check = (EffectData)var3.next();
                  boolean found = false;
                  Iterator var23 = potionEffects.iterator();

                  while(var23.hasNext()) {
                     class_1293 effect = (class_1293)var23.next();
                     if (effect.method_5579() == check.getEffect() && effect.method_5584() >= check.getDuration() && effect.method_5578() >= check.getAmplifier()) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     return false;
                  }
               }
            }

            return true;
         }
      }

      private boolean hasRequiredAttributes(class_1799 stack) {
         if (!stack.method_7985()) {
            return false;
         } else {
            class_2487 nbt = stack.method_7969();
            if (nbt == null || !nbt.method_10545("AttributeModifiers")) {
               return false;
            } else {
               class_2499 modifiers = nbt.method_10554("AttributeModifiers", 10);
               Iterator var4 = this.attributes.iterator();

               while(var4.hasNext()) {
                  AttributeData required = (AttributeData)var4.next();
                  if (!this.hasMatchingModifier(modifiers, required)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }

      private boolean hasMatchingModifier(class_2499 modifiers, AttributeData required) {
         String attributeId = String.valueOf(class_2378.field_23781.method_10221(required.getAttribute()));

         for(int i = 0; i < modifiers.size(); ++i) {
            class_2487 modifier = modifiers.method_10602(i);
            if (attributeId.equals(modifier.method_10558("AttributeName")) && modifier.method_10571("Operation") == required.getOperation().method_6191() && Math.abs(modifier.method_10574("Amount") - required.getValue()) < 1.0E-4) {
               return true;
            }
         }

         return false;
      }

      private boolean hasRequiredAttributeNbtText(class_1799 stack) {
         if (!stack.method_7985()) {
            return false;
         } else {
            String nbtText = this.normalizeText(stack.method_7969().method_10714());
            Iterator var3 = this.attributes.iterator();

            while(var3.hasNext()) {
               AttributeData required = (AttributeData)var3.next();
               String attributeId = this.normalizeText(String.valueOf(class_2378.field_23781.method_10221(required.getAttribute())));
               if (!nbtText.contains(attributeId) || !nbtText.contains(this.normalizeNumber(required.getValue()))) {
                  return false;
               }
            }

            return true;
         }
      }

      private boolean matchesDisplayName(class_1799 stack) {
         String stackName = this.normalizeText(stack.method_7964().getString());
         String expectedName = this.normalizeText(this.name);
         return !stackName.isEmpty() && !expectedName.isEmpty() && stackName.contains(expectedName);
      }

      private boolean hasRequiredTooltipAttributes(class_1799 stack) {
         if (AutoBuyLogic.mc.field_1724 == null) {
            return false;
         } else {
            List<class_2561> tooltip = stack.method_7950(AutoBuyLogic.mc.field_1724, class_1836.class_1837.field_8934);
            Iterator var3 = this.attributes.iterator();

            while(var3.hasNext()) {
               AttributeData required = (AttributeData)var3.next();
               if (!this.hasMatchingTooltipLine(tooltip, required)) {
                  return false;
               }
            }

            return true;
         }
      }

      private boolean hasMatchingTooltipLine(List<class_2561> tooltip, AttributeData required) {
         Iterator var3 = tooltip.iterator();

         while(var3.hasNext()) {
            class_2561 line = (class_2561)var3.next();
            String normalizedLine = this.normalizeText(line.getString());
            if (this.matchesTooltipAlias(normalizedLine, required) && this.matchesTooltipValue(normalizedLine, required)) {
               return true;
            }
         }

         return false;
      }

      private boolean matchesTooltipAlias(String normalizedLine, AttributeData required) {
         if (required.getAttribute() == class_5134.field_23719 && (normalizedLine.contains("скорость атаки") || normalizedLine.contains("attack speed"))) {
            return false;
         }

         String[] aliases = this.getAttributeAliases(required.getAttribute());
         String[] var4 = aliases;
         int var5 = aliases.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String alias = var4[var6];
            if (normalizedLine.contains(alias)) {
               return true;
            }
         }

         return false;
      }

      private boolean matchesTooltipValue(String normalizedLine, AttributeData required) {
         int roundedValue;
         if (required.getOperation() == class_1322.class_1323.field_6331) {
            roundedValue = (int)Math.round(Math.abs(required.getValue()) * 100.0);
            return normalizedLine.contains(String.valueOf(roundedValue)) && normalizedLine.contains("%");
         } else {
            roundedValue = (int)Math.round(Math.abs(required.getValue()));
            return normalizedLine.contains(String.valueOf(roundedValue));
         }
      }

      private String[] getAttributeAliases(class_1320 attribute) {
         if (attribute == class_5134.field_23719) {
            return new String[]{"скорость", "speed"};
         } else if (attribute == class_5134.field_23723) {
            return new String[]{"скорость атаки", "attack speed"};
         } else if (attribute == class_5134.field_23716) {
            return new String[]{"максимальное здоровье", "max health"};
         } else if (attribute == class_5134.field_23724) {
            return new String[]{"броня", "armor"};
         } else if (attribute == class_5134.field_23721) {
            return new String[]{"урон", "attack damage"};
         } else if (attribute == class_5134.field_23725) {
            return new String[]{"твердость брони", "armor toughness"};
         } else {
            return new String[]{this.normalizeText(attribute.method_26830())};
         }
      }

      private String normalizeText(String value) {
         return value == null ? "" : value.toLowerCase().replace("ё", "е").trim();
      }
      private String normalizeNumber(double value) {
         return String.valueOf(value).replace(",", ".");
      }
   }

   private static class EnchantedItem extends CollectItem {
      EnchantedItem(String id, String name, class_1792 item, int maxPrice) {
         super(id, name, item);
         this.maxSellPrice = maxPrice;
      }
   }

   private static class EnchantmentData {
      private final class_1887 enchantment;
      private final int level;

      public EnchantmentData(class_1887 enchantment, int level) {
         this.enchantment = enchantment;
         this.level = level;
      }

      public static EnchantmentData of(class_1887 enchantment, int level) {
         return new EnchantmentData(enchantment, level);
      }

      public class_1887 getEnchantment() {
         return this.enchantment;
      }

      public int getLevel() {
         return this.level;
      }
   }

   private static class TimerUtility {
      private long lastTime;

      private TimerUtility() {
         this.lastTime = System.currentTimeMillis();
      }

      public boolean hasPassed(long millis) {
         return System.currentTimeMillis() - this.lastTime >= millis;
      }

      public void update() {
         this.lastTime = System.currentTimeMillis();
      }
   }

   private static class EffectData {
      private final class_1291 effect;
      private final int duration;
      private final int amplifier;
      private final boolean instant;

      public EffectData(class_1291 effect, int duration, int amplifier, boolean instant) {
         this.effect = effect;
         this.duration = duration;
         this.amplifier = amplifier;
         this.instant = instant;
      }

      public static EffectData of(class_1291 effect, int seconds, int amplifier) {
         return new EffectData(effect, seconds * 20, amplifier, false);
      }

      public static EffectData instant(class_1291 effect, int amplifier) {
         return new EffectData(effect, 1, amplifier, true);
      }

      public class_1291 getEffect() {
         return this.effect;
      }

      public int getDuration() {
         return this.duration;
      }

      public int getAmplifier() {
         return this.amplifier;
      }

      public boolean isInstant() {
         return this.instant;
      }
   }

   private static class AttributeData {
      private final class_1320 attribute;
      private final double value;
      private final class_1322.class_1323 operation;

      public AttributeData(class_1320 attribute, double value, class_1322.class_1323 operation) {
         this.attribute = attribute;
         this.value = value;
         this.operation = operation;
      }

      public static AttributeData of(class_1320 attribute, double value, class_1322.class_1323 operation) {
         return new AttributeData(attribute, value, operation);
      }

      public class_1320 getAttribute() {
         return this.attribute;
      }

      public double getValue() {
         return this.value;
      }

      public class_1322.class_1323 getOperation() {
         return this.operation;
      }
   }
}
