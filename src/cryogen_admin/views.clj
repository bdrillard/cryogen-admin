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
  [users pages posts]
  (html5
    (head "Admin")
    [:body
     navbar
     [:div.container.fluid
      [:div.col-md-8.col-md-offset-2.col-sm-10.col-sm-offset-1
       [:div.row
        [:div.col-md-8
         [:div.row
          [:h2 "Users"]
          [:table.table.table-hover
           [:thead
            [:tr
             [:th "Username"]
             [:th "Name"]
             [:th "Role"]]]
           [:tbody
            (for [user users
                  :let [{:keys [username firstname lastname role]} user]]
              [:tr
               [:td username]
               [:td (clojure.string/join " " [firstname lastname])]
               [:td role]])]]]
         [:div.row
          [:h2 "Pages"]
          [:table.table.table-hover
           [:thead
            [:tr
             [:th "Title"]]]
           [:tbody
            (for [page pages
                  :let [{:keys [title file-name]} page]]
              [:tr
               [:td title]
               [:td file-name]])]]]
         [:div.row
          [:h2 "Posts"]
          [:table.table.table-hover
           [:thead
            [:tr
             [:th "Title"]
             [:th "Date"]
             [:th "Tags"]]]
           [:tbody
            (for [post posts
                  :let [{:keys [title date tags file-name]} post]]
              [:tr
               [:td title]
               [:td date]
               [:td (clojure.string/join ", " tags)]
               [:td file-name]])]]]
          ]]]]]))
