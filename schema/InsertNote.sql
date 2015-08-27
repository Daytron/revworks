INSERT INTO Note(page_num,date_submitted,
is_student_to_lecturer,is_read_student,
is_read_lecturer,coursework_id) 
VALUES (?,now(),?,?,?,?);