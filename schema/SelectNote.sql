SELECT n.id, n.page_num,
n.date_submitted, 
n.is_student_to_lecturer
FROM Note n 
WHERE n.coursework_id = 1 
ORDER BY n.page_num ASC;