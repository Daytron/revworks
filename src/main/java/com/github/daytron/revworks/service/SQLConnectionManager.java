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

import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import java.sql.SQLException;

/**
 * A class for establishing and retrieving connection from the database.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class SQLConnectionManager {

    private SimpleJDBCConnectionPool jbdcConnectionPool = null;

    public SQLConnectionManager() {
    }


    /**
     * Returns {@link SimpleJDBCConnectionPool} object. Creates a new object if
     * no object is attached.
     *
     * @return SimpleJDBCConnectionPool object
     * @throws SQLException occurs whenever something went wrong to the database 
     * connection
     */
    public SimpleJDBCConnectionPool connect() throws SQLException {
        if (jbdcConnectionPool == null) {
            jbdcConnectionPool = new SimpleJDBCConnectionPool(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://localhost/appschema",
                    "uservalidator", "sqluserpw", 2, 10);
        }
        
        return jbdcConnectionPool;
    }

}
