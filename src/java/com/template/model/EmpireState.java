package com.template.model;

/**
 * Estado do Império Comercial e Árvore de Habilidades do PostgreSQL da Fenda do Biquíni.
 */
public class EmpireState {
    private static long coins = 50; // Começa com 50 conchas para testar
    private static int level = 1;
    private static int burgersCooked = 0;
    private static int ordersFulfilled = 0;

    // Skills do Banco de Dados PostgreSQL
    private static boolean btreeIndex = false;       // Acelera queries e timer de pedidos em 40%
    private static boolean connectionPool = false;   // Permite dobrar o ganho por pedido
    private static boolean autoVacuum = false;       // Automação: Bob Esponja cozinha sozinho a cada segundo
    private static boolean firewallPlankton = false; // Bônus de segurança e +50% de conchas

    public static long getCoins() {
        return coins;
    }

    public static void addCoins(long amount) {
        coins += amount;
    }

    public static boolean spendCoins(long amount) {
        if (coins >= amount) {
            coins -= amount;
            return true;
        }
        return false;
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int newLevel) {
        level = newLevel;
    }

    public static int getBurgersCooked() {
        return burgersCooked;
    }

    public static void incrementBurgersCooked() {
        burgersCooked++;
    }

    public static int getOrdersFulfilled() {
        return ordersFulfilled;
    }

    public static void incrementOrdersFulfilled() {
        ordersFulfilled++;
    }

    public static boolean hasBtreeIndex() {
        return btreeIndex;
    }

    public static void setBtreeIndex(boolean val) {
        btreeIndex = val;
    }

    public static boolean hasConnectionPool() {
        return connectionPool;
    }

    public static void setConnectionPool(boolean val) {
        connectionPool = val;
    }

    public static boolean hasAutoVacuum() {
        return autoVacuum;
    }

    public static void setAutoVacuum(boolean val) {
        autoVacuum = val;
    }

    public static boolean hasFirewallPlankton() {
        return firewallPlankton;
    }

    public static void setFirewallPlankton(boolean val) {
        firewallPlankton = val;
    }

    public static void reset() {
        coins = 0;
        level = 1;
        burgersCooked = 0;
        ordersFulfilled = 0;
        btreeIndex = false;
        connectionPool = false;
        autoVacuum = false;
        firewallPlankton = false;
    }
}
