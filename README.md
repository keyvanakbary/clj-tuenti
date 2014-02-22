# Clojure Tuenti API

Unofficial Tuenti API.

## Installation

Add the following dependency to your `project.clj` file:

    [tuenti-api "0.8.1"]

## Usage

```clojure
(ns test.core
  (:require [tuenti-api.core :as tuenti]))

(def session (tuenti/connect "user@example.com" "password"))

(println (tuenti/get-friends session))
```

## License

Copyright © 2014 Keyvan Akbary

Distributed under the Eclipse Public License, the same as Clojure.
