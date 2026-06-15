import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class ExportReleaseToDesktopTask : DefaultTask() {
  @get:Input
  abstract val versionName: Property<String>

  @get:Input
  abstract val versionCode: Property<Int>

  @get:InputFile
  abstract val aabFile: RegularFileProperty

  @get:InputFile
  abstract val releaseNotesFile: RegularFileProperty

  @TaskAction
  fun export() {
    val home = File(System.getProperty("user.home"))
    val candidates = listOf(
      File(home, "OneDrive/바탕 화면"),
      File(home, "OneDrive/Desktop"),
      File(home, "Desktop")
    )
    val desktop = candidates.firstOrNull { it.isDirectory }
      ?: throw GradleException(
        "Could not find a Desktop directory. Tried:\n" +
          candidates.joinToString("\n") { "  - ${it.absolutePath}" }
      )
    val buildFolder = File(desktop, "Build")
    if (!buildFolder.exists()) {
      buildFolder.mkdirs()
    }

    val aab = aabFile.get().asFile
    if (!aab.isFile) {
      throw GradleException("Release AAB not found at ${aab.absolutePath}")
    }

    val releaseNotes = releaseNotesFile.get().asFile
    if (!releaseNotes.isFile) {
      throw GradleException("Missing release notes at ${releaseNotes.absolutePath}")
    }

    val releaseNotesText = releaseNotes.readText().trim()
    if (!releaseNotesText.contains("<ko-KR>") || !releaseNotesText.contains("<en-US>")) {
      throw GradleException("Release notes must contain <ko-KR> and <en-US> blocks: ${releaseNotes.absolutePath}")
    }

    // Play Console hard limit: each locale block body (excluding tags) must be
    // 500 Unicode characters or fewer. Over-limit text is silently truncated by
    // Play Console — the worst kind of failure — so abort the export instead of
    // letting a bad file reach the desktop.
    val localePattern = Regex("<(ko-KR|en-US|ja-JP|zh-CN|zh-TW)>([\\s\\S]*?)</\\1>")
    val violations = mutableListOf<String>()
    for (match in localePattern.findAll(releaseNotesText)) {
      val locale = match.groupValues[1]
      val body = match.groupValues[2].trim()
      val len = body.length
      logger.lifecycle("  %-7s  %4d / 500  %s".format(locale, len, if (len > 500) "OVER" else "OK"))
      if (len > 500) {
        violations += "$locale ($len chars, ${len - 500} over)"
      }
    }
    if (violations.isNotEmpty()) {
      throw GradleException(
        "Play Console release notes exceed the 500-character limit per locale: " +
          violations.joinToString(", ") + ". Trim before exporting."
      )
    }

    val baseName = "DaddyWeekend-v${versionName.get()}-vc${versionCode.get()}"
    val aabTarget = File(buildFolder, "$baseName.aab")
    val txtTarget = File(buildFolder, "$baseName-release-notes.txt")

    aab.copyTo(aabTarget, overwrite = true)
    txtTarget.writeText(releaseNotesText + System.lineSeparator())

    logger.lifecycle("Wrote ${aabTarget.absolutePath} (${aabTarget.length()} bytes)")
    logger.lifecycle("Wrote ${txtTarget.absolutePath} (${txtTarget.length()} bytes)")
  }
}

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
}

android {
  namespace = "com.jeiel85.daddyweekend"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.jeiel85.daddyweekend"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/.keystore/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
}

tasks.register<ExportReleaseToDesktopTask>("exportReleaseToDesktop") {
  group = "release"
  description = "Builds the Play release bundle and exports the AAB plus Play Console notes to Desktop/Build."
  dependsOn("bundleRelease")
  versionName.set(android.defaultConfig.versionName!!)
  versionCode.set(android.defaultConfig.versionCode!!)
  aabFile.set(layout.buildDirectory.file("outputs/bundle/release/app-release.aab"))
  releaseNotesFile.set(rootProject.layout.projectDirectory.file("store-graphics/play-console-current/release-notes.txt"))
}
