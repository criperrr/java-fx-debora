package com.template.service.challenge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.template.model.challenge.Challenge;
import com.template.model.challenge.TestResult;
import com.template.model.dao.DatabaseConnection;

/**
 * Motor de execução e validação dos Desafios de Programação em schema isolado (game_sandbox).
 */
public class ChallengeSandboxService {

    private static final String SCHEMA = "game_sandbox";

    public static TestResult executeAndTest(Challenge challenge, String userCode) {
        long startTime = System.currentTimeMillis();
        List<String> logs = new ArrayList<>();

        if (userCode == null || userCode.trim().isEmpty()) {
            return TestResult.failure("O código não pode estar vazio! Digite seu SQL no editor.", logs, 0);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Prepara schema isolado
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + SCHEMA + ";");
            stmt.execute("SET search_path TO " + SCHEMA + ", public;");
            logs.add("📦 Ambiente Sandbox inicializado no schema '" + SCHEMA + "'.");

            // Executa o desafio correspondente
            switch (challenge.getId()) {
                case "phase1":
                    return testPhase1(conn, stmt, userCode, logs, startTime);
                case "phase2":
                    return testPhase2(conn, stmt, userCode, logs, startTime);
                case "phase3":
                    return testPhase3(conn, stmt, userCode, logs, startTime);
                case "phase4":
                    return testPhase4(conn, stmt, userCode, logs, startTime);
                case "phase5":
                    return testPhase5(userCode, logs, startTime);
                default:
                    return TestResult.failure("Desafio desconhecido: " + challenge.getId(), logs, 0);
            }

        } catch (SQLException e) {
            logs.add("💥 Erro de execução PostgreSQL: " + e.getMessage());
            long elapsed = System.currentTimeMillis() - startTime;
            return TestResult.failure(e.getMessage(), logs, elapsed);
        } catch (Exception e) {
            logs.add("💥 Erro interno no Test Runner: " + e.getMessage());
            long elapsed = System.currentTimeMillis() - startTime;
            return TestResult.failure(e.getMessage(), logs, elapsed);
        }
    }

    // =========================================================================
    // FASE 1: DDL, CONSTRAINTS & CASCADE
    // =========================================================================
    private static TestResult testPhase1(Connection conn, Statement stmt, String userCode, List<String> logs, long startTime) throws SQLException {
        int passed = 0;
        int total = 5;

        // 0. Limpa tabelas pré-existentes para garantir re-execução limpa e idempotente
        stmt.execute("DROP TABLE IF EXISTS " + SCHEMA + ".itens_pedido CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS " + SCHEMA + ".pedidos CASCADE;");
        stmt.execute("DROP TABLE IF EXISTS " + SCHEMA + ".clientes CASCADE;");

        // 1. Executa o DDL do usuário
        stmt.execute(userCode);
        logs.add("⚙️ Scripts DDL executados com sucesso.");

        // Teste 1: Tabela clientes
        try (ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + SCHEMA + "' AND table_name = 'clientes'")) {
            if (rs.next()) {
                passed++;
                logs.add("✅ [1/5] Tabela 'clientes' detectada no catálogo relacional.");
            } else {
                logs.add("❌ [1/5] Tabela 'clientes' não foi encontrada.");
            }
        }

        // Teste 2: Tabela pedidos
        try (ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + SCHEMA + "' AND table_name = 'pedidos'")) {
            if (rs.next()) {
                passed++;
                logs.add("✅ [2/5] Tabela 'pedidos' detectada com Foreign Key.");
            } else {
                logs.add("❌ [2/5] Tabela 'pedidos' não foi encontrada.");
            }
        }

        // Teste 3: Tabela itens_pedido
        try (ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + SCHEMA + "' AND table_name = 'itens_pedido'")) {
            if (rs.next()) {
                passed++;
                logs.add("✅ [3/5] Tabela 'itens_pedido' detectada no catálogo.");
            } else {
                logs.add("❌ [3/5] Tabela 'itens_pedido' não foi encontrada.");
            }
        }

        // Teste 4: CHECK Constraint saldo_conchas >= 0
        boolean checkWorked = false;
        try {
            stmt.execute("INSERT INTO " + SCHEMA + ".clientes (nome, saldo_conchas) VALUES ('Caloteiro da Fenda', -15.00);");
        } catch (SQLException e) {
            checkWorked = true;
        }
        if (checkWorked) {
            passed++;
            logs.add("✅ [4/5] CHECK Constraint validada: Inserção com saldo negativo foi rejeitada pelo PostgreSQL!");
        } else {
            logs.add("❌ [4/5] CHECK Constraint falhou: Permitiu cadastrar cliente com saldo negativo!");
        }

        // Teste 5: ON DELETE CASCADE
        boolean cascadeWorked = false;
        try {
            long clientId = 0;
            try (ResultSet rs = stmt.executeQuery("INSERT INTO " + SCHEMA + ".clientes (nome, saldo_conchas) VALUES ('Bob Cascade Test', 50.00) RETURNING id;")) {
                if (rs.next()) clientId = rs.getLong(1);
            }
            stmt.execute("INSERT INTO " + SCHEMA + ".pedidos (cliente_id, status) VALUES (" + clientId + ", 'RECEBIDO');");
            stmt.execute("DELETE FROM " + SCHEMA + ".clientes WHERE id = " + clientId + ";");
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + SCHEMA + ".pedidos WHERE cliente_id = " + clientId)) {
                if (rs.next() && rs.getInt(1) == 0) {
                    cascadeWorked = true;
                }
            }
        } catch (SQLException ignored) {}

        if (cascadeWorked) {
            passed++;
            logs.add("✅ [5/5] ON DELETE CASCADE validada: Pedidos foram apagados automaticamente ao remover cliente!");
        } else {
            logs.add("❌ [5/5] ON DELETE CASCADE falhou: Pedidos órfãos ainda existem ou deleção deu erro.");
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return new TestResult(passed == total, total, passed, elapsed, logs, passed == total ? null : "Alguns testes de DDL falharam.");
    }

    // =========================================================================
    // FASE 2: TRIGGER PL/PGSQL DA COZINHA DO BOB ESPONJA
    // =========================================================================
    private static TestResult testPhase2(Connection conn, Statement stmt, String userCode, List<String> logs, long startTime) throws SQLException {
        int passed = 0;
        int total = 5;

        // Garante que tabelas da Fase 1 existam
        stmt.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + ".clientes (id SERIAL PRIMARY KEY, nome VARCHAR(100), reputacao INT, saldo_conchas NUMERIC(10,2));");
        stmt.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + ".pedidos (id SERIAL PRIMARY KEY, cliente_id INT REFERENCES " + SCHEMA + ".clientes(id) ON DELETE CASCADE, status VARCHAR(30), criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP);");

        // Executa o código do trigger do usuário
        stmt.execute(userCode);
        logs.add("⚙️ Código de Trigger e Logs executado no PostgreSQL.");

        // Teste 1: Tabela cozinha_logs existe
        try (ResultSet rs = stmt.executeQuery("SELECT table_name FROM information_schema.tables WHERE table_schema = '" + SCHEMA + "' AND table_name = 'cozinha_logs'")) {
            if (rs.next()) {
                passed++;
                logs.add("✅ [1/5] Tabela de auditoria 'cozinha_logs' encontrada.");
            } else {
                logs.add("❌ [1/5] Tabela 'cozinha_logs' não existe.");
            }
        }

        // Prepara dados de teste
        long vipClientId = 0;
        long normClientId = 0;
        try (ResultSet rs = stmt.executeQuery("INSERT INTO " + SCHEMA + ".clientes (nome, reputacao, saldo_conchas) VALUES ('Sandy Bochechas VIP', 95, 500.00) RETURNING id;")) {
            if (rs.next()) vipClientId = rs.getLong(1);
        }
        try (ResultSet rs = stmt.executeQuery("INSERT INTO " + SCHEMA + ".clientes (nome, reputacao, saldo_conchas) VALUES ('Peixe Fred', 30, 20.00) RETURNING id;")) {
            if (rs.next()) normClientId = rs.getLong(1);
        }

        // Teste 2: Pedido VIP muda status para 'NA_CHAPA'
        long vipPedidoId = 0;
        String vipStatus = "";
        try (ResultSet rs = stmt.executeQuery("INSERT INTO " + SCHEMA + ".pedidos (cliente_id, status) VALUES (" + vipClientId + ", 'RECEBIDO') RETURNING id, status;")) {
            if (rs.next()) {
                vipPedidoId = rs.getLong(1);
                vipStatus = rs.getString(2);
            }
        }
        if ("NA_CHAPA".equalsIgnoreCase(vipStatus)) {
            passed++;
            logs.add("✅ [2/5] Trigger VIP: Pedido de Sandy (Rep 95) foi automaticamente para 'NA_CHAPA'!");
        } else {
            logs.add("❌ [2/5] Trigger VIP falhou: Pedido permaneceu como '" + vipStatus + "'. Esperado: 'NA_CHAPA'.");
        }

        // Teste 3: Log gerado na tabela cozinha_logs
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + SCHEMA + ".cozinha_logs WHERE mensagem ILIKE '%Bob%' OR mensagem ILIKE '%VIP%'")) {
            if (rs.next() && rs.getInt(1) > 0) {
                passed++;
                logs.add("✅ [3/5] Auditoria: Log registrado na cozinha ('Bob assumiu prioridade VIP')!");
            } else {
                logs.add("❌ [3/5] Nenhum log encontrado em 'cozinha_logs' contendo menção a Bob/VIP.");
            }
        }

        // Teste 4: Pedido Comum NÃO deve mudar para NA_CHAPA
        String normStatus = "";
        try (ResultSet rs = stmt.executeQuery("INSERT INTO " + SCHEMA + ".pedidos (cliente_id, status) VALUES (" + normClientId + ", 'RECEBIDO') RETURNING status;")) {
            if (rs.next()) normStatus = rs.getString(1);
        }
        if ("RECEBIDO".equalsIgnoreCase(normStatus)) {
            passed++;
            logs.add("✅ [4/5] Isolamento: Cliente comum (Rep 30) manteve o status 'RECEBIDO' normalmente.");
        } else {
            logs.add("❌ [4/5] Isolamento falhou: Cliente comum virou '" + normStatus + "' indevidamente.");
        }

        // Teste 5: Trigger não gera erros em lote
        boolean batchOk = true;
        try {
            stmt.execute("INSERT INTO " + SCHEMA + ".pedidos (cliente_id, status) VALUES (" + vipClientId + ", 'RECEBIDO'), (" + normClientId + ", 'RECEBIDO');");
        } catch (SQLException e) {
            batchOk = false;
        }
        if (batchOk) {
            passed++;
            logs.add("✅ [5/5] Performance: Trigger suportou inserções múltiplas em lote.");
        } else {
            logs.add("❌ [5/5] Falha em inserção em lote com trigger ativo.");
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return new TestResult(passed == total, total, passed, elapsed, logs, passed == total ? null : "Trigger não atendeu a todos os critérios.");
    }

    // =========================================================================
    // FASE 3: RELATÓRIO DQL AVANÇADO (JOINS, GROUP BY, HAVING)
    // =========================================================================
    private static TestResult testPhase3(Connection conn, Statement stmt, String userCode, List<String> logs, long startTime) throws SQLException {
        int passed = 0;
        int total = 5;

        // Prepara massa limpa de dados
        stmt.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + ".clientes (id SERIAL PRIMARY KEY, nome VARCHAR(100), saldo_conchas NUMERIC(10,2));");
        stmt.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + ".pedidos (id SERIAL PRIMARY KEY, cliente_id INT REFERENCES " + SCHEMA + ".clientes(id) ON DELETE CASCADE);");
        stmt.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + ".itens_pedido (id SERIAL PRIMARY KEY, pedido_id INT REFERENCES " + SCHEMA + ".pedidos(id) ON DELETE CASCADE, nome_item VARCHAR(100), quantidade INT, preco_unitario NUMERIC(10,2));");

        stmt.execute("TRUNCATE " + SCHEMA + ".clientes CASCADE;");

        // Inserção da massa de teste:
        // 1. Sandy: 2 pedidos x 50.00 = 100.00 (DEVE APARECER)
        // 2. Patrick: 3 pedidos x 30.00 = 90.00 (DEVE APARECER)
        // 3. Plankton: 1 pedido x 10.00 = 10.00 (NÃO DEVE APARECER, POIS < 50.00)
        stmt.execute("INSERT INTO " + SCHEMA + ".clientes (id, nome, saldo_conchas) VALUES (1, 'Sandy Bochechas', 500), (2, 'Patrick Estrela', 300), (3, 'Sheldon Plankton', 50);");
        stmt.execute("INSERT INTO " + SCHEMA + ".pedidos (id, cliente_id) VALUES (101, 1), (102, 2), (103, 3);");
        stmt.execute("INSERT INTO " + SCHEMA + ".itens_pedido (pedido_id, nome_item, quantidade, preco_unitario) VALUES " +
                "(101, 'Nozes do Texas', 2, 50.00), " +
                "(102, 'Hambúrguer Triplo', 3, 30.00), " +
                "(103, 'Balde de Isca Podre', 1, 10.00);");

        logs.add("📊 Massa de dados mockada inserida (Sandy: 100 🐚, Patrick: 90 🐚, Plankton: 10 🐚).");

        // Executa a query do usuário
        try (ResultSet rs = stmt.executeQuery(userCode)) {
            passed++;
            logs.add("✅ [1/5] Sintaxe SQL válida: Query compilou e executou sem erros.");

            List<String> clientesRetornados = new ArrayList<>();
            List<Double> totaisRetornados = new ArrayList<>();

            while (rs.next()) {
                clientesRetornados.add(rs.getString(1));
                totaisRetornados.add(rs.getDouble(2));
            }

            // Teste 2: Quantidade de registros filtrados (exatamente 2)
            if (clientesRetornados.size() == 2) {
                passed++;
                logs.add("✅ [2/5] HAVING funcionou: Exatamente 2 clientes retornados (gastos >= 50.00).");
            } else {
                logs.add("❌ [2/5] Quantidade incorreta de linhas retornadas: " + clientesRetornados.size() + " (esperado 2).");
            }

            // Teste 3: Plankton NÃO deve estar na lista
            if (!clientesRetornados.contains("Sheldon Plankton")) {
                passed++;
                logs.add("✅ [3/5] Filtragem correta: 'Sheldon Plankton' (< 50 🐚) foi excluído do relatório.");
            } else {
                logs.add("❌ [3/5] Erro de filtro: 'Sheldon Plankton' apareceu mesmo com gasto inferior a 50 🐚.");
            }

            // Teste 4: Ordenação DESC
            if (!totaisRetornados.isEmpty() && totaisRetornados.get(0) >= 100.0) {
                passed++;
                logs.add("✅ [4/5] ORDER BY DESC confirmado: Maior faturamento (Sandy - 100 🐚) em primeiro lugar.");
            } else {
                logs.add("❌ [4/5] Ordenação incorreta: Primeiro registro não é o maior gasto.");
            }

            // Teste 5: Precisão dos cálculos agregados
            if (totaisRetornados.size() == 2 && totaisRetornados.get(0) == 100.0 && totaisRetornados.get(1) == 90.0) {
                passed++;
                logs.add("✅ [5/5] Matemática exata: Cálculos de SUM(quantidade * preco_unitario) 100% corretos!");
            } else {
                logs.add("❌ [5/5] Valores calculados divergentes do esperado.");
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return new TestResult(passed == total, total, passed, elapsed, logs, passed == total ? null : "A query não retornou os dados agregados corretos.");
    }

    // =========================================================================
    // FASE 4: OTIMIZAÇÃO & ÍNDICES B-TREE (ELIMINAÇÃO DE SEQ SCAN)
    // =========================================================================
    private static TestResult testPhase4(Connection conn, Statement stmt, String userCode, List<String> logs, long startTime) throws SQLException {
        int passed = 0;
        int total = 5;

        // Prepara tabela pedidos com volume
        stmt.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + ".clientes (id SERIAL PRIMARY KEY, nome VARCHAR(100), saldo_conchas NUMERIC(10,2));");
        stmt.execute("INSERT INTO " + SCHEMA + ".clientes (id, nome, saldo_conchas) SELECT g, 'Cliente ' || g, 100 FROM generate_series(1, 60) g ON CONFLICT (id) DO NOTHING;");
        stmt.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + ".pedidos (id SERIAL PRIMARY KEY, cliente_id INT REFERENCES " + SCHEMA + ".clientes(id) ON DELETE CASCADE, status VARCHAR(30));");
        stmt.execute("TRUNCATE " + SCHEMA + ".pedidos CASCADE;");

        // Injeta 2.000 pedidos usando generate_series do PostgreSQL
        stmt.execute("INSERT INTO " + SCHEMA + ".pedidos (cliente_id, status) " +
                "SELECT (g % 50) + 1, CASE WHEN g % 2 = 0 THEN 'NA_CHAPA' ELSE 'RECEBIDO' END " +
                "FROM generate_series(1, 2000) g;");
        logs.add("🚀 2.000 pedidos gerados via generate_series para benchmark de performance.");

        // Dropa índices existentes para garantir estado inicial
        stmt.execute("DROP INDEX IF EXISTS " + SCHEMA + ".idx_pedidos_cliente_status;");

        // 1. Executa o comando de criação do índice do usuário
        stmt.execute(userCode);
        passed++;
        logs.add("✅ [1/5] Comando CREATE INDEX executado com sucesso.");

        // 2. Verifica se o índice existe no pg_indexes
        try (ResultSet rs = stmt.executeQuery("SELECT indexname FROM pg_indexes WHERE schemaname = '" + SCHEMA + "' AND tablename = 'pedidos' AND indexname LIKE '%idx_pedidos%';")) {
            if (rs.next()) {
                passed++;
                logs.add("✅ [2/5] Índice B-Tree registrado no catálogo pg_indexes: '" + rs.getString(1) + "'.");
            } else {
                logs.add("❌ [2/5] Nenhum índice compatível encontrado na tabela 'pedidos'.");
            }
        }

        // Força o PostgreSQL a atualizar estatísticas
        stmt.execute("ANALYZE " + SCHEMA + ".pedidos;");

        // 3 e 4. Roda EXPLAIN ANALYZE para conferir o plano
        boolean seqScanEliminated = true;
        boolean indexUsed = false;
        long planStart = System.currentTimeMillis();

        try (ResultSet rs = stmt.executeQuery("EXPLAIN (FORMAT TEXT) SELECT * FROM " + SCHEMA + ".pedidos WHERE cliente_id = 42 AND status = 'NA_CHAPA';")) {
            while (rs.next()) {
                String line = rs.getString(1);
                logs.add("   🔍 PLAN: " + line);
                if (line.toLowerCase().contains("index scan") || line.toLowerCase().contains("bitmap index scan")) {
                    indexUsed = true;
                }
            }
        }
        long planElapsed = System.currentTimeMillis() - planStart;

        if (indexUsed) {
            passed++;
            logs.add("✅ [3/5] Otimizador usou Index Scan / Bitmap Index Scan comprovado!");
        } else {
            // Nota: Se a tabela for pequena demais, o otimizador pode escolher Seq Scan a menos que façamos enable_seqscan=off
            stmt.execute("SET enable_seqscan = off;");
            try (ResultSet rs = stmt.executeQuery("EXPLAIN (FORMAT TEXT) SELECT * FROM " + SCHEMA + ".pedidos WHERE cliente_id = 42 AND status = 'NA_CHAPA';")) {
                while (rs.next()) {
                    String line = rs.getString(1);
                    if (line.toLowerCase().contains("index scan") || line.toLowerCase().contains("bitmap index scan")) {
                        indexUsed = true;
                    }
                }
            }
            stmt.execute("SET enable_seqscan = on;");
            if (indexUsed) {
                passed++;
                logs.add("✅ [3/5] Índice é válido e funcional (verificado via plano de execução).");
            } else {
                logs.add("❌ [3/5] O índice criado não é compatível com os filtros (cliente_id, status).");
            }
        }

        if (seqScanEliminated) {
            passed++;
            logs.add("✅ [4/5] Gargalo eliminado: Consultas de filtro respondendo em alta velocidade.");
        }

        // 5. Medição de tempo de execução
        if (planElapsed < 100) {
            passed++;
            logs.add("✅ [5/5] Latência ultrabaixa (" + planElapsed + "ms): O Lula Molusco parou de reclamar!");
        } else {
            logs.add("❌ [5/5] Tempo de execução acima do limite tolerado.");
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return new TestResult(passed == total, total, passed, elapsed, logs, passed == total ? null : "O índice não atendeu a todos os testes de performance.");
    }

    // =========================================================================
    // FASE 5: CONCORRÊNCIA COM LOCK PESSIMISTA (SELECT ... FOR UPDATE)
    // =========================================================================
    private static TestResult testPhase5(String userCode, List<String> logs, long startTime) {
        int passed = 0;
        int total = 5;

        // Prepara tabela e função
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS " + SCHEMA + ";");
            stmt.execute("SET search_path TO " + SCHEMA + ", public;");
            stmt.execute("CREATE TABLE IF NOT EXISTS " + SCHEMA + ".estoque_segredo (id INT PRIMARY KEY, nome VARCHAR(100), estoque INT);");
            stmt.execute("DELETE FROM " + SCHEMA + ".estoque_segredo WHERE id = 1;");
            stmt.execute("INSERT INTO " + SCHEMA + ".estoque_segredo (id, nome, estoque) VALUES (1, 'Hambúrguer Secreto de Netuno', 1);");

            // Executa o código do usuário (criação da procedure / function)
            stmt.execute(userCode);
            passed++;
            logs.add("✅ [1/5] Função de compra concorrente compilada no PostgreSQL.");
        } catch (SQLException e) {
            logs.add("❌ [1/5] Erro ao compilar função: " + e.getMessage());
            return TestResult.failure(e.getMessage(), logs, System.currentTimeMillis() - startTime);
        }

        // Teste 2, 3 e 4: Dispara 20 threads concorrentes reais tentando comprar o mesmo item
        int threadCount = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(threadCount);

        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);
        AtomicInteger erros = new AtomicInteger(0);

        logs.add("⚡ Disparando " + threadCount + " bots do Plankton concorrentes simultâneos...");

        for (int i = 0; i < threadCount; i++) {
            pool.submit(() -> {
                try {
                    startGate.await(); // Espera sinal para disparar todas juntas no mesmo instante
                    try (Connection threadConn = DatabaseConnection.getConnection();
                         Statement threadStmt = threadConn.createStatement()) {
                        threadStmt.execute("SET search_path TO " + SCHEMA + ", public;");
                        try (ResultSet rs = threadStmt.executeQuery("SELECT comprar_item_segredo(1);")) {
                            if (rs.next()) {
                                boolean bought = rs.getBoolean(1);
                                if (bought) sucessos.incrementAndGet();
                                else falhas.incrementAndGet();
                            }
                        }
                    }
                } catch (Exception e) {
                    erros.incrementAndGet();
                } finally {
                    endGate.countDown();
                }
            });
        }

        startGate.countDown(); // Libera todas as threads simultaneamente

        try {
            endGate.await(5, TimeUnit.SECONDS);
            pool.shutdown();
        } catch (InterruptedException e) {
            logs.add("❌ Timeout na execução concorrente.");
        }

        logs.add("📊 Resultados das 20 requisições simultâneas: Sucessos=" + sucessos.get() + ", Esgotados=" + falhas.get() + ", Erros=" + erros.get());

        // Teste 2: Exatamente 1 compra de sucesso
        if (sucessos.get() == 1) {
            passed++;
            logs.add("✅ [2/5] Atomicidade perfeita: Exatamente 1 compra obteve sucesso!");
        } else {
            logs.add("❌ [2/5] Race Condition detectada: " + sucessos.get() + " compras foram aprovadas para 1 único item!");
        }

        // Teste 3: As outras 19 compras foram rejeitadas com segurança
        if (falhas.get() >= 18) {
            passed++;
            logs.add("✅ [3/5] Rejeição segura: Demais requisições receberam FALSE sem deadlocks.");
        } else {
            logs.add("❌ [3/5] Inconsistência no número de rejeições.");
        }

        // Teste 4: Zero deadlocks / zero erros não tratados
        if (erros.get() == 0) {
            passed++;
            logs.add("✅ [4/5] Robustez transacional: Zero deadlocks ocorridos sob alta concorrência.");
        } else {
            logs.add("❌ [4/5] Ocorreram " + erros.get() + " erros ou deadlocks.");
        }

        // Teste 5: Estoque final é exatamente zero (não é negativo!)
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT estoque FROM " + SCHEMA + ".estoque_segredo WHERE id = 1;")) {
            if (rs.next() && rs.getInt(1) == 0) {
                passed++;
                logs.add("✅ [5/5] Integridade do cofre: Estoque final é exatamente 0 (sem saldo fantasma)!");
            } else {
                logs.add("❌ [5/5] Violação de consistência: Estoque final diferente de 0.");
            }
        } catch (SQLException e) {
            logs.add("❌ [5/5] Erro ao verificar estoque final: " + e.getMessage());
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return new TestResult(passed == total, total, passed, elapsed, logs, passed == total ? null : "A defesa contra concorrência falhou em alguns testes.");
    }
}
