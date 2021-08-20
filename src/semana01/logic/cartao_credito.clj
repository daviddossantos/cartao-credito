(ns semana01.logic.cartao_credito
  (:require [semana01.model.cartao_credito :as c.model]
            [schema.core :as s]))


(s/defn cria-novo-cartao :- c.model/Cartao
  [
   cartao-id :- s/Uuid
   numero :- s/Num
   cvv :- s/Num
   validade :- s/Str
   limite :- s/Num
   cliente-id :- s/Uuid
   ]
  {:cartao-id  cartao-id
   :numero     numero
   :cvv        cvv
   :validade   validade
   :limite     limite
   :cliente-id cliente-id
   })


(defn listar-cartoes [cartoes] cartoes)