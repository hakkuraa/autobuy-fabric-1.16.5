package com.autobuy.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DebugLogger {
   private static final Path DEBUG_DIR = Paths.get("mods", "debug");
   private static final Path DEBUG_FILE = DEBUG_DIR.resolve("debug.txt");
   private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
   private static final Map<String, Long> THROTTLE_MAP = new ConcurrentHashMap();

   public static void log(String message) {
      writeLine(message);
   }

   public static void logThrottled(String key, long cooldownMs, String message) {
      long now = System.currentTimeMillis();
      Long lastTime = (Long)THROTTLE_MAP.get(key);
      if (lastTime == null || now - lastTime >= cooldownMs) {
         THROTTLE_MAP.put(key, now);
         writeLine(message);
      }
   }

   public static void logException(String context, Throwable throwable) {
      writeLine(context + " | " + throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
   }

   private static synchronized void writeLine(String message) {
      try {
         Files.createDirectories(DEBUG_DIR);
         String line = "[" + TIME_FORMAT.format(new Date()) + "] " + message + System.lineSeparator();
         Files.write(DEBUG_FILE, line.getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND});
      } catch (IOException var2) {
      }
   }

   private DebugLogger() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
