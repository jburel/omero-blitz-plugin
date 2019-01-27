package org.openmicroscopy.blitz

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.openmicroscopy.blitz.extensions.BlitzExtension
import org.openmicroscopy.blitz.extensions.SplitExtension
import org.openmicroscopy.blitz.tasks.SplitTask
import org.openmicroscopy.dsl.extensions.VelocityExtension
import org.openmicroscopy.dsl.tasks.DslMultiFileTask
import org.openmicroscopy.dsl.utils.ResourceLoader

class BlitzPluginBase implements Plugin<Project> {

    /**
     * Sets the group name for the DSLPlugin tasks to reside in.
     * i.e. In a terminal, call `./gradlew tasks` to list tasks in their groups in a terminal
     */
    static final def GROUP = "omero-blitz"

    BlitzExtension blitzExt

    @Override
    void apply(Project project) {
        init(project, project.extensions)
    }

    def init(Project project, ExtensionContainer baseExt) {
        setupBlitzExtension(project, baseExt)
        configureCombineTask(project)
        configureSplitTasks(project)
    }

    void setupBlitzExtension(Project project, ExtensionContainer baseExt) {
        // Add the 'blitz' extension object
        blitzExt = baseExt.create('blitz', BlitzExtension, project)

        // Add container for blitz
        blitzExt.extensions.add('api', project.container(SplitExtension))
    }

    /**
     * Creates task to process combined.vm template and spit out .combined
     * files for generating sources.
     * @param project
     * @return
     */
    void configureCombineTask(Project project) {
        // Config for velocity
        def ve = new VelocityExtension(project)

        project.tasks.register("generateCombinedFiles", DslMultiFileTask) { task ->
            task.group = GROUP
            task.description = "Processes combined.vm and generates .combined files"
            task.profile = blitzExt.profile
            task.template = "ResourceLoader.loadFile(project, "templates/combined.vm")
            task.velocityProperties = ve.data.get()
            task.outputDir = blitzExt.combinedDir
            task.omeXmlFiles = blitzExt.omeXmlFiles
            task.formatOutput = { st -> "${st.getShortname()}I.combined" }
        }
    }

    void configureSplitTasks(Project project) {
        blitzExt.api.all { SplitExtension split ->
            String taskName = "split${split.name.capitalize()}"

            project.tasks.register(taskName, SplitTask) { task ->
                task.dependsOn project.tasks.named("generateCombinedFiles")
                task.group = GROUP
                task.description = "Splits ${split.language} from .combined files"
                task.combined = project.fileTree(
                        dir: getDir(split.combinedDir, blitzExt.combinedDir),
                        include: '**/*.combined'
                )
                task.outputDir = getDir(split.outputDir, blitzExt.outputDir)
                task.language = split.language
                task.replaceWith = split.outputName
            }
        }
    }

    File getDir(File expected, File fallback) {
        if (expected) {
            if (expected.isAbsolute()) {
                return expected
            } else if (fallback) {
                return new File(fallback, expected.path)
            }
        }
        return fallback
    }

}
