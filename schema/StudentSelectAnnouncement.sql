SELECT Announcement.id AS id, 
Announcement.title AS title, 
Announcement.message AS message,  
Announcement.date_submitted AS dateSubmitted, 
Announcement.announcement_type_id AS announcementTypeId, 
CustomTable.module_id AS moduleId, 
CustomTable.module_name AS moduleName,  
CustomTable.lecturer_first_name AS lecturer_first, 
CustomTable.lecturer_last_name AS lecturer_last
FROM Announcement  
LEFT JOIN 

(SELECT Class.id AS class_id, Module.id as module_id, 
Module.name AS module_name, User.first_name AS lecturer_first_name,
User.last_name AS lecturer_last_name, 
StudentClass.user_id AS user_id, ClassWideAnnouncement.id AS csId
FROM Class 
INNER JOIN ClassWideAnnouncement  
ON ClassWideAnnouncement.class_id = Class.id  
INNER JOIN Module ON Module.id = Class.module_id
INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_id 
INNER JOIN User ON User.id = Lecturer.user_id 
INNER JOIN Semester ON Semester.id = Class.semester_id
INNER JOIN StudentClass ON StudentClass.class_id = Class.id 
WHERE (curdate() BETWEEN Semester.startDate AND Semester.endDate) AND
StudentClass.user_id = 12) AS CustomTable 
ON CustomTable.csId = Announcement.id 

WHERE (Announcement.announcement_type_id = 1 OR CustomTable.user_id = 12) AND
DATE(Announcement.date_submitted)  >= (NOW() - INTERVAL 7 DAY)  
ORDER BY date_submitted DESC;