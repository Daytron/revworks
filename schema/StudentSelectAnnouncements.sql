SELECT Announcement.id AS id, 
Announcement.title AS title, 
Announcement.message AS message,  
Announcement.date_submitted AS dateSubmitted, 
Announcement.announcement_type_id AS announcementTypeId,
ClassWideAnnouncement.class_id AS classId
FROM Announcement  
LEFT JOIN ClassWideAnnouncement 
ON ClassWideAnnouncement.id = Announcement.id
LEFT JOIN Class
ON Class.id = ClassWideAnnouncement.class_id
LEFT JOIN StudentClass
ON StudentClass.class_id = Class.id
WHERE Announcement.announcement_type_id = 1 OR StudentClass.user_id = 1 
ORDER BY date_submitted DESC
LIMIT 15;