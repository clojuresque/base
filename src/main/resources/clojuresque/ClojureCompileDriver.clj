(in-ns 'user)

(let [find-namespace
      (fn [file]
        (let [of-interest '#{ns clojure.core/ns}
              eof         (Object.)
              input       (clojure.lang.LineNumberingPushbackReader.
                            (java.io.FileReader. file))
              in-seq      (take-while #(not (identical? %% eof))
                                      (repeatedly #(read input false eof)))
              candidate   (first
                            (drop-while
                              #(or (not (instance? clojure.lang.ISeq %%))
                                   (not (contains? of-interest (first %%))))
                              in-seq))]
          (when candidate
            (second candidate))))
      namespaces
      (set (map find-namespace [%1$s]))]
  (doseq [nspace namespaces :when nspace]
    (%2$s nspace)))
