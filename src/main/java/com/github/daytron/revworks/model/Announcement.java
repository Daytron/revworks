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
package com.github.daytron.revworks.model;

import com.github.daytron.revworks.data.AnnouncementType;
import java.time.LocalDateTime;

/**
 * Announcement model class for Announcement table.
 *
 * @author Ryan Gilera
 */
@SuppressWarnings("serial")
public class Announcement {

    private final int id;
    private final String title;
    private final String message;
    private final LocalDateTime dateTimeSubmitted;
    private final AnnouncementType announcementType;
    private final String announcementSource;
    private final String moduleId;
    private final String modulename;

    public Announcement(int id, String title, String message,
            LocalDateTime dateTimeSubmitted, AnnouncementType announcementType,
            String announcementSource) {
        this(id, title, message, dateTimeSubmitted, announcementType,
                announcementSource, null, null);
    }

    public Announcement(int id, String title, String message,
            LocalDateTime dateTimeSubmitted, AnnouncementType announcementType,
            String announcementSource, String moduleId, String moduleName) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.dateTimeSubmitted = dateTimeSubmitted;
        this.announcementType = announcementType;
        this.announcementSource = announcementSource;
        this.moduleId = moduleId;
        this.modulename = moduleName;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateTimeSubmitted() {
        return dateTimeSubmitted;
    }

    public AnnouncementType getAnnouncementType() {
        return announcementType;
    }

    public boolean isClassWideAnnouncement() {
        return announcementType == AnnouncementType.CLASS_WIDE;
    }

    public boolean isSystemWideAnnouncement() {
        return announcementType == AnnouncementType.SYSTEM_WIDE;
    }

    public String getAnnouncementSource() {
        return announcementSource;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getModulename() {
        return modulename;
    }

}
