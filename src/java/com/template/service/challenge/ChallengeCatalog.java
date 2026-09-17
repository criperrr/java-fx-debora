package com.template.service.challenge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.template.model.challenge.Challenge;

/**
 * Catálogo dos 5 grandes desafios técnicos de engenharia de software da Fenda do Biquíni.
 */
public class ChallengeCatalog {

    private static final List<Challenge> challenges = new ArrayList<>();

    static {
        // FASE 1: DDL & MODELAGEM RELACIONAL
        challenges.add(new Challenge(
            "phase1",
            1,
            "Fase 1: Modelagem Relacional & DDL de Emergência",
            "Criação das tabelas centrais com Foreign Keys, Cascades e Checks",
            "🦀",
            "O Seu Siriguejo descobriu que anotar pedidos em folhas de alga estava dando calote de clientes!\n" +
            "Ele precisa de 3 tabelas no schema: 'clientes', 'pedidos' e 'itens_pedido'.\n" +
            "Regras: Ninguém pode ter saldo negativo e deletar um cliente deve apagar seus pedidos automaticamente em cascata!",
            "Crie as seguintes 3 tabelas no schema atual:\n" +
            "1. clientes (id SERIAL PRIMARY KEY, nome VARCHAR(100) NOT NULL, reputacao INT DEFAULT 50, saldo_conchas NUMERIC(10,2) CHECK (saldo_conchas >= 0));\n" +
            "2. pedidos (id SERIAL PRIMARY KEY, cliente_id INT REFERENCES clientes(id) ON DELETE CASCADE, status VARCHAR(30) CHECK (status IN ('RECEBIDO', 'NA_CHAPA', 'PRONTO', 'CANCELADO')), criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP);\n" +
            "3. itens_pedido (id SERIAL PRIMARY KEY, pedido_id INT REFERENCES pedidos(id) ON DELETE CASCADE, nome_item VARCHAR(100) NOT NULL, quantidade INT CHECK (quantidade > 0), preco_unitario NUMERIC(10,2) NOT NULL);",
            "-- Digite seu comando DDL abaixo:\n" +
            "CREATE TABLE IF NOT EXISTS clientes (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    nome VARCHAR(100) NOT NULL,\n" +
            "    reputacao INT DEFAULT 50,\n" +
            "    saldo_conchas NUMERIC(10,2) CHECK (saldo_conchas >= 0)\n" +
            ");\n\n" +
            "CREATE TABLE IF NOT EXISTS pedidos (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    cliente_id INT REFERENCES clientes(id) ON DELETE CASCADE,\n" +
            "    status VARCHAR(30) CHECK (status IN ('RECEBIDO', 'NA_CHAPA', 'PRONTO', 'CANCELADO')),\n" +
            "    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
            ");\n\n" +
            "CREATE TABLE IF NOT EXISTS itens_pedido (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    pedido_id INT REFERENCES pedidos(id) ON DELETE CASCADE,\n" +
            "    nome_item VARCHAR(100) NOT NULL,\n" +
            "    quantidade INT CHECK (quantidade > 0),\n" +
            "    preco_unitario NUMERIC(10,2) NOT NULL\n" +
            ");",
            "CREATE TABLE clientes (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    nome VARCHAR(100) NOT NULL,\n" +
            "    reputacao INT DEFAULT 50,\n" +
            "    saldo_conchas NUMERIC(10,2) CHECK (saldo_conchas >= 0)\n" +
            ");\n\n" +
            "CREATE TABLE pedidos (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    cliente_id INT REFERENCES clientes(id) ON DELETE CASCADE,\n" +
            "    status VARCHAR(30) CHECK (status IN ('RECEBIDO', 'NA_CHAPA', 'PRONTO', 'CANCELADO')),\n" +
            "    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
            ");\n\n" +
            "CREATE TABLE itens_pedido (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    pedido_id INT REFERENCES pedidos(id) ON DELETE CASCADE,\n" +
            "    nome_item VARCHAR(100) NOT NULL,\n" +
            "    quantidade INT CHECK (quantidade > 0),\n" +
            "    preco_unitario NUMERIC(10,2) NOT NULL\n" +
            ");",
            Arrays.asList(
                "Tabela 'clientes' existe com PK e CHECK(saldo_conchas >= 0)",
                "Tabela 'pedidos' existe com FK e CHECK de status",
                "Tabela 'itens_pedido' existe com CHECK(quantidade > 0)",
                "Teste de Constraint: Rejeitar cliente com saldo negativo (-10.00)",
                "Teste de Cascade: Ao deletar cliente, pedidos associados devem ser apagados"
            ),
            250
        ));

        // FASE 2: TRIGGER PL/PGSQL DA COZINHA DO BOB ESPONJA
        challenges.add(new Challenge(
            "phase2",
            2,
            "Fase 2: O Trigger da Fila Prioritária do Bob Esponja",
            "Automação de Cozinha em PL/pgSQL com Tabela de Auditoria",
            "🍍",
            "Bob Esponja na cozinha fica perdido quando chegam dezenas de pedidos!\n" +
            "Precisamos de um trigger que verifique a reputação do cliente: se reputação >= 80 (VIP),\n" +
            "o pedido deve ser imediatamente marcado como 'NA_CHAPA' antes do INSERT,\n" +
            "e registrar um log na tabela 'cozinha_logs' com a mensagem: 'Bob assumiu prioridade VIP para pedido [id]'!",
            "Crie a tabela 'cozinha_logs (id SERIAL PRIMARY KEY, mensagem TEXT, registrado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP)'\n" +
            "e a função trigger 'trg_fila_cozinha()' associada à tabela 'pedidos' (BEFORE INSERT).\n" +
            "Se o cliente correspondente tiver reputacao >= 80, defina NEW.status = 'NA_CHAPA'.",
            "-- Crie a tabela de logs e o trigger PL/pgSQL:\n" +
            "CREATE TABLE IF NOT EXISTS cozinha_logs (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    mensagem TEXT NOT NULL,\n" +
            "    registrado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
            ");\n\n" +
            "CREATE OR REPLACE FUNCTION trg_fila_cozinha()\n" +
            "RETURNS TRIGGER AS $$\n" +
            "DECLARE\n" +
            "    v_reputacao INT;\n" +
            "BEGIN\n" +
            "    SELECT reputacao INTO v_reputacao FROM clientes WHERE id = NEW.cliente_id;\n" +
            "    IF v_reputacao >= 80 THEN\n" +
            "        NEW.status := 'NA_CHAPA';\n" +
            "        INSERT INTO cozinha_logs(mensagem) VALUES ('Bob assumiu prioridade VIP para pedido de cliente ' || NEW.cliente_id);\n" +
            "    END IF;\n" +
            "    RETURN NEW;\n" +
            "END;\n" +
            "$$ LANGUAGE plpgsql;\n\n" +
            "DROP TRIGGER IF EXISTS trigger_prioridade_cozinha ON pedidos;\n" +
            "CREATE TRIGGER trigger_prioridade_cozinha\n" +
            "BEFORE INSERT ON pedidos\n" +
            "FOR EACH ROW EXECUTE FUNCTION trg_fila_cozinha();",
            "CREATE TABLE IF NOT EXISTS cozinha_logs (\n" +
            "    id SERIAL PRIMARY KEY,\n" +
            "    mensagem TEXT NOT NULL,\n" +
            "    registrado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
            ");\n\n" +
            "CREATE OR REPLACE FUNCTION trg_fila_cozinha()\n" +
            "RETURNS TRIGGER AS $$\n" +
            "DECLARE\n" +
            "    v_reputacao INT;\n" +
            "BEGIN\n" +
            "    SELECT reputacao INTO v_reputacao FROM clientes WHERE id = NEW.cliente_id;\n" +
            "    IF v_reputacao >= 80 THEN\n" +
            "        NEW.status := 'NA_CHAPA';\n" +
            "        INSERT INTO cozinha_logs(mensagem) VALUES ('Bob assumiu prioridade VIP para pedido de cliente ' || NEW.cliente_id);\n" +
            "    END IF;\n" +
            "    RETURN NEW;\n" +
            "END;\n" +
            "$$ LANGUAGE plpgsql;\n\n" +
            "DROP TRIGGER IF EXISTS trigger_prioridade_cozinha ON pedidos;\n" +
            "CREATE TRIGGER trigger_prioridade_cozinha\n" +
            "BEFORE INSERT ON pedidos\n" +
            "FOR EACH ROW EXECUTE FUNCTION trg_fila_cozinha();",
            Arrays.asList(
                "Tabela 'cozinha_logs' criada com sucesso",
                "Função 'trg_fila_cozinha()' compilada no PostgreSQL",
                "Trigger 'trigger_prioridade_cozinha' ativo em 'pedidos'",
                "Teste VIP: Cliente com reputação 95 ganha status 'NA_CHAPA'",
                "Teste Comum: Cliente com reputação 50 mantém status original",
                "Log de auditoria registrado na tabela 'cozinha_logs'"
            ),
            500
        ));

        // FASE 3: RELATÓRIO FINANCEIRO DO SIRIGUEJO (DQL AVANÇADO)
        challenges.add(new Challenge(
            "phase3",
            3,
            "Fase 3: Relatório Financeiro VIP do Seu Siriguejo",
            "Consulta SQL com Múltiplos JOINs, Agrupamento e Filtragem por HAVING",
            "💰",
            "O Seu Siriguejo quer premiar os clientes que mais gastam no restaurante!\n" +
            "Escreva uma consulta SELECT que traga o nome do cliente e o valor total gasto por ele em conchas.\n" +
            "Apenas clientes com gasto total >= 50.00 devem aparecer, ordenados do maior para o menor faturamento!",
            "Escreva a query SELECT retornando:\n" +
            "- c.nome AS cliente\n" +
            "- SUM(i.quantidade * i.preco_unitario) AS total_gasto\n" +
            "Fazendo JOIN entre 'clientes c', 'pedidos p' e 'itens_pedido i',\n" +
            "Agrupando por c.nome, com HAVING SUM(...) >= 50.00 e ORDER BY total_gasto DESC.",
            "-- Escreva sua consulta SELECT abaixo:\n" +
            "SELECT c.nome AS cliente, SUM(i.quantidade * i.preco_unitario) AS total_gasto\n" +
            "FROM clientes c\n" +
            "JOIN pedidos p ON p.cliente_id = c.id\n" +
            "JOIN itens_pedido i ON i.pedido_id = p.id\n" +
            "GROUP BY c.nome\n" +
            "HAVING SUM(i.quantidade * i.preco_unitario) >= 50.00\n" +
            "ORDER BY total_gasto DESC;",
            "SELECT c.nome AS cliente, SUM(i.quantidade * i.preco_unitario) AS total_gasto\n" +
            "FROM clientes c\n" +
            "JOIN pedidos p ON p.cliente_id = c.id\n" +
            "JOIN itens_pedido i ON i.pedido_id = p.id\n" +
            "GROUP BY c.nome\n" +
            "HAVING SUM(i.quantidade * i.preco_unitario) >= 50.00\n" +
            "ORDER BY total_gasto DESC;",
            Arrays.asList(
                "Query executa com INNER JOINs válidos",
                "Agrupamento correto com GROUP BY c.nome",
                "Filtro de agregação com HAVING total >= 50.00",
                "Ordenação descendente por total_gasto",
                "Validação dos valores calculados contra massa de teste"
            ),
            1000
        ));

        // FASE 4: OTIMIZAÇÃO EXTREMA & ÍNDICES B-TREE
        challenges.add(new Challenge(
            "phase4",
            4,
            "Fase 4: Otimização Extrema de Performance & Índice B-Tree",
            "Eliminação de Sequential Scan em Busca Filtrada com EXPLAIN ANALYZE",
            "🗿",
            "Lula Molusco começou a tocar clarinete de raiva: o sistema do caixa está congelando!\n" +
            "A tabela 'pedidos' agora tem milhares de registros e a busca por (cliente_id, status) está fazendo Seq Scan!\n" +
            "Crie o índice composto ideal na tabela 'pedidos' para transformar essa busca em um Index Scan veloz!",
            "Crie um índice B-Tree chamado 'idx_pedidos_cliente_status' na tabela 'pedidos' cobrindo (cliente_id, status).\n" +
            "O Test Runner injetará 5.000 registros e rodará um EXPLAIN ANALYZE para certificar que o Seq Scan foi eliminado!",
            "-- Crie o índice composto na tabela pedidos:\n" +
            "CREATE INDEX idx_pedidos_cliente_status ON pedidos (cliente_id, status);",
            "CREATE INDEX idx_pedidos_cliente_status ON pedidos (cliente_id, status);",
            Arrays.asList(
                "Índice 'idx_pedidos_cliente_status' criado com sucesso",
                "Massa de 5.000 pedidos carregada no banco",
                "Verificação via EXPLAIN: Sequential Scan ELIMINADO",
                "Verificação via EXPLAIN: 'Index Scan' ou 'Bitmap Index Scan' ativo",
                "Tempo de consulta reduzido para < 10ms"
            ),
            2500
        ));

        // FASE 5: CONCORRÊNCIA E DEFESA CONTRA O PLANKTON
        challenges.add(new Challenge(
            "phase5",
            5,
            "Fase 5: Defesa Concorrente Anti-Plankton (Lock Pessimista)",
            "Transações Concorrentes com SELECT ... FOR UPDATE para Evitar Race Condition",
            "🔬",
            "O Plankton ativou 20 bots cibernéticos que tentam comprar o último Hambúrguer Secreto de Netuno (estoque = 1) no mesmo milissegundo!\n" +
            "Sem lock pessimista, o estoque fica negativo (-19)! Crie a função de compra com 'SELECT estoque FROM ... FOR UPDATE' para que apenas 1 compra tenha sucesso!",
            "Crie a tabela 'estoque_segredo (id INT PRIMARY KEY, nome VARCHAR(100), estoque INT)'\n" +
            "e uma função ou procedimento que realize a compra atômica com trava pessimista:\n" +
            "CREATE OR REPLACE FUNCTION comprar_item_segredo(p_id INT) RETURNS BOOLEAN ...\n" +
            "Se estoque > 0, decrementa 1 e retorna TRUE; senão, retorna FALSE.",
            "-- Implemente a função de compra protegida com FOR UPDATE:\n" +
            "CREATE TABLE IF NOT EXISTS estoque_segredo (\n" +
            "    id INT PRIMARY KEY,\n" +
            "    nome VARCHAR(100),\n" +
            "    estoque INT\n" +
            ");\n\n" +
            "CREATE OR REPLACE FUNCTION comprar_item_segredo(p_id INT) RETURNS BOOLEAN AS $$\n" +
            "DECLARE\n" +
            "    v_estoque INT;\n" +
            "BEGIN\n" +
            "    SELECT estoque INTO v_estoque FROM estoque_segredo WHERE id = p_id FOR UPDATE;\n" +
            "    IF v_estoque > 0 THEN\n" +
            "        UPDATE estoque_segredo SET estoque = estoque - 1 WHERE id = p_id;\n" +
            "        RETURN TRUE;\n" +
            "    ELSE\n" +
            "        RETURN FALSE;\n" +
            "    END IF;\n" +
            "END;\n" +
            "$$ LANGUAGE plpgsql;",
            "CREATE TABLE IF NOT EXISTS estoque_segredo (\n" +
            "    id INT PRIMARY KEY,\n" +
            "    nome VARCHAR(100),\n" +
            "    estoque INT\n" +
            ");\n\n" +
            "CREATE OR REPLACE FUNCTION comprar_item_segredo(p_id INT) RETURNS BOOLEAN AS $$\n" +
            "DECLARE\n" +
            "    v_estoque INT;\n" +
            "BEGIN\n" +
            "    SELECT estoque INTO v_estoque FROM estoque_segredo WHERE id = p_id FOR UPDATE;\n" +
            "    IF v_estoque > 0 THEN\n" +
            "        UPDATE estoque_segredo SET estoque = estoque - 1 WHERE id = p_id;\n" +
            "        RETURN TRUE;\n" +
            "    ELSE\n" +
            "        RETURN FALSE;\n" +
            "    END IF;\n" +
            "END;\n" +
            "$$ LANGUAGE plpgsql;",
            Arrays.asList(
                "Tabela 'estoque_segredo' e função 'comprar_item_segredo' criadas",
                "Disparo de 20 threads concorrentes simultâneas via JDBC",
                "Estoque final não fica negativo (exatamente 0)",
                "Exatamente 1 thread recebe retorno TRUE",
                "Outras 19 threads recebem retorno FALSE (sem deadlocks)"
            ),
            5000
        ));
    }

    public static List<Challenge> getAllChallenges() {
        return challenges;
    }

    public static Challenge getChallengeById(String id) {
        for (Challenge c : challenges) {
            if (c.getId().equalsIgnoreCase(id)) return c;
        }
        return challenges.get(0);
    }
}
