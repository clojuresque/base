/*-
 * Copyright 2009 © Meikel Brandmeyer.
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

package de.kotka.gradle

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.ConventionMapping
import org.gradle.api.internal.IConventionAware
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.internal.tasks.DefaultSourceSet
import org.gradle.api.tasks.SourceSet
import org.gradle.api.plugins.Convention
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.ProjectPluginsContainer
import org.gradle.api.tasks.ConventionValue

public class ClojurePlugin implements Plugin {
    public void use(Project project, ProjectPluginsContainer handler) {
        JavaPlugin javaPlugin = handler.usePlugin(JavaPlugin.class, project)
        configureCompileDefaults(project)
        configureSourceSetDefaults(project, javaPlugin)
    }

    private JavaPluginConvention javaConvention(Convention convention) {
        return convetion.getPlugin(JavaPluginConvention.class)
    }

    private SourceSet mainSourceSet(Convention convention) {
        return javaConvention(convention)
            .sourceSets[SourceSet.MAIN_SOURCE_SET_NAME]
    }

    private ClojureSourceSet mainClojureSourceSet(Convention convention) {
        return mainSourceSet(convention)
            .convention.getPlugin(ClojureSourceSet.class)
    }

    private configureCompileDefaults(Project project) {
        def dsValue = [ getValue: { Convention convention,
                IConventionAware object ->
            return mainClojureSourceSet(convention).clojure
        } ] as ConventionValue

        def action = [ execute: { ClojureCompileTask task ->
            task.conventionMapping.map("defaultSource", dsValue)
        } ] as Action

        project.tasks.withType(ClojureCompileTask.class).allTasks(action)
    }

    private configureSourceSet(DefaultSourceSet sourceSet,
            Project project, JavaPlugin javaPlugin) {
        ProjectInternal projectInternal = (ProjectInternal)project
        String srcDir = String.format("src/%s/clojure", sourceSet.name)

        DefaultClojureSourceSet clojureSourceSet =
            new DefaultClojureSourceSet(sourceSet.displayName,
                projectInternal.fileResolver)
        sourceSet.convention.plugins.put("clojure", clojureSourceSet)
        clojureSourceSet.clojure.srcDir(srcDir)
        sourceSet.resources.filter.exclude("**/*.clj")
        sourceSet.allSource.add(clojureSourceSet.clojure)

        String compileTaskName = sourceSet.getCompileTaskName("clojure")
        ClojureCompileTask task = project.tasks.add(compileTaskName,
                ClojureCompileTask.class)
        task.sourceSet = clojureSourceSet
        task.dependsOn(sourceSet.getCompileTaskName("java"))
        task.setDescription(String.format("Compile the %s Clojure source.",
                sourceSet.name))

        ConventionMapping convMap = task.conventionMapping

        def dsValue = [ getValue: { Convention c, IConventionAware o ->
            return clojureSourceSet.clojure
        } ] as ConventionValue
        convMap.map("defaultSource", dsValue)

        def cpValue = [ getValue: { Convention c, IConventionAware o ->
            return sourceSet.compileClasspath
        } ] as ConventionValue
        convMap.map("classpath", cpValue)

        def ddValue = [ getValue: { Convention c, IConventionAware o ->
            return sourceSet.classesDir
        } ] as ConventionValue
        convMap.map("destinationDir", ddValue)

        project.tasks[sourceSet.classesTaskName].dependsOn(compileTaskName)
    }

    private void configureSourceSetDefaults(Project project,
            JavaPlugin javaPlugin) {
        def action = [ execute: { SourceSet sourceSet ->
            configureSourceSet(sourceSet, project, javaPlugin)
        } ] as Action

        project.convention.getPlugin(JavaPluginConvention.class)
            .sourceSets.allObjects(action)
    }
}

