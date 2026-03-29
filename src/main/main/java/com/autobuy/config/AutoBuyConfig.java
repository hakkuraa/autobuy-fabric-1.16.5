package com.autobuy.config;

import com.autobuy.util.DebugLogger;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AutoBuyConfig {
   private static final Path BASE_DIR = Paths.get("mods", "AutoBuy");
   private static final Path CONFIG_DIR = BASE_DIR.resolve("config");
   private static final Map<String, String> ITEM_DISPLAY_NAMES = createItemDisplayNames();
   private static final Map<String, String> DISPLAY_NAME_TO_ID = createDisplayNameToId();
   private final Map<String, Long> itemPrices = new HashMap();
   private final Map<String, String> itemMobFilters = new HashMap();
   private String activeProfileName = "default";

   public AutoBuyConfig() {
      this.initializeStorage();
      this.loadProfile(this.findLastUsedProfileName());
   }

   public synchronized void setPrice(String item, long price) {
      if (price > 0L) {
         this.itemPrices.put(item, price);
      } else {
         this.itemPrices.remove(item);
      }
   }

   public synchronized void removePrice(String item) {
      this.itemPrices.remove(item);
   }

   public synchronized long getPrice(String item) {
      return (Long)this.itemPrices.getOrDefault(item, -1L);
   }

   public synchronized boolean hasPrice(String item) {
      return this.itemPrices.containsKey(item) && (Long)this.itemPrices.get(item) > 0L;
   }

   public synchronized void setMobFilter(String item, String mob) {
      if (mob != null && !mob.trim().isEmpty()) {
         this.itemMobFilters.put(item, mob.trim());
      } else {
         this.itemMobFilters.remove(item);
      }
   }

   public synchronized String getMobFilter(String item) {
      return (String)this.itemMobFilters.getOrDefault(item, "");
   }

   public synchronized String getActiveProfileName() {
      return this.activeProfileName;
   }

   public Path getConfigDirectory() {
      this.initializeStorage();
      return CONFIG_DIR.toAbsolutePath();
   }

   public synchronized void saveActiveProfile() {
      this.saveProfile(this.activeProfileName);
   }

   public synchronized void saveProfile(String profileName) {
      String normalized = this.normalizeProfileName(profileName);
      List<String> lines = new ArrayList();
      ITEM_DISPLAY_NAMES.forEach((itemId, displayName) -> {
         Long price = (Long)this.itemPrices.get(itemId);
         if (price != null && price > 0L) {
            lines.add(displayName + " price " + price);
         }

         String mob = (String)this.itemMobFilters.get(itemId);
         if (mob != null && !mob.trim().isEmpty()) {
            lines.add(displayName + " mob " + mob.trim());
         }

      });

      try {
         Files.createDirectories(CONFIG_DIR);
         Files.write(this.getProfilePath(normalized), lines, StandardCharsets.UTF_8);
         this.activeProfileName = normalized;
      } catch (IOException var5) {
         DebugLogger.logException("Failed to save config profile " + normalized, var5);
      }
   }

   public synchronized void loadProfile(String profileName) {
      String normalized = this.normalizeProfileName(profileName);
      Path profilePath = this.getProfilePath(normalized);
      this.itemPrices.clear();
      this.itemMobFilters.clear();
      if (Files.exists(profilePath)) {
         this.loadCfgProfile(profilePath);
         this.touchProfile(profilePath);
      } else if (this.tryLoadLegacyProperties(normalized)) {
         this.saveProfile(normalized);
      } else {
         this.saveProfile(normalized);
      }

      this.activeProfileName = normalized;
   }

   public synchronized boolean deleteProfile(String profileName) {
      String normalized = this.normalizeProfileName(profileName);
      if ("default".equalsIgnoreCase(normalized)) {
         return false;
      } else {
         boolean deleted = false;

         try {
            deleted = Files.deleteIfExists(this.getProfilePath(normalized));
            deleted = Files.deleteIfExists(this.getLegacyProfilePath(normalized)) || deleted;
         } catch (IOException var5) {
            DebugLogger.logException("Failed to delete config profile " + normalized, var5);
            return false;
         }

         if (normalized.equals(this.activeProfileName)) {
            this.loadProfile("default");
         }

         return deleted;
      }
   }

   public synchronized List<String> listProfiles() {
      this.initializeStorage();
      List<String> profiles = new ArrayList();

      try {
         Stream<Path> stream = Files.list(CONFIG_DIR);

         try {
            profiles.addAll((List)stream.filter((path) -> {
               String name = path.getFileName().toString().toLowerCase();
               return name.endsWith(".cfg") || name.endsWith(".properties");
            }).map((path) -> {
               String fileName = path.getFileName().toString();
               if (fileName.endsWith(".cfg")) {
                  return fileName.substring(0, fileName.length() - 4);
               } else {
                  return fileName.endsWith(".properties") ? fileName.substring(0, fileName.length() - ".properties".length()) : fileName;
               }
            }).distinct().collect(Collectors.toList()));
         } catch (Throwable var7) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (stream != null) {
            stream.close();
         }
      } catch (IOException var8) {
         DebugLogger.logException("Failed to list config profiles", var8);
      }

      if (!profiles.contains("default")) {
         profiles.add("default");
      }

      Collections.sort(profiles, String.CASE_INSENSITIVE_ORDER);
      if (profiles.remove("default")) {
         profiles.add(0, "default");
      }

      return profiles;
   }

   private void loadCfgProfile(Path profilePath) {
      try {
         List<String> lines = Files.readAllLines(profilePath, StandardCharsets.UTF_8);
         Iterator var3 = lines.iterator();

         while(var3.hasNext()) {
            String rawLine = (String)var3.next();
            String line = rawLine.trim();
            if (!line.isEmpty()) {
               this.parseCfgLine(line);
            }
         }
      } catch (IOException var6) {
         DebugLogger.logException("Failed to load cfg profile " + profilePath.getFileName(), var6);
      }

   }

   private void parseCfgLine(String line) {
      int priceIndex = line.lastIndexOf(" price ");
      if (priceIndex > 0) {
         String displayName = line.substring(0, priceIndex).trim();
         String value = line.substring(priceIndex + " price ".length()).trim();
         String itemId = this.resolveItemId(displayName);
         if (itemId != null) {
            try {
               long price = Long.parseLong(value);
               if (price > 0L) {
                  this.itemPrices.put(itemId, price);
               }
            } catch (NumberFormatException var7) {
            }
         }

         return;
      }

      int mobIndex = line.lastIndexOf(" mob ");
      if (mobIndex > 0) {
         String displayName = line.substring(0, mobIndex).trim();
         String value = line.substring(mobIndex + " mob ".length()).trim();
         String itemId = this.resolveItemId(displayName);
         if (itemId != null && !value.isEmpty()) {
            this.itemMobFilters.put(itemId, value);
         }
      }

   }

   private boolean tryLoadLegacyProperties(String profileName) {
      Path legacyPath = this.getLegacyProfilePath(profileName);
      if (!Files.exists(legacyPath)) {
         return false;
      } else {
         try {
            List<String> lines = Files.readAllLines(legacyPath, StandardCharsets.UTF_8);
            Iterator var4 = lines.iterator();

            while(var4.hasNext()) {
               String line = (String)var4.next();
               String trimmed = line.trim();
               if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith("!")) {
                  int separator = trimmed.indexOf(61);
                  if (separator > 0) {
                     String key = trimmed.substring(0, separator).trim();
                     String value = trimmed.substring(separator + 1).trim();
                     if (key.startsWith("price.")) {
                        try {
                           this.itemPrices.put(key.substring("price.".length()), Long.parseLong(value));
                        } catch (NumberFormatException var10) {
                        }
                     } else if (key.startsWith("mob.")) {
                        this.itemMobFilters.put(key.substring("mob.".length()), value);
                     }
                  }
               }
            }

            return true;
         } catch (IOException var11) {
            DebugLogger.logException("Failed to load legacy config profile " + profileName, var11);
            return false;
         }
      }
   }

   private String resolveItemId(String displayName) {
      return (String)DISPLAY_NAME_TO_ID.get(this.normalizeDisplayName(displayName));
   }

   private void initializeStorage() {
      try {
         Files.createDirectories(CONFIG_DIR);
         Files.deleteIfExists(BASE_DIR.resolve("active-profile.txt"));
         Files.deleteIfExists(CONFIG_DIR.resolve("active-profile.txt"));
      } catch (IOException var2) {
         DebugLogger.logException("Failed to create config profile directory", var2);
      }

   }

   private String findLastUsedProfileName() {
      this.initializeStorage();
      Path newestPath = null;
      FileTime newestTime = null;

      try {
         Stream<Path> stream = Files.list(CONFIG_DIR);

         try {
            Iterator var4 = stream.filter((path) -> {
               return path.getFileName().toString().toLowerCase().endsWith(".cfg");
            }).iterator();

            while(var4.hasNext()) {
               Path path = (Path)var4.next();
               FileTime lastModified = Files.getLastModifiedTime(path);
               if (newestTime == null || lastModified.compareTo(newestTime) > 0) {
                  newestTime = lastModified;
                  newestPath = path;
               }
            }
         } catch (Throwable var8) {
            if (stream != null) {
               try {
                  stream.close();
               } catch (Throwable var7) {
                  var8.addSuppressed(var7);
               }
            }

            throw var8;
         }

         if (stream != null) {
            stream.close();
         }
      } catch (IOException var9) {
         DebugLogger.logException("Failed to determine last used config profile", var9);
      }

      if (newestPath == null) {
         return "default";
      } else {
         String fileName = newestPath.getFileName().toString();
         return fileName.endsWith(".cfg") ? fileName.substring(0, fileName.length() - 4) : "default";
      }
   }

   private void touchProfile(Path profilePath) {
      try {
         if (Files.exists(profilePath)) {
            Files.setLastModifiedTime(profilePath, FileTime.fromMillis(System.currentTimeMillis()));
         }
      } catch (IOException var3) {
         DebugLogger.logException("Failed to update config profile usage time for " + profilePath.getFileName(), var3);
      }

   }

   private Path getProfilePath(String profileName) {
      return CONFIG_DIR.resolve(profileName + ".cfg");
   }

   private Path getLegacyProfilePath(String profileName) {
      return CONFIG_DIR.resolve(profileName + ".properties");
   }

   private String normalizeProfileName(String profileName) {
      String normalized = profileName == null ? "" : profileName.trim();
      if (normalized.isEmpty()) {
         normalized = "default";
      }

      return normalized.replaceAll("[\\\\/:*?\"<>|]", "_");
   }

   private String normalizeDisplayName(String displayName) {
      return displayName == null ? "" : displayName.trim().toLowerCase();
   }

   private static Map<String, String> createItemDisplayNames() {
      Map<String, String> names = new LinkedHashMap();
      names.put("enchanted_golden_apple", "Зачарованное золотое яблоко");
      names.put("golden_apple", "Золотое яблоко");
      names.put("diamond", "Алмаз");
      names.put("netherite_ingot", "Незеритовый слиток");
      names.put("ender_pearl", "Жемчуг Края");
      names.put("experience_bottle", "Пузырёк опыта");
      names.put("totem_of_undying", "Тотем бессмертия");
      names.put("beacon", "Маяк");
      names.put("shulker_box", "Шалкер");
      names.put("elytra", "Элитры");
      names.put("golden_carrot", "Золотая морковка");
      names.put("dragon_head", "Голова дракона");
      names.put("iron_ingot", "Железный слиток");
      names.put("obsidian", "Обсидиан");
      names.put("helmet_crusher", "Шлем Крушителя");
      names.put("chestplate_crusher", "Нагрудник Крушителя");
      names.put("leggings_crusher", "Поножи Крушителя");
      names.put("boots_crusher", "Ботинки Крушителя");
      names.put("sword_crusher", "Меч Крушителя");
      names.put("trident_crusher", "Трезубец Крушителя");
      names.put("pickaxe_crusher", "Кирка Крушителя");
      names.put("crossbow_crusher", "Арбалет Крушителя");
      names.put("sphere_chaos", "Сфера хаоса");
      names.put("sphere_ares", "Сфера ареса");
      names.put("sphere_aftin", "Сфера афины");
      names.put("talisman_crusher", "Талисман крушителя");
      names.put("talisman_rage", "Талисман ярости");
      names.put("talisman_punisher", "Талисман карателя");
      names.put("spawner", "Спавнер");
      return names;
   }

   private static Map<String, String> createDisplayNameToId() {
      Map<String, String> ids = new HashMap();
      ITEM_DISPLAY_NAMES.forEach((itemId, displayName) -> {
         ids.put(displayName.toLowerCase(), itemId);
      });
      return ids;
   }
}
