/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */


package org.opensearch.sql.common.setting;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Setting.
 */
public abstract class Settings {
  @RequiredArgsConstructor
  public enum Key {

    /**
     * SQL Settings.
     */
    SQL_ENABLED("plugins.sql.enabled"),
    SQL_SLOWLOG("plugins.sql.slowlog"),
    SQL_CURSOR_KEEP_ALIVE("plugins.sql.cursor.keep_alive"),
    SQL_DELETE_ENABLED("plugins.sql.delete.enabled"),

    /**
     * PPL Settings.
     */
    PPL_ENABLED("plugins.ppl.enabled"),

    /**
     * Common Settings for SQL and PPL.
     */
    QUERY_MEMORY_LIMIT("plugins.query.memory_limit"),
    QUERY_SIZE_LIMIT("plugins.query.size_limit"),
    ENCYRPTION_MASTER_KEY("plugins.query.datasources.encryption.masterkey"),
    DATASOURCES_URI_ALLOWHOSTS("plugins.query.datasources.uri.allowhosts"),

    METRICS_ROLLING_WINDOW("plugins.query.metrics.rolling_window"),
    METRICS_ROLLING_INTERVAL("plugins.query.metrics.rolling_interval"),

    CLUSTER_NAME("cluster.name");

    @Getter
    private final String keyValue;

    private static final Map<String, Key> ALL_KEYS;

    static {
      ImmutableMap.Builder<String, Key> builder = new ImmutableMap.Builder<>();
      for (Key key : Key.values()) {
        builder.put(key.getKeyValue(), key);
      }
      ALL_KEYS = builder.build();
    }

    public static Optional<Key> of(String keyValue) {
      String key = Strings.isNullOrEmpty(keyValue) ? "" : keyValue.toLowerCase();
      return Optional.ofNullable(ALL_KEYS.getOrDefault(key, null));
    }
  }

  /**
   * Get Setting Value.
   */
  public abstract <T> T getSettingValue(Key key);

  public abstract List<?> getSettings();
}
