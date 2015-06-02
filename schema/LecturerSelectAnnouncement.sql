SELECT Announcement.id AS id, 
Announcement.title AS title, 
Announcement.message as message, 
Announcement.date_submitted AS dateSubmitted, 
Announcement.announcement_type_id AS announcementTypeId,
CustomTable.module_id AS moduleId, 
CustomTable.module_name AS moduleName 
FROM Announcement 
LEFT JOIN ClassWideAnnouncement 
ON Announcement.id = ClassWideAnnouncement.id  
LEFT JOIN 
(SELECT Class.id AS class_id, Module.id as module_id, 
Module.name AS module_name, Class.lecturer_id AS user_id  
FROM Module 
INNER JOIN Class ON Class.module_id = Module.id 
INNER JOIN Semester ON Semester.id = Class.semester_id 
WHERE curdate() 
BETWEEN Semester.startDate AND Semester.endDate 
AND Class.lecturer_id = 43) AS CustomTable 
ON CustomTable.class_id = ClassWideAnnouncement.class_id 
WHERE (Announcement.announcement_type_id = 1 OR 
CustomTable.user_id = 43) AND
DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY)
ORDER BY date_submitted DESC;
             
             