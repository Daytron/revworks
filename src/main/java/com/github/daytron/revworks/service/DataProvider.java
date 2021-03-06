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

import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import com.github.daytron.revworks.model.Announcement;
import java.util.List;

/**
 * The base template for common SQL data retrieval process for
 * both student and lecturer users.
 *
 * @author Ryan Gilera
 */
public interface DataProvider {

    /**
     * Retrieves announcements from the database for the home view depending on 
     * what type of user is the current user. An error is displayed to the user 
     * when an SQLException occurs.
     * 
     * @return list of announcement as a polymorphic derived class of List
     * @throws SQLErrorQueryException for detecting an SQLException
     * @throws SQLErrorRetrievingConnectionAndPoolException for error in 
     * in connecting to the database
     */
    public List<Announcement> populateHomeViewWithData() throws
            SQLErrorQueryException, 
            SQLErrorRetrievingConnectionAndPoolException;

}
