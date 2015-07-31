(ns cryogen-admin.admin
  (:require [compojure.core :refer [context defroutes]]
            [compojure.handler :as handler]
            [ring.util.response :refer [redirect]]
            [ring.middleware.params :refer [wrap-params]]
            [hiccup.middleware :refer [wrap-base-url]]
            [buddy.auth :refer [authenticated?]]
            [buddy.auth.backends.session :refer [session-backend]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [cryogen-admin.routes :refer [admin-routes]]))

(defn unauthorized-handler
  [request metadata]
  (cond
    (authenticated? request) (-> (redirect "/error-403")
                                 (assoc :status 403))
    :else (let [current-url (:uri request)]
            (redirect (format "/admin?next=%s" current-url)))))

(def auth-backend
  (session-backend {:unauthorized-handler unauthorized-handler}))

(defroutes admin-context
  (context "/admin" [] admin-routes))

(def admin
  (-> admin-context
      (wrap-authorization auth-backend)
      (wrap-authentication auth-backend)
      (handler/site)
      (wrap-base-url)))
