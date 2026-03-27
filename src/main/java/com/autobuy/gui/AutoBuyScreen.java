package com.autobuy.gui;

import com.autobuy.AutoBuyMod;
import com.autobuy.util.DebugLogger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_2585;
import net.minecraft.class_310;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4587;

public class AutoBuyScreen extends class_437 {
   private static final String CREDIT_TEXT = "§l§nBy medny_byk";
   private static final int VISIBLE_ITEMS = 8;
   private static final int VISIBLE_CONFIGS = 8;
   private static final int FIELD_HEIGHT = 38;
   private static final int PANEL_WIDTH = 720;
   private static final int PANEL_HEIGHT = 452;
   private static final int CONFIG_PANEL_WIDTH = 230;
   private static final int CONFIG_LIST_ROW_HEIGHT = 24;
   private final List<PriceField> fields = new ArrayList();
   private final List<String> configProfiles = new ArrayList();
   private int scrollOffset = 0;
   private int configScrollOffset = 0;
   private String selectedProfile;
   private float time = 0.0F;
   private String statusMessage = "";
   private long statusMessageUntil = 0L;
   private int panelOffsetX = 0;
   private int panelOffsetY = 0;
   private boolean draggingPanel = false;
   private int dragStartMouseX = 0;
   private int dragStartMouseY = 0;
   private int dragStartOffsetX = 0;
   private int dragStartOffsetY = 0;
   private class_342 profileNameField;
   private class_4185 saveProfileButton;
   private class_4185 loadButton;
   private class_4185 deleteButton;
   private class_4185 openFolderButton;

   public AutoBuyScreen() {
      super(new class_2585(""));
      this.fields.add(new PriceField("enchanted_golden_apple", "Зачарованное золотое яблоко", new class_1799(class_1802.field_8367), 0));
      this.fields.add(new PriceField("golden_apple", "Золотое яблоко", new class_1799(class_1802.field_8463), 1));
      this.fields.add(new PriceField("diamond", "Алмаз", new class_1799(class_1802.field_8477), 2));
      this.fields.add(new PriceField("netherite_ingot", "Незеритовый слиток", new class_1799(class_1802.field_22020), 3));
      this.fields.add(new PriceField("ender_pearl", "Жемчуг Края", new class_1799(class_1802.field_8634), 4));
      this.fields.add(new PriceField("experience_bottle", "Пузырёк опыта", new class_1799(class_1802.field_8287), 5));
      this.fields.add(new PriceField("totem_of_undying", "Тотем бессмертия", new class_1799(class_1802.field_8288), 6));
      this.fields.add(new PriceField("beacon", "Маяк", new class_1799(class_1802.field_8668), 7));
      this.fields.add(new PriceField("shulker_box", "Шалкер", new class_1799(class_1802.field_8545), 8));
      this.fields.add(new PriceField("elytra", "Элитры", new class_1799(class_1802.field_8833), 9));
      this.fields.add(new PriceField("golden_carrot", "Золотая морковка", new class_1799(class_1802.field_8071), 10));
      this.fields.add(new PriceField("dragon_head", "Голова дракона", new class_1799(class_1802.field_8712), 11));
      this.fields.add(new PriceField("iron_ingot", "Железный слиток", new class_1799(class_1802.field_8620), 12));
      this.fields.add(new PriceField("obsidian", "Обсидиан", new class_1799(class_1802.field_8281), 13));
      this.fields.add(new PriceField("helmet_crusher", "Шлем Крушителя", this.makeEnchantedWithAll(new class_1799(class_1802.field_22027)), 14));
      this.fields.add(new PriceField("chestplate_crusher", "Нагрудник Крушителя", this.makeEnchantedWithAll(new class_1799(class_1802.field_22028)), 15));
      this.fields.add(new PriceField("leggings_crusher", "Поножи Крушителя", this.makeEnchantedWithAll(new class_1799(class_1802.field_22029)), 16));
      this.fields.add(new PriceField("boots_crusher", "Ботинки Крушителя", this.makeEnchantedWithAll(new class_1799(class_1802.field_22030)), 17));
      this.fields.add(new PriceField("sword_crusher", "Меч Крушителя", this.makeEnchantedWithAll(new class_1799(class_1802.field_22022)), 18));
      this.fields.add(new PriceField("trident_crusher", "Трезубец Крушителя", this.makeEnchantedWithAll(new class_1799(class_1802.field_8547)), 19));
      this.fields.add(new PriceField("pickaxe_crusher", "Кирка Крушителя", this.makeEnchantedWithAll(new class_1799(class_1802.field_22024)), 20));
      this.fields.add(new PriceField("crossbow_crusher", "Арбалет Крушителя", this.makeEnchantedWithAll(new class_1799(class_1802.field_8399)), 21));
      this.fields.add(new PriceField("sphere_chaos", "Сфера хаоса", new class_1799(class_1802.field_8575), 22));
      this.fields.add(new PriceField("sphere_ares", "Сфера ареса", new class_1799(class_1802.field_8575), 23));
      this.fields.add(new PriceField("sphere_aftin", "Сфера афины", new class_1799(class_1802.field_8575), 24));
      this.fields.add(new PriceField("talisman_crusher", "Талисман крушителя", new class_1799(class_1802.field_8288), 25));
      this.fields.add(new PriceField("talisman_rage", "Талисман ярости", new class_1799(class_1802.field_8288), 26));
      this.fields.add(new PriceField("talisman_punisher", "Талисман карателя", new class_1799(class_1802.field_8288), 27));
      this.fields.add(new PriceField("spawner", "Спавнер", new class_1799(class_1802.field_8849), 28, true));
      this.refreshProfiles();
      this.selectedProfile = AutoBuyMod.CONFIG.getActiveProfileName();
   }

