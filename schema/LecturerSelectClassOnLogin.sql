SELECT Class.id, Module.id, Module.name
FROM Class 
INNER JOIN Module ON Module.id = Class.module_id
INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id 
WHERE Lecturer.user_id = 47 AND
Class.semester_id = 'C15';