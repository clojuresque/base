(ns clojuresque.tasks.doc
  (:use
    [clojuresque.cli :only (deftask)]
    [clojuresque.codox.main :only (generate-docs)]))

(deftask main
  "Generate the documentation for the specified sources."
  [[destination d "Destination directory."]
   [name        n "Project name."]
   [version     v "Project version."]
   [description D "Project description."]
   sources]
  (let [project {:name        name
                 :version     version
                 :description description
                 :output-dir  destination
                 :sources     sources}]
    (generate-docs project)))
