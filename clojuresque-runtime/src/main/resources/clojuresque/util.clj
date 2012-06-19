(ns clojuresque.util)

(defn namespace-of-file
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

(defn namespaces
  [files]
  (distinct (keep namespace-of-file files)))
