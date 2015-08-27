SELECT UserNotification.id, 
UserNotification.title,
UserNotification.message, 
UserNotification.date_submitted, 
UserNotification.is_read, 
UserNotification.notification_type_id, 
UserNotification.from_user_id,
User.first_name, 
Coursework.id, 
Coursework.title 
FROM UserNotification 
LEFT JOIN User 
ON User.id = UserNotification.from_user_id 
LEFT JOIN NotificationsCoursework 
ON NotificationsCoursework.user_notification_id = UserNotification.id 
LEFT JOIN Coursework 
ON Coursework.id = NotificationsCoursework.coursework_id 
WHERE UserNotification.to_user_id = 49 AND 
UserNotification.is_read = 0;