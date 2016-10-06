(ns weekly-ten.server
  "The server part. Implemented without any third-party libs. All std java and Clojure."
  (:require [clojure.tools.logging :as log])
  (:import java.net.ServerSocket))

(defn handle-client
  "Opens (and closes) streams to the client and delegates to the 'handler-fn'."
  [handler-fn client]
  (log/infof "Processing client connection %s with %s" client handler-fn)
  (with-open [client client
              in     (.getInputStream client)
              out    (.getOutputStream client)]
    (handler-fn in out)))

(defn accept-clients
  "Loop that will accept client connections and process them using 'handler-fn' in another thread."
  [handler-fn server]
  (let [client (.accept server)]
    (log/infof "Client connected : %s" client)
    (future (handle-client handler-fn client))
    (recur handler-fn server)))

(defn start-server
  "Starts a server that will delegate to the 'handler-fn' when a client connects.
  Returns a server that can be closed with '.close'"
  [handler-fn port]
  (let [server (ServerSocket. port)]
    (log/infof "Starting %s on port %d" handler-fn port)
    (future ;; Wait for connections in a separate thread
      (accept-clients handler-fn server))
    server))

(defn run-server
  "Start a server that will serve *one* request and then die."
  [handler-fn port]
  (with-open [server (ServerSocket. (int port))
              client (.accept server)
              in     (.getInputStream client)
              out    (.getOutputStream client)]
    (log/infof "Processing request from client %s" client)
    (handler-fn in out)
    (log/info "Request handled!")
    :done))

