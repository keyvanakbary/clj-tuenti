# Clojure Tuenti API

[![Build Status](https://secure.travis-ci.org/keyvanakbary/clj-tuenti.svg?branch=master)](http://travis-ci.org/keyvanakbary/clj-tuenti)

Unofficial Tuenti API.

## Installation

Add the following dependency to your `project.clj` file:

    [clj-tuenti "0.9.0"]

## Usage

```clojure
(ns test.core
  (:require [clj-tuenti.core :as tuenti]))

(tuenti/connect! "user@example.com" "password")

(println (tuenti/get-friends))
```

## License

Copyright © 2014 Keyvan Akbary

Distributed under the Eclipse Public License, the same as Clojure.
