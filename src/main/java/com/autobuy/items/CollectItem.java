package com.autobuy.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1792;
import net.minecraft.class_1799;

public class CollectItem {
   protected final String id;
   protected final String name;
   protected final class_1792 item;
   protected List<String> tooltips = new ArrayList();

   public CollectItem(String id, String name, class_1792 item) {
      this.id = id;
      this.name = name;
      this.item = item;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public class_1792 getItem() {
      return this.item;
   }

   public CollectItem addTooltips(String... tooltips) {
      this.tooltips.addAll(Arrays.asList(tooltips));
      return this;
   }

   public boolean matches(class_1799 stack) {
      if (stack.method_7909() != this.item) {
         return false;
      } else {
         if (!this.tooltips.isEmpty()) {
            String lore = stack.method_7969() != null ? stack.method_7969().method_10714() : "";
            Iterator var3 = this.tooltips.iterator();

            while(var3.hasNext()) {
               String tooltip = (String)var3.next();
               if (!lore.contains(tooltip)) {
                  return false;
               }
            }
         }

         return true;
      }
   }
}
