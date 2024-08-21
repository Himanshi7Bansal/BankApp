# BankApp

![pic](https://github.com/user-attachments/assets/de73997d-1130-43cc-bf02-70392b7cf764)

Command-Line Banking System 🚀
Description
This project is a Command-Line Based Banking System built using Java and integrated with a MySQL database via JDBC. The system allows users to register, log in, manage accounts, check balances, and securely perform transactions like debiting, crediting, and transferring money. Exception handling is implemented to ensure smooth and error-free operation.

Features ✨
User Registration: Create a new account by registering with valid details.
Secure Login: Log in with proper authentication to access your account(s).
Account Management: Open new accounts, or access existing accounts with ease.
Transaction Operations:
Check balance
Debit/credit money
Transfer money between accounts
Logout: Securely log out after completing operations.

Next Steps 🚀
I am implementing password encryption to enhance security.


Technologies Used 🛠️
Java: Backend logic and functionality.
JDBC: Java Database Connectivity for seamless integration with MySQL.
MySQL: Database management for storing user and transaction information.
Exception Handling: For smooth error handling during user interactions and transactions.

How to Run 🔧
Clone the repository: git clone https://github.com/your-username/BankApp.git
Set up your MySQL database:
Create a database called bankingschema.
Open the project in your favorite IDE.
Run the Main.java file to start the application.
(java -cp ./connector.jar Main.java)

Database Schema 🗄️
user Table: Stores user account details (accNo, fullName, age, profession, email, pin, balance).
register Table: Holds information about user accounts (fullName, email, password).

![schema](https://github.com/user-attachments/assets/74cba3c1-a64f-4e2c-9258-06b4e39af530)

Contributing 🤝
Feel free to contribute to this project by opening a pull request! Whether it’s bug fixes, new features, or suggestions, I’m open to collaboration.

License 📝
This project is open-source and available under the MIT License.
