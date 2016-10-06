(ns weekly-ten.client
  (:require [weekly-ten.utils :refer :all]))


(defn query-client
  "Given input/output streams - send a query to the client and return the response as a string."
  [in out query]
  (let [ready       (bytes->string (read-bytes in 6))
        bytes-query (string->bytes query)]
    (.write out (int->word4 (count bytes-query)))
    (.write out bytes-query)
    (let [answer-len   (word4->int (read-bytes in 4))
          answer-bytes (read-bytes in answer-len)]
      (bytes->string answer-bytes))))

(defn query-server
  "Send a query to a server over the 'simple' protocol."
  [host port query]
  (with-open [socket (java.net.Socket. (java.net.InetAddress/getByName host) (int port))
              in     (.getInputStream socket)
              out    (.getOutputStream socket)]
    (query-client in out query)))

