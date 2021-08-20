(ns main
  (:require
    [datomic.api :as d]
    [semana03.db.datomic :as db])
  (:use [clojure.pprint]))

(defn uuid [] (java.util.UUID/randomUUID))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(def cliente1 {
               :cliente/nome  "Jose Silva"
               :cliente/cpf   44444444444
               :cliente/email "jose.silva@email.com"})

(def cliente2 {
               :cliente/nome  "David Silva"
               :cliente/cpf   999999999999
               :cliente/email "david.silva@email.com"})

(def cartao1 {
              :cartao/numero   333
              :cartao/cvv      123
              :cartao/validade "07/28"
              :cartao/limite   50000M})

(def cartao2 {
              :cartao/numero   111
              :cartao/cvv      545
              :cartao/validade "07/29"
              :cartao/limite   50000M})
(def cartao3 {
              :cartao/numero   222
              :cartao/cvv      421
              :cartao/validade "07/29"
              :cartao/limite   5M})

(def compra1 {
              :compra/valor           15M
              :compra/estabelecimento "Nike"
              :compra/categoria       "Roupa"})

(def compra2 {
              :compra/valor           25M
              :compra/estabelecimento "Dicila"
              :compra/categoria       "Comida"})

(def compra3 {
              :compra/valor           25.52M
              :compra/estabelecimento "BK"
              :compra/categoria       "Comida"})

(println "\nClientes cadastrados:")
(db/adiciona-ou-altera-cliente conn [cliente1 cliente2])
(def clientes (db/todos-os-clientes (d/db conn)))
(pprint clientes)
(def primeiro-cliente (-> clientes
                          first))
(def segundo-cliente (-> clientes
                         second))

(println "\nCompras realizadas:")
(db/adiciona-ou-altera-compra conn [compra1 compra2 compra3])
(def compras (db/todas-as-compras (d/db conn)))
(pprint compras)
(def primeira-compra (-> compras
                         first))
(def segunda-compra (-> compras
                        second))
(def terceira-compra (-> compras
                         (get 2)))

(println "\nCartoes cadastrados:")
(db/adiciona-ou-altera-cartao conn [cartao1 cartao2 cartao3])
(def cartoes  (db/todos-os-cartoes (d/db conn)))
(pprint cartoes)
(def primeiro-cartao (-> cartoes
                          first))
(def segundo-cartao (-> cartoes
                        second))

(def terceiro-cartao (-> cartoes
                         (get 2)))

(db/atribui-compras conn [primeira-compra, segunda-compra] primeiro-cliente)
(db/atribui-cartoes conn [primeiro-cartao segundo-cartao] primeiro-cliente)

(db/atribui-compras conn [primeira-compra] segundo-cliente)
(db/atribui-cartoes conn [terceiro-cartao] segundo-cliente)
;
(println "\nClientes associados:")
(pprint (db/todos-os-clientes (d/db conn)))

(println "\nCliente com maior numero de compras")
(pprint (db/cliente-com-maior-numero-de-compras (d/db conn)))

(println "\nCliente que realizou a compra de maior valor:")
(pprint (db/cliente-com-maior-valor-de-compra (d/db conn)))

(println "\nClientes que nao realizou nenhuma compra:")
(pprint (db/clientes-que-nao-realizaram-compra (d/db conn)))
