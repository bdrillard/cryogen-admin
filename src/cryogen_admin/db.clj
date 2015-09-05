(ns cryogen-admin.db
  (:require [clojure.java.jdbc :refer :all]
            [buddy.hashers :as hashers]))

(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname "users.db"})

(def default-admin
  {:username "admin"
   :password "password"
   :firstname "foo"
   :lastname "bar"
   :role "admin"})

(defn init-db
  "Creates an initial users database and inserts a default admin user"
  []
  (when-not (.exists (clojure.java.io/file "users.db"))
    (db-do-commands 
      db
      (create-table-ddl :users
                        [:username :text "PRIMARY KEY"]
                        [:password :text]
                        [:firstname :text]
                        [:lastname :text]
                        [:role :text]))
    (insert! db :users (assoc default-admin
                              :password
                              (hashers/encrypt (:password default-admin))))))

(defn get-user
  "Returns map of a users info given the username"
  [username]
  (-> (query db ["SELECT * FROM users WHERE username = ?" username])
      first))

(defn get-users
  "Returns a list of user maps"
  []
  (query db ["SELECT * FROM users"]))

(defn create-user
  "Given a map of user attributes, inserts a new user with hashed password"
  [params]
  (insert! db :users (assoc params
                            :password
                            (hashers/encrypt (:password params)))))

(defn update-user
  "Given a map of user attributes and their new values, updates a user"
  [username params]
  (update! db :users params ["username = ?" username]))

(defn delete-user
  "Given a username, deletes a user"
  [username]
  (delete! db :users ["username = ?" username]))
