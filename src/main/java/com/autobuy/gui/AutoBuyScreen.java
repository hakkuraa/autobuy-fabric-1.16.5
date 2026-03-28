package com.autobuy.gui;

import com.autobuy.AutoBuyMod;
import com.autobuy.util.DebugLogger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.text.LiteralText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

public class AutoBuyScreen extends Screen {
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
   private TextFieldWidget profileNameField;
   private ButtonWidget saveProfileButton;
   private ButtonWidget loadButton;
   private ButtonWidget deleteButton;
   private ButtonWidget openFolderButton;

   public AutoBuyScreen() {
      super(new LiteralText(""));
      this.fields.add(new PriceField("enchanted_golden_apple", "Зачарованное золотое яблоко", new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), 0));
      this.fields.add(new PriceField("golden_apple", "Золотое яблоко", new ItemStack(Items.GOLDEN_APPLE), 1));
      this.fields.add(new PriceField("diamond", "Алмаз", new ItemStack(Items.DIAMOND), 2));
      this.fields.add(new PriceField("netherite_ingot", "Незеритовый слиток", new ItemStack(Items.NETHERITE_INGOT), 3));
      this.fields.add(new PriceField("ender_pearl", "Жемчуг Края", new ItemStack(Items.ENDER_PEARL), 4));
      this.fields.add(new PriceField("experience_bottle", "Пузырёк опыта", new ItemStack(Items.EXPERIENCE_BOTTLE), 5));
      this.fields.add(new PriceField("totem_of_undying", "Тотем бессмертия", new ItemStack(Items.TOTEM_OF_UNDYING), 6));
      this.fields.add(new PriceField("beacon", "Маяк", new ItemStack(Items.BEACON), 7));
      this.fields.add(new PriceField("shulker_box", "Шалкер", new ItemStack(Items.SHULKER_BOX), 8));
      this.fields.add(new PriceField("elytra", "Элитры", new ItemStack(Items.ELYTRA), 9));
      this.fields.add(new PriceField("golden_carrot", "Золотая морковка", new ItemStack(Items.GOLDEN_CARROT), 10));
      this.fields.add(new PriceField("dragon_head", "Голова дракона", new ItemStack(Items.DRAGON_HEAD), 11));
      this.fields.add(new PriceField("iron_ingot", "Железный слиток", new ItemStack(Items.IRON_INGOT), 12));
      this.fields.add(new PriceField("obsidian", "Обсидиан", new ItemStack(Items.OBSIDIAN), 13));
      this.fields.add(new PriceField("helmet_crusher", "Шлем Крушителя", this.makeEnchantedWithAll(new ItemStack(Items.NETHERITE_HELMET)), 14));
      this.fields.add(new PriceField("chestplate_crusher", "Нагрудник Крушителя", this.makeEnchantedWithAll(new ItemStack(Items.NETHERITE_CHESTPLATE)), 15));
      this.fields.add(new PriceField("leggings_crusher", "Поножи Крушителя", this.makeEnchantedWithAll(new ItemStack(Items.NETHERITE_LEGGINGS)), 16));
      this.fields.add(new PriceField("boots_crusher", "Ботинки Крушителя", this.makeEnchantedWithAll(new ItemStack(Items.NETHERITE_BOOTS)), 17));
      this.fields.add(new PriceField("sword_crusher", "Меч Крушителя", this.makeEnchantedWithAll(new ItemStack(Items.NETHERITE_SWORD)), 18));
      this.fields.add(new PriceField("trident_crusher", "Трезубец Крушителя", this.makeEnchantedWithAll(new ItemStack(Items.TRIDENT)), 19));
      this.fields.add(new PriceField("pickaxe_crusher", "Кирка Крушителя", this.makeEnchantedWithAll(new ItemStack(Items.NETHERITE_PICKAXE)), 20));
      this.fields.add(new PriceField("crossbow_crusher", "Арбалет Крушителя", this.makeEnchantedWithAll(new ItemStack(Items.CROSSBOW)), 21));
      this.fields.add(new PriceField("sphere_chaos", "Сфера хаоса", new ItemStack(Items.PLAYER_HEAD), 22));
      this.fields.add(new PriceField("sphere_ares", "Сфера ареса", new ItemStack(Items.PLAYER_HEAD), 23));
      this.fields.add(new PriceField("sphere_aftin", "Сфера афины", new ItemStack(Items.PLAYER_HEAD), 24));
      this.fields.add(new PriceField("talisman_crusher", "Талисман крушителя", new ItemStack(Items.TOTEM_OF_UNDYING), 25));
      this.fields.add(new PriceField("talisman_rage", "Талисман ярости", new ItemStack(Items.TOTEM_OF_UNDYING), 26));
      this.fields.add(new PriceField("talisman_punisher", "Талисман карателя", new ItemStack(Items.TOTEM_OF_UNDYING), 27));
      this.fields.add(new PriceField("spawner", "Спавнер", new ItemStack(Items.SPAWNER), 28, true));
      this.refreshProfiles();
      this.selectedProfile = AutoBuyMod.CONFIG.getActiveProfileName();
   }

   private ItemStack makeEnchantedWithAll(ItemStack stack) {
      stack.addEnchantment(Enchantments.MENDING, 1);
      stack.addEnchantment(Enchantments.UNBREAKING, 3);
      stack.addEnchantment(Enchantments.PROTECTION, 4);
      return stack;
   }

   private int getPanelLeft() {
      return this.width / 2 - PANEL_WIDTH / 2 + this.panelOffsetX;
   }

   private int getPanelRight() {
      return this.getPanelLeft() + PANEL_WIDTH;
   }

   private int getPanelTop() {
      return this.height / 2 - PANEL_HEIGHT / 2 + this.panelOffsetY;
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

   private void drawChamferedRect(MatrixStack matrices, int x1, int y1, int x2, int y2, int cut, int color) {
      fill(matrices, x1 + cut, y1, x2 - cut, y2, color);
      fill(matrices, x1, y1 + cut, x2, y2 - cut, color);
   }

   protected void init() {
      int panelLeft = this.getPanelLeft();
      int panelTop = this.getPanelTop();
      Iterator var3 = this.fields.iterator();

      while(var3.hasNext()) {
         PriceField pf = (PriceField)var3.next();
         int y = panelTop + 70 + (pf.index - this.scrollOffset) * FIELD_HEIGHT;
         pf.field = new TextFieldWidget(this.textRenderer, panelLeft + 218, y, pf.hasMobInput ? 90 : 122, 20, LiteralText.EMPTY);
         pf.field.setMaxLength(12);
         pf.field.setTextPredicate((text) -> {
            return text.matches("\\d*");
         });
         pf.field.setVisible(pf.index >= this.scrollOffset && pf.index < this.scrollOffset + VISIBLE_ITEMS);
         this.children.add(pf.field);
         if (pf.hasMobInput) {
            pf.mobField = new TextFieldWidget(this.textRenderer, panelLeft + 344, y, 94, 20, LiteralText.EMPTY);
            pf.mobField.setMaxLength(32);
            pf.mobField.setVisible(pf.index >= this.scrollOffset && pf.index < this.scrollOffset + VISIBLE_ITEMS);
            this.children.add(pf.mobField);
         }
      }

      this.profileNameField = new TextFieldWidget(this.textRenderer, this.getConfigPanelLeft() + 58, this.getConfigPanelTop() + 48, 146, 20, LiteralText.EMPTY);
      this.profileNameField.setMaxLength(32);
      this.profileNameField.setText("");
      this.profileNameField.setTextPredicate((text) -> {
         return true;
      });
      this.profileNameField.setDrawsBackground(false);
      this.profileNameField.setVisible(true);
      this.children.add(this.profileNameField);
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
      this.addButton(this.saveProfileButton);
      this.addButton(this.loadButton);
      this.addButton(this.deleteButton);
      this.addButton(this.openFolderButton);
      this.applyConfigToFields();
      this.updatePositions();
   }

   private ButtonWidget createButton(int x, int y, int width, int height, String label, Runnable action) {
      return new ButtonWidget(x, y, width, height, new LiteralText(label), (button) -> {
         action.run();
      });
   }

   private void saveUsingTypedProfileName() {
      String profileName = this.profileNameField != null ? this.profileNameField.getText().trim() : "";
      if (profileName.isEmpty()) {
         profileName = this.selectedProfile != null ? this.selectedProfile : "default";
      }

      this.saveFieldsToConfig();
      AutoBuyMod.CONFIG.saveProfile(profileName);
      this.refreshProfiles();
      this.selectedProfile = AutoBuyMod.CONFIG.getActiveProfileName();
      if (this.profileNameField != null) {
         this.profileNameField.setText("");
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
            pf.field.setText(saved > 0L ? String.valueOf(saved) : "");
         }

         if (pf.mobField != null) {
            pf.mobField.setText(AutoBuyMod.CONFIG.getMobFilter(pf.itemId));
         }
      }
   }

   private void saveFieldsToConfig() {
      Iterator var1 = this.fields.iterator();

      while(var1.hasNext()) {
         PriceField pf = (PriceField)var1.next();
         String text = pf.field.getText().trim();
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
            AutoBuyMod.CONFIG.setMobFilter(pf.itemId, pf.mobField.getText());
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
            pf.field.y = y;
            pf.field.x = panelLeft + 218;
            pf.field.visible = pf.index >= this.scrollOffset && pf.index < this.scrollOffset + VISIBLE_ITEMS;
         }

         if (pf.mobField != null) {
            pf.mobField.y = y;
            pf.mobField.x = panelLeft + 344;
            pf.mobField.visible = pf.index >= this.scrollOffset && pf.index < this.scrollOffset + VISIBLE_ITEMS;
         }
      }

      if (this.profileNameField != null) {
         this.profileNameField.x = this.getConfigPanelLeft() + 58;
         this.profileNameField.y = this.getConfigPanelTop() + 48;
      }

      if (this.saveProfileButton != null) {
         this.saveProfileButton.x = this.getConfigPanelLeft() + 14;
         this.saveProfileButton.y = this.getConfigPanelTop() + 80;
      }

      if (this.loadButton != null) {
         this.loadButton.x = this.getConfigPanelLeft() + 82;
         this.loadButton.y = this.getConfigPanelTop() + 80;
      }

      if (this.deleteButton != null) {
         this.deleteButton.x = this.getConfigPanelLeft() + 150;
         this.deleteButton.y = this.getConfigPanelTop() + 80;
      }

      if (this.openFolderButton != null) {
         this.openFolderButton.x = this.getConfigPanelLeft() + 14;
         this.openFolderButton.y = this.getConfigPanelTop() + 104;
      }
   }

   public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
      this.time += delta * 0.03F;
      this.updatePositions();
      this.renderBackground(matrices);
      int panelLeft = this.getPanelLeft();
      int panelRight = this.getPanelRight();
      int panelTop = this.getPanelTop();
      int panelBottom = this.getPanelBottom();
      int borderColor = this.getAccentColor(0.72F + 0.08F * (float)Math.sin((double)(this.time * 2.0F)));
      this.drawChamferedRect(matrices, panelLeft, panelTop, panelRight, panelBottom, 16, 857735372);
      fill(matrices, panelLeft + 10, panelTop + 14, panelLeft + 34, panelBottom - 14, 436207615);
      fill(matrices, panelRight - 34, panelTop + 14, panelRight - 10, panelBottom - 14, 436207615);
      fill(matrices, panelLeft + 10, panelTop + 12, panelRight - 10, panelBottom - 12, 754974720);
      this.drawChamferedRect(matrices, panelLeft + 1, panelTop + 1, panelRight - 1, panelBottom - 1, 16, 536870912 | borderColor & 16777215);

      float titleY = (float)(panelTop + 21) + (float)Math.sin((double)this.time * 1.5) * 2.0F;
      drawCenteredText(matrices, this.textRenderer, CREDIT_TEXT, this.width / 2 + this.panelOffsetX, (int)titleY, this.getAccentColor(0.9F));

      for(int dividerX = panelLeft + 18; dividerX < panelRight - 18; dividerX += 5) {
         fill(matrices, dividerX, panelTop + 46, dividerX + 2, panelTop + 47, this.getAccentColor(0.7F));
      }

      drawStringWithShadow(matrices, this.textRenderer, "§7[ цена за 1 шт ]", panelLeft + 194, panelTop + 56, 11184810);
      this.renderConfigPanel(matrices, mouseX, mouseY);
      if (this.profileNameField != null) {
         this.profileNameField.render(matrices, mouseX, mouseY, delta);
      }

      MinecraftClient.getInstance().getItemRenderer().zOffset = 100.0F;

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
            fill(matrices, panelLeft + 10, y - 6 + offsetY, this.getConfigPanelLeft() - 14, y + 26 + offsetY, hoverColor);
         }

         float iconFloat = (float)Math.sin((double)this.time * 2.5 + (double)itemIndex) * 1.5F;
         this.itemRenderer.renderInGui(pf.icon, panelLeft + 22, y - 2 + (int)iconFloat);
         drawStringWithShadow(matrices, this.textRenderer, "§f" + pf.displayName, panelLeft + 48, y + 4, 16777215);
         if (pf.field != null) {
            pf.field.render(matrices, mouseX, mouseY, delta);
         }

         if (pf.mobField != null) {
            drawStringWithShadow(matrices, this.textRenderer, "§7Моб:", panelLeft + 315, y + 6, 11184810);
            pf.mobField.render(matrices, mouseX, mouseY, delta);
         }
      }

      MinecraftClient.getInstance().getItemRenderer().zOffset = 0.0F;
      String pageInfo = String.format("§7[ §d%d-%d §7из §d%d §7]", this.scrollOffset + 1, Math.min(this.scrollOffset + VISIBLE_ITEMS, this.fields.size()), this.fields.size());
      drawStringWithShadow(matrices, this.textRenderer, pageInfo, panelLeft + 318, panelBottom - 18, 8956671);
      super.render(matrices, mouseX, mouseY, delta);
   }

   private void renderConfigPanel(MatrixStack matrices, int mouseX, int mouseY) {
      int configLeft = this.getConfigPanelLeft();
      int configRight = this.getConfigPanelRight();
      int configTop = this.getConfigPanelTop();
      int configBottom = this.getPanelBottom() - 18;
      int labelColor = 11184810;
      int accentColor = this.getAccentColor(0.88F);
      drawStringWithShadow(matrices, this.textRenderer, "§f§lКонфиги", configLeft + 14, configTop + 12, 16777215);
      drawStringWithShadow(matrices, this.textRenderer, "Активный:", configLeft + 14, configTop + 32, labelColor);
      drawStringWithShadow(matrices, this.textRenderer, AutoBuyMod.CONFIG.getActiveProfileName(), configLeft + 82, configTop + 32, accentColor);
      drawStringWithShadow(matrices, this.textRenderer, "Имя:", configLeft + 14, configTop + 56, labelColor);
      fill(matrices, configLeft + 54, configTop + 66, configLeft + 202, configTop + 67, this.getAccentColor(0.62F));

      int listTop = this.getConfigListTop();
      int listBottom = this.getConfigListBottom();
      int lineColor = this.getAccentColor(0.66F);
      fill(matrices, configLeft + 12, listTop - 2, configRight - 12, listTop - 1, lineColor);
      fill(matrices, configLeft + 12, listBottom, configRight - 12, listBottom + 1, lineColor);
      int maxVisible = Math.min(this.configScrollOffset + VISIBLE_CONFIGS, this.configProfiles.size());

      for(int index = this.configScrollOffset; index < maxVisible; ++index) {
         String profile = (String)this.configProfiles.get(index);
         int y = listTop + (index - this.configScrollOffset) * CONFIG_LIST_ROW_HEIGHT;
         boolean active = profile.equals(AutoBuyMod.CONFIG.getActiveProfileName());
         boolean hovered = mouseX >= configLeft + 14 && mouseX <= configRight - 14 && mouseY >= y && mouseY <= y + CONFIG_LIST_ROW_HEIGHT - 2;
         int rowColor = hovered ? 738263039 : 436207615;
         fill(matrices, configLeft + 14, y, configRight - 14, y + CONFIG_LIST_ROW_HEIGHT - 2, rowColor);
         if (active) {
            fill(matrices, configLeft + 14, y, configLeft + 18, y + CONFIG_LIST_ROW_HEIGHT - 2, accentColor);
         }

         drawStringWithShadow(matrices, this.textRenderer, profile, configLeft + 24, y + 8, accentColor);
      }

      if (System.currentTimeMillis() < this.statusMessageUntil) {
         drawStringWithShadow(matrices, this.textRenderer, "§7" + this.statusMessage, configLeft + 14, configBottom - 8, labelColor);
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
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

      return super.mouseScrolled(mouseX, mouseY, amount);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.profileNameField != null && this.profileNameField.mouseClicked(mouseX, mouseY, button)) {
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

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
      if (this.draggingPanel && button == 0) {
         this.panelOffsetX = this.dragStartOffsetX + (int)mouseX - this.dragStartMouseX;
         this.panelOffsetY = this.dragStartOffsetY + (int)mouseY - this.dragStartMouseY;
         this.updatePositions();
         return true;
      } else {
         return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.draggingPanel = false;
      }

      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (this.profileNameField != null && this.profileNameField.isFocused() && this.profileNameField.keyPressed(keyCode, scanCode, modifiers)) {
         return true;
      } else {
         Iterator var4 = this.fields.iterator();

         while(var4.hasNext()) {
            PriceField pf = (PriceField)var4.next();
            if (pf.field != null && pf.field.isFocused() && pf.field.keyPressed(keyCode, scanCode, modifiers)) {
               return true;
            }

            if (pf.mobField != null && pf.mobField.isFocused() && pf.mobField.keyPressed(keyCode, scanCode, modifiers)) {
               return true;
            }
         }

         return super.keyPressed(keyCode, scanCode, modifiers);
      }
   }

   public boolean charTyped(char chr, int keyCode) {
      if (this.profileNameField != null && this.profileNameField.isFocused() && this.profileNameField.charTyped(chr, keyCode)) {
         return true;
      } else {
         Iterator var3 = this.fields.iterator();

         while(var3.hasNext()) {
            PriceField pf = (PriceField)var3.next();
            if (pf.field != null && pf.field.isFocused() && pf.field.charTyped(chr, keyCode)) {
               return true;
            }

            if (pf.mobField != null && pf.mobField.isFocused() && pf.mobField.charTyped(chr, keyCode)) {
               return true;
            }
         }

         return super.charTyped(chr, keyCode);
      }
   }

   public boolean shouldPause() {
      return false;
   }

   private static class PriceField {
      TextFieldWidget field;
      TextFieldWidget mobField;
      final String itemId;
      final String displayName;
      final ItemStack icon;
      final int index;
      final boolean hasMobInput;
      float hoverAnim = 0.0F;
      float glowAnim = 0.0F;
      float currentY = 0.0F;

      PriceField(String itemId, String displayName, ItemStack icon, int index) {
         this(itemId, displayName, icon, index, false);
      }

      PriceField(String itemId, String displayName, ItemStack icon, int index, boolean hasMobInput) {
         this.itemId = itemId;
         this.displayName = displayName;
         this.icon = icon;
         this.index = index;
         this.hasMobInput = hasMobInput;
      }
   }
}
