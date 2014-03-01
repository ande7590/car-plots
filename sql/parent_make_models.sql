UPDATE `MakeModel` SET `ParentMakeModelID` = `MakeModelID`;
UPDATE `MakeModel` SET `ParentMakeModelID` = 68 WHERE MakeName LIKE 'BMW' AND ModelName LIKE '3%';
UPDATE `MakeModel` SET `ParentMakeModelID` = 75 WHERE MakeName LIKE 'BMW' AND ModelName LIKE '5%';
UPDATE `MakeModel` SET `ParentMakeModelID` = 85 WHERE MakeName LIKE 'BMW' AND ModelName LIKE '6%';
UPDATE `MakeModel` SET `ParentMakeModelID` = 88 WHERE MakeName LIKE 'BMW' AND ModelName LIKE '7%';
