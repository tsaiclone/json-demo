# SQL Saturday 2016 JSON Tools Use Cases
Thanks for joining us at SQL Saturday 2016 #499 at the Pyle Center in Madison, WI! Here are the artifacts from the JSON presentation with Steve Perkins. This repo has:
* /dist folder
 * PowerPoint deck (including animations)
 * `db_schema.sql` - SQL script to create the sample database and tables, and populate them with 10,000 test requests/responses (thanks [randomuser.me](http://randomuser.me)!)
 * `SQLExamples.sql` - Example SQL queries against the JSON and tabular data in the test database. Includes the examples from the presentation and some extras.
* root folder
 * Java code for DOSing randomuser.me
 * Kidding, it does do that but only if you multithread too far. The Java code can be used to generate INSERT statements for random user accounts and Stripe request/response JSON associated with a random user.
 
## The PowerPoint
Since SQL Server Management Studio was used to demonstrate the SQL examples during the presentation, we've added hidden slides to the slide deck so you can still see example SQL and resultsets in context. To view them, click the "See Example" links within the presentation.


## Downloading
| I want to get... | |
|---|---|
| Everything | click the Download Zip button in the upper-right corner |
| Just the SQL, thanks | click the two .sql files in the `dist` folder, then the Raw button in the upper right |
| The PowerPoint | click the .pptx file in the `dist` folder, then the Raw button in the upper right |
