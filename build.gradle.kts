// Top-level build file — applied to every module.
// All plugins are declared with `apply false` here and then applied in module-level build files.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
}
