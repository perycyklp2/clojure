(ns clojure.test-clojure.string
  (:require [clojure.string :as s])
  (:use clojure.test))

(deftest t-reverse
  (is (= "tab" (s/reverse "bat"))))

(deftest t-replace
  (is (= "faabar" (s/replace-char \o \a "foobar")))
  (is (= "barbarbar" (s/replace-str "foo" "bar" "foobarfoo")))
  (is (= "FOObarFOO" (s/replace-by #"foo" s/upper-case  "foobarfoo"))))

(deftest t-replace-first
  (is (= "barbarfoo" (s/replace-first-re #"foo" "bar" "foobarfoo")))
  (is (= "FOObarfoo" (s/replace-first-by #"foo" s/upper-case "foobarfoo"))))

(deftest t-join
  (are [x coll] (= x (s/join coll))
       "" nil
       "" []
       "1" [1]
       "12" [1 2])
  (are [x sep coll] (= x (s/join sep coll))
       "1,2,3" \, [1 2 3]
       "" \, []
       "1" \, [1]
       "1 and-a 2 and-a 3" " and-a " [1 2 3]))

(deftest t-chop
  (is (= "fo" (s/chop "foo")))
  (is (= "") (s/chop "f"))
  (is (= "") (s/chop "")))

(deftest t-chomp
  (is (= "foo" (s/chomp "foo\n")))
  (is (= "foo" (s/chomp "foo\r\n")))
  (is (= "foo" (s/chomp "foo")))
  (is (= "" (s/chomp ""))))

(deftest t-capitalize
  (is (= "Foobar" (s/capitalize "foobar")))
  (is (= "Foobar" (s/capitalize "FOOBAR"))))

(deftest t-triml
  (is (= "foo " (s/triml " foo ")))
  (is (= "" (s/triml "   "))))

(deftest t-trimr
  (is (= " foo" (s/trimr " foo ")))
  (is (= "" (s/trimr "   "))))

(deftest t-trim
  (is (= "foo" (s/trim "  foo  \r\n"))))

(deftest t-upper-case
  (is (= "FOOBAR" (s/upper-case "Foobar"))))

(deftest t-lower-case
  (is (= "foobar" (s/lower-case "FooBar"))))

