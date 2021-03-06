package de.tu.darmstadt.moodsense.store;

/**
 * Utility class: Store twitter credentials
 * @author dinesh
 *
 */
public interface CredentialStore {

  String[] read();
  void write(String[]response);
  void clearCredentials();
}
