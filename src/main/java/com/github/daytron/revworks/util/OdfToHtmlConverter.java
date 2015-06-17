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

import com.github.daytron.revworks.data.FilePath;
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
import org.odftoolkit.odfdom.converter.core.BasicURIResolver;
import org.odftoolkit.odfdom.converter.core.FileImageExtractor;
import org.odftoolkit.odfdom.converter.core.ODFConverterException;
import org.odftoolkit.odfdom.converter.xhtml.XHTMLConverter;
import org.odftoolkit.odfdom.converter.xhtml.XHTMLOptions;
import org.odftoolkit.odfdom.doc.OdfTextDocument;

/**
 * Utility class for converting odf family filetype to html.
 *
 * @author Ryan Gilera
 */
public final class OdfToHtmlConverter {

    private static final String htmlExtension = ".html";

    public static List<File> convert(File fileToConvert) throws FileNotFoundException, ODFConverterException, IOException, Exception {
        String basePath = VaadinService.getCurrent()
                .getBaseDirectory().getAbsolutePath()
                + FilePath.TEMP_FILE_HOLDER.getPath();

        List<File> listOfFiles = new ArrayList<>();

            File htmlFile = new File(FilePath.HTML_OUTPUT_NAME.getPath() 
                    + htmlExtension);

            InputStream inputFileStream = new FileInputStream(fileToConvert);
            OdfTextDocument document
                    = OdfTextDocument.loadDocument(inputFileStream);

            XHTMLOptions options = XHTMLOptions.create();

            File locaFolderLocal = new File(basePath + 
                    FilePath.TEMP_PICTURE_FOLDER.getPath()
                    + FilePath.HTML_OUTPUT_NAME.getPath());
            options.setExtractor(new FileImageExtractor(locaFolderLocal));

            BasicURIResolver fileURIResolver = new BasicURIResolver(locaFolderLocal.getAbsolutePath());
            options.URIResolver(fileURIResolver);

            OutputStream outputStream = new FileOutputStream(htmlFile);
            XHTMLConverter.getInstance().convert(document, outputStream, options);

            listOfFiles.add(htmlFile);
            listOfFiles.add(locaFolderLocal);
            return listOfFiles;

        
    }
}
