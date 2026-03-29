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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.potion.PotionUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.registry.Registry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.attribute.EntityAttributes;

public class AutoBuyLogic {
   private static final long SCAN_DELAY_MS = 15L;
   private static final long REFRESH_DELAY_MS = 55L;
   private static final long REOPEN_AH_DELAY_MS = 350L;
   private static final long SCREEN_CLOSE_DELAY_MS = 60L;
   private static MinecraftClient mc = MinecraftClient.getInstance();
   private static long lastActionTime = 0L;
   private static int stage = 0;
   private static int failCounter = 0;
   private static ItemStack lastClickedStack = null;
   private static int lastClickedSlot = -1;
   private static long lastClickTime = 0L;
   private static ItemStack activeStack = null;
   private static String activeCollectId = null;
   private static final TimerUtility buyTimer = new TimerUtility();
   private static final TimerUtility updateTimer = new TimerUtility();
   private static final int[] REFRESH_SLOTS = new int[]{49, 50, 53, 48, 47, 46, 45};
   private static final int[] CONFIRM_SLOTS = new int[]{11, 13, 14, 16, 22, 29, 33};
   private static final String[] AUCTION_COMMANDS = new String[]{"/ah", "/auc", "/auction", "/market"};
   private static final List<CollectItem> ALL_ITEMS = new ArrayList();
   private static final List<EnchantmentData> DEFAULT_ARMOR_ENCHANTS;

