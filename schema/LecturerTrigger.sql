CREATE DEFINER = CURRENT_USER TRIGGER `appschema`.`Lecturer_BEFORE_INSERT` BEFORE INSERT ON `Lecturer` FOR EACH ROW
BEGIN
	IF (SELECT usertype_id 
         FROM User
         WHERE NEW.user_id = User.id) != 3
	THEN
        SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Invalid type of User to insert as Lecturer. Must be UserType Lecturer for User item';
	END IF;
END
