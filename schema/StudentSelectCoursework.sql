SELECT Coursework.id AS coursework_id, 
Coursework.title AS title,
Coursework.date_submitted AS date_submitted,
Coursework.file AS file,
Coursework.file_extension AS file_extension,
Coursework.class_id AS class_id,
Class.module_id AS module_id,
Module.name AS module_name,
User.id AS lecturer_id,
Lecturer.email AS lecturer_email,
User.first_name AS lecturer_firstname,
User.last_name AS lecturer_lastname
FROM Coursework 
INNER JOIN Class ON Class.id = Coursework.class_id 
INNER JOIN Module ON Module.id = Class.module_id 
INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id 
INNER JOIN User ON User.id = Lecturer.user_id 
WHERE Class.semester_id = 'C15' AND Coursework.student_user_id = 1;