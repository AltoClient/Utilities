group = "com.jacobtread.mck.utils"
version = "1.0.0"

dependencies {
    implementation(project(":logger"))
    implementation(project(":authlib"))
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.google.guava:guava:31.0.1-jre")
    implementation("it.unimi.dsi:fastutil:8.5.6")
    implementation("org.joml:joml:1.10.3")
}
