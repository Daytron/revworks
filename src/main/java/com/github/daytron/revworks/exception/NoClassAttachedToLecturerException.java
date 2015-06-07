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
 * A custom exception when executing select class query for lecturer returns 
 * no result.
 *
 * @author Ryan Gilera
 */
public class NoClassAttachedToLecturerException extends Exception {

    public NoClassAttachedToLecturerException(String msg, Throwable t) {

        super(msg, t);
    }

    public NoClassAttachedToLecturerException(String msg) {

        super(msg);
    }

    public NoClassAttachedToLecturerException(Throwable t) {

        super(t);
    }
}
