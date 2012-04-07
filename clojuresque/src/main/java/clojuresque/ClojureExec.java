/*
 * Copyright 2009,2010 © Meikel Brandmeyer.
 * All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package clojuresque;

import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.file.FileResolver;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.JavaExecSpec;
import org.gradle.process.JavaForkOptions;
import org.gradle.process.ProcessForkOptions;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ClojureExec extends ConventionTask implements JavaExecSpec {
    private ClojureExecAction clojureExecAction;

    public ClojureExec() {
        super();

        FileCollection driverConf = getProject().getConfigurations().getByName("clojuresque");
        FileResolver fileResolver = ((ProjectInternal)getProject()).getFileResolver();
        clojureExecAction = new ClojureExecAction(fileResolver, driverConf);
    }

    @TaskAction
    void exec() {
        clojureExecAction.execute();
    }

    public List<String> getAllJvmArgs() {
        return clojureExecAction.getAllJvmArgs();
    }

    public void setAllJvmArgs(Iterable<?> arguments) {
        clojureExecAction.setAllJvmArgs(arguments);
    }

    public List<String> getJvmArgs() {
        return clojureExecAction.getJvmArgs();
    }

    public void setJvmArgs(Iterable<?> arguments) {
        clojureExecAction.setJvmArgs(arguments);
    }

    public ClojureExec jvmArgs(Iterable<?> arguments) {
        clojureExecAction.jvmArgs(arguments);
        return this;
    }

    public ClojureExec jvmArgs(Object... arguments) {
        clojureExecAction.jvmArgs(arguments);
        return this;
    }

    public Map<String, Object> getSystemProperties() {
        return clojureExecAction.getSystemProperties();
    }

    public void setSystemProperties(Map<String, ?> properties) {
        clojureExecAction.setSystemProperties(properties);
    }

    public ClojureExec systemProperties(Map<String, ?> properties) {
        clojureExecAction.systemProperties(properties);
        return this;
    }

    public ClojureExec systemProperty(String name, Object value) {
        clojureExecAction.systemProperty(name, value);
        return this;
    }

    public FileCollection getBootstrapClasspath() {
        return clojureExecAction.getBootstrapClasspath();
    }

    public void setBootstrapClasspath(FileCollection classpath) {
        clojureExecAction.setBootstrapClasspath(classpath);
    }

    public ClojureExec bootstrapClasspath(Object... classpath) {
        clojureExecAction.bootstrapClasspath(classpath);
        return this;
    }

    public String getMaxHeapSize() {
        return clojureExecAction.getMaxHeapSize();
    }

    public void setMaxHeapSize(String heapSize) {
        clojureExecAction.setMaxHeapSize(heapSize);
    }

    public boolean getEnableAssertions() {
        return clojureExecAction.getEnableAssertions();
    }

    public void setEnableAssertions(boolean enabled) {
        clojureExecAction.setEnableAssertions(enabled);
    }

    public boolean getDebug() {
        return clojureExecAction.getDebug();
    }

    public void setDebug(boolean enabled) {
        clojureExecAction.setDebug(enabled);
    }

    public String getMain() {
        return clojureExecAction.getMain();
    }

    public ClojureExec setMain(String mainClassName) {
        clojureExecAction.setMain(mainClassName);
        return this;
    }

    public List<String> getArgs() {
        return clojureExecAction.getArgs();
    }

    public ClojureExec setArgs(Iterable<?> applicationArgs) {
        clojureExecAction.setArgs(applicationArgs);
        return this;
    }

    public ClojureExec args(Object... args) {
        clojureExecAction.args(args);
        return this;
    }

    public JavaExecSpec args(Iterable<?> args) {
        clojureExecAction.args(args);
        return this;
    }

    public ClojureExec setClasspath(FileCollection classpath) {
        clojureExecAction.setClasspath(classpath);
        return this;
    }

    public ClojureExec classpath(Object... paths) {
        clojureExecAction.classpath(paths);
        return this;
    }

    public FileCollection getClasspath() {
        return clojureExecAction.getClasspath();
    }

    public ClojureExec copyTo(JavaForkOptions options) {
        clojureExecAction.copyTo(options);
        return this;
    }

    public String getExecutable() {
        return clojureExecAction.getExecutable();
    }

    public void setExecutable(Object executable) {
        clojureExecAction.setExecutable(executable);
    }

    public ClojureExec executable(Object executable) {
        clojureExecAction.executable(executable);
        return this;
    }

    public File getWorkingDir() {
        return clojureExecAction.getWorkingDir();
    }

    public void setWorkingDir(Object dir) {
        clojureExecAction.setWorkingDir(dir);
    }

    public ClojureExec workingDir(Object dir) {
        clojureExecAction.workingDir(dir);
        return this;
    }

    public Map<String, Object> getEnvironment() {
        return clojureExecAction.getEnvironment();
    }

    public void setEnvironment(Map<String, ?> environmentVariables) {
        clojureExecAction.setEnvironment(environmentVariables);
    }

    public ClojureExec environment(String name, Object value) {
        clojureExecAction.environment(name, value);
        return this;
    }

    public ClojureExec environment(Map<String, ?> environmentVariables) {
        clojureExecAction.environment(environmentVariables);
        return this;
    }

    public ClojureExec copyTo(ProcessForkOptions target) {
        clojureExecAction.copyTo(target);
        return this;
    }

    public ClojureExec setStandardInput(InputStream inputStream) {
        clojureExecAction.setStandardInput(inputStream);
        return this;
    }

    public InputStream getStandardInput() {
        return clojureExecAction.getStandardInput();
    }

    public ClojureExec setStandardOutput(OutputStream outputStream) {
        clojureExecAction.setStandardOutput(outputStream);
        return this;
    }

    public OutputStream getStandardOutput() {
        return clojureExecAction.getStandardOutput();
    }

    public ClojureExec setErrorOutput(OutputStream outputStream) {
        clojureExecAction.setErrorOutput(outputStream);
        return this;
    }

    public OutputStream getErrorOutput() {
        return clojureExecAction.getErrorOutput();
    }

    public JavaExecSpec setIgnoreExitValue(boolean ignoreExitValue) {
        clojureExecAction.setIgnoreExitValue(ignoreExitValue);
        return this;
    }

    public boolean isIgnoreExitValue() {
        return clojureExecAction.isIgnoreExitValue();
    }

    public List<String> getCommandLine() {
        return clojureExecAction.getCommandLine();
    }
}
