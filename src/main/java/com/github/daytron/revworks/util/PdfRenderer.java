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

import com.github.daytron.revworks.model.Coursework;
import com.github.daytron.revworks.service.CurrentUserSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * Utility wrapper for extracting pages of PDF files as images using PDFBox
 * library.
 *
 * @author Ryan Gilera
 */
public final class PdfRenderer {

    private PdfRenderer() {
    }

    public static List<File> extractPages(Coursework coursework) throws IOException {
        List<File> listOfPages = new ArrayList<>();

        PDDocument document = PDDocument.load(coursework.getCourseworkFile());
        PDDocumentCatalog catalog = document.getDocumentCatalog();
        @SuppressWarnings("unchecked")
        PDPageTree pages = catalog.getPages();
        PDFRenderer renderer = new PDFRenderer(document);

        for (int i = 0; i < pages.getCount(); i++) {
            BufferedImage image = renderer.renderImageWithDPI(i, 200f);
            File pageFile = new File("image-" + UUID.randomUUID()
                    + i + ".png");

            ImageIO.write(image, "png", pageFile);
            CurrentUserSession.markForGarbageCollection(pageFile);
            listOfPages.add(pageFile);
        }

        document.close();
        return listOfPages;
    }
}
