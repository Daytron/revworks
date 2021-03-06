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
package com.github.daytron.revworks.exception;

/**
 * A custom exception when retrieving data from the database fails.
 *
 * @author Ryan Gilera
 */
public class SQLErrorQueryException extends Exception {

    /**
     * A class constructor that customises the Throwable object with a custom 
     * message.
     * 
     * @param msg the custom message 
     * @param t Throwable object
     */
    public SQLErrorQueryException(String msg, Throwable t) {
        super(msg, t);
    }

    /**
     * A class constructor that accepts only a custom message.
     * 
     * @param msg the custom message as a String value
     */
    public SQLErrorQueryException(String msg) {
        super(msg);
    }

    /**
     * A class constructor that accepts only a Throwable object.
     * 
     * @param t Throwable object
     */
    public SQLErrorQueryException(Throwable t) {
        super(t);
    }
    
}
