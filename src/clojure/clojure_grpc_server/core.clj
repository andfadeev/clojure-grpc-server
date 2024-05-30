(ns clojure-grpc-server.core
  (:gen-class)
  (:require [clojure.string :as str])
  (:import (com.kroo.api.v1 HelloRequest HelloResponse HelloServiceGrpc HelloServiceGrpc$HelloServiceImplBase)
           (io.grpc ManagedChannelBuilder Server ServerBuilder)
           (io.grpc.stub StreamObserver)))

;; README:
;; Source of truth for API is proto folder, a gRPC service is defined in `hello_service.proto`
;; By running `buf generate` it will use Protobuf compiler to generate Java classes (or other languages if specified)
;; `buf lint` could be used to detect naming issues, etc.

;; The most important feature is to detect breaking changes with `buf breaking ...`, more about options: https://buf.build/docs/reference/inputs

;; All those buf cli steps should be part of checks in CI, so we protected against breaking changes merges.
;; If API is under development there is a concept unstable API that allow all changes.

;; Other benefit is that same code will be used in clients for gRPC requests, but also could be used in tests.
;; No more duplicating and guessing API format in the tests.

;; Try by running `lein run`, it will start gRPC server on 9001 and do a delayed call to Hello RPC

(defn call-grpc-hello
  []
  (println "Creating gRPC channel")
  ;; Channel should be shared
  (let [channel (-> (ManagedChannelBuilder/forAddress "localhost" 9001)
                    (.usePlaintext)
                    (.build))
        stub (HelloServiceGrpc/newBlockingStub channel)
        ;; Build request object (Buf generated class from .proto schemas)
        request (->(HelloRequest/newBuilder)
                   (.setFirstName "Andrey")
                   (.setLastName "Fadeev")
                   (.build))]
    (println "Calling hello RPC")
    ;; Execute gRPC
    (let [response (.hello stub request)]
      (println "Got response, the greeting:" (.getGreeting response)))))

;; gRPC Server implementation: https://github.com/otwieracz/clj-grpc/blob/master/src/clj/clj_grpc/server.clj

(def ^HelloServiceGrpc hello-service-impl
  (proxy
    [HelloServiceGrpc$HelloServiceImplBase]
    []

    (hello [ ^HelloRequest request ^StreamObserver response-observer]

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
  []

  (let [^Server grpc-server (-> (ServerBuilder/forPort 9001)
                                ;; Multiple gRPC services could be added here, allowing modular code
                                (.addService hello-service-impl)
                                (.build))]
    (.start grpc-server)
    (.awaitTermination grpc-server)))

(defn -main
  []
  (future
    (Thread/sleep 5000)
    (call-grpc-hello))

  (println "Creating gRPC server")
  (create-grpc-server))


