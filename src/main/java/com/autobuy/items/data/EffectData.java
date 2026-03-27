package com.autobuy.items.data;

import net.minecraft.class_1291;

public class EffectData {
   private final class_1291 effectType;
   private final int duration;
   private final int amplifier;

   public EffectData(class_1291 effectType, int duration, int amplifier) {
      this.effectType = effectType;
      this.duration = duration;
      this.amplifier = amplifier;
   }

   public static EffectData of(class_1291 effectType, int seconds, int amplifier) {
      return new EffectData(effectType, seconds * 20, amplifier);
   }

   public static EffectData instant(class_1291 effectType, int amplifier) {
      return new EffectData(effectType, 1, amplifier);
   }

   public class_1291 getEffectType() {
      return this.effectType;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }
}
