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
import com.github.daytron.revworks.data.ExceptionMsg;
import com.github.daytron.revworks.data.PreparedQueryStatement;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.ClassTable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Concrete implementation of {@link LecturerDataProvider}.
 *
 * @author Ryan Gilera
 */
public class LecturerDataProviderImpl extends DataProviderAbstract
        implements LecturerDataProvider {

    private List<ClassTable> listOfClasses;

    private LecturerDataProviderImpl() {
        super();
    }

    public static LecturerDataProviderImpl get() {
        return LecturerDataProviderHolder.INSTANCE;
    }

    @Override
    public List<ClassTable> extractClassData()
            throws SQLErrorRetrievingConnectionAndPoolException,
            SQLErrorQueryException {
        if (reserveConnectionPool()) {
            try {
                final PreparedStatement preparedStatement = getConnection()
                        .prepareStatement(
                                PreparedQueryStatement.LECTURER_CLASS_SELECT_QUERY.getQuery());

                preparedStatement.setInt(1,
                        MainUI.get().getAccessControl().getUserId());
                preparedStatement.setString(2, 
                        CurrentUserSession.getCurrentSemester());

                ResultSet resultSet = preparedStatement.executeQuery();

                if (!resultSet.next()) {
                    return new ArrayList<>();
                }

                resultSet.beforeFirst();
                this.listOfClasses = new ArrayList<>();

                while (resultSet.next()) {
                    this.listOfClasses.add(new ClassTable(resultSet.getInt(1),
                            resultSet.getString(2),
                            resultSet.getString(3)));
                }

                preparedStatement.close();
                getConnectionPool().releaseConnection(getConnection());

                return this.listOfClasses;

            } catch (SQLException ex) {
                Logger.getLogger(LecturerDataProviderImpl.class.getName())
                        .log(Level.SEVERE, null, ex);
                throw new SQLErrorQueryException(
                        ExceptionMsg.SQL_ERROR_QUERY.getMsg());
            }
        } else {
            throw new SQLErrorRetrievingConnectionAndPoolException(
                    ExceptionMsg.SQL_ERROR_CONNECTION.getMsg());
        }
    }

    private static class LecturerDataProviderHolder {

        private static final LecturerDataProviderImpl INSTANCE = new LecturerDataProviderImpl();
    }
}
