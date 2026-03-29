package com.autobuy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseHistory {
   private static final List<PurchaseEntry> history = new ArrayList();
   private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
   private static PurchaseEntry lastPurchase = null;

   public static void addPurchase(String itemName, long price, int count) {
      PurchaseEntry newEntry = new PurchaseEntry(itemName, price, count);
      if (lastPurchase == null || !lastPurchase.equals(newEntry)) {
         lastPurchase = newEntry;
         history.add(0, newEntry);
         if (history.size() > 10) {
            history.remove(history.size() - 1);
         }

      }
   }

   public static boolean wasRecentlyPurchased(String itemName, long price, int count, long cooldownMs) {
      if (lastPurchase == null) {
         return false;
      } else if (System.currentTimeMillis() - lastPurchase.time > cooldownMs) {
         return false;
      } else {
         return lastPurchase.itemName.equals(itemName) && lastPurchase.price == price && lastPurchase.count == count;
      }
   }

   public static List<PurchaseEntry> getHistory() {
      return new ArrayList(history);
   }

   public static class PurchaseEntry {
      public final String itemName;
      public final long price;
      public final int count;
      public final long time;

      public PurchaseEntry(String itemName, long price, int count) {
         this.itemName = itemName;
         this.price = price;
         this.count = count;
         this.time = System.currentTimeMillis();
      }

      public String getFormattedTime() {
         return PurchaseHistory.timeFormat.format(new Date(this.time));
      }

      public String getDisplayString() {
         return this.count > 1 ? String.format("%s  %s x%d  %s", this.getFormattedTime(), this.itemName, this.count, this.formatPrice(this.price)) : String.format("%s  %s  %s", this.getFormattedTime(), this.itemName, this.formatPrice(this.price));
      }

      private String formatPrice(long price) {
         if (price >= 1000000L) {
            return price / 1000000L + "." + price % 1000000L / 100000L + "M";
         } else {
            return price >= 1000L ? price / 1000L + "K" : String.valueOf(price);
         }
      }

      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj != null && this.getClass() == obj.getClass()) {
            PurchaseEntry other = (PurchaseEntry)obj;
            return this.itemName.equals(other.itemName) && this.price == other.price && this.count == other.count;
         } else {
            return false;
         }
      }
   }
}
