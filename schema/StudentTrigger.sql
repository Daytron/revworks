CREATE DEFINER = CURRENT_USER TRIGGER `appschema`.`Student_BEFORE_INSERT` BEFORE INSERT ON `Student` FOR EACH ROW
BEGIN
	IF (SELECT usertype_id 
         FROM User
         WHERE NEW.user_id = User.id) != 2
	THEN
        SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Invalid type of User to insert as Student. Must be UserType Student for User item';
	END IF;
END
