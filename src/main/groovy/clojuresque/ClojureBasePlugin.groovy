/*-
 * Copyright 2009-2013 © Meikel Brandmeyer.
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package clojuresque

import clojuresque.tasks.ClojureCompile
import clojuresque.tasks.ClojureDoc
import clojuresque.tasks.ClojureSourceSet
import clojuresque.tasks.ClojureTest
import clojuresque.tasks.ClojureUploadConvention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.Upload

public class ClojureBasePlugin implements Plugin<Project> {
    static final String CLOJURE_GROUP = "clojure development"

    void apply(Project project) {
        project.apply plugin: "java"
        project.apply plugin: "de.kotka.clojuresque.common"

        project.convention.plugins.clojureDeprecated =
            new ClojurePluginDeprecatedConvention(project)

        project.extensions.create("clojure", ClojurePluginExtension)

        def repos = project.repositories
        repos.convention.plugins.clojure =
            new ClojureRepositoryConvention(repos)

        configureSourceSets(project)
        configureCompilation(project)
        configureDocs(project)
        configureTests(project)
        configureClojarsUpload(project)
    }

    private void configureSourceSets(project) {
        ProjectInternal projectInternal = (ProjectInternal)project

        project.sourceSets.all { sourceSet ->
             def clojureSourceSet =
                new ClojureSourceSet(sourceSet.name, projectInternal.fileResolver)

            sourceSet.convention.plugins.clojure = clojureSourceSet
            sourceSet.clojure.srcDir "src/${sourceSet.name}/clojure"
            sourceSet.allSource.source(clojureSourceSet.clojure)

            sourceSet.clojure.delayedAotCompile =
                { project.clojure.aotCompile }
            sourceSet.clojure.delayedWarnOnReflection =
                { project.clojure.warnOnReflection }
        }
    }

    private void configureCompilation(project) {
        project.sourceSets.all { set ->
            if (set.equals(project.sourceSets.test))
                return
            def compileTaskName = set.getCompileTaskName("clojure")
            def task = project.task(compileTaskName, type: ClojureCompile) {
                from set.clojure
                delayedAotCompile       = { set.clojure.aotCompile }
                delayedWarnOnReflection = { set.clojure.warnOnReflection }
                delayedDestinationDir   = { set.output.classesDir }
                delayedClasspath = {
                    project.files(
                        set.compileClasspath,
                        project.configurations.development
                    )
                }
                description = "Compile the ${set.name} Clojure source."
            }
            project.tasks[set.classesTaskName].dependsOn task
        }
    }

    private void configureDocs(project) {
        project.sourceSets.main { set ->
            def compileTaskName = set.getCompileTaskName("clojure")
            def docTaskName = set.getTaskName(null, "clojuredoc")
            def compileTask = project.tasks[compileTaskName]
            def task = project.task(docTaskName, type: ClojureDoc) {
                from set.clojure
                delayedDestinationDir = {
                    project.file(project.docsDir.path + "/clojuredoc")
                }
                delayedJvmOptions = { compileTask.jvmOptions }
                delayedClasspath = { compileTask.classpath }
                description =
                    "Generate documentation for the Clojure source."
                group = JavaBasePlugin.DOCUMENTATION_GROUP
            }
        }
    }

    private void configureTests(project) {
        def compileTask = project.tasks[
            project.sourceSets.main.getCompileTaskName("clojure")
        ]
        def clojureTest = project.task("clojureTest", type: ClojureTest) {
            from project.sourceSets.test.clojure
            delayedJvmOptions = { compileTask.jvmOptions }
            delayedClasspath  = { project.configurations.testRuntime }
            delayedClassesDir = { project.sourceSets.main.output.classesDir }
            delayedJunitOutputDir = {
                project.file(project.buildDir.path + "/test-results")
            }
            dependsOn project.tasks.classes, project.configurations.testRuntime
            description = "Run Clojure tests in src/test."
            group = JavaBasePlugin.VERIFICATION_GROUP
            if (project.hasProperty("clojuresque.test.vars")) {
                tests = project.getProperty("clojuresque.test.vars").split(",")
            }
        }
        project.tasks.test.dependsOn clojureTest
    }

    private void configureClojarsUpload(project) {
        project.tasks.whenTaskAdded { upload ->
            if (!(upload instanceof Upload))
                return
            upload.convention.plugins.clojure =
                new ClojureUploadConvention(upload)
        }
    }
}
