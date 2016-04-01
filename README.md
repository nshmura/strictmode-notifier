# strictmode-notifier
An Android library that enhances the StrictMode reporting.

- *Head-up Notification* of StrictMode violations.
- *Custom Actions* that called when StrictMode violations is happend.
- *Violation History Viewer* that automatically installed. <br> <img src="/library/src/main/res/drawable-xxxhdpi/strictmode_notifier_ic_launcher.png" width="30"/>

<img src="assets/notification.png" width="25%" hspace="10" vspace="10"/>
<img src="assets/detail.png" width="25%" hspace="10" vspace="10"/>
<img src="assets/list.png" width="25%" hspace="10" vspace="10"/>

## About StrictMode
- [StrictMode for Runtime Analysis on Android](https://medium.com/google-developers/strictmode-for-runtime-analysis-on-android-f8d0a2c5667e#.elffd4gi1)
- [StrictMode for enforcing best practices at runtime](https://www.youtube.com/watch?v=BxTfwT7mkB4)

## Getting started

In your `build.gradle`:

```gradle
 repositories {
    jcenter()
 }

 dependencies {
    debugCompile 'com.nshmura:strictmode-notifer:0.8.2'
    releaseCompile 'com.nshmura:strictmode-notifer-no-op:0.8.2'
 }
```

In your `Application` class:

```java
public class ExampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    
    if (BuildConfig.DEBUG) {
      //setup this library
      StrictModeNotifier.install(this);

      //setup StrictMode.
      // 
      // penaltyLog() should be called for strictmode-notifier
      //
      new Handler().post(new Runnable() {
        @Override public void run() {
          StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
              .detectAll()
              .permitDiskReads()
              .permitDiskWrites()
              .penaltyLog() // Must!
              .build();
          StrictMode.setThreadPolicy(threadPolicy);

          StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
              .detectAll()
              .penaltyLog() // Must!
              .build();
          StrictMode.setVmPolicy(vmPolicy);
        }
      });
    }
  }
}
```

## How does it work?
1. `strictmode-notifier` starts `logcat`  command in backgound thread, and infinitely reads the log from `logcat`.
2. If StrictMode violation is happend, error logs is outputed.
3. `strictmode-notifier` reads that log via `logcat`, and shows a notification of the violation.


## Customizing

You need to make sure that the customization happens only in debug build,
since the `strictmode-notifier-no-op` only contains the StrictModeNotifier class.

Make `MyStrictMode` class in debug build directory:

```java
public class MyStrictMode {

  public static void init(Context context) {
    StrictModeNotifier
        .install(context)
        //... Customizing ...

    //setup StrictMode.
    //...
  }
}
```

Make `MyStrictMode` class in release build directory:

```java
public class MyStrictMode {

  public static void init(Context context) {
    //no-op
  }
}
```

In your `Application` class:

```java
public class ExampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    MyStrictMode.init(this);
  }
}
```

### How to ignore specific violations

```java
StrictModeNotifier
    .install(context)
    .setIgnoreAction(new NotifierConfig.IgnoreAction() {
      @Override public boolean ignore(StrictModeViolation violation) {
        // ex) ignore LEAKED_CLOSABLE_OBJECTS that contains android.foo.bar in stacktrace.
        return violation.violationType == ViolationType.LEAKED_CLOSABLE_OBJECTS
            && violation.getStacktraceText().contains("android.foo.bar");
      }
    });
```

### How to add custom actions

```java
StrictModeNotifier
    .install(context)
    .addCustomAction(new NotifierConfig.CustomAction() {
      @Override public void onViolation(StrictModeViolation violation) {
        //ex) Send messages into Slack
      }
    });
```

### Other settings

```java
StrictModeNotifier
    .install(context)
    .setHeadupEnabled(false)
    .setDebugMode(true);
```

## Todo
Parsing following violations
- ActivityLeaks
- LeakedRegistrationObjects
- LeakedSqlLiteObjects

## Thanks
Inspired by [square/leakcanary](https://github.com/square/leakcanary)

## License
```
Copyright (C) 2016 nshmura

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
