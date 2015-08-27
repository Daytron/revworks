SELECT Semester.id FROM Semester 
WHERE (curdate() BETWEEN Semester.startDate AND Semester.endDate);