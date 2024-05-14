package eu.phaf.gradle.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskContainer
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import java.io.File
import java.util.*

val Project.`sourceSets`: SourceSetContainer get() = extensions.getByName("sourceSets") as SourceSetContainer

class RegisterOpenApiTasksPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        setupOpenApiTasks(
                project.projectDir.path,
                project.layout.buildDirectory.get().toString(),
                project.tasks,
                project.sourceSets.getByName("main").java)
    }
}

enum class OpenApiType {
    CLIENT, SERVER
}

fun setupOpenApiTasks(projectDir: String, buildDirectory: String, tasks: TaskContainer, sourceDirectorySet: SourceDirectorySet) {
    val sourceDir = "$projectDir/src/main/openapi/"
    val generatedDir = "$buildDirectory/generated"
    val openApiList = getOpenApiList(sourceDir)
    val openApiTasks: MutableList<GenerateTask> = mutableListOf()
    openApiList.forEach {
        val apiName = getApiName(it.key)
        if (it.value == OpenApiType.CLIENT) {
//            openApiTasks.add(createClientOpenApiTask(apiName, it.key, projectDir, generatedDir, sourceDir, clientConfigOptions(), tasks, "Reactive"))
            openApiTasks.add(createClientOpenApiTask(apiName, it.key, projectDir, generatedDir, sourceDir, nonReactiveClientConfigOptions(), tasks, "NonReactive"))
        } else if (it.value == OpenApiType.SERVER) {
//            openApiTasks.add(createServerOpenApiTask(apiName, it.key, projectDir, generatedDir, sourceDir, serverConfigOptions(), tasks, "Reactive"))
            openApiTasks.add(createServerOpenApiTask(apiName, it.key, projectDir, generatedDir, sourceDir, nonReactiveServerConfigOptions(), tasks, "NonReactive"))
        }
    }
    tasks.create("openApiGenerateAll") {
        it.inputs.dir(sourceDir)
        it.outputs.dir(generatedDir)
        it.dependsOn(openApiTasks)
    }
    sourceDirectorySet.srcDir("$generatedDir/src/main/java")
}

fun createClientOpenApiTask(
        apiName: String,
        file: File,
        projectDir: String,
        generatedSourceDir: String,
        sourceDir: String,
        configOptionsMap: Map<String, String>,
        tasks: TaskContainer,
        taskPostFix: String
): GenerateTask {
    return tasks.create(getTaskName(apiName, "Client$taskPostFix"),
            GenerateTask::class.java) {
        it.inputs.file(file.path)
        it.outputs.dir(generatedSourceDir)
        // uses tags to create file & class names
        it.additionalProperties.put("useTags", true)
        it.inputSpec.set(file.path)
        it.outputDir.set(generatedSourceDir)

        it.generatorName.set("java")
        it.apiPackage.set("eu.phaf.openapi.$apiName.infrastructure.api.client")
        it.invokerPackage.set("eu.phaf.openapi.$apiName.infrastructure.config." + taskPostFix.lowercase(Locale.US))
        it.modelPackage.set("eu.phaf.openapi.$apiName.domain.dto")
        it.ignoreFileOverride.set("$projectDir/.openapi-generator-ignore")
        it.templateDir.set("$sourceDir/templates")
        it.schemaMappings.set(mapOf("ProblemDetail" to "org.springframework.http.ProblemDetail"))
        it.configOptions.set(configOptionsMap)
    }
}

fun createServerOpenApiTask(
        apiName: String,
        file: File,
        projectDir: String,
        generatedSourceDir: String,
        sourceDir: String,
        configOptionsMap: Map<String, String>,
        tasks: TaskContainer,
        taskPostFix: String
): GenerateTask {
    return tasks.create(getTaskName(apiName, "Server$taskPostFix"),
            GenerateTask::class.java) {
        it.inputs.file(file.path)
        it.outputs.dir(generatedSourceDir)
        // uses tags to create file & class names
        it.additionalProperties.put("useTags", true)
        it.inputSpec.set(file.path)
        it.outputDir.set(generatedSourceDir)

        it.generatorName.set("spring")
        it.apiPackage.set("eu.phaf.openapi.$apiName.infrastructure.api.server")
        it.invokerPackage.set("eu.phaf.openapi.$apiName.invoke")
        it.modelPackage.set("eu.phaf.openapi.$apiName.domain.dto")
        it.ignoreFileOverride.set("$projectDir/.openapi-generator-ignore")
        it.templateDir.set("$sourceDir/templates")
        it.configOptions.set(configOptionsMap)
    }
}

fun clientConfigOptions(): Map<String, String> {
    return mapOf(
            "reactive" to "true",
            "java8" to "true",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "library" to "webclient",
            "serializationLibrary" to "jackson",
            "useJakartaEe" to "true",
            "openApiNullable" to "false",
            "enumUnknownDefaultCase" to "true",
    )
}

fun nonReactiveClientConfigOptions(): Map<String, String> {
    return mapOf(
            "reactive" to "false",
            "java8" to "true",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "library" to "resttemplate",
            "serializationLibrary" to "jackson",
            "useJakartaEe" to "true",
            "openApiNullable" to "false",
            "enumUnknownDefaultCase" to "true",
    )
}

fun serverConfigOptions(): Map<String, String> {
    return mapOf(
            "reactive" to "true",
            "java8" to "true",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "useSpringBoot3" to "true",
            "library" to "spring-boot",
            "serializationLibrary" to "jackson",
            "useJakartaEe" to "true",
            "openApiNullable" to "false",
            "enumUnknownDefaultCase" to "true",
    )
}

fun nonReactiveServerConfigOptions(): Map<String, String> {
    return mapOf(
            "java8" to "true",
            "interfaceOnly" to "true",
            "skipDefaultInterface" to "true",
            "useSpringBoot3" to "true",
            "library" to "spring-boot",
            "serializationLibrary" to "jackson",
            "useJakartaEe" to "true",
            "openApiNullable" to "false",
            "enumUnknownDefaultCase" to "true",
    )
}

fun getOpenApiList(sourceDir: String): Map<File, OpenApiType> {
    return getOpenApiList(sourceDir, OpenApiType.CLIENT) + getOpenApiList(sourceDir, OpenApiType.SERVER)
}

fun getOpenApiList(sourceDir: String, type: OpenApiType): Map<File, OpenApiType> {
    val openApiList = mutableMapOf<File, OpenApiType>()
    File("$sourceDir/${type.toString().lowercase()}").walk().forEach {
        if (it.name.endsWith(".yaml") || it.name.endsWith(".yml")) {
            openApiList.put(it, type)
        }
    }
    return openApiList
}


fun getApiName(it: File) = it.name
        .replace(".yaml", "")
        .replace(".yml", "")
        .replace("-", "")
        .replace(".", "_")

fun getTaskName(apiName: String, type: String) = "openApiGenerate" + type +
        apiName.replaceFirstChar { it.titlecase(Locale.getDefault()) }
