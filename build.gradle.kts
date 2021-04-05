import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    val kotlinVersion = "1.4.31"

    id("org.springframework.boot") version "2.4.4" apply false
    id("io.spring.dependency-management") version "1.0.11.RELEASE" apply false
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
    `maven-publish`
    java
}

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"
}

subprojects {

    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "org.gradle.idea")

    java.sourceCompatibility = JavaVersion.VERSION_11

    repositories {
        mavenLocal()
        maven(url = "https://maven.aliyun.com/repository/central") // central
        maven(url = "https://maven.aliyun.com/repository/public") // jcenter
        mavenCentral()
        jcenter()
    }

//    // 当每个module都使用了插件 org.springframework.boot 和 io.spring.dependency-management
//    // 好像也不需要使用下面的mavenBom
//    // https://zhuanlan.zhihu.com/p/185013144
//    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#managing-dependencies-gradle-bom-support
//    dependencies {
//        implementation(enforcedPlatform(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES))
//    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    tasks.withType<Jar> {
        enabled = true
    }

    tasks.withType<BootJar> {
        enabled = false
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

}