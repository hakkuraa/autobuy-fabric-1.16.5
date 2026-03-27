package com.autobuy.items.data;

import net.minecraft.class_1320;
import net.minecraft.class_1322;

public class AttributeData {
   private final class_1320 attribute;
   private final double value;
   private final class_1322.class_1323 operation;

   public AttributeData(class_1320 attribute, double value, class_1322.class_1323 operation) {
      this.attribute = attribute;
      this.value = value;
      this.operation = operation;
   }

   public static AttributeData of(class_1320 attribute, double value, class_1322.class_1323 operation) {
      return new AttributeData(attribute, value, operation);
   }

   public class_1320 getAttribute() {
      return this.attribute;
   }

   public double getValue() {
      return this.value;
   }

   public class_1322.class_1323 getOperation() {
      return this.operation;
   }
}
