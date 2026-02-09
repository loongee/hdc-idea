import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.Changelog.OutputType.HTML
import org.jetbrains.changelog.Changelog.OutputType.MARKDOWN

plugins {
    // Must match the Kotlin version bundled with the IDE
    id("org.jetbrains.kotlin.jvm") version "2.2.20"

    // https://github.com/JetBrains/intellij-platform-gradle-plugin
    id("org.jetbrains.intellij.platform") version "2.10.5"

    // https://github.com/ajoberstar/reckon
    id("org.ajoberstar.reckon") version "0.14.0"

    // https://github.com/b3er/gradle-local-properties-plugin
    id("com.github.b3er.local.properties") version "1.1"

    // https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "2.2.1"
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "hdc_idea"
        group = "com.loongee.intellij.plugin.hdcidea"
        changeNotes.set(provider { recentChanges(HTML) })
        ideaVersion.sinceBuild.set(project.property("sinceBuild").toString())
        ideaVersion.untilBuild.set(provider { null })
    }
    buildSearchableOptions.set(false)
    instrumentCode = false
}

changelog {
    repositoryUrl.set("https://github.com/loongee/hdc-idea")
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.empty()
    combinePreReleases.set(true)
}

reckon {
    scopeFromProp()
    snapshotFromProp()
}

kotlin {
    jvmToolchain(17)
}

tasks.runIde {
    jvmArgs = listOf("-Xmx4096m", "-XX:+UnlockDiagnosticVMOptions")
}

tasks.register("printLastChanges") {
    notCompatibleWithConfigurationCache("Uses recentChanges function which is not cacheable")
    doLast {
        println(recentChanges(outputType = MARKDOWN))
        println(recentChanges(outputType = HTML))
    }
}

val localIdePath: String by project.extra
localIdePath.let {
    val runLocalIde by intellijPlatformTesting.runIde.registering {
        localPath.set(file(it))
    }
}

dependencies {
    intellijPlatform {
        instrumentationTools()

        if (project.hasProperty("localIdeOverride")) {
            local(property("localIdeOverride").toString())
        } else {
            local(localIdePath)
        }
        // 依赖 DevEco Studio 自带的 OpenHarmony 插件（不在 JetBrains 仓库，需用 localPlugin 指向本地）
        val idePath = if (project.hasProperty("localIdeOverride")) {
            property("localIdeOverride").toString()
        } else {
            localIdePath
        }
        localPlugin(file(idePath).resolve("plugins/openharmony").absolutePath)
    }

    implementation("org.jooq:joor:0.9.15")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.7.0")
    testImplementation("com.google.truth:truth:1.4.4")
}

fun recentChanges(outputType: Changelog.OutputType): String {
    var s = ""
    changelog.getAll().toList().drop(1) // drop the [Unreleased] section
        .take(5) // last 5 changes
        .forEach { (key, _) ->
            s += changelog.renderItem(
                changelog.get(key).withHeader(true).withEmptySections(false), outputType
            )
        }

    return s
}

// 添加 HDC 构建任务
tasks.register("buildHdc") {
    group = "build"
    description = "Build HDC version of the plugin for DevEco Studio"
    dependsOn("build")
}
