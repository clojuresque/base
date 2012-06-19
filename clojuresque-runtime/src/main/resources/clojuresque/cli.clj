(ns clojuresque.cli)

(defn- str-cut
  [s n]
  (subs s 0 (- (count s) n)))

(defn- print-usage
  "Print usage information for the given option spec."
  [description specs]
  (println description)
  (newline)
  (println "Options:")
  (doseq [spec (filter vector? specs)]
    (let [loption (name (first spec))
          spec    (rest spec)
          soption (when (symbol? (first spec)) (name (first spec)))
          spec    (if soption (rest spec) spec)
          descr   (first spec)
          default (first (rest spec))]
      (print (format "  --%-10s " loption))
      (when soption
        (print (format "-%-3s " soption)))
      (print descr)
      (when default
        (newline)
        (print (format "                    The default is '%s'." default))))
    (newline))
  (flush))

(defn with-command-line*
  "Parse the command line arguments according to the given specifications.
  A specification consists of a vector of an option name, an optional short
  option name, a description and an optional default value. An option name
  ending in ? designates a boolean flag. The last entry in the list of
  specifications might be a symbol which is bound to the rest of the command
  line arguments when -- or the first non-option argument is encountered.

  -h, --help or -? stop the parsing and trigger the printing of the usage
  message and thunk is not called.

  thunk is called with a map of option-value pairs found on the command line."
  [args description specs thunk]
  (let [[options soptions]
        (reduce (fn [[opts sopts] spec]
                  (let [lopt  (name (first spec))
                        sopt  (second spec)
                        sopt  (if (symbol? sopt) (name sopt) nil)
                        [lopt sopt type]
                        (if (.endsWith lopt "?")
                          [(str-cut lopt 1) sopt :flag]
                          [lopt             sopt :option])]
                    (vector (assoc opts lopt type)
                            (assoc sopts sopt lopt))))
                [{} {}]
                (filter vector? specs))
        rest-arg (when (symbol? (last specs)) (name (last specs)))]
    (loop [args   (seq args)
           argmap (hash-map)]
      (let [arg (first args)]
        (cond
          (nil? args)
          (if-not rest-arg
            (thunk argmap)
            (throw (Exception. "Missing command line arguments")))

          (some #{arg} ["-h" "--help" "-?"])
          (print-usage description specs)

          (= arg "--")
          (if rest-arg
            (thunk (assoc argmap rest-arg (rest args)))
            (throw (Exception.  "Unexpected command line arguments")))

          (.startsWith arg "--")
          (let [option (subs arg 2)]
            (condp = (get options option)
              :flag   (recur (next args) (assoc argmap option true))
              :option (if-let [value (second args)]
                        (recur (nthnext args 2) (assoc argmap option value))
                        (throw
                          (Exception.  (str "Missing value for option: " arg))))
              nil     (throw (Exception. (str "Unknown option: " option)))))

          (.startsWith arg "-")
          (let [option (subs arg 1)]
            (if-let [loption (get soptions option)]
              (recur (cons (str "--" loption) (rest args)) argmap)
              (throw (Exception. (str "Unknown option: " option)))))

          :else
          (if rest-arg
            (thunk (assoc argmap rest-arg args))
            (throw (Exception.  "Unexpected command line arguments"))))))))

(defmacro with-command-line
  "Parses the command line arguments given according to the specifications.
  A specification consists of a vector of an option name, an optional short
  option name, a description and an optional default value. An option name
  ending in ? designates a boolean flag. The last entry in the list of
  specifications might be a symbol which is bound to the rest of the command
  line arguments when -- or the first non-option argument is encountered.

  -h, --help or -? stop the parsing and trigger the printing of the usage
  message and body is not executed.

  The body is executed with the long option names bound to the value found
  on the command line or the default value if the option was not given.
  Flags default to nil, ie. logical false."
  [args description specs & body]
  (let [defaults (map (fn [spec]
                        (cond
                          (not (vector? spec)) [spec nil]

                          (-> spec first name (.endsWith "?"))
                          (vector (-> spec first name (str-cut 1) symbol) false)

                          (-> spec second symbol?)
                          (vector (first spec) (when (= (count spec) 4)
                                                 (nth spec 3)))

                          :else
                          (vector (first spec) (when (= (count spec) 3)
                                                         (nth spec 2)))))
                      specs)]
    `(with-command-line* ~args
       ~description
       (quote ~specs)
       (fn [{:strs ~(vec (map first defaults))
             :or   ~(into {} defaults)}]
         ~@body))))

(defmacro deftask
  "Defines a new task which is callable from clojuresque via the runtime.
  It adds command line option parsing around a function. So a „task“ is
  also callable as a normal function with a sequence of options."
  [name description options & body]
  `(defn ~name
     ~description
     [& args#]
     (with-command-line args#
       ~description
       ~options
       ~@body)))
