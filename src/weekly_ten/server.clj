(ns weekly-ten.server
  "The server part. Implemented without any third-party libs. All std java and Clojure."
  (:import (java.net ServerSocket)))

(defn run [port]
  (with-open [server (ServerSocket. (int port))
              client (.accept server)
              out (clojure.java.io/writer (.getOutputStream client))
              in (clojure.java.io/reader (.getInputStream client))]
    (do
      (.write out "Hello")
      (.flush out))))

