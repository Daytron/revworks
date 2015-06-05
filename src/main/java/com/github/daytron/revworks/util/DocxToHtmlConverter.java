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

import com.vaadin.server.VaadinService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * Utility class for converting docx files to html.
 * F
 * @author Ryan Gilera
 */
public class DocxToHtmlConverter {
    private static final String relativePathToStore = "/VAADIN/themes/mytheme/";
    private static final String tempIdentifier = "convertedWork";
    private static final String htmlExtension = ".html";
    private static final String folderName = "pictures/";
    
    
    public static List<File> convert(File fileToConvert) throws FileNotFoundException, IOException {
        String basePath = VaadinService.getCurrent()
                    .getBaseDirectory().getAbsolutePath()+
                relativePathToStore;
        List<File> listOfFiles = new ArrayList<>();
        
        try {
            File htmlFile = new File(tempIdentifier + htmlExtension);
            
            InputStream inputFileStream = new FileInputStream(fileToConvert);
            XWPFDocument document = new XWPFDocument(inputFileStream);
            
            XHTMLOptions options = XHTMLOptions.create();
            
            File locaFolderLocal = new File(basePath + folderName
                    + tempIdentifier);
            options.setExtractor(new FileImageExtractor(locaFolderLocal));
            
            FileURIResolver fileURIResolver = new FileURIResolver(locaFolderLocal);
            options.URIResolver(fileURIResolver);
            
            OutputStream outputStream = new FileOutputStream(htmlFile);
            XHTMLConverter.getInstance().convert(document, outputStream, options);
            
            listOfFiles.add(htmlFile);
            listOfFiles.add(locaFolderLocal);
            return listOfFiles;
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocxToHtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } catch (IOException ex) {
            Logger.getLogger(DocxToHtmlConverter.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        
        
    }
    
}
