CREATE DEFINER = CURRENT_USER TRIGGER `appschema`.`ClassWideAnnouncement_BEFORE_INSERT` BEFORE INSERT ON `ClassWideAnnouncement` FOR EACH ROW
BEGIN
    IF (SELECT announcement_type_id 
         FROM Announcement
         WHERE NEW.id = Announcement.id) != 2
	THEN
        SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Invalid type of Announcement 
            to insert as Lecturer. 
            Must be AnnouncementType ClassWide 
            for Announcement item';
	END IF;
END
