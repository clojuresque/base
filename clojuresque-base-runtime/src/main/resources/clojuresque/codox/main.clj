(ns clojuresque.codox.main
  "Main namespace for generating documentation"
  (:use [clojuresque.codox.utils :only (ns-filter add-source-paths)]
        [clojuresque.codox.reader :only (read-namespaces)])
  (:require [clojuresque.util :as util]))

(defn- writer [{:keys [writer]}]
  (let [writer-sym (or writer 'clojuresque.codox.writer.html/write-docs)
        writer-ns (symbol (namespace writer-sym))]
    (try
      (require writer-ns)
      (catch Exception e
        (throw
         (Exception. (str "Could not load codox writer " writer-ns) e))))
    (if-let [writer (resolve writer-sym)]
      writer
      (throw
         (Exception. (str "Could not resolve codox writer " writer-sym))))))

(defn generate-docs
  "Generate documentation from source files."
  ([]
     (generate-docs {}))
  ([{:keys [sources source-dirs include exclude writer] :as options
     :or {writer "clojuresque.codox.writer.html/write-docs"}}]
     (let [namespaces (-> (apply read-namespaces sources)
                          #_(ns-filter include exclude)
                          (add-source-paths source-dirs))
          #_ write (writer options)
          write (util/resolve-require writer)]
       (write (assoc options :namespaces namespaces)))))
