package org.openmicroscopy.blitz


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.openmicroscopy.blitz.tasks.ImportMappingsTask
import org.openmicroscopy.dsl.DslPlugin

class BlitzPlugin implements Plugin<Project> {

    private static final def Log = Logging.getLogger(BlitzPlugin)

    @Override
    void apply(Project project) {
        // Apply DslPluginBase
        project.plugins.apply(DslPlugin)
        project.plugins.apply(BlitzPluginBase)

        configureImportMappingsTask(project)
        // ssetDefaultOmeXmlFiles(project)
    }

    /**
     * Creates task to extract .ome.xml files from omero-model
     * and place them in {@code omeXmlDir}
     * @param project
     * @return
     */
    void configureImportMappingsTask(Project project) {
        project.tasks.register("importOmeXmlTask", ImportMappingsTask) { t ->
            t.group = BlitzPluginBase.GROUP
            t.description = "Extracts mapping files from omero-model jar"
            t.extractDir = "${project.buildDir}/extracted"
            project.tasks.named('generateCombinedFiles').configure {
                dependsOn t
                omeXmlFiles = project.files(t.extractDir)
            }
        }
    }

    /**
     * Sets each generateXXX task to depend on the ImportMappingsTask and
     * sets their .omeXmlFiles property to its output.
     *
     * @param project
     * @return
     */
//    def setDefaultOmeXmlFiles(Project project) {
//        project.afterEvaluate {
//            def generateTasks = project.tasks.withType(DslBaseTask)
//            if (!generateTasks) {
//                throw new GradleException("Can't find generateTasks")
//            }
//
//            generateTasks.each { task ->
//                Log.info("Task found $task.name")
//                task.dependsOn "importOmeXmlTask"
//                task.omeXmlFiles = project.fileTree(dir: "${project.buildDir}/extracted", include: "*.ome.xml")
//            }
//        }
//    }

}

