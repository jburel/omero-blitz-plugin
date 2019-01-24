import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.openmicroscopy.blitz.tasks.ImportMappingsTask

public class ImportTest {

    @Rule
    public TemporaryFolder testProjectDir = new TemporaryFolder()


    @Test
    void doWork() {
        def project = ProjectBuilder.builder()
                .withProjectDir(testProjectDir.root)
                .build()

        def task = project.task("importTask", type: ImportMappingsTask)
        task.extractDir = project.buildDir
        project['importTask'].apply()
    }


}
