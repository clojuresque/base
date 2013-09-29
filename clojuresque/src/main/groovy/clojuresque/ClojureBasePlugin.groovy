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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.Upload

import clojuresque.tasks.ClojureCompileTask
import clojuresque.tasks.ClojureDocTask
import clojuresque.tasks.ClojureSourceSet
import clojuresque.tasks.ClojureTestTask
import clojuresque.tasks.ClojureUploadConvention

public class ClojureBasePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.apply plugin: "java"
        project.apply plugin: "maven"

        project.convention.plugins.clojure =
            new ClojurePluginConvention(project)

        project.extensions.create("clojure", ClojurePluginExtension)

        def repos = project.repositories
        repos.convention.plugins.clojure =
            new ClojureRepositoryConvention(repos)

        configureConfigurations(project)
        configureSourceSets(project)
        configureCompilation(project)
        configureDocs(project)
        configureTests(project)
        configureClojarsUpload(project)
    }

    private void configureConfigurations(project) {
        project.configurations {
            clojuresque {
                transitive = false
                visible = false
                description = "Clojuresque internal configuration. Don't use!"
            }
            development {
                transitive = false
                visible = false
                description = "Development only dependencies"
            }
        }
        project.dependencies {
            clojuresque group: "clojuresque", name: "clojuresque-runtime",
                version: Util.properties.getProperty("clojuresque.version")
        }
    }

    private void configureSourceSets(project) {
        ProjectInternal projectInternal = (ProjectInternal)project

        project.sourceSets.each { sourceSet ->
             def clojureSourceSet =
                new ClojureSourceSet(sourceSet.name, projectInternal.fileResolver)

            sourceSet.convention.plugins.clojure = clojureSourceSet
            sourceSet.clojure.srcDirs = [
                String.format("src/%s/clojure", sourceSet.name)
            ]
            sourceSet.allSource.source(clojureSourceSet.clojure)
        }
    }

    private void configureCompilation(project) {
        project.sourceSets.each { set ->
            if (set.equals(project.sourceSets.test))
                return
            def compileTaskName = set.getCompileTaskName("clojure")
            def task = project.task(compileTaskName,
                    type: ClojureCompileTask) {
                delayedDestinationDir = { set.output.classesDir }
                source set.clojure
                clojureRoots = set.clojure
                delayedClasspath = {
                    project.files(
                        set.compileClasspath,
                        project.configurations.development
                    )
                }
                description =
                    String.format("Compile the %s Clojure source.",
                            set.name)
            }
            project.tasks[set.classesTaskName].dependsOn task
        }
    }

    private void configureDocs(project) {
        project.sourceSets.each { set ->
            if (set.equals(project.sourceSets.test))
                return
            def compileTaskName = set.getCompileTaskName("clojure")
            def docTaskName = set.getTaskName(null, "clojuredoc")
            def compileTask = project.tasks[compileTaskName]
            def task = project.task(docTaskName, type: ClojureDocTask) {
                delayedDestinationDir = {
                    project.file(project.docsDir.path + "/clojuredoc")
                }
                jvmOpts = { compileTask.jvmOpts }
                source set.clojure
                clojureRoots = set.clojure
                delayedClasspath = { compileTask.classpath }
                description =
                    String.format("Generate documentation for the %s Clojure source.",
                            set.name)
            }
        }
    }

    private void configureTests(project) {
        def compileTask = project.tasks[
            project.sourceSets.main.getCompileTaskName("clojure")
        ]
        def clojureTest = project.task("clojureTest",
                type: ClojureTestTask) {
            source project.sourceSets.test.clojure
            testRoots = project.sourceSets.test.clojure
            delayedJvmOpts = { compileTask.jvmOpts }
            delayedClasspath  = { project.configurations.testRuntime }
            delayedClassesDir = { project.sourceSets.main.output.classesDir }
            delayedJunitOutputDir = {
                project.file(project.buildDir.path + "/test-results")
            }
            dependsOn project.tasks.classes, project.configurations.testRuntime
            description = "Run Clojure tests in src/test."
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
