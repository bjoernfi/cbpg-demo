// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html

plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "cbpg.demo"
version = "1.0.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2023.2")
        instrumentationTools()
        zipSigner()
        // caution: unfortunately, plugin.xml needs to be updated as well
        bundledPlugins("com.intellij.java")
    }

    implementation("javax.inject:javax.inject:1")
    implementation("org.keycloak:keycloak-authz-client:20.0.1") {
        // other minor version is already loaded by IntelliJ, excluding resolves errors with the class loader
        exclude("org.apache.httpcomponents", "httpclient")
    }

    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")

    implementation("com.j256.ormlite:ormlite-core:6.1")
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("com.h2database:h2:2.1.210")

    // @NotNull
    implementation("org.jetbrains:annotations:24.0.0")

    implementation("io.github.resilience4j:resilience4j-retry:2.0.0")
}

intellijPlatform {
    pluginConfiguration {
        id = "cbpg.demo.plugin"
        name = "CBPG Demo Plugin"

        ideaVersion {
            sinceBuild = "232"

            // compatibility with all future versions
            untilBuild = provider { null }
        }
    }

    signing {
        certificateChain = System.getenv("CERTIFICATE_CHAIN")
        privateKey = System.getenv("PRIVATE_KEY")
        password = System.getenv("PRIVATE_KEY_PASSWORD")
    }

}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
    }

    runIde {
        systemProperties.put("idea.log.debug.categories", "#cbpg.demo.plugin")
    }
}
