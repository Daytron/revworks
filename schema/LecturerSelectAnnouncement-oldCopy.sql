SELECT Announcement.id AS id, 
Announcement.title AS title, 
Announcement.message as message, 
Announcement.date_submitted AS dateSubmitted, 
Announcement.announcement_type_id AS announcementTypeId,
ClassWideAnnouncement.class_id AS classId
FROM Announcement 
LEFT JOIN ClassWideAnnouncement 
ON Announcement.id = ClassWideAnnouncement.id  
LEFT JOIN Class
ON Class.id = ClassWideAnnouncement.class_id
WHERE (Announcement.announcement_type_id = 1 OR 
Class.lecturer_user_id = 47) AND
DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY)
ORDER BY date_submitted DESC;
             
             