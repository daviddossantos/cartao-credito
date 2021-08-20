(ns semana01.logic.cliente
  (:require [schema.core :as s]
            [semana01.model.cliente :as c.model]))


(s/defn cria-novo-cliente :- c.model/Cliente
  [
   cliente-id :- s/Uuid
   nome :- s/Str
   cpf :- s/Num
   email :- s/Str
   ]
  {:cliente-id cliente-id,
   :nome       nome,
   :cpf        cpf,
   :email      email
   })


(defn listar-clientes [clientes] clientes)