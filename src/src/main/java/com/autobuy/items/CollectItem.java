package com.autobuy.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CollectItem {
   protected final String id;
   protected final String name;
   protected final Item item;
   protected List<String> tooltips = new ArrayList();

   public CollectItem(String id, String name, Item item) {
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

   public Item getItem() {
      return this.item;
   }

   public CollectItem addTooltips(String... tooltips) {
      this.tooltips.addAll(Arrays.asList(tooltips));
      return this;
   }

   public boolean matches(ItemStack stack) {
      if (stack.getItem() != this.item) {
         return false;
      } else {
         if (!this.tooltips.isEmpty()) {
            String lore = stack.getTag() != null ? stack.getTag().asString() : "";
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
