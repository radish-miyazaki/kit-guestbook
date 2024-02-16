(ns kit.guestbook.web.controllers.guestbook 
  (:require
   [clojure.tools.logging :as log]
   [ring.util.http-response :as http-response]))

(defn save-message!
  [{:keys [query-fn]}
   {{:strs [name message]} :form-params}]
  (log/debug "saving message" name message)
  (try
    (if (or (empty? name) (empty? message))
      (cond-> (http-response/found "/")
        (empty? name) (assoc-in [:flash :errors :name] "Name is required")
        (empty? message) (assoc-in [:flash :errors :message] "Message is required"))
      (do
        (query-fn :save-message! {:name name :message message})
        (http-response/found "/")))
    (catch Exception e
      (log/error e "failed to save message!")
      (-> (http-response/found "/")
          (assoc :flash {:errors {:unknown (.getMessage e)}})))))

