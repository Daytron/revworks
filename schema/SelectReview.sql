SELECT Review.id AS id, 
Review.page_num as pageNum,
Review.date_submitted as date 
FROM Review
WHERE Review.coursework_id = 1 
ORDER BY Review.page_num ASC;