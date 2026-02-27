package dev.piyush.shyamduels.database;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import dev.piyush.shyamduels.ShyamDuels;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class DatabaseManagerOptimized {
    private final ShyamDuels plugin;
    private ComboPooledDataSource arenaDataSource;
    private ComboPooledDataSource kitDataSource;
    private ComboPooledDataSource statsDataSource;
    private String type;

    public DatabaseManagerOptimized(ShyamDuels plugin) {
        this.plugin = plugin;
        this.type = plugin.getConfig().getString("database.type", "sqlite");
    }

    public void init() {
        if (type.equalsIgnoreCase("mysql")) {
            initMySQL();
        } else {
            initSQLite();
        }
    }

    private void initSQLite() {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create data directory");
            return;
        }

        try {
            File arenaFile = new File(dataFolder, "arenas.db");
            if (!arenaFile.exists() && !arenaFile.createNewFile()) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create arenas.db");
                return;
            }
            
            arenaDataSource = createSQLitePool("jdbc:sqlite:" + arenaFile.getAbsolutePath());

            File kitFile = new File(dataFolder, "kits.db");
            if (!kitFile.exists() && !kitFile.createNewFile()) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create kits.db");
                return;
            }
            kitDataSource = createSQLitePool("jdbc:sqlite:" + kitFile.getAbsolutePath());

            File statsFile = new File(dataFolder, "stats.db");
            if (!statsFile.exists() && !statsFile.createNewFile()) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create stats.db");
                return;
            }
            statsDataSource = createSQLitePool("jdbc:sqlite:" + statsFile.getAbsolutePath());

            if (plugin.getConfig().getBoolean("debug", false)) {
                plugin.getLogger().info("SQLite databases initialized with connection pooling.");
            }
        } catch (IOException | PropertyVetoException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not initialize SQLite", e);
            closeAllConnections();
        }
    }

    private ComboPooledDataSource createSQLitePool(String jdbcUrl) throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("org.sqlite.JDBC");
        dataSource.setJdbcUrl(jdbcUrl);
        
        dataSource.setMinPoolSize(1);
        dataSource.setMaxPoolSize(5);
        dataSource.setAcquireIncrement(1);
        dataSource.setMaxIdleTime(300);
        dataSource.setCheckoutTimeout(5000);
        
        return dataSource;
    }

    private void initMySQL() {
        String host = plugin.getConfig().getString("database.mysql.host");
        int port = plugin.getConfig().getInt("database.mysql.port");
        String database = plugin.getConfig().getString("database.mysql.database");
        String username = plugin.getConfig().getString("database.mysql.username");
        String password = plugin.getConfig().getString("database.mysql.password");

        if (host == null || host.isEmpty() || database == null || database.isEmpty()) {
            plugin.getLogger().log(Level.SEVERE, "MySQL configuration is incomplete");
            return;
        }

        try {
            String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database 
                    + "?useSSL=false&autoReconnect=true&useUnicode=true&characterEncoding=UTF-8";
            
            arenaDataSource = createMySQLPool(jdbcUrl, username, password);
            kitDataSource = arenaDataSource;
            statsDataSource = arenaDataSource;
            
            plugin.getLogger().info("MySQL connected with connection pooling.");
        } catch (PropertyVetoException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to MySQL", e);
        }
    }

    private ComboPooledDataSource createMySQLPool(String jdbcUrl, String username, String password) 
            throws PropertyVetoException {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password != null ? password : "");
        
        dataSource.setMinPoolSize(3);
        dataSource.setMaxPoolSize(20);
        dataSource.setAcquireIncrement(2);
        dataSource.setMaxIdleTime(1800);
        dataSource.setCheckoutTimeout(10000);
        dataSource.setIdleConnectionTestPeriod(300);
        dataSource.setTestConnectionOnCheckout(false);
        dataSource.setTestConnectionOnCheckin(true);
        dataSource.setPreferredTestQuery("SELECT 1");
        
        return dataSource;
    }

    public Connection getArenaConnection() throws SQLException {
        if (arenaDataSource == null) {
            throw new SQLException("Arena data source not initialized");
        }
        return arenaDataSource.getConnection();
    }

    public Connection getKitConnection() throws SQLException {
        if (kitDataSource == null) {
            throw new SQLException("Kit data source not initialized");
        }
        return kitDataSource.getConnection();
    }

    public Connection getStatsConnection() throws SQLException {
        if (statsDataSource == null) {
            throw new SQLException("Stats data source not initialized");
        }
        return statsDataSource.getConnection();
    }

    public void close() {
        closeAllConnections();
    }

    private void closeAllConnections() {
        if (arenaDataSource != null) {
            arenaDataSource.close();
            arenaDataSource = null;
        }
        
        if (kitDataSource != null && kitDataSource != arenaDataSource) {
            kitDataSource.close();
            kitDataSource = null;
        }
        
        if (statsDataSource != null && statsDataSource != arenaDataSource) {
            statsDataSource.close();
            statsDataSource = null;
        }
    }
}
