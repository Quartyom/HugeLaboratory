package com.quartyom.lab6.Database;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quartyom.lab6.QuApplication;
import com.quartyom.lab6.NetworkDevices.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

public class RemoteDB {
    private static final Logger log = LoggerFactory.getLogger("db");
    QuApplication application;
    ObjectMapper mapper;
    private final String connectionURL;
    private final String username;
    private final String password;

    public RemoteDB(QuApplication application){
        this.application = application;
        this.mapper = application.mapper;

        Properties properties = application.properties;
        connectionURL = properties.getProperty("dbURL");
        username = properties.getProperty("dbUsername");
        password = properties.getProperty("dbPassword");
    }

    public boolean saveToDB(){
        ArrayList<Smartphone> smartphones = new ArrayList<>();
        ArrayList<Tablet> tablets = new ArrayList<>();
        ArrayList<Computer> computers = new ArrayList<>();

        for (NetworkDevice device : application.localDB.getDevices()){
            if (device instanceof Smartphone smartphone){
                smartphones.add(smartphone);
            }
            else if (device instanceof Tablet tablet){
                tablets.add(tablet);
            }
            else if (device instanceof Computer computer){
                computers.add(computer);
            }
            else {
                log.error("Unknown type of " + device);
            }
        }

        return saveSmartphones(smartphones) &&
                saveTablets(tablets) &&
                saveComputers(computers);
    }
    private boolean saveSmartphones(ArrayList<Smartphone> smartphones){
        try (Connection conn = DriverManager.getConnection(connectionURL, username, password)) {
            StringBuilder ss = new StringBuilder("INSERT INTO smartphones VALUES ");

            for (int i = 0; i < smartphones.size(); i++){
                ss.append("(?,?,?,?,?,?,?)");
                ss.append(i==smartphones.size() - 1? ";" : ",");
            }
            PreparedStatement ps = conn.prepareStatement(ss.toString());

            for (int i = 0; i < smartphones.size(); i++){
                Smartphone item = smartphones.get(i);
                String wifi = null, blue = null;
                try {
                    wifi = mapper.writeValueAsString(item.acceptedWiFiConnectionsId);
                    blue = mapper.writeValueAsString(item.acceptedBluetoothConnectionsId);
                }
                catch (Exception ex) {
                    log.error(ex.getMessage());
                }

                ps.setLong(7 * i + 1, item.id);
                ps.setString(7 * i + 2, item.name);
                ps.setFloat(7 * i + 3, item.position);
                ps.setString(7 * i + 4, item.callResponse);
                if (item.connectedWiFiToId == null){
                    ps.setString(7 * i + 5, null);
                }
                else {
                    ps.setLong(7 * i + 5, item.connectedWiFiToId);
                }
                ps.setString(7 * i + 6, wifi);
                ps.setString(7 * i + 7, blue);
            }
            conn.prepareStatement("TRUNCATE smartphones").execute();
            if (!smartphones.isEmpty()) {
                ps.execute();
            }
        }
        catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
        log.info("Saved smartphones");
        return true;
    }
    private boolean saveTablets(ArrayList<Tablet> tablets){
        try (Connection conn = DriverManager.getConnection(connectionURL, username, password)) {
            StringBuilder ss = new StringBuilder("INSERT INTO tablets VALUES ");

            for (int i = 0; i < tablets.size(); i++){
                ss.append("(?,?,?,?,?,?,?)");
                ss.append(i==tablets.size() - 1? ";" : ",");
            }
            PreparedStatement ps = conn.prepareStatement(ss.toString());

            for (int i = 0; i < tablets.size(); i++){
                Tablet item = tablets.get(i);
                String wifi = null, blue = null;
                try {
                    wifi = mapper.writeValueAsString(item.acceptedWiFiConnectionsId);
                    blue = mapper.writeValueAsString(item.acceptedBluetoothConnectionsId);
                }
                catch (Exception ex) {
                    log.error(ex.getMessage());
                }

                ps.setLong(7 * i + 1, item.id);
                ps.setString(7 * i + 2, item.name);
                ps.setFloat(7 * i + 3, item.position);
                ps.setString(7 * i + 4, item.drawing);
                if (item.connectedWiFiToId == null){
                    ps.setString(7 * i + 5, null);
                }
                else {
                    ps.setLong(7 * i + 5, item.connectedWiFiToId);
                }
                ps.setString(7 * i + 6, wifi);
                ps.setString(7 * i + 7, blue);
            }
            conn.prepareStatement("TRUNCATE tablets").execute();
            if (!tablets.isEmpty()) {
                ps.execute();
            }
        }
        catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
        log.info("Saved tablets");
        return true;
    }
    private boolean saveComputers(ArrayList<Computer> computers){
        try (Connection conn = DriverManager.getConnection(connectionURL, username, password)) {
            StringBuilder ss = new StringBuilder("INSERT INTO computers VALUES ");

            for (int i = 0; i < computers.size(); i++){
                ss.append("(?,?,?,?,?,?,?)");
                ss.append(i==computers.size() - 1? ";" : ",");
            }
            PreparedStatement ps = conn.prepareStatement(ss.toString());

            for (int i = 0; i < computers.size(); i++){
                Computer item = computers.get(i);
                String wifi = null, blue = null;
                try {
                    wifi = mapper.writeValueAsString(item.acceptedWiFiConnectionsId);
                    blue = mapper.writeValueAsString(item.acceptedBluetoothConnectionsId);
                }
                catch (Exception ex) {
                    log.error(ex.getMessage());
                }

                ps.setLong(7 * i + 1, item.id);
                ps.setString(7 * i + 2, item.name);
                ps.setFloat(7 * i + 3, item.position);
                ps.setString(7 * i + 4, item.wireResponse);
                if (item.connectedWiFiToId == null){
                    ps.setString(7 * i + 5, null);
                }
                else {
                    ps.setLong(7 * i + 5, item.connectedWiFiToId);
                }
                ps.setString(7 * i + 6, wifi);
                ps.setString(7 * i + 7, blue);
            }
            conn.prepareStatement("TRUNCATE computers").execute();
            if (!computers.isEmpty()) {
                ps.execute();
            }
        }
        catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
        log.info("Saved computers");
        return true;
    }
    public boolean loadFromDB() {
        ArrayList<NetworkDevice> devices = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(connectionURL, username, password);
            PreparedStatement pss = conn.prepareStatement("SELECT * FROM smartphones");
            ResultSet rss = pss.executeQuery();
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM tablets");
            ResultSet rst = pst.executeQuery();
            PreparedStatement psc = conn.prepareStatement("SELECT * FROM computers");
            ResultSet rsc = psc.executeQuery())
        {

            devices.addAll(smartphonesFromResultSet(rss));
            devices.addAll(tabletsFromResultSet(rst));
            devices.addAll(computersFromResultSet(rsc));

        }
        catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }

        application.localDB.setDevices(devices);
        log.info("set devices to local db");
        return true;
    }

    private ArrayList<Smartphone> smartphonesFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Smartphone> devices = new ArrayList<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            Smartphone smartphone = new Smartphone(id);
            smartphone.name = rs.getString("name");
            smartphone.position = rs.getFloat("position");
            smartphone.callResponse = rs.getString("call_response");
            smartphone.connectedWiFiToId = rs.getLong("connected_wifi_to_id");
            try {
                String wifi = rs.getString("accepted_wifi_connections");
                if (wifi != null) {
                    smartphone.acceptedWiFiConnectionsId.addAll(
                            mapper.readValue(wifi, new TypeReference<HashSet<Long>>(){}));
                }
                String blue = rs.getString("accepted_bluetooth_connections");
                if (blue != null) {
                    smartphone.acceptedBluetoothConnectionsId.addAll(
                            mapper.readValue(blue, new TypeReference<HashSet<Long>>(){}));
                }
            }
            catch (Exception e){
                log.error(e.getMessage());
            }
            devices.add(smartphone);
        }
        log.info("loaded smartphones");
        return devices;
    }

    private ArrayList<Tablet> tabletsFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Tablet> devices = new ArrayList<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            Tablet tablet = new Tablet(id);
            tablet.name = rs.getString("name");
            tablet.position = rs.getFloat("position");
            tablet.drawing = rs.getString("drawing");
            tablet.connectedWiFiToId = rs.getLong("connected_wifi_to_id");
            try {
                String wifi = rs.getString("accepted_wifi_connections");
                if (wifi != null) {
                    tablet.acceptedWiFiConnectionsId.addAll(
                            mapper.readValue(wifi, new TypeReference<HashSet<Long>>(){}));
                }
                String blue = rs.getString("accepted_bluetooth_connections");
                if (blue != null) {
                    tablet.acceptedBluetoothConnectionsId.addAll(
                            mapper.readValue(blue, new TypeReference<HashSet<Long>>(){}));
                }
            }
            catch (Exception e){
                log.error(e.getMessage());
            }
            devices.add(tablet);
        }
        log.info("loaded tablets");
        return devices;
    }

    private ArrayList<Computer> computersFromResultSet(ResultSet rs) throws SQLException {
        ArrayList<Computer> devices = new ArrayList<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            Computer computer = new Computer(id);
            computer.name = rs.getString("name");
            computer.position = rs.getFloat("position");
            computer.wireResponse = rs.getString("wire_response");
            computer.connectedWiFiToId = rs.getLong("connected_wifi_to_id");
            try {
                String wifi = rs.getString("accepted_wifi_connections");
                if (wifi != null) {
                    computer.acceptedWiFiConnectionsId.addAll(
                            mapper.readValue(wifi, new TypeReference<HashSet<Long>>(){}));
                }
                String blue = rs.getString("accepted_bluetooth_connections");
                if (blue != null) {
                    computer.acceptedBluetoothConnectionsId.addAll(
                            mapper.readValue(blue, new TypeReference<HashSet<Long>>(){}));
                }
            }
            catch (Exception e){
                log.error(e.getMessage());
            }
            devices.add(computer);
        }
        log.info("loaded computers");
        return devices;
    }
}
