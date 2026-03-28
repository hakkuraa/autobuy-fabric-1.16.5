package com.autobuy.items.data;

import net.minecraft.enchantment.Enchantment;

public class EnchantmentData {
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

   public String toString() {
      return "EnchantmentData{enchantment='" + this.enchantment + "', level='" + this.level + "'}";
   }
}
