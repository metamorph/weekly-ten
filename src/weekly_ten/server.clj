(ns weekly-ten.server
  "The server part. Implemented without any third-party libs. All std java and Clojure."
  (:import (java.net ServerSocket)))

(defn array->string "Takes a char-array and returns it as a string"
  ([arr] (array->string arr (count arr)))
  ([arr n] (apply str (take n (seq arr)))))

(defn thread!
  "Run something in a thread"
  [fun & args]
  (.start (Thread. #(apply fun args))))

(defn dummy [in out]
  (let [buf (make-array Character/TYPE 4)
        n-read (.read in buf)
        data (array->string buf n-read)]
    (.write out (str "Hello " data))
    (.flush out)))

(defn handle-request [client-socket handler-fn]
  (with-open [client client-socket
              in (clojure.java.io/reader (.getInputStream client))
              out (clojure.java.io/writer (.getOutputStream client))]
    (handler-fn in out)))

(defn run [port]
  (with-open [server (ServerSocket. (int port))]
    (loop [server server]
      (let [client (.accept server)]
        (thread! handle-request client dummy)
        (recur server)))))



