(ns clojuresque.tasks.compile)

(defn find-namespace
  [file]
  (let [of-interest '#{ns clojure.core/ns}
        eof         (Object.)
        input       (clojure.lang.LineNumberingPushbackReader.
                      (java.io.FileReader. file))
        in-seq      (take-while #(not (identical? % eof))
                                (repeatedly #(read input false eof)))
        candidate   (first
                      (drop-while
                        #(or (not (instance? clojure.lang.ISeq %))
                             (not (contains? of-interest (first %))))
                        in-seq))]
    (when candidate
      (second candidate))))

(defn main
  [args]
  (let [[mode args] (if (= (first args) "--compile")
                      [compile (next args)]
                      [require args])
        [warn args] (if (= (first args) "--warn-on-reflection")
                      [true  (next args)]
                      [false args])
        namespaces  (map find-namespace args)]
    (binding [*warn-on-reflection* warn
              *compile-path*       (System/getProperty "clojure.compile.path")]
      (doseq [nspace namespaces :when nspace]
        (mode nspace)))))
