(ns cryogen-admin.routes
  (:require [ring.util.response :refer [redirect response]]
            [compojure.core :refer [GET PUT POST DELETE defroutes]]
            [buddy.hashers :as hashers]
            [buddy.auth :refer [authenticated? throw-unauthorized]]
            [cryogen-admin.db :as db]
            [cryogen-admin.views :as layout]))

(defn login
  [request]
  (layout/login))

(defn home
  [request]
  (if-not (authenticated? request)
    (throw-unauthorized)
    (layout/home)))

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

(defroutes admin-routes
  (GET "/" [] login)
  (POST "/" [] login-authenticate)
  (GET "/logout" [] logout)

  (GET "/home" [] home))
