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
package com.github.daytron.revworks.data;

/**
 * Collection of exception messages.
 *
 * @author Ryan Gilera
 */
public enum ExceptionMsg {
    
    AUTHENTICATION_EXCEPTION_NO_USER("No such user found. Invalid credentials."),
    AUTHENTICATION_EXCEPTION_SYS_ERROR("System error occured "),
    NO_CURRENT_USER_EXCEPTION("No user is login"),
    WRONG_CURRENT_USER_TYPE_EXCEPTION("Wrong user type access."),
    EMPTY_SQL_RESULT("Empty SQL query result."),
    SQL_ERROR_CONNECTION("SQL: Related connection service failed."),
    SQL_NO_RESULT_FOUND("SQL: The last query resulted to empty set."),
    SQL_ERROR_QUERY("SQL: Error query action."),
    NO_CLASS_ATTACHED_TO_LECTURER("No class found for lecturer user.");
    
    private final String msg;

    private ExceptionMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

}
