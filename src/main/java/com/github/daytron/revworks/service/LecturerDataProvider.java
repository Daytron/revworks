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

import com.github.daytron.revworks.exception.NoClassAttachedToLecturerException;
import com.github.daytron.revworks.exception.SQLErrorQueryException;
import com.github.daytron.revworks.exception.SQLErrorRetrievingConnectionAndPoolException;
import java.util.List;
import com.vaadin.data.util.BeanItemContainer;

/**
 * The base template for all SQL data retrieval exclusive to lecturer users.
 *
 * @author Ryan Gilera
 */
public interface LecturerDataProvider {

    
    public List<BeanItemContainer> extractCourseworkData() throws 
            SQLErrorRetrievingConnectionAndPoolException, SQLErrorQueryException, 
            NoClassAttachedToLecturerException;
}
