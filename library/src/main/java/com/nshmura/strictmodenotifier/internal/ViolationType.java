package com.nshmura.strictmodenotifier.internal;

import android.os.Build;
import com.nshmura.strictmodenotifier.internal.detector.ClassInstanceLimitDetector;
import com.nshmura.strictmodenotifier.internal.detector.CleartextNetworkDetector;
import com.nshmura.strictmodenotifier.internal.detector.CustomSlowCallDetector;
import com.nshmura.strictmodenotifier.internal.detector.Detector;
import com.nshmura.strictmodenotifier.internal.detector.FileUriExposureDetector;
import com.nshmura.strictmodenotifier.internal.detector.LeakedClosableObjectsDetector;
import com.nshmura.strictmodenotifier.internal.detector.NetworkDetector;
import com.nshmura.strictmodenotifier.internal.detector.ResourceMismatchDetector;

public enum ViolationType {

  // ThreadPolicy
  Custom_Slow_Call(Build.VERSION_CODES.HONEYCOMB, new CustomSlowCallDetector()),
  Network(Build.VERSION_CODES.GINGERBREAD, new NetworkDetector()),
  Resource_Mismatches(Build.VERSION_CODES.M, new ResourceMismatchDetector()),

  // VmPolicy
  Class_Instance_Limit(Build.VERSION_CODES.HONEYCOMB, new ClassInstanceLimitDetector()),
  Cleartext_Network(Build.VERSION_CODES.M, new CleartextNetworkDetector()),
  File_Uri_Exposure(Build.VERSION_CODES.JELLY_BEAN_MR2, new FileUriExposureDetector()),
  Leaked_Closable_Objects(Build.VERSION_CODES.HONEYCOMB, new LeakedClosableObjectsDetector()),
  Activity_Leaks(Build.VERSION_CODES.HONEYCOMB, null),
  Leaked_Registration_Objects(Build.VERSION_CODES.JELLY_BEAN, null),
  Leaked_Sql_Lite_Objects(Build.VERSION_CODES.GINGERBREAD, null);

  public final int minSdkVersion;
  public final Detector detector;

  ViolationType(int minSdkVersion, Detector detector) {
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
    return name().replace("_", "") + " Violation";
  }
}
