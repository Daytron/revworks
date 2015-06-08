CREATE DEFINER = CURRENT_USER TRIGGER `appschema`.`Admin_BEFORE_INSERT` BEFORE INSERT ON `Admin` FOR EACH ROW
BEGIN
		IF (SELECT usertype_id 
         FROM User
         WHERE NEW.user_id = User.id) != 1
	THEN
        SIGNAL SQLSTATE '45000'
			SET MESSAGE_TEXT = 'Invalid type of User to insert as Admin. Must be UserType Admin for User item';
	END IF;
END
