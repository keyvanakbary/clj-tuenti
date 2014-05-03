(ns tuenti-api.test.util
  (:require [clojure.contrib.mock :refer [expect has-args returns]]))

(defmacro expect-request [body func]
  `(expect [tuenti-api.core/post
     (has-args
      ["https://api.tuenti.com/api/"
       {:body ~body
        :connection "keep-alive"
        :user-agent "Tuenti/1.2 CFNetwork/485.10.2 Darwin/10.3.1"
        :accept-language "es-es"
        :content-type :json
        :accept :json}] (returns {:body "{}"}))] ~func))

(defmacro expect-auth-call [json call]
  `(expect-request (str "{\"session_id\":null,\"version\":\"0.5\",\"requests\":[" ~json "]}") ~call))

(defmacro expect-call [json call]
  `(expect-request (str "{\"version\":\"0.5\",\"requests\":[" ~json "]}") ~call))
