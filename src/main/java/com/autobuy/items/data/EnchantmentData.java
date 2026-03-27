package com.autobuy.items.data;

import net.minecraft.class_1887;

public class EnchantmentData {
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

   public String toString() {
      return "EnchantmentData{enchantment='" + this.enchantment + "', level='" + this.level + "'}";
   }
}
