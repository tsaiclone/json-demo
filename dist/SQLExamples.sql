-- Get all account and requests/responses by user's first and last names
SELECT acct.*,
		log.*
FROM dbo.user_account acct
JOIN dbo.stripe_log log ON acct.username = JSON_VALUE(log.request, '$.username')
WHERE LOWER(acct.first_name) = 'aaron' AND LOWER(acct.last_name) = 'howell';


---
-- Slide example 1
-- Get account and all requests/responses by user's first and last names
---
SELECT acct.first_name, acct.last_name,
		log.request, log.response
FROM dbo.user_account acct
JOIN dbo.stripe_log log ON acct.username = JSON_VALUE(log.request, '$.username')
WHERE LOWER(acct.first_name) = 'aaron' AND LOWER(acct.last_name) = 'howell';


-- Get the total spend for all users
SELECT acct.username,
		FORMAT(
		  SUM(
			CAST(JSON_VALUE(log.request, '$.amount') AS DECIMAL(10, 2))
		  )/100.0
		, 'N2') AS total_spend
FROM dbo.user_account acct
JOIN dbo.stripe_log log ON acct.username = JSON_VALUE(log.request, '$.username')
GROUP BY acct.username;


---
-- Slide example 2
-- Get a subset of the request JSON (source object) for a specific user
---
SELECT acct.username,
	JSON_QUERY(log.request, '$.source') AS card_from_request
FROM dbo.user_account acct
JOIN dbo.stripe_log log ON acct.username = JSON_VALUE(log.request, '$.username')
WHERE acct.username='beautifulbear110';


-- Get a subset of the request JSON (source object) for all users
SELECT acct.username,
	JSON_QUERY(log.request, '$.source')
FROM dbo.user_account acct
JOIN dbo.stripe_log log ON acct.username = JSON_VALUE(log.request, '$.username');




-- Get all credit card numbers by accessing a nested JSON object property
SELECT JSON_VALUE(log.request, '$.source.number') AS credit_cards_mwahaha
FROM dbo.stripe_log log;


---
-- Slide example 3
-- Get the total spend for a specific user
---
SELECT acct.username,
		FORMAT(
		  SUM(
			CAST(JSON_VALUE(log.request, '$.amount') AS DECIMAL(10, 2))
		  )/100.0
		, 'N2') AS total_spend
FROM dbo.user_account acct
JOIN dbo.stripe_log log ON acct.username = JSON_VALUE(log.request, '$.username')
WHERE acct.username='beautifulbear110'
GROUP BY acct.username;

-- Update a JSON property
UPDATE dbo.stripe_log SET request=JSON_MODIFY(request, '$.username', 'zoomit') where id=3610;


---
-- Slide example 4
-- FOR JSON AUTO mangles URLs
---
SELECT account.id,
	account.username,
	account.image_url
FROM dbo.user_account account
WHERE id = 3610
FOR JSON AUTO;