   private class_1799 makeEnchantedWithAll(class_1799 stack) {
      stack.method_7978(class_1893.field_9101, 1);
      stack.method_7978(class_1893.field_9119, 3);
      stack.method_7978(class_1893.field_9111, 4);
      return stack;
   }

   private int getPanelLeft() {
      return this.field_22789 / 2 - PANEL_WIDTH / 2 + this.panelOffsetX;
   }

   private int getPanelRight() {
      return this.getPanelLeft() + PANEL_WIDTH;
   }

   private int getPanelTop() {
      return this.field_22790 / 2 - PANEL_HEIGHT / 2 + this.panelOffsetY;
   }

   private int getPanelBottom() {
      return this.getPanelTop() + PANEL_HEIGHT;
   }

   private int getConfigPanelLeft() {
      return this.getPanelRight() - CONFIG_PANEL_WIDTH - 18;
   }

   private int getConfigPanelRight() {
      return this.getPanelRight() - 18;
   }

   private int getConfigPanelTop() {
      return this.getPanelTop() + 56;
   }

   private int getConfigListTop() {
      return this.getConfigPanelTop() + 138;
   }

   private int getConfigListBottom() {
      return this.getPanelBottom() - 24;
   }

   private int getAccentColor(float brightness) {
      int r = (int)(100.0F * brightness);
      int g = (int)(150.0F * brightness);
      int b = (int)(220.0F * brightness);
      return r << 16 | g << 8 | b;
   }

   private void drawChamferedRect(class_4587 matrices, int x1, int y1, int x2, int y2, int cut, int color) {
      method_25294(matrices, x1 + cut, y1, x2 - cut, y2, color);
      method_25294(matrices, x1, y1 + cut, x2, y2 - cut, color);
   }

