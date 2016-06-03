package com.nshmura.strictmodenotifier;

import android.os.Build;
import com.nshmura.strictmodenotifier.detector.ClassInstanceLimitDetector;
import com.nshmura.strictmodenotifier.detector.CleartextNetworkDetector;
import com.nshmura.strictmodenotifier.detector.CustomSlowCallDetector;
import com.nshmura.strictmodenotifier.detector.Detector;
import com.nshmura.strictmodenotifier.detector.FileUriExposureDetector;
import com.nshmura.strictmodenotifier.detector.LeakedClosableObjectsDetector;
import com.nshmura.strictmodenotifier.detector.NetworkDetector;
import com.nshmura.strictmodenotifier.detector.ResourceMismatchDetector;

public enum ViolationTypeInfo {

  // ThreadPolicy
  CUSTOM_SLOW_CALL(
      "Custom Slow Call",
      ViolationType.CUSTOM_SLOW_CALL,
      Build.VERSION_CODES.HONEYCOMB,
      new CustomSlowCallDetector()),

  NETWORK(
      "Network",
      ViolationType.NETWORK,
      Build.VERSION_CODES.GINGERBREAD,
      new NetworkDetector()),

  RESOURCE_MISMATCHES(
      "Resource Mismatches",
      ViolationType.RESOURCE_MISMATCHES,
      Build.VERSION_CODES.M,
      new ResourceMismatchDetector()),

  // VmPolicy
  CLASS_INSTANCE_LIMIT(
      "Class Instance Limit",
      ViolationType.CLASS_INSTANCE_LIMIT,
      Build.VERSION_CODES.HONEYCOMB,
      new ClassInstanceLimitDetector()),

  CLEARTEXT_NETWORK(
      "Cleartext Network",
      ViolationType.CLEARTEXT_NETWORK,
      Build.VERSION_CODES.M,
      new CleartextNetworkDetector()),

  FILE_URI_EXPOSURE(
      "File Uri Exposure",
      ViolationType.FILE_URI_EXPOSURE,
      Build.VERSION_CODES.JELLY_BEAN_MR2,
      new FileUriExposureDetector()),

  LEAKED_CLOSABLE_OBJECTS(
      "Leaked Closable Objects",
      ViolationType.LEAKED_CLOSABLE_OBJECTS,
      Build.VERSION_CODES.HONEYCOMB,
      new LeakedClosableObjectsDetector()),

  ACTIVITY_LEAKS(
      "Activity Leaks",
      ViolationType.ACTIVITY_LEAKS,
      Build.VERSION_CODES.HONEYCOMB,
      null),

  LEAKED_REGISTRATION_OBJECTS(
      "Leaked Registration_Objects",
      ViolationType.LEAKED_REGISTRATION_OBJECTS,
      Build.VERSION_CODES.JELLY_BEAN,
      null),

  LEAKED_SQL_LITE_OBJECTS(
      "Leaked Sql Lite Objects",
      ViolationType.LEAKED_SQL_LITE_OBJECTS,
      Build.VERSION_CODES.GINGERBREAD,
      null),

  UNKNOWN("UNKNOWN", ViolationType.UNKNOWN, 0, null);

  private String name;
  public final ViolationType violationType;
  public final int minSdkVersion;
  public final Detector detector;

  ViolationTypeInfo(String name, ViolationType violationType, int minSdkVersion,
      Detector detector) {
    this.name = name;
    this.violationType = violationType;
    this.minSdkVersion = minSdkVersion;

    if (detector != null) {
      this.detector = detector;
    } else {
      this.detector = new Detector() {
        @Override public boolean detect(StrictModeLog log) {
          return false;
        }
      };
    }
  }

  public static ViolationTypeInfo convert(ViolationType type) {
    for (ViolationTypeInfo info : values()) {
      if (info.violationType == type) {
        return info;
      }
    }
    return ViolationTypeInfo.UNKNOWN;
  }

  public String violationName() {
    return name + " Violation";
  }
}
