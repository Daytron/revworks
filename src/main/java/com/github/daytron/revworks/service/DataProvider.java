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
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.event.AppEvent;
import com.github.daytron.revworks.util.NotificationUtil;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ryan Gilera
 */
public class DataProvider {

    private ResultSet announcementsContainer = null;
    private JDBCConnectionPool connectionPool;
    private Connection connection;
    private PreparedStatement preparedStatement;

    private DataProvider() {
    }

    public static DataProvider get() {
        return DataProviderHolder.INSTANCE;
    }

    @Subscribe
    private void populateHomeViewWithData(
            final AppEvent.LoadHomeViewDataEvent event) {

        if (getConnectionPool()) {
            try {

                if (MainUI.get().getAccessControl().isUserAStudent()) {
                    preparedStatement = connection.prepareStatement(
                            PreparedQueryStatement.ANNOUNCEMENT_STUDENT_QUERY
                            .getQuery());

                } else {
                    // Otherwise it's a lecturer
                    preparedStatement = connection.prepareStatement(
                            PreparedQueryStatement.ANNOUNCEMENT_LECTURER_QUERY
                            .getQuery());
                }

                preparedStatement.setString(1,
                        MainUI.get().getAccessControl().getUserId());

                this.announcementsContainer = preparedStatement.executeQuery();

                if (!announcementsContainer.next()) {
                    Throwable throwable
                            = new NoCurrentUserException(
                                    ExceptionMsg.EMPTY_SQL_RESULT.getMsg());
                    Logger.getLogger(DataProvider.class.getName())
                            .log(Level.SEVERE, null, throwable);
                    showSQLErrorNotification();
                    return;
                }

            } catch (SQLException ex) {
                Logger.getLogger(DataProvider.class.getName())
                        .log(Level.SEVERE, null, ex);
                showSQLErrorNotification();
            }
        }

    }

    @Subscribe
    public void closeStatementAndConnection(
            final AppEvent.CloseSQLStatementAndConnectionEvent event) {
        try {
            preparedStatement.close();
            connectionPool.releaseConnection(connection);
            announcementsContainer = null;
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName())
                    .log(Level.SEVERE, null, ex);
        }

    }

    private void showSQLErrorNotification() {
        NotificationUtil.showError(
                ErrorMsg.DATA_FETCH_ERROR.getText(),
                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
    }

    private boolean getConnectionPool() {
        try {
            this.connectionPool = SQLConnectionManager.get().connect();
            this.connection = connectionPool.reserveConnection();
            
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(DataProvider.class.getName())
                    .log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public ResultSet getAnnouncementsContainer() {
        return announcementsContainer;
    }

    private static class DataProviderHolder {

        private static final DataProvider INSTANCE = new DataProvider();
    }
}
