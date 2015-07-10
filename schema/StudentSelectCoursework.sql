SELECT Coursework.id AS coursework_id, 
Coursework.title AS title,
Coursework.date_submitted AS date_submitted,
Coursework.file AS file,
Coursework.file_extension AS file_extension,
Coursework.is_read_student AS is_read_s, 
Coursework.is_read_lecturer AS is_read_l 
FROM Coursework 
INNER JOIN Class ON Class.id = Coursework.class_id 
INNER JOIN Module ON Module.id = Class.module_id 
INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id 
INNER JOIN User ON User.id = Lecturer.user_id 
WHERE Coursework.class_id = 2 AND 
Coursework.student_user_id = 1;