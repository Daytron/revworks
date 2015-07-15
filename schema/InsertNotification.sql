INSERT INTO UserNotification(id,
title,message,date_submitted,
is_read,notification_type_id,
to_user_id,from_user_id) 
VALUES (?,?,?,now(),?,?,?,?);