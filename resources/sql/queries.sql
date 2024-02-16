-- :name save-message! :! :n
-- :doc creates a new message
INSERT INTO guestbook (name, message) VALUES (:name, :message)

-- :name get-messages :? :*
-- :doc select all avaiable messages
SELECT * FROM guestbook

