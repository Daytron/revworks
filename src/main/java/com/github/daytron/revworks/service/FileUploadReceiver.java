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
import com.google.gwt.thirdparty.guava.common.io.Files;
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

    private final String PDF_EXTENSION = "pdf";

    private final ProgressBar progressbar;
    private File fileUploaded;
    private Upload uploader;
    private boolean isUploaded;
    private boolean isCustomeError;

    /**
     * A class constructor that takes a ProgressBar object.
     * 
     * @param progressBar ProgressBar object to calculate the upload progress
     */
    public FileUploadReceiver(ProgressBar progressBar) {
        this.progressbar = progressBar;
        this.isUploaded = false;
        this.isCustomeError = false;
    }

    /**
     * Mutates the Upload object.
     * 
     * @param upload Upload object
     */
    public void setUploader(Upload upload) {
        this.uploader = upload;
    }

    /**
     * Creates a new file as a placeholder to be written by the FileOutStream.
     * 
     * @param filename the filename of the coursework file
     * @param mimeType the mimetype of the file
     * @return the OutputStream object if no FileNotFoundException occurs, 
     * otherwise returns null
     */
    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        FileOutputStream fileOutputStream;

        try {
            fileUploaded = new File(filename);
            fileOutputStream = new FileOutputStream(fileUploaded);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUploadReceiver.class.getName())
                    .log(Level.SEVERE, null, ex);
            return null;
        }

        return fileOutputStream;
    }

    /**
     * Calculates and updates the upload progress bar. If contentLength is 
     * of negative value, then set progress bar to indeterminate.
     * 
     * @param readBytes the total stored memory size of the received data in 
     * bytes as long value
     * @param contentLength the total length it takes to fill the bar 
     */
    @Override
    public void updateProgress(long readBytes, long contentLength) {

        if (contentLength == -1) {
            progressbar.setIndeterminate(true);
        } else {
            progressbar.setIndeterminate(false);
            float progressValue = readBytes / (float) contentLength;
            progressbar.setValue(progressValue);
        }

    }

    /**
     * Determines if the file's extension of the file being uploaded is within 
     * the accepted file extension format and validates the existence of the 
     * file. If no file is uploaded or invalid file type is detected, display an 
     * appropriate error message. 
     * 
     * <p>
     * The method also initialises the progress bar's value and set its 
     * visibility to true.
     * 
     * <p>
     * If the size of file being uploaded is greater than the database would 
     * allow, cancel upload event and show the appropriate error to user.  
     * 
     * @param event the StartedEvent object defined by the Upload class
     */
    @Override
    public void uploadStarted(Upload.StartedEvent event) {
        // Set progressbar visiblity
        this.progressbar.setValue(0.0f);
        this.progressbar.setVisible(true);

        // Detect if the user has the right file extension
        String fileExtension = Files.getFileExtension(event.getFilename()).toLowerCase();
        if (fileExtension.isEmpty()) {
            NotificationUtil.showError(
                    ErrorMsg.STUDENT_FAILED_UPLOAD_COURSEWORK.getText(), 
                    ErrorMsg.STUDENT_NO_FILE_UPLOAD.getText());
            this.isCustomeError = true;
            this.uploader.interruptUpload();
            this.isUploaded = false;
            this.isCustomeError = true;
        } else if (!(fileExtension.equalsIgnoreCase(PDF_EXTENSION))) {
            NotificationUtil.showError(
                    ErrorMsg.STUDENT_FAILED_UPLOAD_COURSEWORK.getText(),
                    ErrorMsg.STUDENT_WRONG_FILE_TYPE_UPLOAD.getText()
                    + " Yours was " + fileExtension);
            this.uploader.interruptUpload();
            this.isUploaded = false;
            this.isCustomeError = true;
            return;
        }

        // Detect the size of the file
        // If file size if bigger than allowed, 
        // cancel upload and notify user
        if (event.getContentLength() > MAX_SIZE_FILE_ALLOWED) {
            this.uploader.interruptUpload();
            this.isUploaded = false;
            this.isCustomeError = true;
            NotificationUtil.showError(
                    ErrorMsg.STUDENT_FAILED_UPLOAD_COURSEWORK.getText(),
                    ErrorMsg.STUDENT_REACHED_MAX_FILE_SIZE.getText());
        }

    }

    /**
     * No concrete implementation needed. The uploadSucceeded method takes this 
     * job.
     * 
     * @param event FinishedEvent object from Upload class
     */
    @Override
    public void uploadFinished(Upload.FinishedEvent event) {
    }

    /**
     * If the upload failed, deletes the temporary file, resets the progress 
     * bar and displays the appropriate error notification to the user.
     * 
     * @param event FailedEvent object from Upload class
     */
    @Override
    public void uploadFailed(Upload.FailedEvent event) {
        this.isUploaded = false;

        tryDeleteFile();
        this.progressbar.setValue(0.0f);
        this.progressbar.setVisible(false);

        if (!isCustomeError) {
            NotificationUtil.showError(
                    ErrorMsg.STUDENT_FAILED_UPLOAD_COURSEWORK.getText(),
                    ErrorMsg.CONSULT_YOUR_ADMIN.getText());
            this.isCustomeError = false;
        }
    }

    /**
     * If the upload succeed, show an information notification. Save the 
     * temporary file created for cleanup later on when session ends.
     * 
     * @param event SucceededEvent object from Upload class
     */
    @Override
    public void uploadSucceeded(Upload.SucceededEvent event) {
        NotificationUtil.showInformation(
                FontAwesomeIcon.CLOUD_UPLOAD.getLgSize(),
                "File upload finished.", null);
        this.isUploaded = true;

        // Just to be sure. A user may submit another file
        // so the counter must be reset
        this.isCustomeError = false;

        // Save reference of file to the current session to be cleanup after
        // the session ended
        CurrentUserSession.markForGarbageCollection(fileUploaded);

    }

    /**
     * Tries to delete the temporary file.
     */
    public void tryDeleteFile() {
        if (fileUploaded != null) {
            fileUploaded.delete();
        }
    }

    /**
     * Access the uploaded file.
     * 
     * @return File object 
     */
    public File getFileUploaded() {
        return fileUploaded;
    }

    /**
     * Access the flag for determining if the the file is uploaded or not.
     * 
     * @return true if uploaded successfully, otherwise false 
     */
    public boolean isUploaded() {
        return isUploaded;
    }

}
