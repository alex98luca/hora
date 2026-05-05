plugins {
    id("org.sonarqube") version "7.2.3.7755"
}

sonar {
    properties {
        property("sonar.projectKey", "alex98luca_hora")
        property("sonar.organization", "alex98luca")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

dependencyLocking {
    lockAllConfigurations()
}

subprojects {
    dependencyLocking {
        lockAllConfigurations()
    }

    sonar {
        properties {
            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile,
            )
        }
    }
}
