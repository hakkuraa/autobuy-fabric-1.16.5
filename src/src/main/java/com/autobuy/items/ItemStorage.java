package com.autobuy.items;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Items;

public class ItemStorage {
   public static final List<CollectItem> ALL = new ArrayList();

   public static void reload() {
      ALL.clear();
      ALL.add(new CollectItem("enchanted_golden_apple", "Зачарованное золотое яблоко", Items.ENCHANTED_GOLDEN_APPLE));
      ALL.add(new CollectItem("golden_apple", "Золотое яблоко", Items.GOLDEN_APPLE));
      ALL.add(new CollectItem("diamond", "Алмаз", Items.DIAMOND));
      ALL.add(new CollectItem("gold_ingot", "Золотой слиток", Items.GOLD_INGOT));
      ALL.add(new CollectItem("netherite_ingot", "Незеритовый слиток", Items.NETHERITE_INGOT));
      ALL.add(new CollectItem("ender_pearl", "Жемчуг Края", Items.ENDER_PEARL));
      ALL.add(new CollectItem("experience_bottle", "Пузырёк опыта", Items.EXPERIENCE_BOTTLE));
      ALL.add(new CollectItem("totem_of_undying", "Тотем бессмертия", Items.TOTEM_OF_UNDYING));
      ALL.add(new CollectItem("beacon", "Маяк", Items.BEACON));
      ALL.add(new CollectItem("shulker_box", "Шалкер", Items.SHULKER_BOX));
      ALL.add(new CollectItem("elytra", "Элитры", Items.ELYTRA));
      ALL.add(new CollectItem("golden_carrot", "Золотая морковка", Items.GOLDEN_CARROT));
      ALL.add(new CollectItem("dragon_head", "Голова дракона", Items.DRAGON_HEAD));
      ALL.add(new CollectItem("gunpowder", "Порох", Items.GUNPOWDER));
      ALL.add(new CollectItem("emerald", "Изумруд", Items.EMERALD));
      ALL.add(new CollectItem("iron_ingot", "Железный слиток", Items.IRON_INGOT));
      ALL.add(new CollectItem("redstone", "Редстоун", Items.REDSTONE));
      ALL.add(new CollectItem("lapis_lazuli", "Лазурит", Items.LAPIS_LAZULI));
      ALL.add(new CollectItem("coal", "Уголь", Items.COAL));
      ALL.add(new CollectItem("obsidian", "Обсидиан", Items.OBSIDIAN));
      ALL.add(new CollectItem("helmet_crusher", "Шлем крушителя", Items.NETHERITE_HELMET));
      ALL.add(new CollectItem("chestplate_crusher", "Нагрудник крушителя", Items.NETHERITE_CHESTPLATE));
      ALL.add(new CollectItem("leggings_crusher", "Поножи крушителя", Items.NETHERITE_LEGGINGS));
      ALL.add(new CollectItem("boots_crusher", "Ботинки крушителя", Items.NETHERITE_BOOTS));
      ALL.add(new CollectItem("sword_crusher", "Меч крушителя", Items.NETHERITE_SWORD));
      ALL.add(new CollectItem("trident_crusher", "Трезубец крушителя", Items.TRIDENT));
      ALL.add(new CollectItem("pickaxe_crusher", "Кирка крушителя", Items.NETHERITE_PICKAXE));
      ALL.add(new CollectItem("crossbow_crusher", "Арбалет крушителя", Items.CROSSBOW));
      ALL.add(new CollectItem("sharpened_sword", "Заострённый меч", Items.NETHERITE_SWORD));
   }

   static {
      reload();
   }
}
