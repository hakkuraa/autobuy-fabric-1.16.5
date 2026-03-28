package com.autobuy.items.data;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public class AttributeData {
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
