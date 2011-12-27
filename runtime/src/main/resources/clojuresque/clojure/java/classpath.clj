;;; classpath.clj: utilities for working with the Java class path

;; by Stuart Sierra, http://stuartsierra.com/
;; April 19, 2009

;; Copyright (c) Rich Hickey, Stuart Sierra, and contributors.
;; All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.


(ns
  ^{:author "Stuart Sierra"
     :doc "Utilities for dealing with the JVM's classpath"}
  clojuresque.clojure.java.classpath
  (:require [clojure.java.io :as io])
  (:import (java.io File)
           (java.util.jar JarFile JarEntry)
           (java.net URL URLClassLoader)))

(defn jar-file?
  "Returns true if file is a normal file with a .jar or .JAR extension."
  [f]
  (let [file (io/file f)]
    (and (.isFile file)
         (or (.endsWith (.getName file) ".jar")
             (.endsWith (.getName file) ".JAR")))))

(defn filenames-in-jar
  "Returns a sequence of Strings naming the non-directory entries in
  the JAR file."
  [^JarFile jar-file]
  (map #(.getName ^JarEntry %)
       (filter #(not (.isDirectory ^JarEntry %))
               (enumeration-seq (.entries jar-file)))))

(defn system-classpath
  "Returns a sequence of File paths from the 'java.class.path' system
  property."
  []
  (map #(File. ^String %)
       (.split (System/getProperty "java.class.path")
               (System/getProperty "path.separator"))))

(defn loader-classpath
  "Returns a sequence of File paths from a classloader."
  [loader]
  (when (instance? java.net.URLClassLoader loader)
    (map
     #(java.io.File. (.getPath ^java.net.URL %))
     (.getURLs ^java.net.URLClassLoader loader))))

(defn classpath
  "Returns a sequence of File objects of the elements on the classpath."
  ([classloader]
     (distinct
      (mapcat
       loader-classpath
       (take-while
        identity
        (iterate #(.getParent %) classloader)))))
  ([] (classpath (clojure.lang.RT/baseLoader))))

(defn classpath-directories
  "Returns a sequence of File objects for the directories on classpath."
  []
  (filter #(.isDirectory ^File %) (classpath)))

(defn classpath-jarfiles
  "Returns a sequence of JarFile objects for the JAR files on classpath."
  []
  (map #(JarFile. ^File %) (filter jar-file? (classpath))))
