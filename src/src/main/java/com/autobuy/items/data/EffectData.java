package com.autobuy.items.data;

import net.minecraft.entity.effect.StatusEffect;

public class EffectData {
   private final StatusEffect effectType;
   private final int duration;
   private final int amplifier;

   public EffectData(StatusEffect effectType, int duration, int amplifier) {
      this.effectType = effectType;
      this.duration = duration;
      this.amplifier = amplifier;
   }

   public static EffectData of(StatusEffect effectType, int seconds, int amplifier) {
      return new EffectData(effectType, seconds * 20, amplifier);
   }

   public static EffectData instant(StatusEffect effectType, int amplifier) {
      return new EffectData(effectType, 1, amplifier);
   }

   public StatusEffect getEffectType() {
      return this.effectType;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }
}
