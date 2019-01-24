import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class ImportMappingsTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    // Project project

//    def setup() {
//        project = ProjectBuilder.builder()
//                .withProjectDir(testProjectDir.root)
//                .build()
//    }

//    def "do work"() {
//        given:
//        def task = project.tasks.create("importTask", ImportMappingsTask) { task ->
//            task.extractDir = project.buildDir
//        }
//
//        when:
//        task.apply()
//
//        then:
//        project.fileTree(dir: task.extractDir, includes: "**/*.ome.xml").files.isEmpty()
//    }


    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def "do work"() {
        buildFile << """
            plugins {
                id 'org.openmicroscopy.blitz'
            }

            task importTask(type: org.openmicroscopy.blitz.tasks.ImportMappingsTask) {
                extractDir = "\${buildDir}"
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .build()

        then:
        result.task(":importTask").outcome == SUCCESS
    }


}
