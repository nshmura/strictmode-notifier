# strictmode-notifier
An Android library that enhances the StrictMode reporting.

- *Head-up Notification* of StrictMode violations.
- *Custom Actions* that called when StrictMode violations is happend.
- *Violation Histories Viewer* that automatically installed. 
Icon is <img src="/library/src/main/res/drawable-xxxhdpi/strictmode_notifier_ic_launcher.png" width="30"/>.

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
    debugCompile 'com.nshmura:strictmode-notifer:0.1.1'
    releaseCompile 'com.nshmura:strictmode-notifer-no-op:0.1.1'
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

      //setup StrictMode. penaltyLog() should be call.
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

## Custom actions

In your `Application` class:
```java
public class ExampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();

    if (BuildConfig.DEBUG) {
      StrictModeNotifier.install(this, CustomLogWatchService.class);
    
      //setup StrictMode.
      //...
    }
  }
}
```

In your `CustomLogWatchService` class:
```java
public class CustomLogWatchService extends LogWatchService {

  @Override
  protected void notifyViolation(StrictModeViolation violation) {
    super.notifyViolation(violation); //show a Notification.

    // Custom Actions.
    // ex) Send logs to Slack.
  }
}
```
## How does it work?
1. `strictmode-notfier` starts `logcat`  command in backgound thread, and infinitely reads the log from `logcat`.
2. If StrictMode violation is happend, error logs is outputed.
3. `strictmode-notfier` reads that log via `logcat`, and shows a notification of the violation.

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
