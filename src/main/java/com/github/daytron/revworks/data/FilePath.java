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
 * Collection of file directory paths.
 *
 * @author Ryan Gilera
 */
public enum FilePath {

    TEMP_FILE_HOLDER("/VAADIN/tempFileHolder/"),
    HTML_OUTPUT_NAME("convertedWork"),
    FILE_OUTPUT_NAME("pdfFile"),
    TEMP_PICTURE_FOLDER("pictures/");
    private final String path;

    private FilePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
