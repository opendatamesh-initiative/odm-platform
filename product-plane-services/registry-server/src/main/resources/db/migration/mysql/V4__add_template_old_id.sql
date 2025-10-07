-- Add OLD_ID column to TEMPLATES table
ALTER TABLE `ODMREGISTRY`.`TEMPLATES` ADD COLUMN `OLD_ID` VARCHAR(255);

-- Copy existing ID values to OLD_ID column for backward compatibility
UPDATE `ODMREGISTRY`.`TEMPLATES` SET `OLD_ID` = `ID`;
