/*
 * Copyright 2015 Ryan Gilera.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.daytron.revworks.service;

import com.github.daytron.revworks.MainUI;
import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The top level abstract class for common SQL reserving connection process for
 * both data insertions and retrievals.
 *
 * @author Ryan Gilera
 */
public class QueryManagerAbstract {

    private JDBCConnectionPool connectionPool;
    private Connection connection;

    /**
     * Connects to the database via connection manager. No extra connection pool 
     * is created. The same SimpleJDBCConnectionPool object is used to connect 
     * throughout the session's lifecycle. A connection is established from the 
     * same connection pool. Although SimpleJDBCConnectionPool is not a connection 
     * pool per se, its implementation acts like one.
     * 
     * @return boolean value true if successfully connected, otherwise false
     */
    public final boolean reserveConnectionPool() {
        try {
            this.connectionPool = MainUI.get().getConnectionManager().connect();
            this.connection = connectionPool.reserveConnection();

            return true;
        } catch (SQLException ex) {
            releaseConnection();
            Logger.getLogger(QueryManagerAbstract.class.getName())
                    .log(Level.SEVERE, null, ex);
            return false;
        } catch (MissingResourceException ex) {
            Logger.getLogger(QueryManagerAbstract.class.getName())
                    .log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Releases the current connection from the connection pool so it can be 
     * used by others later on.
     */
    public final void releaseConnection() {
        if (connectionPool != null || connection != null) {
            this.connectionPool.releaseConnection(connection);
        }
    }

    /**
     * Access the current connection created from the connection pool.
     * 
     * @return Connection object
     */
    public final Connection getConnection() {
        return connection;
    }

    /**
     * Access the connection pool itself.
     * 
     * @return JDBCConnectionPool object
     */
    public final JDBCConnectionPool getConnectionPool() {
        return connectionPool;
    }
    
    /**
     * Displays a generic data send error notification for query error events.
     */
    public final void notifyDataSendError() {
        NotificationUtil.showError(
                ErrorMsg.DATA_SEND_ERROR.getText(),
                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
    }

}
