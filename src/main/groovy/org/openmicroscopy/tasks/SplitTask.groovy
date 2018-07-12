package org.openmicroscopy.tasks

import org.apache.commons.io.FilenameUtils
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Transformer
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.copy.RegExpNameMapper
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.openmicroscopy.Language
import org.openmicroscopy.Prefix

import java.util.regex.Pattern

class SplitTask extends DefaultTask {

    private static final def DEFAULT_SOURCE_NAME = "(.*?)I[.]combined"

    /**
     * List of the languages we want to split from .combined files
     */
    @Input
    Language language

    /**
     * Directory to spit out source files
     */
    @OutputDirectory
    File outputDir

    /**
     * Collection of .combined files to process
     */
    @InputFiles
    @PathSensitive(PathSensitivity.NONE)
    FileCollection combined

    @Optional
    Tuple2<String, String> renameParams

    void setLanguage(String language) {
        Language lang = Language.find(language)
        if (lang == null) {
            throw new GradleException("Unsupported language : ${language}")
        }
        this.language = lang
    }

    void language(String language) {
        setLanguage(language)
    }

    void language(Language lang) {
        this.language = lang
    }

    /**
     * Directory to spit out source files
     * @param dir
     * @return
     */
    void outputDir(File dir) {
        this.outputDir = dir
    }

    /**
     * Directory to spit out source files
     * @param dir
     * @return
     */
    void outputDir(String dir) {
        this.outputDir = new File(dir)
    }

    /**
     * Custom set method for concatenating FileCollections
     * @param combinedFiles
     */
    void combined(FileCollection combinedFiles) {
        if (this.combined) {
            this.combined = this.combinedFiles + combinedFiles
        } else {
            this.combined = combinedFiles
        }
    }

    void rename(Pattern sourceRegEx, String replaceWith) {
        this.renameParams = new Tuple2<String, String>(
                sourceRegEx.pattern(),
                replaceWith
        )
    }

    void rename(String sourceRegEx, String replaceWith) {
        this.renameParams = new Tuple2<String, String>(
                sourceRegEx,
                replaceWith
        )
    }

    void rename(String replaceWith) {
        rename(DEFAULT_SOURCE_NAME, replaceWith)
    }

    @TaskAction
    void action() {
        language.prefixes.each { Prefix prefix ->
            // Transform prefix enum to lower case for naming
            final def prefixName = prefix.name().toLowerCase()

            println renameParams

            // Assign default to rename
            Transformer<String, String> nameTransformer
            if (!renameParams) {
                nameTransformer = new RegExpNameMapper(DEFAULT_SOURCE_NAME,
                        "\$1I${prefix.extension}")
            } else {
                nameTransformer = new RegExpNameMapper(renameParams.first,
                        formatSecond(prefix, renameParams.second))
            }

            project.copy { c ->
                c.from combined
                c.into outputDir
                c.rename nameTransformer
                c.filter { String line -> filerLine(line, prefixName) }
            }
        }
    }

    static def formatSecond(Prefix prefix, String second) {
        final int index = FilenameUtils.indexOfExtension(second)
        if (index == -1) {
            return "${second}${prefix.extension}"
        } else {
            return second
        }
    }

    static def filerLine(String line, String prefix) {
        return line.matches("^\\[all](.*)|^\\[${prefix}](.*)") ?
                line.replaceAll("^\\[all]|^\\[${prefix}]", "") :
                null
    }
}
