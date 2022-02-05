import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecraftforge.gradle.common.util.MinecraftExtension
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
  eclipse
  idea
  id("justjanne.kotlin")
  id("justjanne.publish")
  id("org.jlleitschuh.gradle.ktlint")
  id("org.jetbrains.dokka")
  id("kovarna-plugin") version "0.0.2"
  id("com.github.johnrengelman.shadow") version "7.1.1"
}

version = "0.0.2"
group = "de.justjanne.modding"

dependencies {
  minecraft("net.minecraftforge:forge:1.18.1-39.0.66")
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
  archiveBaseName.set("kovarna")

  manifest {
    attributes(
      mapOf(
        "FMLModType" to "LANGPROVIDER",
        "Automatic-Module-Name" to "kovarna",
        "Specification-Title" to "kovarna",
        "Specification-Vendor" to "Forge",
        "Specification-Version" to "1", // We are version 1 of ourselves
        "Implementation-Title" to project.name,
        "Implementation-Version" to "${project.version}",
        "Implementation-Vendor" to "justjanne.de",
        "Implementation-Timestamp" to Instant.now()
          .atZone(ZoneOffset.UTC)
          .format(DateTimeFormatter.ISO_DATE_TIME)
      )
    )
  }
}

tasks {
  named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    dependencies {
      include(dependency("org.jetbrains.kotlin:kotlin-stdlib:1.6.10"))
      include(dependency("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10"))
    }
  }

  build {
    dependsOn(shadowJar)
  }
}

artifacts {
  archives(tasks.shadowJar.get())
}

publishing {
  publications {
    create<MavenPublication>("shadow") {
      project.shadow.component(this@create)
    }
  }
}

configure<MinecraftExtension> {
  mappings("official", "1.18.1")

  runs {
    create("client") {
      workingDirectory(project.file("run"))

      property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
      property("forge.logging.console.level", "debug")

      mods {
        create("kovarna") {
          source(sourceSets.main.get())
        }
      }
    }

    create("server") {
      workingDirectory(project.file("run"))

      property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
      property("forge.logging.console.level", "debug")

      mods {
        create("kovarna") {
          source(sourceSets.main.get())
        }
      }
    }
  }
}
