SELECT Comment.message AS message,
 Comment.date_submitted AS dateSubmitted, 
 Comment.is_student_to_lecturer AS isStudToLec 
 FROM Comment
 WHERE Comment.review_id = ? 
 ORDER BY Comment.date_submitted ASC;