   protected void method_25426() {
      int panelLeft = this.getPanelLeft();
      int panelTop = this.getPanelTop();
      Iterator var3 = this.fields.iterator();

      while(var3.hasNext()) {
         PriceField pf = (PriceField)var3.next();
         int y = panelTop + 70 + (pf.index - this.scrollOffset) * FIELD_HEIGHT;
         pf.field = new class_342(this.field_22793, panelLeft + 218, y, pf.hasMobInput ? 90 : 122, 20, class_2585.field_24366);
         pf.field.method_1880(12);
         pf.field.method_1890((text) -> {
            return text.matches("\\d*");
         });
         pf.field.method_1862(pf.index >= this.scrollOffset && pf.index < this.scrollOffset + VISIBLE_ITEMS);
         this.field_22786.add(pf.field);
         if (pf.hasMobInput) {
            pf.mobField = new class_342(this.field_22793, panelLeft + 344, y, 94, 20, class_2585.field_24366);
            pf.mobField.method_1880(32);
            pf.mobField.method_1862(pf.index >= this.scrollOffset && pf.index < this.scrollOffset + VISIBLE_ITEMS);
            this.field_22786.add(pf.mobField);
         }
      }

      this.profileNameField = new class_342(this.field_22793, this.getConfigPanelLeft() + 58, this.getConfigPanelTop() + 48, 146, 20, class_2585.field_24366);
      this.profileNameField.method_1880(32);
      this.profileNameField.method_1852("");
      this.profileNameField.method_1890((text) -> {
         return true;
      });
      this.profileNameField.method_1858(false);
      this.profileNameField.method_1862(true);
      this.field_22786.add(this.profileNameField);
      this.saveProfileButton = this.createButton(0, 0, 64, 20, "Сохранить", this::saveUsingTypedProfileName);
      this.loadButton = this.createButton(0, 0, 64, 20, "Загрузить", () -> {
         if (this.selectedProfile != null) {
            AutoBuyMod.CONFIG.loadProfile(this.selectedProfile);
            this.applyConfigToFields();
            this.refreshProfiles();
            this.setStatus("Загружен конфиг: " + this.selectedProfile);
         }
      });
      this.deleteButton = this.createButton(0, 0, 64, 20, "Удалить", () -> {
         if (this.selectedProfile == null) {
            return;
         } else if ("default".equalsIgnoreCase(this.selectedProfile)) {
            this.setStatus("Default конфиг удалить нельзя");
         } else {
            String deletedProfile = this.selectedProfile;
            if (AutoBuyMod.CONFIG.deleteProfile(deletedProfile)) {
               this.refreshProfiles();
               this.selectedProfile = AutoBuyMod.CONFIG.getActiveProfileName();
               this.applyConfigToFields();
               this.setStatus("Удален конфиг: " + deletedProfile);
            } else {
               this.setStatus("Не удалось удалить конфиг");
            }
         }
      });
      this.openFolderButton = this.createButton(0, 0, CONFIG_PANEL_WIDTH - 28, 20, "Открыть папку", this::openConfigFolder);
      this.method_25411(this.saveProfileButton);
      this.method_25411(this.loadButton);
      this.method_25411(this.deleteButton);
      this.method_25411(this.openFolderButton);
      this.applyConfigToFields();
      this.updatePositions();
   }

   private class_4185 createButton(int x, int y, int width, int height, String label, Runnable action) {
      return new class_4185(x, y, width, height, new class_2585(label), (button) -> {
         action.run();
      });
   }

   private void saveUsingTypedProfileName() {
      String profileName = this.profileNameField != null ? this.profileNameField.method_1882().trim() : "";
      if (profileName.isEmpty()) {
         profileName = this.selectedProfile != null ? this.selectedProfile : "default";
      }

      this.saveFieldsToConfig();
      AutoBuyMod.CONFIG.saveProfile(profileName);
      this.refreshProfiles();
      this.selectedProfile = AutoBuyMod.CONFIG.getActiveProfileName();
      if (this.profileNameField != null) {
         this.profileNameField.method_1852("");
      }

      this.setStatus("Сохранен конфиг: " + this.selectedProfile);
   }

   private void openConfigFolder() {
      try {
         Path configDir = AutoBuyMod.CONFIG.getConfigDirectory();
         Runtime.getRuntime().exec(new String[]{"explorer.exe", configDir.toString()});
         this.setStatus("Открыта папка конфигов");
      } catch (Exception var2) {
         DebugLogger.logException("Failed to open config folder", var2);
         this.setStatus("Не удалось открыть папку");
      }
   }

   private void refreshProfiles() {
      this.configProfiles.clear();
      this.configProfiles.addAll(AutoBuyMod.CONFIG.listProfiles());
      if (this.selectedProfile == null || !this.configProfiles.contains(this.selectedProfile)) {
         this.selectedProfile = AutoBuyMod.CONFIG.getActiveProfileName();
      }

      int maxOffset = Math.max(0, this.configProfiles.size() - VISIBLE_CONFIGS);
      if (this.configScrollOffset > maxOffset) {
         this.configScrollOffset = maxOffset;
      }
   }

