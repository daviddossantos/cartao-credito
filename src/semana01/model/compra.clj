(ns semana01.model.compra
  (:require [schema.core :as s]))

(def Compra
  {:compra-id       s/Uuid
   :data            s/Str
   :valor           s/Num
   :estabelecimento s/Str
   :categoria       s/Str
   :cartao-id       s/Uuid})
