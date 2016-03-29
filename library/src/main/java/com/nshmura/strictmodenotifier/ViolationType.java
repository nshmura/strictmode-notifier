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

public enum ViolationType {

  // ThreadPolicy
  CUSTOM_SLOW_CALL("Custom Slow Call", Build.VERSION_CODES.HONEYCOMB, new CustomSlowCallDetector()),
  NETWORK("Network", Build.VERSION_CODES.GINGERBREAD, new NetworkDetector()),
  RESOURCE_MISMATCHES("Resource Mismatches", Build.VERSION_CODES.M, new ResourceMismatchDetector()),

  // VmPolicy
  CLASS_INSTANCE_LIMIT("Class Instance Limit", Build.VERSION_CODES.HONEYCOMB, new ClassInstanceLimitDetector()),
  CLEARTEXT_NETWORK("Cleartext Network", Build.VERSION_CODES.M, new CleartextNetworkDetector()),
  FILE_URI_EXPOSURE("File Uri Exposure", Build.VERSION_CODES.JELLY_BEAN_MR2, new FileUriExposureDetector()),
  LEAKED_CLOSABLE_OBJECTS("Leaked Closable Objects", Build.VERSION_CODES.HONEYCOMB, new LeakedClosableObjectsDetector()),
  ACTIVITY_LEAKS("Activity Leaks", Build.VERSION_CODES.HONEYCOMB, null),
  LEAKED_REGISTRATION_OBJECTS("Leaked Registration_Objects", Build.VERSION_CODES.JELLY_BEAN, null),
  LEAKED_SQL_LITE_OBJECTS("Leaked Sql Lite Objects", Build.VERSION_CODES.GINGERBREAD, null);

  public final String name;
  public final int minSdkVersion;
  public final Detector detector;

  ViolationType(String name, int minSdkVersion, Detector detector) {
    this.name = name;
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

  public String violationName() {
    return name + " Violation";
  }
}
