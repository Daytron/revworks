SELECT Review.id AS id, 
Review.scroll_location as scroll,
Review.date_submitted as date 
FROM Review
WHERE Review.coursework_id = 1;