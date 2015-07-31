(ns cryogen-admin.views
  (:require [hiccup.page :refer [html5 include-css include-js]]
            [cryogen-core.io :refer [get-resource]]))

(def site-title
  (-> "templates/config.edn"
      get-resource
      slurp
      read-string
      :site-title))

(defn head
  [title & body]
  [:head
   [:title (str site-title " - " title)]
   (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css")
   (include-js "https://code.jquery.com/jquery-1.11.3.min.js"
               "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js")])

(def navbar
  [:nav.navbar.navbar-default
   [:div.container.fluid
    [:div.navbar-header
     [:a.navbar-brand {:href "/"} site-title]]]])

(defn login
  [& errors]
  (html5
    (head "Login")
    [:body
     navbar
     [:div.container.fluid
      [:div.col-md-8.col-md-offset-2.col-sm-10.col-sm-offset-1
       [:div.jumbotron
        [:p "Login"]
        [:form {:method "post" :role "login"}
         [:div.row
          [:div.form-group
           [:label "Email address"]
           [:input.form-control {:type "text" :placeholder "Email" :name "username"}]]]
         [:div.row
          [:div.form-group
           [:label "Password"]
           [:input.form-control {:type "password" :placeholder "Password" :name "password"}]]]
         [:div.row
          [:div.form-group
           [:input.btn.btn-default {:type "submit"}]]]]]]]]))

(defn home
  []
  (html5
    (head "Admin")
    [:body
     navbar
     [:p "Hello, world!"]]))
