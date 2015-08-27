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

    /**
     * A class constructor that takes an integer, three String objects, a 
     * LocaDateTime object and an AnnouncementType enum item.
     * 
     * @param id announcement id
     * @param title announcement title
     * @param message announcement message
     * @param dateTimeSubmitted date time submitted
     * @param announcementType type of announcement
     * @param announcementSource  source of announcement
     */
    public Announcement(int id, String title, String message,
            LocalDateTime dateTimeSubmitted, AnnouncementType announcementType,
            String announcementSource) {
        this(id, title, message, dateTimeSubmitted, announcementType,
                announcementSource, null, null);
    }

    /**
     * A class constructor that takes an integer, four String objects, a 
     * LocalDateTime object and an AnnouncementType enum item.
     * 
     * @param id announcement id
     * @param title announcement title
     * @param message announcement message
     * @param dateTimeSubmitted date time of the announcement submission
     * @param announcementType type of announcement
     * @param announcementSource source of announcement
     * @param moduleId module id of the associated announcement
     * @param moduleName module name of the associated announcement
     */
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

    /**
     * Access announcement id.
     * 
     * @return id of the announcement as an integer
     */
    public int getId() {
        return id;
    }

    /**
     * Access the announcement title.
     * 
     * @return title of the announcement as String. 
     */
    public String getTitle() {
        return title;
    }

    /**
     * Access the announcement message.
     * 
     * @return message of the announcement as String  
     */
    public String getMessage() {
        return message;
    }

    /**
     * Access the date and time of announcement submission.
     * 
     * @return LocalDateTime object
     */
    public LocalDateTime getDateTimeSubmitted() {
        return dateTimeSubmitted;
    }

    /**
     * Access the type of announcement.
     * 
     * @return AnnouncementType enum item 
     */
    public AnnouncementType getAnnouncementType() {
        return announcementType;
    }

    /**
     * Checks whether the announcement is class wide type.
     * 
     * @return true if the announcement is class wide type otherwise false 
     */
    public boolean isClassWideAnnouncement() {
        return announcementType == AnnouncementType.CLASS_WIDE;
    }

    /**
     * Checks whether the announcement is system wide type.
     * 
     * @return true if the announcement is system wide type otherwise false 
     */
    public boolean isSystemWideAnnouncement() {
        return announcementType == AnnouncementType.SYSTEM_WIDE;
    }

    /**
     * Access the source of announcement. This pertains to the author of 
     * the announcement.
     * 
     * @return author of announcement as String value.
     */
    public String getAnnouncementSource() {
        return announcementSource;
    }

    /**
     * Access the module id associated with the announcement.
     * 
     * @return module id as String 
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * Access the module name associated with the announcement.
     * 
     * @return module name as String 
     */
    public String getModulename() {
        return modulename;
    }

}
