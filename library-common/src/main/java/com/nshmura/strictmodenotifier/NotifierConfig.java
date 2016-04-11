package com.nshmura.strictmodenotifier;

import java.util.ArrayList;
import java.util.List;

public class NotifierConfig {

  private static NotifierConfig instance;

  private List<CustomAction> customActions = new ArrayList<>();
  private IgnoreAction ignoreAction;
  private boolean debugMode;
  private boolean headupEnabled = true;

  private NotifierConfig() {

  }

  public static NotifierConfig getInstance() {
    if (instance == null) {
      instance = new NotifierConfig();
    }
    return instance;
  }

  public NotifierConfig addCustomAction(CustomAction customAction) {
    customActions.add(customAction);
    return this;
  }

  public List<CustomAction> getCustomActions() {
    return customActions;
  }

  public NotifierConfig setIgnoreAction(IgnoreAction ignoreAction) {
    this.ignoreAction = ignoreAction;
    return this;
  }

  public IgnoreAction getIgnoreAction() {
    return ignoreAction;
  }

  public boolean isDebugMode() {
    return debugMode;
  }

  public NotifierConfig setDebugMode(boolean debugMode) {
    this.debugMode = debugMode;
    return this;
  }

  public boolean isHeadupEnabled() {
    return headupEnabled;
  }

  public NotifierConfig setHeadupEnabled(boolean headupEnabled) {
    this.headupEnabled = headupEnabled;
    return this;
  }
}
