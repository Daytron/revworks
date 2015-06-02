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
 * Collection of error messages.
 *
 * @author Ryan Gilera
 */
public enum ErrorMsg {

    CONSULT_YOUR_ADMIN("Please consult your administrator for help."),
    INVALID_INPUT_CAPTION("Invalid input!"),
    SIGNIN_FAILED_CAPTION("Sign-In failed!"),
    NO_USER_SIGNIN("No login user found. The session has been reset. "),
    DATA_FETCH_ERROR("Could not retrieve user data."),
    DATA_SEND_ERROR("Could not send data.");

    private final String text;

    private ErrorMsg(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
