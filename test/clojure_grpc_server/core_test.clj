(ns clojure-grpc-server.core-test
  (:require [clojure.test :refer [deftest testing is]]
            [clojure-grpc-server.core :as core])
  (:import (com.andreyfadeev.api.v1 HelloRequest HelloResponse HelloServiceGrpc)
           (com.github.tomakehurst.wiremock WireMockServer)
           (com.github.tomakehurst.wiremock.client WireMock)
           (com.github.tomakehurst.wiremock.core WireMockConfiguration)
           (com.github.tomakehurst.wiremock.extension ExtensionFactory)
           (org.wiremock.grpc GrpcExtensionFactory)
           (org.wiremock.grpc.dsl WireMockGrpc WireMockGrpcService)))

(defmacro typed-array
  [klass vals]
  (let [^Class resolved (resolve klass)]
    (with-meta
      (list 'into-array resolved vals)
      {:tag (str "[L" (.getName resolved) ";")})))

(deftest grpc-wiremock-testing
  (let [^WireMockServer wm (WireMockServer.
                             (-> (WireMockConfiguration/wireMockConfig)
                                 (.dynamicPort)
                                 (.withRootDirectory "src/java")
                                 (.extensions (typed-array
                                                ExtensionFactory
                                                [(GrpcExtensionFactory.)]))))]

    (.start wm)

    (let [mock-hello-service (WireMockGrpcService.
                               (WireMock. (.port wm))
                               HelloServiceGrpc/SERVICE_NAME)]

      (.stubFor mock-hello-service
                (-> (WireMockGrpc/method "hello")
                    (.withRequestMessage (WireMockGrpc/equalToMessage
                                           (-> (HelloRequest/newBuilder)
                                               (.setFirstName "Andrey")
                                               (.setLastName "Fadeev"))))
                    (.willReturn (WireMockGrpc/message
                                   (-> (HelloResponse/newBuilder)
                                       (.setGreeting "HELLO FROM WIREMOCK GRPC"))))))

      (let [grpc-channel (core/create-grpc-channel {:host "localhost"
                                                    :port (.port wm)})]
        (is (= "HELLO FROM WIREMOCK GRPC" (core/call-grpc-hello {:grpc-channel grpc-channel})))))

    (.stop wm)))