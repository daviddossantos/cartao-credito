(ns semana03.model.cliente
  (:require [schema.core :as s]
            [semana03.model.compra :as model.compra]
            [semana03.model.cartao_credito :as model.cartao]))

(def Cliente {
              :cliente/id                      java.util.UUID
              :cliente/nome                    s/Str
              :cliente/cpf                     s/Num
              :cliente/email                   s/Str
              (s/optional-key :cliente/compra) model.compra/Compra
              (s/optional-key :cliente/cartao) model.cartao/Cartao })




