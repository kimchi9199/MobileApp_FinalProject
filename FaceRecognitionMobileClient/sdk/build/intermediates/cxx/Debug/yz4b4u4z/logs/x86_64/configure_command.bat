@echo off
"C:\\Users\\Thai\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HD:\\Study\\Cirriculums\\IoT\\KC\\FaceRecognition\\FaceRecognition\\FaceRecognitionMobileClient - Copy\\sdk\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86_64" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86_64" ^
  "-DANDROID_NDK=C:\\Users\\Thai\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Thai\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Thai\\AppData\\Local\\Android\\Sdk\\ndk\\23.1.7779620\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Thai\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=D:\\Study\\Cirriculums\\IoT\\KC\\FaceRecognition\\FaceRecognition\\FaceRecognitionMobileClient - Copy\\sdk\\build\\intermediates\\cxx\\Debug\\yz4b4u4z\\obj\\x86_64" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=D:\\Study\\Cirriculums\\IoT\\KC\\FaceRecognition\\FaceRecognition\\FaceRecognitionMobileClient - Copy\\sdk\\build\\intermediates\\cxx\\Debug\\yz4b4u4z\\obj\\x86_64" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BD:\\Study\\Cirriculums\\IoT\\KC\\FaceRecognition\\FaceRecognition\\FaceRecognitionMobileClient - Copy\\sdk\\.cxx\\Debug\\yz4b4u4z\\x86_64" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"