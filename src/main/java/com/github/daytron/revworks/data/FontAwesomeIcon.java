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

    CHECK_CIRCLE("<i class=\"fa fa-check-circle "),
    CHECK_CIRCLE_O("<i class=\"fa fa-check-circle-o "),
    CLOUD_UPLOAD("<i class=\"fa fa-cloud-upload "),
    INFO_CIRCLE("<i class=\"fa fa-info-circle "),
    THUMBS_O_UP("<i class=\"fa fa-thumbs-o-up "),
    EXCLAMATION_CIRCLE("<i class=\"fa fa-exclamation-circle "),
    CALENDAR("<i class=\"fa fa-calendar "),
    LIFE_RING("<i class=\"fa fa-life-ring "),
    EXCLAMATION_TRIANGLE("<i class=\"fa fa-exclamation-triangle "),
    DESKTOP("<i class=\"fa fa-desktop "),
    SERVER("<i class=\"fa fa-server "),
    EXTERNAL_LINK("<i class=\"fa fa-external-link ");

    private final String code;

    private FontAwesomeIcon(String code) {
        this.code = code;
    }

    /**
     * Access the HTML code for the particular icon with 33% increase in size 
     * and two literal spaces at the end.
     * 
     * @return HTML code for an icon. 
     */
    public String getLgSize() {
        return code + "fa-lg\"></i>&nbsp;&nbsp;";
    }
    
    /**
     * Access the HTML code for the particular icon with 33% increase in size 
     * and no spaces.
     * 
     * @return HTML code for an icon. 
     */
    public String getLgSizeWithNoSpace() {
        return code + "fa-lg\"></i>";
    }
    
    /**
     * Access the HTML code for the particular icon in normal size 
     * with no spaces.
     * 
     * @return HTML code for an icon. 
     */
    public String get1xSizeWithNoSpace() {
        return code + "fa-1x\"></i>";
    }

    /**
     * Access the HTML code for the particular icon with 200% increase in size 
     * and two literal spaces at the end.
     * 
     * @return HTML code for an icon. 
     */
    public String get2xSize() {
        return code + "fa-2x\"></i>&nbsp;&nbsp;";
    }

    /**
     * Access the HTML code for the particular icon with 300% increase in size 
     * and two literal spaces at the end.
     * 
     * @return HTML code for an icon. 
     */
    public String get3xSize() {
        return code + "fa-3x\"></i>&nbsp;&nbsp;";
    }

    /**
     * Access the HTML code for the particular icon with 400% increase in size 
     * and two literal spaces at the end.
     * 
     * @return HTML code for an icon. 
     */
    public String get4xSize() {
        return code + "fa-4x\"></i>&nbsp;&nbsp;";
    }

    /**
     * Access the HTML code for the particular icon with 500% increase in size 
     * and two literal spaces at the end.
     * 
     * @return HTML code for an icon. 
     */
    public String get5xSize() {
        return code + "fa-5x\"></i>&nbsp;&nbsp;";
    }
    
    /**
     * Access the HTML code for the particular icon with 500% increase in size 
     * and no extra spaces.
     * 
     * @return HTML code for an icon. 
     */
    public String get5xSizeWithNoSpace() {
        return code + "fa-5x\"></i>";
    }

}
