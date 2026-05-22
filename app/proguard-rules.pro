# MAPA — ProGuard rules
# We don't ship a release build with shrinking in v1. These rules are placeholders.

# Keep all test-mode hooks intact even in release (we want Appium to work on either build type)
-keep class com.practice.mapa.util.TestMode { *; }
