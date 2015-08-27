SELECT n.id, n.page_num,
n.date_submitted, 
n.is_student_to_lecturer,
n.is_read_student, 
n.is_read_lecturer  
FROM Note n 
WHERE n.id = 3 
ORDER BY n.date_submitted ASC;