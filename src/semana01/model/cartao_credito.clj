(ns semana01.model.cartao_credito
  (:require [schema.core :as s]))

(def Cartao {:cartao-id  s/Uuid
             :numero     s/Num
             :cvv        s/Num
             :validade   s/Str
             :limite     s/Num
             :cliente-id s/Uuid
             })