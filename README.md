# Erweiterte AusweisApp2

Im Rahmen eines Hochschulprojekts an der HTW Berlin wird hier eine Erweiterung der offiziellen **AusweisApp2** entstehen. Diese wird Daten der Selbstauskunft über die Android API der AusweisApp2 bereitstellen.

This repository is part of a university project at the HTW Berlin and aims to extend the german eID application **AusweisApp2**.
The objective is to be able to extract user information from the german eID and publish them through the exisiting Android SDK.

## Build: AusweisApp2 (Extended) SDK
Environment:

```
$ export ANDROID_NDK=/.../Android/Sdk/ndk/VERSION
$ export ANDROID_HOME=/.../Android/Sdk/
$ export JAVA_HOME=/.../jdk-1.8.0/
$ cd $ANDROID_NDK/toolchains
$ ln -s llvm llvm-4.9 # cmake build script für Android scheinbar veraltet und erwartet llvm-LLVM_VERSION statt llvm
```

Build Qt and OpenSSL for Android:
(Qt could take some time)
```
$ mkdir awapp2-build-lib
$ cd awapp2-build-lib
$ cmake -DCMAKE_ANDROID_ARCH_ABI=arm64-v8a -DCMAKE_BUILD_TYPE=release -DCMAKE_TOOLCHAIN_FILE=../awapp2-ext/cmake/android.toolchain.cmake ../awapp2-ext/libs
$ make -j 16
```

Android aar library, currently only possible under Linux:
```
$ mkdir awapp2-build-app
$ cd awapp2-build-app
$ cmake -DCMAKE_ANDROID_ARCH_ABI=arm64-v8a -DCMAKE_BUILD_TYPE=debug -DCMAKE_PREFIX_PATH=/.../awapp2-build-lib/dist -DCMAKE_TOOLCHAIN_FILE=../awapp2-ext/cmake/android.toolchain.cmake ../awapp2-ext
$ make -j 16
$ make install
$ make aar
```

*Hint: `/.../` means absolute path is required!*

## Android App & SDK

Following submodules can be found under `android/AusweisAppSDKTest`:
```
/app                       Sample app, die ausweisapp2sdkextended nutzt.
/ausweisapp-1.2.0-extended Extended AusweisApp2 with a self-auth worklfow triggered within the json ui plugin.
/ausweisapp2sdkextended    Android aar library, which requires/uses ausweisapp-1.2.0-extended.
```

### ausweisapp-1.2.0-extended

After the build the `.aar` lib needs to be copied from `awapp2-build-app/dist/` to `android/AusweisAppSDKTest/ausweisapp-1.20.0-extended/`.
Now run the gradle `build` task within the `app` module.

## Sample App

After a successfull authentification, the sample app shows the eID user data.

### Usage

1. The user is required to enter the eID pin,
2. attaches the eID card to the smartphone and
3. waits until the loading indicator vanishes
4. Finally, the data or an error toast is shown.

## Limitations

The self auth mechanism isn't officially specificated, therefore the json fields (name and value format (e.g. upper case vs. camelcase)) can change.
It is the responisbility of the Workflow class to sanitize these data.
As a result, one eID holder should always be identifiable with the same uuid in the User class.