   private void applyConfigToFields() {
      Iterator var1 = this.fields.iterator();

      while(var1.hasNext()) {
         PriceField pf = (PriceField)var1.next();
         if (pf.field != null) {
            long saved = AutoBuyMod.CONFIG.getPrice(pf.itemId);
            pf.field.method_1852(saved > 0L ? String.valueOf(saved) : "");
         }

         if (pf.mobField != null) {
            pf.mobField.method_1852(AutoBuyMod.CONFIG.getMobFilter(pf.itemId));
         }
      }
   }

   private void saveFieldsToConfig() {
      Iterator var1 = this.fields.iterator();

      while(var1.hasNext()) {
         PriceField pf = (PriceField)var1.next();
         String text = pf.field.method_1882().trim();
         if (!text.isEmpty()) {
            try {
               AutoBuyMod.CONFIG.setPrice(pf.itemId, Long.parseLong(text));
            } catch (NumberFormatException var6) {
               AutoBuyMod.CONFIG.removePrice(pf.itemId);
            }
         } else {
            AutoBuyMod.CONFIG.removePrice(pf.itemId);
         }

         if (pf.mobField != null) {
            AutoBuyMod.CONFIG.setMobFilter(pf.itemId, pf.mobField.method_1882());
         }
      }
   }

   private void setStatus(String message) {
      this.statusMessage = message;
      this.statusMessageUntil = System.currentTimeMillis() + 2500L;
   }

   private void updatePositions() {
      int panelLeft = this.getPanelLeft();
      int panelTop = this.getPanelTop();
      Iterator var3 = this.fields.iterator();

      while(var3.hasNext()) {
         PriceField pf = (PriceField)var3.next();
         int targetY = panelTop + 70 + (pf.index - this.scrollOffset) * FIELD_HEIGHT;
         pf.currentY += ((float)targetY - pf.currentY) * 0.15F;
         int y = (int)pf.currentY;
         if (pf.field != null) {
            pf.field.field_22761 = y;
            pf.field.field_22760 = panelLeft + 218;
            pf.field.field_22764 = pf.index >= this.scrollOffset && pf.index < this.scrollOffset + VISIBLE_ITEMS;
         }

         if (pf.mobField != null) {
            pf.mobField.field_22761 = y;
            pf.mobField.field_22760 = panelLeft + 344;
            pf.mobField.field_22764 = pf.index >= this.scrollOffset && pf.index < this.scrollOffset + VISIBLE_ITEMS;
         }
      }

      if (this.profileNameField != null) {
         this.profileNameField.field_22760 = this.getConfigPanelLeft() + 58;
         this.profileNameField.field_22761 = this.getConfigPanelTop() + 48;
      }

      if (this.saveProfileButton != null) {
         this.saveProfileButton.field_22760 = this.getConfigPanelLeft() + 14;
         this.saveProfileButton.field_22761 = this.getConfigPanelTop() + 80;
      }

      if (this.loadButton != null) {
         this.loadButton.field_22760 = this.getConfigPanelLeft() + 82;
         this.loadButton.field_22761 = this.getConfigPanelTop() + 80;
      }

      if (this.deleteButton != null) {
         this.deleteButton.field_22760 = this.getConfigPanelLeft() + 150;
         this.deleteButton.field_22761 = this.getConfigPanelTop() + 80;
      }

      if (this.openFolderButton != null) {
         this.openFolderButton.field_22760 = this.getConfigPanelLeft() + 14;
         this.openFolderButton.field_22761 = this.getConfigPanelTop() + 104;
      }
   }

