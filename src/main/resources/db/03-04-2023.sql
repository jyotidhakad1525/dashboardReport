ALTER TABLE `salesDataSetup`.`vehicles_inventory` 
ADD COLUMN `state_type` VARCHAR(45) NULL AFTER `make_id`,
ADD COLUMN `e_way_bill` VARCHAR(45) NULL AFTER `state_type`,
ADD COLUMN `gst_rate` VARCHAR(45) NULL AFTER `e_way_bill`,
ADD COLUMN `purchased_from` VARCHAR(45) NULL AFTER `gst_rate`;

ALTER TABLE `salesDataSetup`.`vehicles_inventory_history` 
ADD COLUMN `state_type` VARCHAR(45) NULL AFTER `make_id`,
ADD COLUMN `e_way_bill` VARCHAR(45) NULL AFTER `state_type`,
ADD COLUMN `gst_rate` VARCHAR(45) NULL AFTER `e_way_bill`,
ADD COLUMN `purchased_from` VARCHAR(45) NULL AFTER `gst_rate`;



ALTER TABLE `salesDataSetup`.`vehicles_inventory_history` 
CHANGE COLUMN `branch_id` `branch_id` INT NULL DEFAULT 0 ,
CHANGE COLUMN `lead_id` `lead_id` INT NULL DEFAULT 0 ,
CHANGE COLUMN `stockyard_location_id` `stockyard_location_id` INT NULL DEFAULT 0 ,
CHANGE COLUMN `stockyard_branch_id` `stockyard_branch_id` INT NULL DEFAULT 0 ,
CHANGE COLUMN `location_id` `location_id` INT NULL DEFAULT 0 ;


ALTER TABLE `salesDataSetup`.`vehicles_inventory` 
CHANGE COLUMN `branch_id` `branch_id` INT NULL DEFAULT 0 ,
CHANGE COLUMN `lead_id` `lead_id` INT NULL DEFAULT 0 ,
CHANGE COLUMN `stockyard_location_id` `stockyard_location_id` INT NULL DEFAULT 0 ,
CHANGE COLUMN `stockyard_branch_id` `stockyard_branch_id` INT NULL DEFAULT 0 ,
CHANGE COLUMN `location_id` `location_id` INT NULL DEFAULT 0 ;