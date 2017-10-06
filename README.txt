Emilio Arellano

To build this project I ended up using a few triggers and sequences mostly for inserting new tuples into several of my tables.
I also used Functions to calculate total seconds called a specific account in a certain month as well as data used, texts sent
and phones purchased.

My Data generation code only works when all tables and sequences are droped and recreated. For both of these actions will be provided.
and the last few UsageText tulples will be printed out to the console. these must be copied and pasted to SQLDeveloper or just added 
manualy to the database. 

In my project I created two interfaces. The first interface is an online portal for my customers where they can log in using
their Account ID and primary Phone number. Once they have logged into the account they are able to buy new devices for their
existing phone numbers. Purchase an additional phone number and a device with it. Discontinue any phone number in their account.
Change their primary number. And change their billing plan. In this interface new customers can also open a new account with
Jog. When doing so they need to provide a full name and address as well as the type of account that they want to open. There
are three types of accounts: individual (only one phone number), family (up to 6 phone numbers) and Business (unlimited
amounts of phone numbers).
     
When purchasing a new Device the user must link it to an existing number. He cannot buy an unlinked devise.
     
When opening an account the user is given three numbers to choose from. And then he must purchase a device to accompany it.

When discontinuing a phone number the number is untied from the account but it stays in my database. But because I do not
want thousands of discontinued numbers in my database, once I reach three discontinued numbers then the next person to want 
to get a new number will have to select one of those three.

A user can only close their account at jog by discontinuing all of their phone numbers. But if that user decides to re-open 
their account they can do so with their client ID and Name. This way I do not end up having customer with multiple instances in 
my database.

A good way to test run this interface is to first run start by creating a new account of type family or business. Create it 
with more than 3 numbers. Then discontinue three of the lines and add a new line to see that the three lines discontinued are the 
one you will be able to choose from. Then buy a new device. Switch your primary number. And log out. Then try to log back in with 
your account ID and primary number. And log out. 

The second interface I created is a Billing statement generator. Can generate a single account’s billing statement for a specific 
year and month (as of right now I only have activity logged in for April 2016). It also has the option to generate the billing 
statements for all accounts in the Data base. To implement this interface I had to add two new relations to my ER design. I added a 
table relation called Purchased that keeps track of the date all the devises purchased by a given account. The second table I added 
is one called BillingStement that saves the billing statements into the database so that if the client decides to change his billing 
plan the previous months’ statements will not be affected.

To test this interface I would suggest getting the billing plan of either account ID 1 or two since they have multiple phone numbers. 
The monthly pay will probably be ridiculously high because my data usage is extremely expensive. That is where I make all of my money. You 
can also try getting the statement for the client that you just created in the previous interface. At that point the charges will only 
be for the purchase of the phone devices and the monthly payment if it applies to the selected billing plan.
