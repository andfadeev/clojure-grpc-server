(defproject clojure-grpc-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [io.grpc/grpc-netty "1.64.0"]
                 [io.grpc/grpc-stub "1.64.0"]
                 [io.grpc/grpc-protobuf "1.64.0"]
                 [com.google.protobuf/protobuf-java "4.27.0"]
                 [javax.annotation/javax.annotation-api "1.3.2"]

                 ;; testing
                 [org.wiremock/wiremock-standalone "3.6.0"]
                 [org.wiremock/wiremock-grpc-extension "0.6.0"]]
  :main ^:skip-aot clojure-grpc-server.core
  :target-path "target/%s"
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java"]
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
