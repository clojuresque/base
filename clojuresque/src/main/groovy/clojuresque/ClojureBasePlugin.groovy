/*-
 * Copyright 2009,2010 © Meikel Brandmeyer.
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
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.Upload

import java.io.InputStreamReader
import java.util.Properties

public class ClojureBasePlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.apply plugin: JavaPlugin.class
        project.apply plugin: MavenPlugin.class

        project.convention.plugins.clojure =
            new ClojurePluginConvention(project)

        RepositoryHandler repos = project.repositories
        repos.convention.plugins.clojure =
            new ClojureRepositoryConvention(repos)

        configureConfigurations(project)
        configureSourceSets(project)
        configureCompilation(project)
        configureTests(project)
        configureClojarsUpload(project)
    }

    private Properties getProperties() {
        Properties props = new Properties()
        InputStreamReader propStream =
            new InputStreamReader(this.class.getResourceAsStream("clojuresque.properties"), "UTF-8")

        try {
            props.load(propStream)
        } finally {
            propStream.close()
        }

        return props
    }

    private void configureConfigurations(Project project) {
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
            clojuresque group: "clojuresque", name: "runtime",
                version: this.properties.getProperty("clojuresque.version")
        }
    }

    private void configureSourceSets(Project project) {
        ProjectInternal projectInternal = (ProjectInternal)project

        project.sourceSets.each { sourceSet ->
            ClojureSourceSet clojureSourceSet =
                new ClojureSourceSet(sourceSet.name, projectInternal.fileResolver)

            sourceSet.convention.plugins.clojure = clojureSourceSet
            sourceSet.clojure.srcDirs = [ String.format("src/%s/clojure", sourceSet.name) ]
            sourceSet.resources.filter.exclude("**/*.clj")
            sourceSet.allSource.source(clojureSourceSet.clojure)
        }
    }

    private void configureCompilation(Project project) {
        project.sourceSets.each { set ->
            if (set.equals(project.sourceSets.test))
                return
            String compileTaskName = set.getCompileTaskName("clojure")
            ClojureCompileTask task = project.tasks.add(name: compileTaskName,
                    type: ClojureCompileTask.class) {
                destinationDir = set.output.classesDir
                source set.clojure
                clojureRoots = set.clojure
                compileClasspath = set.compileClasspath
                dependsOn set.compileClasspath, project.configurations.development
                description =
                    String.format("Compile the %s Clojure source.",
                            set.name)
            }
            project.tasks[set.classesTaskName].dependsOn task
            set.compileClasspath = project.files(
                set.compileClasspath,
                project.configurations.development
            )
        }
    }

    private void configureTests(Project project) {
        ClojureTestTask clojureTest = project.tasks.add(name: "clojureTest",
                type: ClojureTestTask.class) {
            source project.sourceSets.test.clojure
            testRoots = project.sourceSets.test.clojure
            testClasspath = project.configurations.testRuntime
            classesDir = project.sourceSets.main.output.classesDir
            dependsOn project.tasks.classes, project.configurations.testRuntime
            description = "Run Clojure tests in src/test."
            if (project.hasProperty("clojuresque.test.vars")) {
                tests = project.getProperty("clojuresque.test.vars").split(",")
            }
        }
        project.tasks.test.dependsOn clojureTest
    }

    private void configureClojarsUpload(Project project) {
        project.tasks.whenTaskAdded { upload ->
            if (!(upload instanceof Upload))
                return
            upload.convention.plugins.clojure =
                new ClojureUploadConvention(upload)
        }
    }
}
