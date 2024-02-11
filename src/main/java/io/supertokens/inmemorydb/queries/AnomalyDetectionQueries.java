/*
 *    Copyright (c) 2020, VRAI Labs and/or its affiliates. All rights reserved.
 *
 *    This software is licensed under the Apache License, Version 2.0 (the
 *    "License") as published by the Apache Software Foundation.
 *
 *    You may not use this file except in compliance with the License. You may
 *    obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 */

package io.supertokens.inmemorydb.queries;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.supertokens.pluginInterface.exceptions.StorageQueryException;
import io.supertokens.inmemorydb.ConnectionWithLocks;
import io.supertokens.inmemorydb.Start;
import io.supertokens.inmemorydb.config.Config;
import io.supertokens.pluginInterface.multitenancy.AppIdentifier;

import java.sql.Connection;
import java.sql.SQLException;

import static io.supertokens.inmemorydb.QueryExecutorTemplate.execute;
import static io.supertokens.inmemorydb.QueryExecutorTemplate.update;
import static io.supertokens.inmemorydb.config.Config.getConfig;

public class AnomalyDetectionQueries {

    public static String getQueryToCreateUserIPHistoryTable(Start start) {
        String tableName = Config.getConfig(start).getUserIPHistoryTable();
        // @formatter:off
        return "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "app_id VARCHAR(64) DEFAULT 'public',"
                + "user_id VARCHAR(128) NOT NULL,"
                + "user_ip_address VARCHAR(48) NOT NULL,"
                + "FOREIGN KEY (app_id) REFERENCES " + Config.getConfig(start).getAppsTable()
                + " (app_id) ON DELETE CASCADE"
                + ");";
        // @formatter:on
    }

    // TODO: Satbir: Return an int here.
    public static void insertNewIPAddress(Start start, AppIdentifier appIdentifier, String userId, , String newIPAddress)
            throws SQLException, StorageQueryException {
        String QUERY = "INSERT INTO " + getConfig(start).getUserIPHistoryTable()
                + "(app_id, user_id, user_ip_address) VALUES(?, ?, ?) ";

        update(start, QUERY, pst -> {
            pst.setString(1, appIdentifier.getAppId());
            pst.setString(2, userId);
            pst.setString(3, newIPAddress);
        });
    }

    public static String getLastIPAddress(Start start, AppIdentifier appIdentifier, String userId)
            throws SQLException, StorageQueryException {
        String QUERY = "SELECT user_ip_address FROM " + getConfig(start).getUserIPHistoryTable()
                + " WHERE app_id = ? AND user_id = ? ORDER BY rowid DESC LIMIT 1";
        return execute(start, QUERY, pst -> {
            pst.setString(1, appIdentifier.getAppId());
            pst.setString(2, userId);
        }, result -> {
            if (result.next()) {
                return result.getString("user_metadata");
            }
            return null;
        });
    }
}
