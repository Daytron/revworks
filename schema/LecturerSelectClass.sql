SELECT Class.id AS id, 
Class.module_id AS moduleId, 
Module.name AS moduldeName 
FROM Class 
INNER JOIN Module ON Module.id = Class.module_id 
INNER JOIN Semester ON Semester.id = Class.semester_id 
WHERE Class.lecturer_id = 47 AND 
curdate() BETWEEN Semester.startDate AND Semester.endDate;