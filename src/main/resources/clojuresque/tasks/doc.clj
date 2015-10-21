(ns clojuresque.tasks.doc
  (:use
    [clojuresque.util :only (deftask)]
    [clojuresque.codox.main :only (generate-docs)]))

(deftask main
  [{:keys [project codox destination-dir source-dirs source-files]}]
  (let [project-map (merge project
                           {:output-dir  destination-dir
                            :sources     source-files
                            :source-dirs source-dirs}
                           codox)]
    (generate-docs project-map)
    true))
