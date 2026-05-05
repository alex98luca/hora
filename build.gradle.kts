plugins {
    id("org.sonarqube") version "7.2.3.7755"
}

sonar {
    properties {
        property("sonar.projectKey", "alex98luca_hora")
        property("sonar.organization", "alex98luca")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "hora-core/build/reports/jacoco/test/jacocoTestReport.xml",
        )
    }
}
