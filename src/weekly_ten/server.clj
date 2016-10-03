(ns weekly-ten.server
  "The server part. Implemented without any third-party libs. All std java and Clojure."
  (:require [clojure.tools.logging :as log])
  (:import java.net.ServerSocket))

(defn thread!
  "Run something in a thread"
  [fun & args]
  (.start (Thread. #(apply fun args))))

(defn handle-request [client-socket handler-fn]
  (log/infof "Processing request from %s with %s" client-socket handler-fn)
  (with-open [client client-socket
              in (clojure.java.io/input-stream (.getInputStream client))
              out (clojure.java.io/output-stream (.getOutputStream client))]
    (handler-fn in out)))

(defn start [handler-fn port]
  (log/infof "Starting server on port %d" port)
  (let [server (ServerSocket. (int port))
        poll-fn (fn [s]
                  (log/info "Starting client handler")
                  (let [client (.accept s)]
                    (log/infof "Client connected as %s" client)
                    (thread! handle-request client handler-fn)
                    (recur s)))]
    (thread! poll-fn server)
    server))

(defn stop [server]
  (when server
    (do
      (log/infof "Closing server %s" server)
      (.close server))))
