(defproject cryogen-admin "0.1.0-SNAPSHOT"
  :description "A tiny Compojure admin wrapper around Cryogen"
  :url "https://github.com/bdrillard/cryogen-admin"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.3.2"]
                 [cryogen-core "0.1.24"]
                 [buddy/buddy-hashers "0.6.0"]
                 [buddy/buddy-auth "0.6.0"]
                 [org.clojure/java.jdbc "0.4.1"]
                 [org.xerial/sqlite-jdbc "3.8.10.2"]]
  :scm {:name "git"
        :url "https://github.com/bdrillard/cryogen-admin"})
