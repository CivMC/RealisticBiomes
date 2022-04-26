plugins {
	`java-library`
	id("net.civmc.civgradle.plugin")
	id("io.papermc.paperweight.userdev") version "1.3.1"
}

civGradle {
	paper {
		pluginName = "RealisticBiomes"
	}
}

dependencies {
	paperDevBundle("1.18.2-R0.1-SNAPSHOT")

    compileOnly("net.civmc.civmodcore:paper:2.0.0-SNAPSHOT:dev-all")
	compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.8")
    implementation("org.apache.commons:commons-math3:3.6.1")
}
