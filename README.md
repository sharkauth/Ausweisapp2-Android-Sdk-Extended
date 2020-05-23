# Erweiterte AusweisApp2

Im Rahmen eines Hochschulprojekts an der HTW Berlin wird hier eine Erweiterung der offiziellen AusweisApp2 entstehen. Diese wird Daten der Selbstauskunft über die Android API der AusweisApp2 bereitstellen.

## Build
Environment Variablen:

```
$ export ANDROID_NDK=/.../Android/Sdk/ndk/VERSION
$ export ANDROID_HOME=/.../Android/Sdk/
$ export JAVA_HOME=/.../jdk-1.8.0/
$ cd $ANDROID_NDK/toolchains
$ ln -s llvm llvm-4.9 # cmake build script für Android scheinbar veraltet und erwartet llvm-LLVM_VERSION statt llvm
```
Qt und OpenSSL für Android bauen:
(Qt dauert je nach Rechenleistung recht lange)
```
$ mkdir awapp2-build-lib
$ cd awapp2-build-lib
$ cmake -DCMAKE_ANDROID_ARCH_ABI=arm64-v8a -DCMAKE_BUILD_TYPE=release -DCMAKE_TOOLCHAIN_FILE=../awapp2-ext/cmake/android.toolchain.cmake ../awapp2-ext/libs
$ make -j 16
```

Android AAR:
```
$ mkdir awapp2-build-app
$ cd awapp2-build-app
$ cmake -DCMAKE_ANDROID_ARCH_ABI=arm64-v8a -DCMAKE_BUILD_TYPE=debug -DCMAKE_PREFIX_PATH=/.../awapp2-build-lib/dist -DCMAKE_TOOLCHAIN_FILE=../awapp2-ext/cmake/android.toolchain.cmake ../awapp2-ext
$ make -j 16
$ make install
$ make aar
```

*Hinweis: `/.../` bedeutet absoluter Pfad wir benötigt!*

`.aar` unter `awapp2-build-app/dist/` nach `android/AusweisAppSDKTest/app/libs/` kopieren und Android App bauen.