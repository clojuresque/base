(ns clojuresque.tasks.repl
  (:require
    [clojuresque.cli :as cli]
    [clojuresque.util :as util]
    [clojure.tools.nrepl.server :as repl]))

(def barrier (promise))
(def server nil)

(cli/deftask start-repl
  "Start a nrepl server on the given port. An optional handler
  may be given as fully-qualified symbol. The repl server may
  be stopped via clojuresque.tasks.repl/stop-server."
  [[port     p "server port"]
   [handler  h "handler function" nil]]
  (let [p (Long/parseLong port)
        h (if handler
            (let [custom-handler (util/resolve-required handler)]
              (when-not custom-handler
                (throw (Exception. (str "Unknown handler: " handler))))
              (custom-handler))
            (repl/default-handler))
        s (repl/start-server :port p :handler h)]
    (println "Repl started on port" port)
    (alter-var-root #'server (constantly s)))
  @barrier)

(defn stop-repl
  []
  (when server
    (repl/stop-server server)
    (deliver barrier true)))
