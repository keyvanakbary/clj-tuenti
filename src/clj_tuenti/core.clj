(ns clj-tuenti.core
  (:require [clj-http.client :as http]
            [clojure.data.json :as json]
            [digest]))

(def ^:private version "0.5")
(def ^:private api-key "MDI3MDFmZjU4MGExNWM0YmEyYjA5MzRkODlmMjg0MTU6MC43NzQ4ODAwMCAxMjc1NDcyNjgz")
(def ^:private api-url "https://api.tuenti.com/api/")
(def ^:private session (atom {}))

(defn- ^:dynamic post [url parameters]
  (io! (http/post url parameters)))

(defn- do-post [request]
  (post api-url
        {:body            (json/write-str request)
         :connection      "keep-alive"
         :user-agent      "Tuenti/1.2 CFNetwork/485.10.2 Darwin/10.3.1"
         :accept-language "es-es"
         :content-type    :json
         :accept          :json
         }))

(defn- parse-response [response]
  (first (json/read-str (:body response) :key-fn keyword)))

(defn- build-message [method parameters]
  {:version version :requests [[method parameters]]})

(defn- call [method parameters]
    (-> {:version version :requests [[method parameters]]}
        do-post
        parse-response))

(defn- auth-call [method & [parameters or {}]]
  (-> {:session_id (:session_id @session) :version version :requests [[method parameters]]}
        do-post
        parse-response))

(defn get-friends []
  (auth-call
    "getFriendsData"
    {:fields ["name" "surname" "avatar" "sex" "status" "phone_number" "chat_server"]}))

(defn me [] (:user_id @session))

(defn get-profile [{:keys [user-id] :or {user-id (me)}}]
  (auth-call
    "getUsersData"
    {:ids [user-id] :fields
     ["favorite_books" "favorite_movies" "favorite_music" "favorite_quotes" "hobbies" "website"
      "about_me_title" "about_me" "birthday" "city" "province" "name" "surname" "avatar" "sex"
      "status" "phone_number" "chat_server"]}))

(defn get-profile-wall-with-status [{:keys [user-id page size] :or {user-id (me) page 0 size 10}}]
  (auth-call "getProfileWallWithStatus" {:user_id user-id :page page :page_size size}))

(defn set-status [status]
  (auth-call "setUserData" {:status status}))

(defn get-personal-notifications []
  (auth-call
    "getUserNotifications"
    {:types ["unread_friend_messages" "unread_spam_messages" "new_profile_wall_posts"
            "new_friend_requests" "accepted_friend_requests" "new_photo_wall_posts"
            "new_tagged_photos" "new_event_invitations" "new_profile_wall_comments"]}))

(defn get-friends-notifications [{:keys [page size] :or {page 0 size 10}}]
  (auth-call "getFriendsNotifications" {:page page :page_size size}))

(defn get-inbox [{:keys [page size] :or {page 0 size 10}}]
  (auth-call "getInbox" {:page page :page_size size}))

(defn get-sentbox [{:keys [page size] :or {page 0 size 10}}]
  (auth-call "getSentBox" {:page page :page_size size}))

(defn get-spambox [{:keys [page size] :or {page 0 size 10}}]
  (auth-call "getSpamBox" {:page page :page_size size}))

(defn get-thread [thread-key & [{:keys [page size] :or {page 0 size 10}}]]
  (auth-call "getThread" {:thread_key thread-key :page page :page_size size}))

(defn send-message [user-id thread-key message]
  (auth-call "sendMessage" {:recipient user-id :thread_key thread-key :body message}))

(defn get-albums [{:keys [user-id page size] :or {user-id (me) page 0 size 10}}]
  (auth-call "getUserAlbums" {:user_id user-id :page page :albums_per_page size}))

(defn get-album-photos [album-id & [{:keys [user-id page] :or {user-id (me) page 0}}]]
  (auth-call "getAlbumPhotos" {:user_id user-id :album_id album-id :page page}))

(defn get-photo-tags [photo-id]
  (auth-call "getPhotoTags" {:photo_id photo-id}))

(defn add-post-to-photo-wall [photo-id message]
  (auth-call "addPostToPhotoWall" {:photo_id photo-id :body message}))

(defn get-photo-wall [photo-id & [{:keys [page size] :or {page 0 size 10}}]]
  (auth-call "getPhotoWall" {:photo_id photo-id :page page :post_per_page size}))

(defn get-upcoming-events [{:keys [size birthdays] :or {size 10 birthdays :with-birthdays}}]
  (auth-call "getUpcomingEvents"
                        {:desired_number size
                         :include_friend_birthdays (not= birthdays :without-birthdays)}))

(defn get-event [event-id]
  (auth-call "getEvent" {:event_id event-id}))

(defn get-event-wall [event-id]
  (auth-call "getEvent" {:event_id event-id}))

(defn get-event-wall [event-id & {:keys [page size] :or {page 0 size 10}}]
  (auth-call "getEventWall" {:event_id event-id :page page :post_per_page size}))

(defn get-challenge []
  (call "getChallenge" {:type "login"}))

(defn- generate-passcode [password challenge]
  (digest/md5 (str challenge (digest/md5 password))))

(defn connect! [email password]
  (let [{:keys [challenge seed timestamp]} (get-challenge)
        s (call "getSession"
                {:passcode        (generate-passcode password challenge)
                 :seed            seed
                 :email           email
                 :timestamp       timestamp
                 :application_key api-key
                 })]
    (swap! session conj s) nil))

