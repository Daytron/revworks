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
package com.github.daytron.revworks.service;

import com.github.daytron.revworks.data.ErrorMsg;
import com.github.daytron.revworks.data.FontAwesomeIcon;
import com.github.daytron.revworks.util.NotificationUtil;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A receiver that process file upload for coursework submission from the
 * students and manage its state.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class FileUploadReceiver implements Upload.Receiver, Upload.ProgressListener, Upload.StartedListener, Upload.FinishedListener,
        Upload.FailedListener, Upload.SucceededListener {

    // In bytes 10 MB = 10,000,000 bytes
    private static final long MAX_SIZE_FILE_ALLOWED = 10000000;

    private final String DOCX_EXTENSION = "docx";
    private final String ODF_EXTENSION = "odf";
    private final String ODT_EXTENSION = "odt";

    private final ProgressBar progressbar;
    private File fileUploaded;
    private Upload uploader;
    private String extension;
    private boolean isUploaded;

    public FileUploadReceiver(ProgressBar progressBar) {
        this.progressbar = progressBar;
        this.isUploaded = false;
    }

    public void setUploader(Upload upload) {
        this.uploader = upload;
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        FileOutputStream fileOutputStream;

        try {
            fileUploaded = new File(filename);
            fileOutputStream = new FileOutputStream(fileUploaded);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUploadReceiver.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return fileOutputStream;
    }

    @Override
    public void updateProgress(long readBytes, long contentLength) {
        float progressValue = readBytes / (float) contentLength;
        progressbar.setValue(progressValue);
    }

    @Override
    public void uploadStarted(Upload.StartedEvent event) {
        // Detect if the user has the right file extension
        boolean validFileType = false;
        int dotIndex = event.getFilename().lastIndexOf('.');
        String fileExt = event.getFilename().substring(dotIndex + 1);

        if (fileExt.equalsIgnoreCase(DOCX_EXTENSION)) {
            extension = DOCX_EXTENSION;
            validFileType = true;
        } else if (fileExt.equalsIgnoreCase(ODF_EXTENSION)) {
            extension = ODF_EXTENSION;
            validFileType = true;
        } else if (fileExt.equalsIgnoreCase(ODT_EXTENSION)) {
            extension = ODT_EXTENSION;
            validFileType = true;
        }

        if (!validFileType) {
            NotificationUtil.showError(
                    ErrorMsg.STUDENT_FAILED_UPLOAD_COURSEWORK.getText(),
                    ErrorMsg.STUDENT_WRONG_FILE_TYPE_UPLOAD.getText()
                    + " Yours was " + fileExt);
            this.uploader.interruptUpload();
            this.isUploaded = false;
            tryDeleteFile();
            return;
        }

        // Detect the size of the file
        // If file size if bigger than allowes, 
        // cancel upload and notify user
        if (event.getContentLength() > MAX_SIZE_FILE_ALLOWED) {
            this.uploader.interruptUpload();
            this.isUploaded = false;
            tryDeleteFile();

            NotificationUtil.showError(
                    ErrorMsg.STUDENT_FAILED_UPLOAD_COURSEWORK.getText(),
                    ErrorMsg.STUDENT_REACHED_MAX_FILE_SIZE.getText());
        }

    }

    @Override
    public void uploadFinished(Upload.FinishedEvent event) {
    }

    @Override
    public void uploadFailed(Upload.FailedEvent event) {
        this.isUploaded = false;
        tryDeleteFile();
        NotificationUtil.showError(
                ErrorMsg.STUDENT_FAILED_UPLOAD_COURSEWORK.getText(),
                ErrorMsg.CONSULT_YOUR_ADMIN.getText());
    }

    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {
        NotificationUtil.showInformation(
                FontAwesomeIcon.CLOUD_UPLOAD.getLgSize(),
                "File upload finished.", null);
        this.isUploaded = true;

    }

    public void tryDeleteFile() {
        if (fileUploaded != null) {
            fileUploaded.delete();
        }
    }

    public File getFileUploaded() {
        return fileUploaded;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

}
