package com.autobuy.items;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1802;

public class ItemStorage {
   public static final List<CollectItem> ALL = new ArrayList();

   public static void reload() {
      ALL.clear();
      ALL.add(new CollectItem("enchanted_golden_apple", "Зачарованное золотое яблоко", class_1802.field_8367));
      ALL.add(new CollectItem("golden_apple", "Золотое яблоко", class_1802.field_8463));
      ALL.add(new CollectItem("diamond", "Алмаз", class_1802.field_8477));
      ALL.add(new CollectItem("gold_ingot", "Золотой слиток", class_1802.field_8695));
      ALL.add(new CollectItem("netherite_ingot", "Незеритовый слиток", class_1802.field_22020));
      ALL.add(new CollectItem("ender_pearl", "Жемчуг Края", class_1802.field_8634));
      ALL.add(new CollectItem("experience_bottle", "Пузырёк опыта", class_1802.field_8287));
      ALL.add(new CollectItem("totem_of_undying", "Тотем бессмертия", class_1802.field_8288));
      ALL.add(new CollectItem("beacon", "Маяк", class_1802.field_8668));
      ALL.add(new CollectItem("shulker_box", "Шалкер", class_1802.field_8545));
      ALL.add(new CollectItem("elytra", "Элитры", class_1802.field_8833));
      ALL.add(new CollectItem("golden_carrot", "Золотая морковка", class_1802.field_8071));
      ALL.add(new CollectItem("dragon_head", "Голова дракона", class_1802.field_8712));
      ALL.add(new CollectItem("gunpowder", "Порох", class_1802.field_8054));
      ALL.add(new CollectItem("emerald", "Изумруд", class_1802.field_8687));
      ALL.add(new CollectItem("iron_ingot", "Железный слиток", class_1802.field_8620));
      ALL.add(new CollectItem("redstone", "Редстоун", class_1802.field_8725));
      ALL.add(new CollectItem("lapis_lazuli", "Лазурит", class_1802.field_8759));
      ALL.add(new CollectItem("coal", "Уголь", class_1802.field_8713));
      ALL.add(new CollectItem("obsidian", "Обсидиан", class_1802.field_8281));
      ALL.add(new CollectItem("helmet_crusher", "Шлем крушителя", class_1802.field_22027));
      ALL.add(new CollectItem("chestplate_crusher", "Нагрудник крушителя", class_1802.field_22028));
      ALL.add(new CollectItem("leggings_crusher", "Поножи крушителя", class_1802.field_22029));
      ALL.add(new CollectItem("boots_crusher", "Ботинки крушителя", class_1802.field_22030));
      ALL.add(new CollectItem("sword_crusher", "Меч крушителя", class_1802.field_22022));
      ALL.add(new CollectItem("trident_crusher", "Трезубец крушителя", class_1802.field_8547));
      ALL.add(new CollectItem("pickaxe_crusher", "Кирка крушителя", class_1802.field_22024));
      ALL.add(new CollectItem("crossbow_crusher", "Арбалет крушителя", class_1802.field_8399));
      ALL.add(new CollectItem("sharpened_sword", "Заострённый меч", class_1802.field_22022));
   }

   static {
      reload();
   }
}
