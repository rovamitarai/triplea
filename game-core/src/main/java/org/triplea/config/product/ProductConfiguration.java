package org.triplea.config.product;

import org.triplea.config.PropertyReader;
import org.triplea.config.ResourcePropertyReader;
import org.triplea.util.Version;

import com.google.common.annotations.VisibleForTesting;

/**
 * Provides access to the product configuration. The product configuration applies to all components of the TripleA
 * application suite (e.g. game client, lobby server, etc.).
 */
public final class ProductConfiguration {
  private final PropertyReader propertyReader;

  public ProductConfiguration() {
    this(new ResourcePropertyReader("META-INF/triplea/product.properties"));
  }

  @VisibleForTesting
  ProductConfiguration(final PropertyReader propertyReader) {
    this.propertyReader = propertyReader;
  }

  public Version getVersion() {
    return new Version(propertyReader.readProperty(PropertyKeys.VERSION));
  }

  @VisibleForTesting
  interface PropertyKeys {
    String VERSION = "version";
  }
}
