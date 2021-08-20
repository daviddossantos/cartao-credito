(ns semana01.model.cliente
  (:require [schema.core :as s]))

(def Cliente {
              :cliente-id s/Uuid
              :nome       s/Str
              :cpf        s/Num
              :email      s/Str})
