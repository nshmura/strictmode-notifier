package io.github.juanimoli.strictmode.notifier

import android.os.Build.VERSION_CODES
import io.github.juanimoli.strictmode.notifier.commons.ViolationType
import io.github.juanimoli.strictmode.notifier.detector.*

enum class ViolationTypeInfo(
    val violationName: String,
    val violationType: ViolationType,
    val minSdkVersion: Int,
    detector: Detector?
) {
    // ThreadPolicy
    CUSTOM_SLOW_CALL(
        "Custom Slow Call",
        ViolationType.CUSTOM_SLOW_CALL,
        VERSION_CODES.HONEYCOMB,
        CustomSlowCallDetector()
    ),
    NETWORK(
        "Network",
        ViolationType.NETWORK,
        VERSION_CODES.GINGERBREAD,
        NetworkDetector()
    ),
    RESOURCE_MISMATCHES(
        "Resource Mismatches",
        ViolationType.RESOURCE_MISMATCHES,
        VERSION_CODES.M,
        ResourceMismatchDetector()
    ),  // VmPolicy
    CLASS_INSTANCE_LIMIT(
        "Class Instance Limit",
        ViolationType.CLASS_INSTANCE_LIMIT,
        VERSION_CODES.HONEYCOMB,
        ClassInstanceLimitDetector()
    ),
    CLEARTEXT_NETWORK(
        "Cleartext Network",
        ViolationType.CLEARTEXT_NETWORK,
        VERSION_CODES.M,
        CleartextNetworkDetector()
    ),
    FILE_URI_EXPOSURE(
        "File Uri Exposure",
        ViolationType.FILE_URI_EXPOSURE,
        VERSION_CODES.JELLY_BEAN_MR2,
        FileUriExposureDetector()
    ),
    LEAKED_CLOSABLE_OBJECTS(
        "Leaked Closable Objects",
        ViolationType.LEAKED_CLOSABLE_OBJECTS,
        VERSION_CODES.HONEYCOMB,
        LeakedClosableObjectsDetector()
    ),
    ACTIVITY_LEAKS(
        "Activity Leaks",
        ViolationType.ACTIVITY_LEAKS,
        VERSION_CODES.HONEYCOMB,
        null
    ),
    LEAKED_REGISTRATION_OBJECTS(
        "Leaked Registration_Objects",
        ViolationType.LEAKED_REGISTRATION_OBJECTS,
        VERSION_CODES.JELLY_BEAN,
        null
    ),
    LEAKED_SQL_LITE_OBJECTS(
        "Leaked Sql Lite Objects",
        ViolationType.LEAKED_SQL_LITE_OBJECTS,
        VERSION_CODES.GINGERBREAD,
        null
    ),
    UNKNOWN("UNKNOWN", ViolationType.UNKNOWN, 0, null);

    var detector: Detector? = null

    fun violationName(): String {
        return "$violationName Violation"
    }

    companion object {
        fun convert(type: ViolationType?): ViolationTypeInfo {
            for (info in values()) {
                if (info.violationType == type) {
                    return info
                }
            }

            return UNKNOWN
        }
    }

    init {
        if (detector != null) {
            this.detector = detector
        } else {
            this.detector = object : Detector {
                override fun detect(log: StrictModeLog): Boolean {
                    return false
                }
            }
        }
    }
}