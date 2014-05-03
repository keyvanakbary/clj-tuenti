# Clojure Tuenti API

Unofficial Tuenti API.

## Installation

Add the following dependency to your `project.clj` file:

    [tuenti-api "0.9.0"]

## Usage

```clojure
(ns test.core
  (:require [tuenti-api.core :as tuenti]))

(tuenti/connect! "user@example.com" "password")

(println (tuenti/get-friends))
```

## License

Copyright © 2014 Keyvan Akbary

Distributed under the Eclipse Public License, the same as Clojure.
