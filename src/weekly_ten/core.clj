(ns weekly-ten.core
  (:gen-class)
  (:require [weekly-ten.server :as server]))

(defn int->word4
  "Convert an integer to a 4-byte word."
  [i]
  (-> (java.nio.ByteBuffer/allocate Integer/BYTES)
      (.putInt i)
      (.array)
      (bytes)))

(defn bytes->string "Convert bytes to string" [bytes] (apply str (map char bytes)))

(defn string->bytes "Converts string to bytes" [s] (byte-array (map byte s)))

(defn word4->int [w]
  "Converts a 4-byte word to an integer"
  (-> (java.nio.ByteBuffer/allocate Integer/BYTES)
      (.put (bytes w))
      (.flip)
      (.getInt)))

(defn read-bytes [input-stream len]
  (let [buf (byte-array len)
        _ (.read input-stream buf)]
    buf))

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
  [& args]
  (if (= (first args) "server")
    (weekly-ten.server/start simple-protocol-handler 9999)
    (throw (Exception. "Noop"))))
