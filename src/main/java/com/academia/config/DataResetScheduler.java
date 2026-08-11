package com.academia.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Rotina agendada que restaura o banco de dados da demo para o estado
 * inicial (seed), apagando tudo o que os visitantes cadastraram.
 *
 * Local no projeto: src/main/java/com/academia/config/DataResetScheduler.java
 *
 * IMPORTANTE: só executa de fato se "app.reset.enabled=true" estiver
 * configurado (ver application.properties / application-demo.properties).
 * Isso evita que o reset rode sem querer no seu ambiente local de
 * desenvolvimento e apague dados que você está usando para testar.
 */
@Component
public class DataResetScheduler {

    private static final Logger log = LoggerFactory.getLogger(DataResetScheduler.class);

    private final DataSource dataSource;
    private final boolean resetEnabled;

    public DataResetScheduler(DataSource dataSource,
                               @Value("${app.reset.enabled:false}") boolean resetEnabled) {
        this.dataSource = dataSource;
        this.resetEnabled = resetEnabled;
    }

    /**
     * O intervalo é lido de application.properties (chave app.reset.interval-ms),
     * em milissegundos. Se a chave não existir, usa 21600000 ms (6 horas) como padrão.
     *
     * Para ajustar o tempo, você NÃO precisa mexer neste arquivo — basta mudar
     * a propriedade app.reset.interval-ms. Alguns valores de referência:
     *   1 hora   -> 3600000
     *   3 horas  -> 10800000
     *   6 horas  -> 21600000
     *   12 horas -> 43200000
     *   24 horas -> 86400000
     *
     * fixedRate (em vez de fixedDelay) conta o intervalo a partir do início de
     * cada execução, então o reset roda em um ritmo previsível independente de
     * quanto tempo a execução anterior levou.
     */
    @Scheduled(fixedRateString = "${app.reset.interval-ms:21600000}")
    public void restaurarBancoParaEstadoInicial() {
        if (!resetEnabled) {
            // Proteção extra: garante que nada rode se o profile/flag não estiver ativo.
            return;
        }

        log.info("[DataReset] Iniciando restauração agendada do banco de dados...");

        // Observação sobre @Transactional:
        // Não usamos @Transactional aqui de propósito. O script executa TRUNCATE,
        // e no MySQL (InnoDB) TRUNCATE é um comando DDL que faz commit implícito —
        // ou seja, não pode ser desfeito por uma transação do Spring, com ou sem
        // @Transactional na frente do método. Se você quiser atomicidade real
        // (tudo ou nada), veja a variante com DELETE FROM explicada na resposta.
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/reset-data.sql"));
            log.info("[DataReset] Banco de dados restaurado com sucesso para o estado inicial.");
        } catch (SQLException e) {
            log.error("[DataReset] Falha ao restaurar o banco de dados", e);
        }
    }
}
