import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

plugins {
    val kotlinVersion = "1.3.70"
    idea
    kotlin("jvm") version kotlinVersion
    war
    id("fish.payara.micro-gradle-plugin") version "1.0.3"
    id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    jacoco
}

group = "poc"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    maven {
        url = uri("https://repo.gradle.org/gradle/libs-releases-local/")
    }
}

val microprofileVersion = "3.2"
val payaraMicroVersion = "5.2020.2"
val log4j2Version = "2.13.1"
val slf4jVersion = "1.8.0-beta4" // compatible to log4j2
val openTracingApi = "0.33.0" // compatible to microprofile

val junitVersion = "5.6.2"
val junitPlatformVersion = "1.6.2"
val spekVersion = "2.0.9"
val kluentVersion = "1.59"
val mockitoKotlinVersion = "2.2.0"
val arquillianVersion = "1.4.1.Final"
val arquillianPayaraMicroContainerVersion = "1.0.Beta3"
val shrinkwrapVersion = "3.1.3"
val restAssuredVersion = "4.2.0"
val gradleToolApiVersion = "6.2.2"
val jjwtVersion = "0.9.1"
val microshedVersion = "0.8"
val cucumberVersion = "5.7.0"
val awaitilityVersion = "4.0.2"

val payaraMicroJarDir = "$buildDir/payara-micro"
val payaraMicroJarName = "payara-micro.jar"
val payaraMicroJarPath = "$payaraMicroJarDir/$payaraMicroJarName"
val payaraMicroPostBootCommandScript = "$projectDir/configs/post-boot-command.txt"

dependencyManagement {
    imports {
        mavenBom("org.jboss.arquillian:arquillian-bom:$arquillianVersion")
    }
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.apache.logging.log4j:log4j-slf4j18-impl:$log4j2Version")
    implementation("org.slf4j:osgi-over-slf4j:$slf4jVersion")
    implementation("org.slf4j:jul-to-slf4j:$slf4jVersion")
    implementation("org.slf4j:log4j-over-slf4j:$slf4jVersion")
    implementation("org.slf4j:jcl-over-slf4j:$slf4jVersion")

    providedCompile("org.eclipse.microprofile:microprofile:$microprofileVersion")
    providedCompile("io.opentracing:opentracing-api:$openTracingApi")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.platform:junit-platform-engine:$junitPlatformVersion")
    testImplementation("org.junit.platform:junit-platform-commons:$junitPlatformVersion")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")

    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:$mockitoKotlinVersion")

    testImplementation("org.junit.vintage:junit-vintage-engine:$junitVersion")
    testImplementation("org.jboss.arquillian.junit:arquillian-junit-container")
    testImplementation("org.jboss.shrinkwrap.resolver:shrinkwrap-resolver-impl-gradle:$shrinkwrapVersion") {
        exclude(module = "gradle-tooling-api")
    }
    testImplementation("org.gradle:gradle-tooling-api:$gradleToolApiVersion")
    testRuntimeOnly("fish.payara.arquillian:arquillian-payara-micro-5-managed:$arquillianPayaraMicroContainerVersion")
    testRuntime("fish.payara.extras:payara-micro:$payaraMicroVersion")
    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion") {
        // suspend the warning of "'dependencyManagement.dependencies.dependency.systemPath' for com.sun:tools:jar must specify an absolute path but is ${tools.jar} in com.sun.xml.bind:jaxb-osgi:2.2.10"
        exclude(module = "jaxb-osgi")
    }

    testImplementation("io.jsonwebtoken:jjwt:$jjwtVersion")
    testImplementation("fish.payara.api:payara-api:$payaraMicroVersion")

    testImplementation("org.microshed:microshed-testing-payara-micro:$microshedVersion")

    testImplementation("io.cucumber:cucumber-java8:$cucumberVersion")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:$cucumberVersion")

    testImplementation("org.awaitility:awaitility:$awaitilityVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.war {
    archiveFileName.set("${project.name}.war")
}

tasks.withType<Test> {
    dependsOn("copyPayaraMicro")
    environment("MICRO_JAR", "$payaraMicroJarDir/$payaraMicroJarName")
    environment("EXTRA_MICRO_OPTIONS", "--postbootcommandfile $payaraMicroPostBootCommandScript")

    useJUnitPlatform {
        includeEngines("spek2", "junit-jupiter", "junit-vintage", "cucumber")
    }

    finalizedBy("jacocoTestReport")
}

task<Copy>("copyPayaraMicro") {
    from(configurations.testRuntime.get().files { it.name == "payara-micro" })
    into(payaraMicroJarDir)
    rename { payaraMicroJarName }
}

/**
 * Check the guide in https://docs.payara.fish/documentation/ecosystem/gradle-plugin.html
 */
payaraMicro {
    payaraVersion = payaraMicroVersion
    deployWar = true
    commandLineOptions = mapOf(
        "postbootcommandfile" to payaraMicroPostBootCommandScript
    )
}

jacoco {
    toolVersion = "0.8.5"
}

configure<AllOpenExtension> {
    annotation("javax.enterprise.context.RequestScoped")
    annotation("javax.enterprise.context.ApplicationScoped")
}

configure<NoArgExtension> {
    annotation("javax.enterprise.context.RequestScoped")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("poc.microprofile.annotation.DeserializableModel")
}