   public void method_25394(class_4587 matrices, int mouseX, int mouseY, float delta) {
      this.time += delta * 0.03F;
      this.updatePositions();
      this.method_25420(matrices);
      int panelLeft = this.getPanelLeft();
      int panelRight = this.getPanelRight();
      int panelTop = this.getPanelTop();
      int panelBottom = this.getPanelBottom();
      int borderColor = this.getAccentColor(0.72F + 0.08F * (float)Math.sin((double)(this.time * 2.0F)));
      this.drawChamferedRect(matrices, panelLeft, panelTop, panelRight, panelBottom, 16, 857735372);
      method_25294(matrices, panelLeft + 10, panelTop + 14, panelLeft + 34, panelBottom - 14, 436207615);
      method_25294(matrices, panelRight - 34, panelTop + 14, panelRight - 10, panelBottom - 14, 436207615);
      method_25294(matrices, panelLeft + 10, panelTop + 12, panelRight - 10, panelBottom - 12, 754974720);
      this.drawChamferedRect(matrices, panelLeft + 1, panelTop + 1, panelRight - 1, panelBottom - 1, 16, 536870912 | borderColor & 16777215);

      float titleY = (float)(panelTop + 21) + (float)Math.sin((double)this.time * 1.5) * 2.0F;
      method_25300(matrices, this.field_22793, CREDIT_TEXT, this.field_22789 / 2 + this.panelOffsetX, (int)titleY, this.getAccentColor(0.9F));

      for(int dividerX = panelLeft + 18; dividerX < panelRight - 18; dividerX += 5) {
         method_25294(matrices, dividerX, panelTop + 46, dividerX + 2, panelTop + 47, this.getAccentColor(0.7F));
      }

      method_25303(matrices, this.field_22793, "§7[ цена за 1 шт ]", panelLeft + 194, panelTop + 56, 11184810);
      this.renderConfigPanel(matrices, mouseX, mouseY);
      if (this.profileNameField != null) {
         this.profileNameField.method_25394(matrices, mouseX, mouseY, delta);
      }

      class_310.method_1551().method_1480().field_4730 = 100.0F;

      for(int itemIndex = this.scrollOffset; itemIndex < this.scrollOffset + VISIBLE_ITEMS && itemIndex < this.fields.size(); ++itemIndex) {
         PriceField pf = (PriceField)this.fields.get(itemIndex);
         int y = (int)pf.currentY;
         boolean hover = mouseX >= panelLeft + 8 && mouseX <= this.getConfigPanelLeft() - 12 && mouseY >= y - 6 && mouseY <= y + 24;
         if (hover) {
            pf.hoverAnim = Math.min(1.0F, pf.hoverAnim + delta * 0.12F);
            pf.glowAnim += delta * 0.15F;
         } else {
            pf.hoverAnim = Math.max(0.0F, pf.hoverAnim - delta * 0.1F);
            pf.glowAnim = 0.0F;
         }

         if (pf.hoverAnim > 0.01F) {
            int hoverAlpha = (int)(64.0F * pf.hoverAnim);
            int hoverColor = hoverAlpha << 24 | this.getAccentColor(0.8F) & 16777215;
            int offsetY = (int)(Math.sin((double)(pf.glowAnim * 8.0F)) * 2.0);
            method_25294(matrices, panelLeft + 10, y - 6 + offsetY, this.getConfigPanelLeft() - 14, y + 26 + offsetY, hoverColor);
         }

         float iconFloat = (float)Math.sin((double)this.time * 2.5 + (double)itemIndex) * 1.5F;
         this.field_22788.method_27953(pf.icon, panelLeft + 22, y - 2 + (int)iconFloat);
         method_25303(matrices, this.field_22793, "§f" + pf.displayName, panelLeft + 48, y + 4, 16777215);
         if (pf.field != null) {
            pf.field.method_25394(matrices, mouseX, mouseY, delta);
         }

         if (pf.mobField != null) {
            method_25303(matrices, this.field_22793, "§7Моб:", panelLeft + 315, y + 6, 11184810);
            pf.mobField.method_25394(matrices, mouseX, mouseY, delta);
         }
      }

      class_310.method_1551().method_1480().field_4730 = 0.0F;
      String pageInfo = String.format("§7[ §d%d-%d §7из §d%d §7]", this.scrollOffset + 1, Math.min(this.scrollOffset + VISIBLE_ITEMS, this.fields.size()), this.fields.size());
      method_25303(matrices, this.field_22793, pageInfo, panelLeft + 318, panelBottom - 18, 8956671);
      super.method_25394(matrices, mouseX, mouseY, delta);
   }

