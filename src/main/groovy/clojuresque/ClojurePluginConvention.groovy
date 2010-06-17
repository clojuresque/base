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

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Upload

class ClojurePluginConvention {
    Project project
    boolean warnOnReflection
    boolean aotCompile

    private boolean didConfigureDeployerJars = false

    public ClojurePluginConvention(Project project) {
        this.project = project
        this.warnOnReflection = false
        this.aotCompile = false
    }

    public void configureClojarsDeploy(Upload task) {
        File dummyRepo = new File(project.buildDir, 'deploy')

        project.configure(task) {
            repositories.mavenDeployer {
                name = 'dummyClojarsLocalDeployer'
                description  = 'Dummy deployer to trick gradle into pom generation'
                repository(url: 'file://' + dummyRepo.path)
            }
        }

        task.doLast {
            /* Clojars. :( This was changed in the source by Alex based
             * on a note I sent him. This was before christmas 2009. Now,
             * end of January 2010 it's still the old version live. */
            project.fileTree(dir: dummyRepo, includes: ['**/*.pom']).each {
                String name = it.path
                String basename = name.substring(0, name.length() - 4)
                String newname = basename + '.xml'

                project.ant.move(file: it, tofile: project.file(newname))
            }

            Map args = [
                executable:  '/usr/bin/scp',
                failOnError: true
            ]

            project.ant.exec(args) {
                project.fileTree(dummyRepo).each { arg value: it }
                arg value: 'clojars@clojars.org:'
            }
        }
    }

    public boolean getWarnOnReflection() {
        return this.warnOnReflection
    }

    public void setWarnOnReflection(boolean flag) {
        this.warnOnReflection = flag
    }

    public boolean getAotCompile() {
        return this.aotCompile
    }

    public void setAotCompile(boolean flag) {
        this.aotCompile = flag
    }
}
