SELECT n.id, n.page_num,
n.date_submitted, 
n.is_student_to_lecturer,
n.is_read_student, 
n.is_read_lecturer  
FROM Note n 
WHERE n.coursework_id = ? 
ORDER BY n.page_num ASC;