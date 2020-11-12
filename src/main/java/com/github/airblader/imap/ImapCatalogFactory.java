package com.github.airblader.imap;

import static com.github.airblader.ConnectorConfigOptions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.var;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.api.ValidationException;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.factories.CatalogFactory;

public class ImapCatalogFactory implements CatalogFactory {
  @Override
  public Map<String, String> requiredContext() {
    var context = new HashMap<String, String>();
    context.put("type", "imap");

    return context;
  }

  @Override
  public List<String> supportedProperties() {
    return CATALOG_OPTIONS.stream().map(ConfigOption::key).collect(Collectors.toList());
  }

  @Override
  public Catalog createCatalog(String name, Map<String, String> properties) {
    Configuration configuration = new Configuration();
    properties.forEach(configuration::setString);

    ScanMode scanMode;
    try {
      scanMode = ScanMode.from(configuration.get(MODE));
    } catch (IllegalArgumentException e) {
      throw new ValidationException("Invalid value for " + MODE.key(), e);
    }

    return new ImapCatalog(
        name,
        ImapCatalogOptions.builder()
            .host(configuration.get(HOST))
            .envHost(configuration.get(ENV_HOST))
            .port(configuration.get(PORT))
            .envPort(configuration.get(ENV_PORT))
            .user(configuration.get(USER))
            .envUser(configuration.get(ENV_USER))
            .password(configuration.get(PASSWORD))
            .envPassword(configuration.get(ENV_PASSWORD))
            .ssl(configuration.get(SSL))
            .mode(scanMode)
            .connectionTimeout(configuration.get(CONNECTION_TIMEOUT))
            .idle(configuration.get(IDLE))
            .heartbeat(configuration.get(HEARTBEAT))
            .heartbeatInterval(configuration.get(HEARTBEAT_INTERVAL))
            .interval(configuration.get(INTERVAL))
            .deletions(configuration.get(DELETIONS))
            .addressFormat(configuration.get(ADDRESS_FORMAT))
            .build());
  }
}
