@echo off
"E:\\AndroidDev\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HE:\\Download\\FaceRecognition - Final\\FaceRecognition - Final\\FaceRecognitionMobileClient\\sdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=E:\\AndroidDev\\ndk\\23.1.7779620" ^
  "-DCMAKE_ANDROID_NDK=E:\\AndroidDev\\ndk\\23.1.7779620" ^
  "-DCMAKE_TOOLCHAIN_FILE=E:\\AndroidDev\\ndk\\23.1.7779620\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=E:\\AndroidDev\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=E:\\Download\\FaceRecognition - Final\\FaceRecognition - Final\\FaceRecognitionMobileClient\\sdk\\build\\intermediates\\cxx\\RelWithDebInfo\\6m21653g\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=E:\\Download\\FaceRecognition - Final\\FaceRecognition - Final\\FaceRecognitionMobileClient\\sdk\\build\\intermediates\\cxx\\RelWithDebInfo\\6m21653g\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=RelWithDebInfo" ^
  "-BE:\\Download\\FaceRecognition - Final\\FaceRecognition - Final\\FaceRecognitionMobileClient\\sdk\\.cxx\\RelWithDebInfo\\6m21653g\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
