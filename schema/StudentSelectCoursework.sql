SELECT Coursework.id AS coursework_id, 
Coursework.title AS title,
Coursework.date_submitted AS date_submitted,
Coursework.file AS file,
Coursework.file_extension AS file_extension
FROM Coursework 
INNER JOIN Class ON Class.id = Coursework.class_id 
INNER JOIN Module ON Module.id = Class.module_id 
INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id 
INNER JOIN User ON User.id = Lecturer.user_id 
WHERE Class.id = ?;