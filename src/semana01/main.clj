(ns semana01.main
  (:require [semana01.db.cliente :as cliente.db]
            [semana01.db.cartao_credito :as cartao.db]
            [semana01.db.compra :as compra.db]
            [semana01.logic.cartao_credito :as cartao.logic]
            [semana01.logic.cliente :as cliente.logic]
            [semana01.logic.compra :as compra.logic]
            )
  (:use [clojure.pprint]))


(println "\n\n Clientes listados:")
(pprint (cliente.logic/listar-clientes cliente.db/clientes))

(println "\n\n Cartões listados:")
(pprint (cartao.logic/listar-cartoes cartao.db/cartoes))
;
(println "\n\n Compras listadas:")
(pprint (compra.logic/listar-compras compra.db/compras))
;
(println "\n\n Gastos por categoria:")
(pprint (compra.logic/total-compras-por-categoria compra.db/compras))
;
(println "\n\n Compra por estabelecimento:")
(pprint (compra.logic/compras-por-estabelecimento "Renner" compra.db/compras))

(println "\n\n Gasto por mês:")
(pprint (compra.logic/fatura-por-mes "03" compra.db/compras cartao.db/cartoes))

