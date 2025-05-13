CREATE DATABASE Library_System3;
USE Library_System3;



CREATE TABLE staff (
    StaffID INT PRIMARY KEY,
    Fname VARCHAR(20) NOT NULL,
    minit VARCHAR(20),
    Lname VARCHAR(20) NOT NULL,
    age TINYINT,
    email VARCHAR(100),
    position VARCHAR(50),
    is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE book (
    bookID INT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    StaffID INT,
    FOREIGN KEY (StaffID) REFERENCES staff(StaffID)
);

CREATE TABLE inventory (
    bookID INT PRIMARY KEY,
    total_copies INT NOT NULL,
    available_copies INT NOT NULL,
    FOREIGN KEY (bookID) REFERENCES book(bookID)
);

CREATE TABLE members (
    MemberID INT PRIMARY KEY,
    Fname VARCHAR(20) NOT NULL,
    minit VARCHAR(20),
    Lname VARCHAR(20) NOT NULL,
    age TINYINT NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL
);

CREATE TABLE Membership (
    MembershipID INT AUTO_INCREMENT PRIMARY KEY,
    MemberID INT NOT NULL,
    DurationMonths INT NOT NULL,
    StartDate DATE NOT NULL,
    EndDate DATE NOT NULL,
    MembershipType ENUM('Regular', 'VIP', 'Premium') NOT NULL DEFAULT 'Regular',
    MembershipFee INT NOT NULL,
    MembershipStatus ENUM('Active', 'Cancelled') NOT NULL DEFAULT 'Active',
    FOREIGN KEY (MemberID) REFERENCES members(MemberID) ON DELETE CASCADE
);


CREATE TABLE borrow_transactions (
    TransactionID INT AUTO_INCREMENT PRIMARY KEY,
    MemberID INT NOT NULL,
    BookID INT NOT NULL,
    BorrowDate DATE NOT NULL,
    ReturnDate DATE,
    FOREIGN KEY (MemberID) REFERENCES members(MemberID) ON DELETE CASCADE,
    FOREIGN KEY (BookID) REFERENCES book(bookID) ON DELETE CASCADE
);


INSERT INTO book (bookID, title, author, is_available) VALUES
(1, 'The Great Gatsby', 'F. Scott Fitzgerald', TRUE),
(2, 'To Kill a Mockingbird', 'Harper Lee', TRUE),
(3, '1984', 'George Orwell', TRUE),
(4, 'Pride and Prejudice', 'Jane Austen', TRUE);

INSERT INTO inventory (bookID, total_copies, available_copies) VALUES
(1, 5, 5),
(2, 3, 3),
(3, 4, 4),
(4, 2, 2);

INSERT INTO members (MemberID, Fname, minit, Lname, age, email) VALUES
(1, 'John', 'A', 'Doe', 30, 'john.doe@example.com'),
(2, 'Jane', 'B', 'Smith', 25, 'jane.smith@example.com'),
(3, 'Alice', 'C', 'Johnson', 28, 'alice.johnson@example.com'),
(4, 'Bob', 'D', 'Brown', 35, 'bob.brown@example.com');

INSERT INTO Membership (MemberID, DurationMonths, StartDate, EndDate, MembershipType, MembershipFee, MembershipStatus) VALUES
(1, 12, '2023-01-01', '2024-01-01', 'Regular', 50, 'Active'),
(2, 6, '2023-06-01', '2023-12-01', 'VIP', 100, 'Active'),
(3, 12, '2023-03-01', '2024-03-01', 'Premium', 150, 'Active'),
(4, 3, '2023-09-01', '2023-12-01', 'Regular', 30, 'Cancelled');

INSERT INTO staff (StaffID, Fname, minit, Lname, age, email, position, is_available) VALUES
(1, 'Emily', 'E', 'Davis', 40, 'emily.davis@example.com', 'Librarian', TRUE),
(2, 'Michael', 'F', 'Wilson', 35, 'michael.wilson@example.com', 'Assistant Librarian', TRUE),
(3, 'Sarah', 'G', 'Taylor', 29, 'sarah.taylor@example.com', 'Library Manager', TRUE),
(4, 'David', 'H', 'Anderson', 45, 'david.anderson@example.com', 'IT Support', TRUE);

INSERT INTO borrow_transactions (MemberID, BookID, BorrowDate, ReturnDate) VALUES
(1, 1, '2023-10-01', NULL),
(2, 2, '2023-10-02', NULL),
(3, 3, '2023-10-03', NULL),
(4, 4, '2023-10-04', NULL);


DELETE FROM book WHERE bookID = 5; 
DELETE FROM members WHERE MemberID = 5; 
DELETE FROM Membership WHERE MembershipID = 1; 
DELETE FROM borrow_transactions WHERE TransactionID = 1; 


UPDATE book SET is_available = FALSE WHERE bookID = 1;
UPDATE members SET age = 31 WHERE MemberID = 1;
UPDATE Membership SET MembershipStatus = 'Cancelled' WHERE MembershipID = 2;
UPDATE borrow_transactions SET ReturnDate = '2023-10-10' WHERE TransactionID = 1;


DELIMITER //
CREATE TRIGGER after_borrow_insert
AFTER INSERT ON borrow_transactions
FOR EACH ROW
BEGIN
    UPDATE inventory
    SET available_copies = available_copies - 1
    WHERE bookID = NEW.BookID;
END //

DELIMITER //
CREATE TRIGGER after_borrow_update
AFTER UPDATE ON borrow_transactions
FOR EACH ROW
BEGIN
    IF NEW.ReturnDate IS NOT NULL AND OLD.ReturnDate IS NULL THEN
        UPDATE inventory
        SET available_copies = available_copies + 1
        WHERE bookID = NEW.BookID;
    END IF;
END //


DELIMITER //
CREATE TRIGGER before_book_delete
BEFORE DELETE ON book
FOR EACH ROW
BEGIN
    DECLARE transaction_count INT;

    SELECT COUNT(*) INTO transaction_count
    FROM borrow_transactions
    WHERE BookID = OLD.bookID AND ReturnDate IS NULL;

    IF transaction_count > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Cannot delete book with active borrow transactions';
    END IF;
END //


DELIMITER //
CREATE TRIGGER before_membership_insert
BEFORE INSERT ON Membership
FOR EACH ROW
BEGIN
    SET NEW.EndDate = DATE_ADD(NEW.StartDate, INTERVAL NEW.DurationMonths MONTH);
END //
DELIMITER ;

DELIMITER //
CREATE PROCEDURE AddNewMember(
    IN p_Fname VARCHAR(20),
    IN p_minit VARCHAR(20),
    IN p_Lname VARCHAR(20),
    IN p_age TINYINT,
    IN p_email VARCHAR(100)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;

    IF (SELECT COUNT(*) FROM members WHERE email = p_email) > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Email already exists';
    ELSE
        INSERT INTO members (Fname, minit, Lname, age, email) VALUES (p_Fname, p_minit, p_Lname,p_age, p_email);
    END IF;

    COMMIT;
END //
DELIMITER ;

CREATE VIEW StaffBorrowedBooks AS
SELECT 
    m.MemberID,
    m.Fname AS MemberFirstName,
    m.Lname AS MemberLastName,
    b.title AS BookTitle,
    bt.BorrowDate,
    bt.ReturnDate
FROM 
    borrow_transactions bt
JOIN 
    members m ON bt.MemberID = m.MemberID
JOIN 
    book b ON bt.BookID = b.bookID;
    
    CREATE VIEW MemberBorrowedBooks AS
SELECT 
    bt.TransactionID,
    b.title AS BookTitle,
    bt.BorrowDate,
    bt.ReturnDate
FROM 
    borrow_transactions bt
JOIN 
    book b ON bt.BookID = b.bookID;
    
SELECT * FROM StaffBorrowedBooks; 


SELECT * FROM members WHERE MemberID = 1;

SELECT title FROM book WHERE is_available = TRUE
UNION
SELECT title FROM book WHERE author = 'Jane Austen';

SELECT DISTINCT MembershipType FROM Membership;

SELECT COUNT(*) AS TotalMembers FROM members;

SELECT * FROM members WHERE MemberID IN (1, 2, 3);

SELECT * FROM borrow_transactions WHERE BorrowDate BETWEEN '2023-10-01' AND '2023-10-04';

SELECT * FROM members WHERE email IS NULL;

SELECT * FROM book WHERE NOT is_available;

SELECT * FROM members WHERE email LIKE '%@example.com';

SELECT * FROM members WHERE MemberID IN (SELECT MemberID FROM Membership WHERE MembershipStatus = 'Active');

SELECT * FROM book WHERE bookID > ALL (SELECT bookID FROM book WHERE is_available = FALSE);

SELECT MembershipType, COUNT(*) AS Count FROM Membership GROUP BY MembershipType;

SELECT * FROM members ORDER BY age DESC;

SELECT Fname, Lname, 
    CASE 
        WHEN age < 30 THEN 'Young'
        WHEN age BETWEEN 30 AND 50 THEN 'Middle-aged'
        ELSE 'Senior'
    END AS AgeGroup
FROM members;