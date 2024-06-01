(ns clojure-grpc-server.core
  (:gen-class)
  (:require [clojure.string :as str])
  (:import (clojure.lang IDeref)
           (com.andreyfadeev.api.v1 AddRequest AddResponse AddServiceGrpc AddServiceGrpc$AddServiceImplBase HelloRequest HelloResponse HelloServiceGrpc HelloServiceGrpc$HelloServiceImplBase)
           (io.grpc BindableService ManagedChannel ManagedChannelBuilder Server ServerBuilder)
           (io.grpc.stub StreamObserver)
           (java.io Closeable)))

;; README

;; Source of truth for API is proto folder, a gRPC service is defined in `hello_service.proto`
;; By running `buf generate` it will use Protobuf compiler to generate Java classes (or other languages if specified)
;; `buf lint` could be used to detect naming issues, etc.

;; The most important feature is to detect breaking changes with `buf breaking ...`, more about options: https://buf.build/docs/reference/inputs

;; All those Buf CLI steps should be part of checks in CI, so we protected against breaking changes merges.
;; If API is under development there is a concept unstable API that allow all changes.

;; Other benefit is that same code will be used in clients for gRPC requests, but also could be used in tests.
;; No more duplicating and guessing API format in the tests.

;; Try by running `lein run`, it will start gRPC server on 9001 and do a delayed call to Hello RPC

(defn call-grpc-hello
  [{:keys [grpc-channel]}]
  (let [stub (HelloServiceGrpc/newBlockingStub grpc-channel)
        ;; Build request object (Buf generated class from .proto schemas)
        request (-> (HelloRequest/newBuilder)
                    (.setFirstName "Andrey")
                    (.setLastName "Fadeev")
                    (.build))]
    (println "Calling hello RPC")
    ;; Execute gRPC
    (let [response (.hello stub request)]
      (println "Got response, the greeting:" (.getGreeting response))
      (.getGreeting response))))

(defn create-grpc-channel
  [{:keys [host port]}]
  (-> (ManagedChannelBuilder/forAddress host port)
      (.usePlaintext)
      (.build)))

(defn call-grpc-add
  [{:keys [grpc-channel]}]
  (let [stub (AddServiceGrpc/newBlockingStub grpc-channel)
        ;; Build request object (Buf generated class from .proto schemas)
        request (->(AddRequest/newBuilder)
                   (.setX 1)
                   (.setY 2)
                   (.build))]
    (println "Calling hello RPC")
    ;; Execute gRPC
    (let [response (.add stub request)]
      (println "Sum of 1 and 2 is" (.getSum response)))))

;; gRPC Server implementation: https://github.com/otwieracz/clj-grpc/blob/master/src/clj/clj_grpc/server.clj

(defn build-add-service
  ^BindableService [{:keys [grpc-channel]}]
  (proxy
    [AddServiceGrpc$AddServiceImplBase]
    []
    (add [^AddRequest request ^StreamObserver response-observer]
      (println "Handling request /add in gRPC server" request)
      (let [response (-> (AddResponse/newBuilder)
                         (.setSum (+ (.getX request) (.getY request)))
                         (.build))]
        (.onNext response-observer response)
        (.onCompleted response-observer)))))

(defn build-hello-service
  ^BindableService [{:keys [grpc-channel] :as deps}]
  (proxy
    [HelloServiceGrpc$HelloServiceImplBase]
    []
    (hello [^HelloRequest request ^StreamObserver response-observer]
      (call-grpc-add deps)
      ;; This is where we can start writing plain Clojure code
      ;; we can get all data from Buf generated classes for request, e.g. HelloRequest
      (println "Handling request /hello in gRPC server" request)
      (let [greeting (str/join " " ["Hello" (.getFirstName request) (.getLastName request)])
            ;; Finally we need to convert Clojure data to generated response class, e.g. HelloResponse
            response (-> (HelloResponse/newBuilder)
                         (.setGreeting greeting)
                         (.build))]
        (.onNext response-observer response)
        (.onCompleted response-observer)))))

(defn create-grpc-server
  [{:keys [hello-service
           add-service]}]
  (let [^Server grpc-server (-> (ServerBuilder/forPort 9001)
                                ;; Multiple gRPC services could be added here, allowing modular code
                                (.addService ^BindableService hello-service)
                                (.addService ^BindableService add-service)
                                (.build))]
    (.start grpc-server)
    grpc-server))

(defn closeable
  ([value] (closeable value identity))
  ([value close] (reify
                   IDeref
                   (deref [_] value)
                   Closeable
                   (close [_] (close value)))))

(defn run-system
  []
  (with-open [grpc-channel (closeable
                             (create-grpc-channel {:host "localhost"
                                                   :port 9001})
                             (fn [^ManagedChannel channel]
                               (.shutdown channel)))
              hello-service (closeable
                              (build-hello-service {:grpc-channel @grpc-channel}))
              add-service (closeable
                            (build-add-service {:grpc-channel @grpc-channel}))
              grpc-server (closeable
                            (create-grpc-server
                              {:hello-service @hello-service
                               :add-service @add-service})
                            (fn [grpc-server]
                              (.shutdown grpc-server)))]
    (println "System started")

    ;; Delay call to the gRPC server
    (future
      (Thread/sleep 5000)
      (call-grpc-hello {:grpc-channel @grpc-channel}))

    (.awaitTermination @grpc-server)))

(defn -main
  []
  (run-system))
