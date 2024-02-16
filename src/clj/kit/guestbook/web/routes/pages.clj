(ns kit.guestbook.web.routes.pages
  (:require
   [integrant.core :as ig]
   [kit.guestbook.web.controllers.guestbook :as guestbook]
   [kit.guestbook.web.middleware.exception :as exception]
   [kit.guestbook.web.pages.layout :as layout]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]))

(defn wrap-page-defaults []
  (let [error-page (layout/error-page
                     {:status 403
                      :title "Invalid anti-forgery token"})]
    #(wrap-anti-forgery % {:error-response error-page})))

(defn home
  [{:keys [query-fn]} {:keys [flash] :as request}]
  (layout/render request "home.html" {:messages (query-fn :get-messages [])
                                      :errors {:errors flash}}))

;; Routes
(defn page-routes [_opts]
  [["/" {:get home}]
   ["/save" {:post guestbook/save-message!}]])

(defn route-data [opts]
  (merge
   opts
   {:middleware 
    [;; Default middleware for pages
     (wrap-page-defaults)
     ;; query-params & form-params
     parameters/parameters-middleware
     ;; encoding response body
     muuntaja/format-response-middleware
     ;; exception handling
     exception/wrap-exception]}))

(derive :reitit.routes/pages :reitit/routes)

(defmethod ig/init-key :reitit.routes/pages
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  (layout/init-selmer! opts)
  [base-path (route-data opts) (page-routes opts)])