   private static void initItemDatabase() {
      ALL_ITEMS.add(new CollectItem("enchanted_golden_apple", "Зачарованное золотое яблоко", Items.ENCHANTED_GOLDEN_APPLE));
      ALL_ITEMS.add(new CollectItem("golden_apple", "Золотое яблоко", Items.GOLDEN_APPLE));
      ALL_ITEMS.add(new CollectItem("diamond", "Алмаз", Items.DIAMOND));
      ALL_ITEMS.add(new CollectItem("gold_ingot", "Золотой слиток", Items.GOLD_INGOT));
      ALL_ITEMS.add(new CollectItem("netherite_ingot", "Незеритовый слиток", Items.NETHERITE_INGOT));
      ALL_ITEMS.add(new CollectItem("ender_pearl", "Жемчуг Края", Items.ENDER_PEARL));
      ALL_ITEMS.add(new CollectItem("experience_bottle", "Пузырёк опыта", Items.EXPERIENCE_BOTTLE));
      ALL_ITEMS.add(new CollectItem("totem_of_undying", "Тотем бессмертия", Items.TOTEM_OF_UNDYING));
      ALL_ITEMS.add(new CollectItem("beacon", "Маяк", Items.BEACON));
      ALL_ITEMS.add(new CollectItem("shulker_box", "Шалкер", Items.SHULKER_BOX));
      ALL_ITEMS.add(new CollectItem("elytra", "Элитры", Items.ELYTRA));
      ALL_ITEMS.add(new CollectItem("golden_carrot", "Золотая морковка", Items.GOLDEN_CARROT));
      ALL_ITEMS.add(new CollectItem("dragon_head", "Голова дракона", Items.DRAGON_HEAD));
      ALL_ITEMS.add(new CollectItem("gunpowder", "Порох", Items.GUNPOWDER));
      ALL_ITEMS.add(new CollectItem("emerald", "Изумруд", Items.EMERALD));
      ALL_ITEMS.add(new CollectItem("iron_ingot", "Железный слиток", Items.IRON_INGOT));
      ALL_ITEMS.add(new CollectItem("redstone", "Редстоун", Items.REDSTONE));
      ALL_ITEMS.add(new CollectItem("lapis_lazuli", "Лазурит", Items.LAPIS_LAZULI));
      ALL_ITEMS.add(new CollectItem("coal", "Уголь", Items.COAL));
      ALL_ITEMS.add(new CollectItem("obsidian", "Обсидиан", Items.OBSIDIAN));
      ALL_ITEMS.add((new CollectItem("sphere_chaos", "Сфера хаоса", Items.PLAYER_HEAD)).addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.07, EntityAttributeModifier.Operation.MULTIPLY_TOTAL).addAttribute(EntityAttributes.GENERIC_ATTACK_SPEED, 0.13, EntityAttributeModifier.Operation.MULTIPLY_TOTAL).addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, -4.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ARMOR, 2.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0, EntityAttributeModifier.Operation.ADDITION));
      ALL_ITEMS.add((new CollectItem("sphere_ares", "Сфера ареса", Items.PLAYER_HEAD)).addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, -2.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ARMOR, -2.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0, EntityAttributeModifier.Operation.ADDITION));
      ALL_ITEMS.add((new CollectItem("sphere_aftin", "Сфера афины", Items.PLAYER_HEAD)).addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.15, EntityAttributeModifier.Operation.MULTIPLY_TOTAL).addAttribute(EntityAttributes.GENERIC_ATTACK_SPEED, 0.15, EntityAttributeModifier.Operation.MULTIPLY_TOTAL).addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, -2.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0, EntityAttributeModifier.Operation.ADDITION));
      ALL_ITEMS.add((new CollectItem("talisman_crusher", "Талисман крушителя", Items.TOTEM_OF_UNDYING)).addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, 4.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ARMOR, 2.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 2.0, EntityAttributeModifier.Operation.ADDITION));
      ALL_ITEMS.add((new CollectItem("talisman_rage", "Талисман ярости", Items.TOTEM_OF_UNDYING)).addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, -4.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0, EntityAttributeModifier.Operation.ADDITION));
      ALL_ITEMS.add((new CollectItem("talisman_punisher", "Талисман карателя", Items.TOTEM_OF_UNDYING)).addAttribute(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL).addAttribute(EntityAttributes.GENERIC_MAX_HEALTH, -4.0, EntityAttributeModifier.Operation.ADDITION).addAttribute(EntityAttributes.GENERIC_ATTACK_DAMAGE, 7.0, EntityAttributeModifier.Operation.ADDITION));
      ALL_ITEMS.add(new CollectItem("spawner", "Спавнер", Items.SPAWNER));
      ALL_ITEMS.add((new EnchantedItem("helmet_crusher", "Шлем крушителя", Items.NETHERITE_HELMET, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(Enchantments.AQUA_AFFINITY, 1), new EnchantmentData(Enchantments.RESPIRATION, 3)}).addArmorDefaults());
      ALL_ITEMS.add((new EnchantedItem("chestplate_crusher", "Нагрудник крушителя", Items.NETHERITE_CHESTPLATE, 15000000)).addArmorDefaults());
      ALL_ITEMS.add((new EnchantedItem("leggings_crusher", "Поножи крушителя", Items.NETHERITE_LEGGINGS, 15000000)).addArmorDefaults());
      ALL_ITEMS.add((new EnchantedItem("boots_crusher", "Ботинки крушителя", Items.NETHERITE_BOOTS, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(Enchantments.FEATHER_FALLING, 4), new EnchantmentData(Enchantments.DEPTH_STRIDER, 3), new EnchantmentData(Enchantments.SOUL_SPEED, 3), new EnchantmentData(Enchantments.PROTECTION, 4), new EnchantmentData(Enchantments.BLAST_PROTECTION, 4), new EnchantmentData(Enchantments.FIRE_PROTECTION, 4), new EnchantmentData(Enchantments.PROJECTILE_PROTECTION, 4), new EnchantmentData(Enchantments.UNBREAKING, 3), new EnchantmentData(Enchantments.MENDING, 1)}));
      ALL_ITEMS.add((new EnchantedItem("sword_crusher", "Меч крушителя", Items.NETHERITE_SWORD, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(Enchantments.BANE_OF_ARTHROPODS, 7), new EnchantmentData(Enchantments.FIRE_ASPECT, 2), new EnchantmentData(Enchantments.LOOTING, 5), new EnchantmentData(Enchantments.MENDING, 1), new EnchantmentData(Enchantments.SHARPNESS, 7), new EnchantmentData(Enchantments.SMITE, 7), new EnchantmentData(Enchantments.SWEEPING, 3), new EnchantmentData(Enchantments.UNBREAKING, 5)}));
      ALL_ITEMS.add((new EnchantedItem("trident_crusher", "Трезубец крушителя", Items.TRIDENT, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(Enchantments.CHANNELING, 1), new EnchantmentData(Enchantments.FIRE_ASPECT, 2), new EnchantmentData(Enchantments.IMPALING, 5), new EnchantmentData(Enchantments.LOYALTY, 3), new EnchantmentData(Enchantments.MENDING, 1), new EnchantmentData(Enchantments.SHARPNESS, 7), new EnchantmentData(Enchantments.UNBREAKING, 5)}));
      ALL_ITEMS.add((new EnchantedItem("pickaxe_crusher", "Кирка крушителя", Items.NETHERITE_PICKAXE, 25000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(Enchantments.EFFICIENCY, 10), new EnchantmentData(Enchantments.FORTUNE, 5), new EnchantmentData(Enchantments.MENDING, 1), new EnchantmentData(Enchantments.UNBREAKING, 5)}).addCustomEnchants("Бульдозер II", "Авто-Плавка", "Опытный III", "Пингер", "Магнит", "Паутина"));
      ALL_ITEMS.add((new EnchantedItem("crossbow_crusher", "Арбалет крушителя", Items.CROSSBOW, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(Enchantments.MENDING, 1), new EnchantmentData(Enchantments.MULTISHOT, 1), new EnchantmentData(Enchantments.PIERCING, 4), new EnchantmentData(Enchantments.QUICK_CHARGE, 3), new EnchantmentData(Enchantments.UNBREAKING, 3)}));
      ALL_ITEMS.add((new EnchantedItem("sharpened_sword", "Заострённый меч", Items.NETHERITE_SWORD, 15000000)).addEnchantments(new EnchantmentData[]{new EnchantmentData(Enchantments.SHARPNESS, 7), new EnchantmentData(Enchantments.MENDING, 1), new EnchantmentData(Enchantments.UNBREAKING, 3)}));
   }

   public static void tick(MinecraftClient client) {
      if (AutoBuyMod.autoBuyEnabled) {
         try {
            if (client.currentScreen == null) {
               handleClosedScreen(client);
               return;
            }

            if (!(client.currentScreen instanceof HandledScreen)) {
               return;
            }

            HandledScreen<?> screen = (HandledScreen)client.currentScreen;
            String title = screen.getTitle().getString().toLowerCase();
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

   private static void spookyScanAndBuy(HandledScreen<?> screen) {
      ScreenHandler screenHandler = screen.getScreenHandler();
      int syncId = screenHandler.syncId;

      for(int i = 0; i < screenHandler.slots.size(); ++i) {
         Slot slot = (Slot)screenHandler.slots.get(i);
         ItemStack stack = slot.getStack();
         if (!stack.isEmpty()) {
            int totalPrice = getPriceFromLore(stack);
            if (totalPrice > 0) {
               int count = stack.getCount();
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
                        activeStack = stack.copy();
                        activeCollectId = collectItem.getId();
                        lastClickedStack = stack.copy();
                        lastClickedSlot = i;
                        lastClickTime = System.currentTimeMillis();
                        String purchaseName = getPurchaseName(collectItem, stack);
                        if (!PurchaseHistory.wasRecentlyPurchased(purchaseName, (long)totalPrice, count, 5000L)) {
                           PurchaseHistory.addPurchase(purchaseName, (long)totalPrice, count);
                        }

                        mc.interactionManager.clickSlot(syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
                        buyTimer.update();
                        stage = 1;
                        return;
                     } else {
                        DebugLogger.logThrottled("total-price-high:" + collectItem.getId(), 1500L, "Skipping " + collectItem.getId() + " because total price is too high. MaxAllowed=" + maxTotalPrice + ", actual=" + totalPrice + ", stack=" + describeStack(stack));
                     }
                  }
               } else if (isDebugCandidate(stack)) {
                  DebugLogger.logThrottled("unmatched:" + sanitizeKey(stack.getName().getString()), 1500L, "Found priced head/totem but it did not match any configured item. Price=" + totalPrice + ", stack=" + describeStack(stack));
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

   private static void clickRefreshButton(ScreenHandler screenHandler, int syncId) {
      int[] possibleRefreshSlots = new int[]{49, 50, 53, 48, 47, 46, 45};
      int[] var3 = possibleRefreshSlots;
      int var4 = possibleRefreshSlots.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int slot = var3[var5];
         if (slot < screenHandler.slots.size()) {
            ItemStack stack = ((Slot)screenHandler.slots.get(slot)).getStack();
            if (!stack.isEmpty()) {
               String name = stack.getName().getString().toLowerCase();
               if (name.contains("обновить") || name.contains("refresh") || name.contains("⟲") || name.contains("⟳") || name.contains("\ud83d\udd04") || stack.getItem() == Items.NETHER_STAR) {
                  DebugLogger.logThrottled("refresh-slot:" + slot, 1000L, "Clicking refresh slot " + slot + " with item " + stack.getName().getString());
                  mc.interactionManager.clickSlot(syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
                  return;
               }
            }
         }
      }

      if (49 < screenHandler.slots.size()) {
         DebugLogger.logThrottled("refresh-slot-fallback", 1000L, "Falling back to refresh slot 49.");
         mc.interactionManager.clickSlot(syncId, 49, 0, SlotActionType.QUICK_MOVE, mc.player);
      }

   }

   private static boolean areItemStacksEqual(ItemStack a, ItemStack b) {
      if (a != null && b != null) {
         return a.getItem() == b.getItem() && a.getCount() == b.getCount() && Objects.equals(a.getTag(), b.getTag());
      } else {
         return false;
      }
   }

   private static boolean isDebugCandidate(ItemStack stack) {
      return stack.getItem() == Items.PLAYER_HEAD || stack.getItem() == Items.TOTEM_OF_UNDYING;
   }

   private static String describeStack(ItemStack stack) {
      String name = stack.getName().getString();
      String nbt = stack.hasTag() ? stack.getTag().asString() : "";
      if (nbt.length() > 220) {
         nbt = nbt.substring(0, 220) + "...";
      }

      return "name=\"" + name + "\", count=" + stack.getCount() + ", nbt=" + nbt;
   }

   private static String sanitizeKey(String value) {
      return value == null ? "null" : value.replaceAll("[^a-zA-Z0-9_-]", "_");
   }

   private static int getPriceFromLore(ItemStack stack) {
      try {
         if (!stack.hasTag() || !stack.getTag().contains("display")) {
            return -1;
         }

         NbtCompound display = stack.getTag().getCompound("display");
         if (!display.contains("Lore")) {
            return -1;
         }

         NbtList lore = display.getList("Lore", 8);

         for(int j = 0; j < lore.size(); ++j) {
            String line = lore.getString(j);
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

   private static String getPurchaseName(CollectItem collectItem, ItemStack stack) {
      if ("spawner".equals(collectItem.getId())) {
         String mob = getSpawnerMobFromLore(stack);
         return mob != null && !mob.isEmpty() ? collectItem.getName() + " (" + mob + ")" : collectItem.getName();
      } else {
         return collectItem.getName();
      }
   }

   private static String getSpawnerMobFromLore(ItemStack stack) {
      try {
         if (!stack.hasTag() || !stack.getTag().contains("display")) {
            return "";
         }

         NbtCompound display = stack.getTag().getCompound("display");
         if (!display.contains("Lore")) {
            return "";
         }

         NbtList lore = display.getList("Lore", 8);

         for(int i = 0; i < lore.size(); ++i) {
            String line = lore.getString(i).replaceAll("§.", "").replaceAll("В§.", "").trim();
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

   private static CollectItem findMatchingItem(ItemStack stack) {
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

   private static void handleClosedScreen(MinecraftClient client) {
      if (System.currentTimeMillis() - lastActionTime > REOPEN_AH_DELAY_MS) {
         if (client.player != null) {
            DebugLogger.logThrottled("reopen-ah", 1500L, "Auction screen is closed while AutoBuy is enabled. Sending /ah.");
            client.player.sendChatMessage("/ah");
         }

         lastActionTime = System.currentTimeMillis();
      }

   }

   private static void handleConfirmation(HandledScreen<?> screen) {
      for(int i = 0; i < screen.getScreenHandler().slots.size(); ++i) {
         ItemStack stack = ((Slot)screen.getScreenHandler().slots.get(i)).getStack();
         if (!stack.isEmpty() && stack.getItem() == Items.GREEN_STAINED_GLASS_PANE) {
            DebugLogger.log("Confirmation accepted via emerald button in slot " + i + ". activeCollectId=" + activeCollectId);
            mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.QUICK_MOVE, mc.player);
            startScreenCloser();
            stage = 0;
            return;
         }
      }

      int[] var5 = CONFIRM_SLOTS;
      int var6 = var5.length;

      for(int var3 = 0; var3 < var6; ++var3) {
         int slot = var5[var3];
         if (slot < screen.getScreenHandler().slots.size()) {
            DebugLogger.logThrottled("confirmation-fallback-slot:" + slot, 1000L, "Confirmation accepted via fallback slot " + slot + ". activeCollectId=" + activeCollectId);
            mc.interactionManager.clickSlot(screen.getScreenHandler().syncId, slot, 0, SlotActionType.QUICK_MOVE, mc.player);
            startScreenCloser();
            stage = 0;
            return;
         }
      }

      ++failCounter;
      DebugLogger.logThrottled("confirmation-failed", 1000L, "Confirmation not found. failCounter=" + failCounter + ", activeCollectId=" + activeCollectId + ", screen=" + screen.getTitle().getString());
      if (failCounter > 5) {
         if (mc.player != null) {
            DebugLogger.log("Closing screen after repeated confirmation failures. activeCollectId=" + activeCollectId);
            mc.player.closeScreen();
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
            if (mc.player != null) {
               mc.player.closeScreen();
            }

         });
      })).start();
   }

   static {
      DEFAULT_ARMOR_ENCHANTS = Arrays.asList(new EnchantmentData(Enchantments.PROTECTION, 5), new EnchantmentData(Enchantments.BLAST_PROTECTION, 5), new EnchantmentData(Enchantments.FIRE_PROTECTION, 5), new EnchantmentData(Enchantments.PROJECTILE_PROTECTION, 5), new EnchantmentData(Enchantments.MENDING, 1), new EnchantmentData(Enchantments.UNBREAKING, 5));
      initItemDatabase();
   }

   private static class CollectItem {
      protected final String id;
      protected final String name;
      protected final Item item;
      protected String tag;
      protected int maxSellPrice = 10000000;
      protected ItemStack customStack;
      protected List<EnchantmentData> requiredEnchants = new ArrayList();
      protected List<String> customEnchants = new ArrayList();
      protected List<AttributeData> attributes = new ArrayList();
      protected List<EffectData> effects = new ArrayList();
      protected boolean addArmorDefaults = false;

      CollectItem(String id, String name, Item item) {
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

      public CollectItem setStack(ItemStack stack) {
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

      public CollectItem addAttribute(EntityAttribute attribute, double value, EntityAttributeModifier.Operation operation) {
         this.attributes.add(AutoBuyLogic.AttributeData.of(attribute, value, operation));
         return this;
      }

      public CollectItem addEffect(StatusEffect effect, int seconds, int amplifier) {
         this.effects.add(AutoBuyLogic.EffectData.of(effect, seconds, amplifier));
         return this;
      }

      public boolean matches(ItemStack stack) {
         if (stack.getItem() != this.item) {
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
               if (!stack.hasTag()) {
                  return false;
               }

               nbt = stack.getTag().asString();
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
                  Map<Enchantment, Integer> stackEnchants = EnchantmentHelper.get(stack);
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

            if (!this.customEnchants.isEmpty() && stack.hasTag() && stack.getTag().contains("display")) {
               NbtCompound display = stack.getTag().getCompound("display");
               if (display.contains("Lore")) {
                  NbtList lore = display.getList("Lore", 8);
                  List<String> loreLines = new ArrayList();

                  for(int i = 0; i < lore.size(); ++i) {
                     loreLines.add(lore.getString(i).toLowerCase());
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
               List<StatusEffectInstance> potionEffects = PotionUtil.getPotionEffects(stack);
               if (potionEffects.size() != this.effects.size()) {
                  return false;
               }

               var3 = this.effects.iterator();

               while(var3.hasNext()) {
                  EffectData check = (EffectData)var3.next();
                  boolean found = false;
                  Iterator var23 = potionEffects.iterator();

                  while(var23.hasNext()) {
                     StatusEffectInstance effect = (StatusEffectInstance)var23.next();
                     if (effect.getEffectType() == check.getEffect() && effect.getDuration() >= check.getDuration() && effect.getAmplifier() >= check.getAmplifier()) {
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

      private boolean hasRequiredAttributes(ItemStack stack) {
         if (!stack.hasTag()) {
            return false;
         } else {
            NbtCompound nbt = stack.getTag();
            if (nbt == null || !nbt.contains("AttributeModifiers")) {
               return false;
            } else {
               NbtList modifiers = nbt.getList("AttributeModifiers", 10);
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

      private boolean hasMatchingModifier(NbtList modifiers, AttributeData required) {
         String attributeId = String.valueOf(Registry.ATTRIBUTE.getId(required.getAttribute()));

         for(int i = 0; i < modifiers.size(); ++i) {
            NbtCompound modifier = modifiers.getCompound(i);
            if (attributeId.equals(modifier.getString("AttributeName")) && modifier.getByte("Operation") == required.getOperation().getId() && Math.abs(modifier.getDouble("Amount") - required.getValue()) < 1.0E-4) {
               return true;
            }
         }

         return false;
      }

      private boolean hasRequiredAttributeNbtText(ItemStack stack) {
         if (!stack.hasTag()) {
            return false;
         } else {
            String nbtText = this.normalizeText(stack.getTag().asString());
            Iterator var3 = this.attributes.iterator();

            while(var3.hasNext()) {
               AttributeData required = (AttributeData)var3.next();
               String attributeId = this.normalizeText(String.valueOf(Registry.ATTRIBUTE.getId(required.getAttribute())));
               if (!nbtText.contains(attributeId) || !nbtText.contains(this.normalizeNumber(required.getValue()))) {
                  return false;
               }
            }

            return true;
         }
      }

      private boolean matchesDisplayName(ItemStack stack) {
         String stackName = this.normalizeText(stack.getName().getString());
         String expectedName = this.normalizeText(this.name);
         return !stackName.isEmpty() && !expectedName.isEmpty() && stackName.contains(expectedName);
      }

      private boolean hasRequiredTooltipAttributes(ItemStack stack) {
         if (AutoBuyLogic.mc.player == null) {
            return false;
         } else {
            List<Text> tooltip = stack.getTooltip(AutoBuyLogic.mc.player, TooltipContext.Default.NORMAL);
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

      private boolean hasMatchingTooltipLine(List<Text> tooltip, AttributeData required) {
         Iterator var3 = tooltip.iterator();

         while(var3.hasNext()) {
            Text line = (Text)var3.next();
            String normalizedLine = this.normalizeText(line.getString());
            if (this.matchesTooltipAlias(normalizedLine, required) && this.matchesTooltipValue(normalizedLine, required)) {
               return true;
            }
         }

         return false;
      }

      private boolean matchesTooltipAlias(String normalizedLine, AttributeData required) {
         if (required.getAttribute() == EntityAttributes.GENERIC_MOVEMENT_SPEED && (normalizedLine.contains("скорость атаки") || normalizedLine.contains("attack speed"))) {
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
         if (required.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
            roundedValue = (int)Math.round(Math.abs(required.getValue()) * 100.0);
            return normalizedLine.contains(String.valueOf(roundedValue)) && normalizedLine.contains("%");
         } else {
            roundedValue = (int)Math.round(Math.abs(required.getValue()));
            return normalizedLine.contains(String.valueOf(roundedValue));
         }
      }

      private String[] getAttributeAliases(EntityAttribute attribute) {
         if (attribute == EntityAttributes.GENERIC_MOVEMENT_SPEED) {
            return new String[]{"скорость", "speed"};
         } else if (attribute == EntityAttributes.GENERIC_ATTACK_SPEED) {
            return new String[]{"скорость атаки", "attack speed"};
         } else if (attribute == EntityAttributes.GENERIC_MAX_HEALTH) {
            return new String[]{"максимальное здоровье", "max health"};
         } else if (attribute == EntityAttributes.GENERIC_ARMOR) {
            return new String[]{"броня", "armor"};
         } else if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE) {
            return new String[]{"урон", "attack damage"};
         } else if (attribute == EntityAttributes.GENERIC_ARMOR_TOUGHNESS) {
            return new String[]{"твердость брони", "armor toughness"};
         } else {
            return new String[]{this.normalizeText(attribute.getTranslationKey())};
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
      EnchantedItem(String id, String name, Item item, int maxPrice) {
         super(id, name, item);
         this.maxSellPrice = maxPrice;
      }
   }

   private static class EnchantmentData {
      private final Enchantment enchantment;
      private final int level;

      public EnchantmentData(Enchantment enchantment, int level) {
         this.enchantment = enchantment;
         this.level = level;
      }

      public static EnchantmentData of(Enchantment enchantment, int level) {
         return new EnchantmentData(enchantment, level);
      }

      public Enchantment getEnchantment() {
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
      private final StatusEffect effect;
      private final int duration;
      private final int amplifier;
      private final boolean instant;

      public EffectData(StatusEffect effect, int duration, int amplifier, boolean instant) {
         this.effect = effect;
         this.duration = duration;
         this.amplifier = amplifier;
         this.instant = instant;
      }

      public static EffectData of(StatusEffect effect, int seconds, int amplifier) {
         return new EffectData(effect, seconds * 20, amplifier, false);
      }

      public static EffectData instant(StatusEffect effect, int amplifier) {
         return new EffectData(effect, 1, amplifier, true);
      }

      public StatusEffect getEffect() {
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
      private final EntityAttribute attribute;
      private final double value;
      private final EntityAttributeModifier.Operation operation;

      public AttributeData(EntityAttribute attribute, double value, EntityAttributeModifier.Operation operation) {
         this.attribute = attribute;
         this.value = value;
         this.operation = operation;
      }

      public static AttributeData of(EntityAttribute attribute, double value, EntityAttributeModifier.Operation operation) {
         return new AttributeData(attribute, value, operation);
      }

      public EntityAttribute getAttribute() {
         return this.attribute;
      }

      public double getValue() {
         return this.value;
      }

      public EntityAttributeModifier.Operation getOperation() {
         return this.operation;
      }
   }
}
