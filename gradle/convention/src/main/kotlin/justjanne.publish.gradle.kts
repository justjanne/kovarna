plugins {
  id("maven-publish")
  id("signing")
}

version = rootProject.version
group = rootProject.group

afterEvaluate {
  publishing {
    publications {
      getByName<MavenPublication>("shadow").configurePom()
    }
  }

  configure<SigningExtension> {
    sign(publishing.publications["shadow"])
  }
}

fun MavenPublication.configurePom() {
  pom {
    name.set(buildHumanReadableName(artifactId))
    description.set("Kotlin Language Provider for Minecraft Forge")
    url.set("https://github.com/ACCOUNT/REPOSITORY")
    licenses {
      license {
        name.set("Mozilla Public License Version 2.0")
        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }
    developers {
      developer {
        name.set("Jane Doe")
        email.set("user@example.com")
      }
    }
    scm {
      connection.set("https://github.com/ACCOUNT/REPOSITORY.git")
      developerConnection.set("scm:git:ssh:git@github.com:ACCOUNT/REPOSITORY.git")
      url.set("https://github.com/ACCOUNT/REPOSITORY")
    }
  }
}

fun buildHumanReadableName(name: String) = name
  .splitToSequence('-')
  .joinToString(" ", transform = String::capitalize)
