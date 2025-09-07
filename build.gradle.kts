plugins {
    id("java")
}

group = "com.melt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("javax.servlet:javax.servlet-api:4.0.1")

    implementation("org.eclipse.jetty:jetty-server:9.4.53.v20231009")
    implementation("org.eclipse.jetty:jetty-servlet:9.4.53.v20231009")
}

tasks.test {
    useJUnitPlatform()
}