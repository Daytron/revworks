SELECT Class.id, Module.id, Module.name, 
User.id, Lecturer.email, User.first_name, User.last_name
FROM Class 
INNER JOIN StudentClass ON StudentClass.class_id = Class.id 
INNER JOIN Student ON Student.user_id = StudentClass.user_id
INNER JOIN Module ON Module.id = Class.module_id
INNER JOIN Lecturer ON Lecturer.user_id = Class.lecturer_user_id 
INNER JOIN User ON User.id = Lecturer.user_id
WHERE Student.student_id = 126250 AND  
Class.semester_id = 'C15';