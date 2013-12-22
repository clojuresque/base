(ns clojuresque.tasks.doc
  (:import
    java.io.File)
  (:use
    [clojuresque.cli :only (deftask)]
    [clojuresque.codox.main :only (generate-docs)]))

(deftask main
  "Generate the documentation for the specified sources."
  [[destination d "Destination directory."]
   [name        n "Project name."]
   [version     v "Project version."]
   [description D "Project description."]
   [sourcepaths s "Source directories."]
   sources]
  (let [project {:name        name
                 :version     version
                 :description description
                 :output-dir  destination
                 :sources     sources
                 :source-dirs (seq (.split sourcepaths File/pathSeparator))}]
    (generate-docs project)
    true))
