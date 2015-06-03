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
 * Collection of font awesome styles.
 *
 * @author Ryan Gilera
 */
public enum FontAwesomeIcon {

    CHECK_CIRCLE_O("<i class=\"fa fa-check-circle-o "),
    THUMBS_O_UP("<i class=\"fa fa-thumbs-o-up ");

    private final String code;

    private FontAwesomeIcon(String code) {
        this.code = code;
    }

    public String getLgSize() {
        return code + "fa-lg\"></i>&nbsp;&nbsp;";
    }

    public String get2xSize() {
        return code + "fa-2x\"></i>&nbsp;&nbsp;";
    }

    public String get3xSize() {
        return code + "fa-3x\"></i>&nbsp;&nbsp;";
    }

    public String get4xSize() {
        return code + "fa-4x\"></i>&nbsp;&nbsp;";
    }

    public String get5xSize() {
        return code + "fa-5x\"></i>&nbsp;&nbsp;";
    }

}
