# strictmode-notifier
An Android library that shows a notification of StrictMode violation.

<img src="assets/notification.png" width="25%" hspace="10" vspace="10"/>
<img src="assets/detail.png" width="25%" hspace="10" vspace="10"/>
<img src="assets/list.png" width="25%" hspace="10" vspace="10"/>

## About StrictMode
- [StrictMode for Runtime Analysis on Android](https://medium.com/google-developers/strictmode-for-runtime-analysis-on-android-f8d0a2c5667e#.elffd4gi1)
- [StrictMode for enforcing best practices at runtime](https://www.youtube.com/watch?v=BxTfwT7mkB4)

## Getting started

In your `build.gradle`:

```gradle
 dependencies {
    debugCompile 'com.nshmura:strictmode-notifer:0.1.0'
    releaseCompile 'com.nshmura:strictmode-notifer-no-op:0.1.0'
 }
```

In your `Application` class:

```java
public class ExampleApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    
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
```

## How does it work?
1. StrictModeNotifier starts `logcat -v time -s StrictMode:*` command, and infinitely reads the output.
2. If StrictMode violation is happend, logcat outputs logs.
3. StrictModeNotifier reads that log, and shows a notification of the violation.

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
