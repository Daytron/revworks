SELECT Note.id AS id, 
Note.page_num as pageNum,
Note.date_submitted as date 
FROM Note
WHERE Note.coursework_id = 1 
ORDER BY Note.page_num ASC;