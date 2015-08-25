(defproject ldaps-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.3"]
                 [org.clojure/tools.trace "0.7.8"]
                 [org.clojars.pntblnk/clj-ldap "0.0.9"]]
  :main ^:skip-aot ldaps-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
