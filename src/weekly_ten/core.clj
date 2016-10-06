(ns weekly-ten.core
  (:gen-class)
  (:require
   [weekly-ten.client :as client]
   [weekly-ten.server :as server]
   [weekly-ten.utils :refer :all]))


(defn simple-query-handler
  "Answer a query"
  [query]
  (apply str (reverse query)))

(defn simple-protocol-handler
  "Implement the simple protocol:
  - When a connection is received, it must send six (6) bytes to the client: “ready\n”.
  - It must read a 4-byte word from the client, indicating the length of the query to read.
  - It must read that many bytes as the query, and then perform the requested query (see above).
  - It must send a 4-byte word to the client, indicating the length of the response.
  - It must send the response."
  [in out]

  (.write out (string->bytes "ready\n"))
  (let [query-len (word4->int (read-bytes in 4))
        query     (bytes->string (read-bytes in query-len))
        answer    (string->bytes (simple-query-handler query))]
    (.write out (int->word4 (count answer)))
    (.write out answer)))

(defn -main
  "Main application entry point"
  [mode & args]
  (case mode

    "--server" (let [port (Integer/parseInt (first args))]
                 (printf "Starting Server on %d" port)
                 (server/start-server simple-protocol-handler port))

    "--client" (let [[host port query] args]
                 (printf "Querying server %s:%s : %s => \n " host port query)
                 (println (client/query-server host (Integer/parseInt port) query)))

    :else (throw (IllegalArgumentException. "Not a valid option"))))
