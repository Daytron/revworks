SELECT Coursework.id AS id,
Coursework.title AS title,
Coursework.date_submitted AS dateSubmitted,
Coursework.file AS file,
Coursework.file_extension As fileExtension,
Coursework.student_user_id AS studentUserID,
Student.student_id AS studentID,
User.first_name AS studentFirstName,
User.last_name AS studentLastName
FROM Coursework
INNER JOIN Student ON Student.user_id = Coursework.student_user_id
INNER JOIN User ON User.id = Coursework.student_user_id
WHERE Coursework.class_id = 5;