   private void renderConfigPanel(class_4587 matrices, int mouseX, int mouseY) {
      int configLeft = this.getConfigPanelLeft();
      int configRight = this.getConfigPanelRight();
      int configTop = this.getConfigPanelTop();
      int configBottom = this.getPanelBottom() - 18;
      int labelColor = 11184810;
      int accentColor = this.getAccentColor(0.88F);
      method_25303(matrices, this.field_22793, "§f§lКонфиги", configLeft + 14, configTop + 12, 16777215);
      method_25303(matrices, this.field_22793, "Активный:", configLeft + 14, configTop + 32, labelColor);
      method_25303(matrices, this.field_22793, AutoBuyMod.CONFIG.getActiveProfileName(), configLeft + 82, configTop + 32, accentColor);
      method_25303(matrices, this.field_22793, "Имя:", configLeft + 14, configTop + 56, labelColor);
      method_25294(matrices, configLeft + 54, configTop + 66, configLeft + 202, configTop + 67, this.getAccentColor(0.62F));

      int listTop = this.getConfigListTop();
      int listBottom = this.getConfigListBottom();
      int lineColor = this.getAccentColor(0.66F);
      method_25294(matrices, configLeft + 12, listTop - 2, configRight - 12, listTop - 1, lineColor);
      method_25294(matrices, configLeft + 12, listBottom, configRight - 12, listBottom + 1, lineColor);
      int maxVisible = Math.min(this.configScrollOffset + VISIBLE_CONFIGS, this.configProfiles.size());

      for(int index = this.configScrollOffset; index < maxVisible; ++index) {
         String profile = (String)this.configProfiles.get(index);
         int y = listTop + (index - this.configScrollOffset) * CONFIG_LIST_ROW_HEIGHT;
         boolean active = profile.equals(AutoBuyMod.CONFIG.getActiveProfileName());
         boolean hovered = mouseX >= configLeft + 14 && mouseX <= configRight - 14 && mouseY >= y && mouseY <= y + CONFIG_LIST_ROW_HEIGHT - 2;
         int rowColor = hovered ? 738263039 : 436207615;
         method_25294(matrices, configLeft + 14, y, configRight - 14, y + CONFIG_LIST_ROW_HEIGHT - 2, rowColor);
         if (active) {
            method_25294(matrices, configLeft + 14, y, configLeft + 18, y + CONFIG_LIST_ROW_HEIGHT - 2, accentColor);
         }

         method_25303(matrices, this.field_22793, profile, configLeft + 24, y + 8, accentColor);
      }

      if (System.currentTimeMillis() < this.statusMessageUntil) {
         method_25303(matrices, this.field_22793, "§7" + this.statusMessage, configLeft + 14, configBottom - 8, labelColor);
      }
   }

   public boolean method_25401(double mouseX, double mouseY, double amount) {
      if (mouseX >= (double)(this.getConfigPanelLeft() + 12) && mouseX <= (double)(this.getConfigPanelRight() - 12) && mouseY >= (double)(this.getConfigListTop() - 6) && mouseY <= (double)this.getConfigListBottom()) {
         int maxOffset = Math.max(0, this.configProfiles.size() - VISIBLE_CONFIGS);
         if (amount > 0.0D && this.configScrollOffset > 0) {
            --this.configScrollOffset;
            return true;
         }

         if (amount < 0.0D && this.configScrollOffset < maxOffset) {
            ++this.configScrollOffset;
            return true;
         }
      } else if (mouseX >= (double)this.getPanelLeft() && mouseX <= (double)this.getConfigPanelLeft() && mouseY >= (double)this.getPanelTop() && mouseY <= (double)this.getPanelBottom()) {
         int maxOffset = Math.max(0, this.fields.size() - VISIBLE_ITEMS);
         if (amount > 0.0D && this.scrollOffset > 0) {
            --this.scrollOffset;
            this.updatePositions();
            return true;
         }

         if (amount < 0.0D && this.scrollOffset < maxOffset) {
            ++this.scrollOffset;
            this.updatePositions();
            return true;
         }
      }

      return super.method_25401(mouseX, mouseY, amount);
   }

