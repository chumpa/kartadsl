plugins {
    id("application")
}
val appVersion: String by project

group = "io.rsug"
version = "0.0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

dependencies {
    implementation("commons-io:commons-io:2.22.0")
//    implementation("xerces:xercesImpl:2.12.2")
//    runtimeOnly("org.relaxng:trang:20241231")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("com.sun.xml.bind:jaxb-impl:2.3.1")
    implementation("com.sun.xml.bind:jaxb-core:2.3.0.1")
    implementation(
        fileTree(
            mapOf(
                "dir" to "libs", "include" to listOf("*.jar")
            )
        )
    )
    implementation("org.eclipse.jetty:jetty-server:12.1.11")
//    implementation("org.eclipse.jetty.ee10:jetty-ee10:12.1.11")
    implementation("org.eclipse.jetty.ee10:jetty-ee10-webapp:12.1.11")
    implementation("org.antlr:ST4:4.3.4")

//    implementation("org.antlr:antlr4:4.13.2")
    //implementation("net.sourceforge.plantuml:plantuml:8059")
    //свежий plantuml придётся брать локально
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<Jar>("apiJar") {
    archiveBaseName.set("zatupka")
    archiveVersion.set(version.toString())

    from(sourceSets.main.get().output) {
        include("io/rsug/zatupka/**")
    }
    from(sourceSets.main.get().resources) {
        include("io.rsug.zatupka.xsd/**")
    }
//  exclude("**/*Impl.class")
    manifest {
        attributes(
            "Implementation-Title" to "Zatupka",
            "Implementation-Version" to version,
            "Build-Jdk" to System.getProperty("java.version")
        )
    }
    // Чтобы JAR собирался при вызове assemble
    dependsOn(tasks.classes)
}

tasks.test {
    useJUnitPlatform()
}
