(ns semana01.db.cartao_credito
  (:require [semana01.logic.cartao_credito :as cartao.logic]
            [semana01.db.cliente :as cliente.db]))

(def cartao1 (cartao.logic/cria-novo-cartao
               (java.util.UUID/randomUUID)
               2555
               155
               "07/28"
               5000
               (:cliente-id cliente.db/cliente1)))

(def cartao2 (cartao.logic/cria-novo-cartao
               (java.util.UUID/randomUUID)
               2444
               144
               "08/29"
               6000
               (:cliente-id cliente.db/cliente2)))

(def cartoes [cartao1 cartao2])


