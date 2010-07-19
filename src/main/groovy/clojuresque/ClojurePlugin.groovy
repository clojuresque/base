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
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.Upload
import org.gradle.api.tasks.bundling.Jar

public class ClojurePlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.apply plugin: JavaPlugin.class
        project.apply plugin: MavenPlugin.class

        project.convention.plugins.clojure = new ClojurePluginConvention()

        RepositoryHandler repos = project.repositories
        repos.convention.plugins.clojure =
            new ClojureRepositoryConvention(repos)

        configureSourceSets(project)
        configureCompilation(project)
        configureConfigurations(project)
        configureUberjar(project)
        configureDepsTask(project)
        configureClojarsUpload(project)
    }

    private void configureSourceSets(Project project) {
        project.sourceSets.each { sourceSet ->
            String srcDir = String.format("src/%s/clojure", sourceSet.name)

            ClojureSourceSetConvention clojureSourceSetConvention =
                new ClojureSourceSetConvention(project, srcDir)

            sourceSet.convention.plugins.clojure = clojureSourceSetConvention
            sourceSet.resources.filter.exclude("**/*.clj")
            sourceSet.allSource.plus(clojureSourceSetConvention.clojure)
        }
    }

    private void configureCompilation(Project project) {
        project.sourceSets.each { set ->
            String compileTaskName = set.getCompileTaskName("clojure")
            ClojureCompileTask task = project.tasks.add(name: compileTaskName,
                    type: ClojureCompileTask.class) {
                destinationDir = set.classesDir
                source set.clojure
                inputRoot set.clojure.sourceDir
                compileClasspath = set.compileClasspath
                dependsOn set.compileClasspath
                description =
                    String.format("Compile the %s Clojure source.",
                            set.name)
            }
            project.tasks[set.classesTaskName].dependsOn task
        }
    }

    private void configureConfigurations(Project project) {
        project.configurations {
            clojuresque {
                transitive = false
                visible = false
                description = "Deprecated: Please use the development configuration"
            }
            development {
                transitive = false
                visible = false
                description = "Development only dependencies"
                extendsFrom clojuresque
            }
            compile.extendsFrom development
        }
    }

    private void configureUberjar(Project project) {
        project.tasks.withType(Jar.class).each { jar ->
            project.tasks.add(name: "uber" + jar.name, type: Jar.class) {
                description =
                    'Constructs a jar with all runtime dependencies included'
                dependsOn jar.source, project.configurations.runtime
                baseName = jar.baseName + "-standalone"
                enabled = false
                doFirst {
                    project.configurations.runtime.each {
                        from project.zipTree(it)
                    }
                    from jar.source
                }
            }
        }
    }

    private void configureDepsTask(Project project) {
        Copy deps = project.tasks.add("deps", Copy.class)

        deps.configure {
            description =
                'Copy runtime dependencies into the build/lib directory'
            into 'lib'
            from project.configurations.testRuntime
        }
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