   public boolean method_25402(double mouseX, double mouseY, int button) {
      if (this.profileNameField != null && this.profileNameField.method_25402(mouseX, mouseY, button)) {
         return true;
      }

      if (button == 0 && mouseX >= (double)this.getPanelLeft() && mouseX <= (double)this.getPanelRight() && mouseY >= (double)this.getPanelTop() && mouseY <= (double)(this.getPanelTop() + 40)) {
         this.draggingPanel = true;
         this.dragStartMouseX = (int)mouseX;
         this.dragStartMouseY = (int)mouseY;
         this.dragStartOffsetX = this.panelOffsetX;
         this.dragStartOffsetY = this.panelOffsetY;
      }

      if (button == 0 && mouseX >= (double)(this.getConfigPanelLeft() + 14) && mouseX <= (double)(this.getConfigPanelRight() - 14) && mouseY >= (double)this.getConfigListTop() && mouseY <= (double)this.getConfigListBottom()) {
         int relativeIndex = (int)((mouseY - (double)this.getConfigListTop()) / (double)CONFIG_LIST_ROW_HEIGHT);
         int profileIndex = this.configScrollOffset + relativeIndex;
         if (profileIndex >= 0 && profileIndex < this.configProfiles.size()) {
            this.selectedProfile = (String)this.configProfiles.get(profileIndex);
            return true;
         }
      }

      return super.method_25402(mouseX, mouseY, button);
   }

   public boolean method_25403(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.draggingPanel && button == 0) {
         this.panelOffsetX = this.dragStartOffsetX + (int)mouseX - this.dragStartMouseX;
         this.panelOffsetY = this.dragStartOffsetY + (int)mouseY - this.dragStartMouseY;
         this.updatePositions();
         return true;
      } else {
         return super.method_25403(mouseX, mouseY, button, deltaX, deltaY);
      }
   }

   public boolean method_25406(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.draggingPanel = false;
      }

      return super.method_25406(mouseX, mouseY, button);
   }

   public boolean method_25404(int keyCode, int scanCode, int modifiers) {
      if (this.profileNameField != null && this.profileNameField.method_25370() && this.profileNameField.method_25404(keyCode, scanCode, modifiers)) {
         return true;
      } else {
         Iterator var4 = this.fields.iterator();

         while(var4.hasNext()) {
            PriceField pf = (PriceField)var4.next();
            if (pf.field != null && pf.field.method_25370() && pf.field.method_25404(keyCode, scanCode, modifiers)) {
               return true;
            }

            if (pf.mobField != null && pf.mobField.method_25370() && pf.mobField.method_25404(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         return super.method_25404(keyCode, scanCode, modifiers);
      }
   }

   public boolean method_25400(char chr, int keyCode) {
      if (this.profileNameField != null && this.profileNameField.method_25370() && this.profileNameField.method_25400(chr, keyCode)) {
         return true;
      } else {
         Iterator var3 = this.fields.iterator();

         while(var3.hasNext()) {
            PriceField pf = (PriceField)var3.next();
            if (pf.field != null && pf.field.method_25370() && pf.field.method_25400(chr, keyCode)) {
               return true;
            }

            if (pf.mobField != null && pf.mobField.method_25370() && pf.mobField.method_25400(chr, keyCode)) {
               return true;
            }
         }

         return super.method_25400(chr, keyCode);
      }
   }

   public boolean shouldPause() {
      return false;
   }

   private static class PriceField {
      class_342 field;
      class_342 mobField;
      final String itemId;
      final String displayName;
      final class_1799 icon;
      final int index;
      final boolean hasMobInput;
      float hoverAnim = 0.0F;
      float glowAnim = 0.0F;
      float currentY = 0.0F;

      PriceField(String itemId, String displayName, class_1799 icon, int index) {
         this(itemId, displayName, icon, index, false);
      }

      PriceField(String itemId, String displayName, class_1799 icon, int index, boolean hasMobInput) {
         this.itemId = itemId;
         this.displayName = displayName;
         this.icon = icon;
         this.index = index;
         this.hasMobInput = hasMobInput;
      }
   }
}
