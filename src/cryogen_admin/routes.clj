(ns cryogen-admin.routes
  (:require [ring.util.response :refer [redirect response]]
            [compojure.core :refer [GET PUT POST DELETE defroutes context]]
            [buddy.hashers :as hashers]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [cryogen-core.compiler :refer [read-config read-page-meta find-posts find-pages]]
            [cryogen-core.markup :as m]
            [clojure.java.io :refer [reader]]
            [cryogen-admin.db :as db]
            [cryogen-admin.views :as layout]))

(defn parse-page-markup
  [page config markup]
  (with-open [rdr (java.io.PushbackReader. (reader page))]
    (let [page-name (.getName page)
          page-meta (read-page-meta page-name rdr)
          content ((fn [rdr conf] (->> (java.io.BufferedReader. rdr)
                                       (line-seq)
                                       (clojure.string/join "\n"))) 
                   rdr config)]
      (merge {:file-name page-name
              :content content}
             page-meta))))

(defn read-posts-markup
  [config]
  (->> (mapcat
         (fn [mu]
           (->>
             (find-posts config mu)
             (map #(parse-page-markup % config mu))))
         (m/markups))
       (sort-by :file-name)))

(defn read-pages-markup
  [config]
  (->> (mapcat
         (fn [mu]
           (->>
             (find-pages config mu)
             (map #(parse-page-markup % config mu))))
         (m/markups))
       (sort-by :page-index)))

(defn login
  [request]
  (layout/login))

(defn login-authenticate
  [request]
  (let [username (get-in request [:form-params "username"])
        password (get-in request [:form-params "password"])
        session (:session request)
        found-password (-> username (db/get-user) :password)
        errors (cond
                 (not found-password) {:username "The given email is not registered"}
                 (not (hashers/check password found-password)) {:password "Incorrect password"}
                 :else nil)]
    (if (seq errors)
      (response (layout/login errors))
      (let [next-url (get-in request [:query-params "next"] "/")
            updated-session (assoc session :identity (keyword username))]
        (-> (redirect next-url)
            (assoc :session updated-session))))))

(defn logout
  [request]
  (-> (redirect "/")
      (assoc :session {})))

(defn home
  [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    (let [conf (read-config)
          users (map #(select-keys % [:username :firstname :lastname :role])
                     (db/get-users))
          pages (read-pages-markup conf)
          posts (read-posts-markup conf)]
      (prn (read-posts-markup conf))
      (prn (read-pages-markup conf))
      (prn users)
      (layout/home users pages posts))))

(defn get-user
  [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    (let [username (get-in request [:params :username])
          user (db/get-user username)]
    (layout/user user))))

(defn new-user
  [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    nil))

(defn update-user
  [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    nil))

(defn delete-user
  [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    nil))

(defroutes admin-routes
  (GET "/" [] login)
  (POST "/" [] login-authenticate)
  (GET "/logout" [] logout)

  (GET "/home" [] home)
  (context "/user" []
    (GET "/:username" [] get-user)
    (POST "/" [] new-user)
    (PUT "/" [] update-user)
    (DELETE "/:username" [] delete-user)))
