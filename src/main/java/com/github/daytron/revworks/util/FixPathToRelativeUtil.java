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
package com.github.daytron.revworks.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility Class to fix all rendered absolute paths for image to relative paths,
 * in order to show the images in coursework view.
 *
 * @author Ryan Gilera
 */
public class FixPathToRelativeUtil {

    public static String fixPath(File htmlFile) throws IOException {
        try {
            String htmlCodeWithAbsolutePath = extractHtmlCode(htmlFile);
            String newHTMLCodeWithRelativePath = htmlCodeWithAbsolutePath
                    .replace("/home/ryan/GitRepos/revworks/target/revworks-1.0-SNAPSHOT/VAADIN/themes/mytheme/", "./VAADIN/themes/mytheme/");

            return newHTMLCodeWithRelativePath;
        } catch (IOException ex) {
            Logger.getLogger(FixPathToRelativeUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }

    private static String extractHtmlCode(File htmlFile) throws FileNotFoundException, IOException {
        try {
            StringBuilder stringBuilder;

            // Extra try clause automatically closes 
            // the inReader after using it
            try (BufferedReader inReader
                    = new BufferedReader(new FileReader(htmlFile))) {
                String aLineString;
                stringBuilder = new StringBuilder();
                while ((aLineString = inReader.readLine()) != null) {
                    stringBuilder.append(aLineString);
                }
            }

            return stringBuilder.toString();
        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        }
    }
}
