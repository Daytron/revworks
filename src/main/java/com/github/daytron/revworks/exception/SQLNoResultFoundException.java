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
public class SQLNoResultFoundException extends Exception {

    public SQLNoResultFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    public SQLNoResultFoundException(String msg) {
        super(msg);
    }

    public SQLNoResultFoundException(Throwable t) {
        super(t);
    }
    
}
