(ns ldaps-test.core
  (:gen-class)
  (:use [clojure.tools.trace :only [trace]])
  (:require [clojure.edn :as edn]
            [clojure.string :as string]
            [clj-ldap.client :as ldap]
            [clojure.tools.cli :refer [parse-opts]])
  (:import (java.net InetAddress)))

(def cli-options
  [["-P" "--port PORT" "Port number"
    :default 20636
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-u" "--user USER" "Username to bind as"
    :missing "No user was specified"]
   ["-p" "--password PASSWORD" "Password to bind as"
    :missing "No password was specified"]
   ["-H" "--hostname HOST" "Remote host"
    :default (InetAddress/getByName "localhost")
    :default-desc "localhost"
    :parse-fn #(InetAddress/getByName %)]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Log in and get roles for a specific ldap user"
        ""
        "Usage: program-name [options] action"
        ""
        "Options:"
        options-summary]
    (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
    (string/join \newline errors)))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn people-path [uid]
  (str "uid=" uid ",ou=People,dc=opintopolku,dc=fi"))

(defn ldap-pool [{:keys [host port user hostname password]}] (ldap/connect {:host [{:address (.getHostName hostname) :port port}]
                                                                            :bind-dn (people-path user)
                                                                            :password password
                                                                            :ssl? true
                                                                            :num-connections 1}))

(defn -main [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) (exit 0 (usage summary))
      errors (exit 1 (error-msg errors)))
    (let [ldap-server (ldap-pool options)]
      (print "Username: ")
      (flush)
      (let [username (read-line)]
        (print "Password: ")
        (flush)
        (let [password (read-line)]
          (if (ldap/bind? ldap-server (people-path username) password)
            (let [ldap-desc (ldap/get ldap-server (people-path username))]
              (trace (edn/read-string (:description ldap-desc))))))))))
