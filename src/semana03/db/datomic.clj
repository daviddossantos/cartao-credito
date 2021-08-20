(ns semana03.db.datomic
  (:require [datomic.api :as d]
            [schema.core :as s]
            [clojure.walk :as walk]
            [semana03.model.cliente :as model.cliente]
            [semana03.model.cartao_credito :as model.cartao]
            [semana03.model.compra :as model.compra]))

(defn uuid [] (java.util.UUID/randomUUID))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao [] (d/create-database db-uri) (d/connect db-uri))

(defn apaga-banco [] (d/delete-database db-uri))

(def schema [{:db/ident       :cliente/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}
             {:db/ident       :cliente/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O nome de um cliente"}
             {:db/ident       :cliente/cpf
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "O cpf de um cliente"}
             {:db/ident       :cliente/email
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O email de um cliente"}
             {:db/ident       :cliente/compra
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/many
              :db/doc         "Compras de um cliente"}
             {:db/ident       :cliente/cartao
              :db/valueType   :db.type/ref
              :db/cardinality :db.cardinality/many
              :db/doc         "Cartao de um cliente"}

             ; Compra
             {:db/ident       :compra/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}
             {:db/ident       :compra/estabelecimento
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O estabelecimento de uma compra"}
             {:db/ident       :compra/categoria
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "A categoria de uma compra"}
             {:db/ident       :compra/valor
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "O valor de uma compra"}

             ; Cartao
             {:db/ident       :cartao/id
              :db/valueType   :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique      :db.unique/identity}
             {:db/ident       :cartao/numero
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "Numero de cartao de um cliente"}
             {:db/ident       :cartao/cvv
              :db/valueType   :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc         "CVV de um cartao de um cliente"}
             {:db/ident       :cartao/validade
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "Validade de um cartao de um cliente"}
             {:db/ident       :cartao/limite
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "Limite de um cartao de um cliente"}

             ])


(defn cria-schema [conn] (d/transact conn schema))

(defn dissoc-db-id [entidade]
  (if (map? entidade)
    (dissoc entidade :db/id)
    entidade))

(defn datomic-para-entidade [entidades]
  (walk/prewalk dissoc-db-id entidades))


(defn verifica-id
  [nome-id colecao]
  (if (get colecao nome-id)
    colecao
    (assoc colecao nome-id (uuid))))

(defn todos-os-clientes [db]
  (datomic-para-entidade
    (d/q '[:find [(pull ?cliente [* {:cliente/compra [*]} {:cliente/cartao [*]}]) ...]
           :where [?cliente :cliente/nome]] db)))

(defn todas-as-compras [db]
  (datomic-para-entidade
    (d/q '[:find [(pull ?entidade [*]) ...]
           :where [?entidade :compra/estabelecimento]] db)))

(defn todos-os-cartoes [db]
  (datomic-para-entidade
    (d/q '[:find [(pull ?entidade [*]) ...]
           :where [?entidade :cartao/numero]] db)))

(s/defn adiciona-ou-altera-cliente :- model.cliente/Cliente
  [conn, clientes :- model.cliente/Cliente]
  (->> clientes
       (map #(verifica-id :cliente/id %))
       (d/transact conn)))

(s/defn adiciona-ou-altera-compra :- model.compra/Compra
  [conn, compras :- model.compra/Compra]
  (->> compras
       (map #(verifica-id :compra/id %))
       (d/transact conn)))


(s/defn adiciona-ou-altera-cartao
  [conn, cartao :- model.cartao/Cartao]
  (->> cartao
       (map #(verifica-id :cartao/id %))
       (d/transact conn)))


(defn db-adds-de-atribuicao-de-compras [compras cliente]
  (reduce (fn [db-adds compra] (conj db-adds [:db/add
                                              [:cliente/id (:cliente/id cliente)]
                                              :cliente/compra
                                              [:compra/id (:compra/id compra)]]))
          []
          compras))

(defn db-adds-de-atribuicao-de-cartoes [cartoes cliente]
  (reduce (fn [db-adds cartao]
            (conj db-adds [:db/add
                           [:cliente/id (:cliente/id cliente)]
                           :cliente/cartao
                           [:cartao/id (:cartao/id cartao)]]))
          []
          cartoes))

(defn atribui-compras [conn compras cliente]
  (let [a-transacionar (db-adds-de-atribuicao-de-compras compras cliente)]
    (d/transact conn a-transacionar)))


(defn atribui-cartoes [conn cartoes cliente]
  (let [a-transacionar (db-adds-de-atribuicao-de-cartoes cartoes cliente)]
    (d/transact conn a-transacionar)))

(defn cliente-com-maior-numero-de-compras [db]
  (datomic-para-entidade
    (d/q '[:find ?nome (max ?total-de-compras)
           :where
           [(q '[:find ?nome (count ?compra)
                 :keys cliente quantidade-de-compra
                 :with ?cliente
                 :where
                 [?cliente :cliente/nome ?nome]
                 [?cliente :cliente/compra ?compra]
                 ] $) [[?nome ?total-de-compras]]]
           ] db)))

(defn cliente-com-maior-valor-de-compra [db]
  (datomic-para-entidade
    (d/q '[:find (pull ?compra [:compra/estabelecimento :compra/valor {:cliente/_compra [:cliente/nome]}])
           :where [(q '[:find (max ?maior-valor)
                        :where [_ :compra/valor ?maior-valor]] $) [[?valor]]]
           [?compra :compra/valor ?valor]
           ] db)))


(defn clientes-que-nao-realizaram-compra [db]
  (datomic-para-entidade
    (d/q '[:find [(pull ?cliente [* {:cliente/compra [*]} {:cliente/cartao [*]}]) ...]
           :where [?cliente :cliente/id ?id]
           ;predicate utilizado `true`se a
           ;entidade nao tem valor atruibuido ao banco
           [(missing? $ ?cliente :cliente/compra)]]
         db)))
