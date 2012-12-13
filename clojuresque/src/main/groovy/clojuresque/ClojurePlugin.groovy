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
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar

public class ClojurePlugin implements Plugin<Project> {
    public void apply(Project project) {
        project.apply plugin: ClojureBasePlugin.class

        configureUberjar(project)
        configureDepsTask(project)
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
                        exclude 'META-INF/MANIFEST.MF'
                        exclude 'META-INF/*.SF'
                        exclude 'META-INF/*.DSA'
                        exclude 'META-INF/*.RSA'
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
            from project.configurations.development
        }
    }
}
