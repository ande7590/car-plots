UPDATE Location L
INNER JOIN Search SS ON L.Zipcode = SS.Zipcode
SET L.ScraperSearch = 1
