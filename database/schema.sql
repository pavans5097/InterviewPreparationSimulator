CREATE DATABASE IF NOT EXISTS interview_simulator
 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE interview_simulator;

CREATE TABLE IF NOT EXISTS users (
 user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 full_name VARCHAR(100) NOT NULL,
 email VARCHAR(254) NOT NULL UNIQUE,
 password_hash VARCHAR(128) NOT NULL,
 password_salt VARCHAR(64) NOT NULL,
 password_iterations INT NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS questions (
 question_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 category VARCHAR(80) NOT NULL,
 difficulty ENUM('Easy','Medium','Hard') NOT NULL DEFAULT 'Easy',
 question_text TEXT NOT NULL,
 option_a VARCHAR(500) NOT NULL,
 option_b VARCHAR(500) NOT NULL,
 option_c VARCHAR(500) NOT NULL,
 option_d VARCHAR(500) NOT NULL,
 correct_option CHAR(1) NOT NULL,
 explanation TEXT NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 INDEX idx_questions_category (category)
);

CREATE TABLE IF NOT EXISTS attempts (
 attempt_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_id BIGINT NOT NULL,
 mode VARCHAR(30) NOT NULL DEFAULT 'Practice',
 start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 end_time TIMESTAMP NULL,
 score INT NOT NULL DEFAULT 0,
 total_questions INT NOT NULL DEFAULT 0,
 FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_answers (
 answer_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 attempt_id BIGINT NOT NULL,
 question_id BIGINT NOT NULL,
 selected_option CHAR(1) NULL,
 is_correct BOOLEAN NOT NULL DEFAULT FALSE,
 time_taken INT NOT NULL DEFAULT 0,
 FOREIGN KEY (attempt_id) REFERENCES attempts(attempt_id) ON DELETE CASCADE,
 FOREIGN KEY (question_id) REFERENCES questions(question_id),
 UNIQUE KEY uq_attempt_question (attempt_id, question_id)
);

INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation)
SELECT 'Java','Easy','Which keyword is used to inherit a class in Java?','implements','extends','inherits','super','B','The extends keyword is used when one class inherits another.'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE category='Java');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation)
SELECT 'Java','Easy','Which method is the usual entry point of a Java application?','start()','run()','main()','init()','C','The JVM invokes public static void main(String[] args) in a standard Java application.'
WHERE (SELECT COUNT(*) FROM questions WHERE category='Java') < 2;
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation)
SELECT 'Java','Easy','Which collection does not allow duplicate elements?','ArrayList','LinkedList','HashSet','Vector','C','A Set stores unique elements; HashSet is an implementation of Set.'
WHERE (SELECT COUNT(*) FROM questions WHERE category='Java') < 3;
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation)
SELECT 'DSA','Easy','Which data structure follows FIFO order?','Stack','Queue','Tree','Graph','B','A queue follows First In, First Out.'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE category='DSA');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation)
SELECT 'DSA','Easy','What is the time complexity of binary search on a sorted array?','O(n)','O(log n)','O(n log n)','O(1) always','B','Binary search halves the remaining search interval each step.'
WHERE (SELECT COUNT(*) FROM questions WHERE category='DSA') < 2;
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation)
SELECT 'DBMS','Easy','Which SQL command retrieves rows from a table?','GET','OPEN','SELECT','FETCH TABLE','C','SELECT retrieves data from one or more tables.'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE category='DBMS');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation)
SELECT 'Operating Systems','Easy','Which component allocates CPU time to processes?','Compiler','Scheduler','Linker','Loader only','B','The OS scheduler chooses which ready process runs on the CPU.'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE category='Operating Systems');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation)
SELECT 'Computer Networks','Easy','Which protocol is commonly used to securely browse websites?','HTTP','FTP','HTTPS','Telnet','C','HTTPS uses TLS to protect communication.'
WHERE NOT EXISTS (SELECT 1 FROM questions WHERE category='Computer Networks');


-- Feature tables: bookmarks are user-specific and safe to create on repeated schema runs.
CREATE TABLE IF NOT EXISTS bookmarks (
 bookmark_id BIGINT PRIMARY KEY AUTO_INCREMENT,
 user_id BIGINT NOT NULL,
 question_id BIGINT NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 UNIQUE KEY uq_bookmark_user_question (user_id, question_id),
 FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
 FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);


-- Expanded question bank: safe to run repeatedly; duplicates are skipped by question text.
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Easy', 'Which of these is not a primitive type in Java?', 'int', 'boolean', 'String', 'char', 'C', 'String is a class, not a primitive type.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which of these is not a primitive type in Java?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Easy', 'Which keyword creates an object in Java?', 'make', 'new', 'class', 'object', 'B', 'The new operator creates an object.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which keyword creates an object in Java?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Easy', 'Which access modifier allows access from anywhere?', 'private', 'protected', 'public', 'default', 'C', 'Public members can be accessed wherever the class is accessible.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which access modifier allows access from anywhere?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Medium', 'What is method overloading?', 'Same method name with different parameter lists', 'Same signature in child class', 'Hiding all methods', 'Changing return type only', 'A', 'Overloading uses the same name with different parameter lists.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is method overloading?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Easy', 'Which block is used to handle an exception?', 'try', 'final', 'throws', 'catching', 'D', 'A catch block handles an exception thrown from a try block.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which block is used to handle an exception?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Medium', 'Which interface is the root of the Java Collections Framework hierarchy?', 'List', 'Collection', 'Iterable only', 'Map', 'B', 'Collection is the main root interface for List, Set, and Queue.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which interface is the root of the Java Collections Framework hierarchy?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Easy', 'Which keyword prevents a class from being inherited?', 'static', 'const', 'final', 'private', 'C', 'A final class cannot be extended.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which keyword prevents a class from being inherited?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Medium', 'Which keyword refers to the current object?', 'this', 'super', 'self', 'current', 'A', 'this refers to the current instance.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which keyword refers to the current object?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Easy', 'Which method compares the contents of two strings?', '==', 'compare', 'equals()', 'same()', 'C', 'String.equals compares character content.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which method compares the contents of two strings?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Java', 'Medium', 'Which exception may occur when an integer is divided by zero?', 'IOException', 'ArithmeticException', 'NullPointerException', 'ClassNotFoundException', 'B', 'Integer division by zero throws ArithmeticException.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which exception may occur when an integer is divided by zero?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Easy', 'Which data structure uses LIFO order?', 'Queue', 'Stack', 'Heap', 'Graph', 'B', 'A stack follows Last In, First Out.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which data structure uses LIFO order?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Easy', 'What is the worst-case time complexity of linear search?', 'O(1)', 'O(log n)', 'O(n)', 'O(n log n)', 'C', 'Linear search may inspect every element.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is the worst-case time complexity of linear search?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Medium', 'Which sorting algorithm is generally stable in its standard implementation?', 'Selection sort', 'Heap sort', 'Merge sort', 'Quick sort', 'C', 'Merge sort can preserve the relative order of equal elements.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which sorting algorithm is generally stable in its standard implementation?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Easy', 'Which traversal of a binary search tree returns sorted keys?', 'Preorder', 'Inorder', 'Postorder', 'Level order', 'B', 'Inorder traversal visits left subtree, node, then right subtree.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which traversal of a binary search tree returns sorted keys?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Medium', 'What is the average time complexity of quicksort?', 'O(n)', 'O(log n)', 'O(n log n)', 'O(n² log n)', 'C', 'Balanced partitions yield average O(n log n) time.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is the average time complexity of quicksort?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Easy', 'Which data structure is commonly used for BFS?', 'Stack', 'Queue', 'Hash table only', 'Heap', 'B', 'Breadth-first search uses a queue.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which data structure is commonly used for BFS?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Medium', 'What is the time complexity of inserting at the head of a linked list?', 'O(1)', 'O(log n)', 'O(n)', 'O(n²)', 'A', 'Only the head pointer and new node links need updating.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is the time complexity of inserting at the head of a linked list?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Easy', 'A graph with no cycles is called what?', 'Complete graph', 'Cyclic graph', 'Acyclic graph', 'Weighted graph', 'C', 'An acyclic graph contains no cycles.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='A graph with no cycles is called what?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Medium', 'Which structure is suitable for implementing recursion?', 'Queue', 'Call stack', 'Hash set', 'Adjacency matrix', 'B', 'Function calls and local state are tracked on the call stack.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which structure is suitable for implementing recursion?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DSA', 'Easy', 'What is the index of the first element in a Java array?', '-1', '0', '1', 'Depends on size', 'B', 'Java arrays use zero-based indexing.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is the index of the first element in a Java array?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DBMS', 'Easy', 'What does a primary key ensure?', 'Duplicate rows', 'Unique identification of each row', 'Only sorted rows', 'Encrypted data', 'B', 'A primary key uniquely identifies each record and cannot be NULL.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What does a primary key ensure?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DBMS', 'Easy', 'Which SQL command modifies existing rows?', 'ALTER', 'UPDATE', 'INSERT', 'CREATE', 'B', 'UPDATE changes values in existing rows.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which SQL command modifies existing rows?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DBMS', 'Medium', 'What is a foreign key used for?', 'Compressing a table', 'Referential integrity', 'Sorting columns', 'Creating backups', 'B', 'A foreign key references a key in another or the same table.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is a foreign key used for?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DBMS', 'Easy', 'Which normal form removes partial dependencies on a composite key?', '1NF', '2NF', '3NF', 'BCNF only', 'B', 'Second normal form eliminates partial dependency on part of a candidate key.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which normal form removes partial dependencies on a composite key?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DBMS', 'Medium', 'Which ACID property means a transaction is all-or-nothing?', 'Consistency', 'Isolation', 'Atomicity', 'Durability', 'C', 'Atomicity means a transaction completes fully or has no effect.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which ACID property means a transaction is all-or-nothing?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DBMS', 'Easy', 'Which clause filters grouped results?', 'WHERE', 'ORDER BY', 'HAVING', 'LIMIT', 'C', 'HAVING filters groups after aggregation.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which clause filters grouped results?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DBMS', 'Easy', 'Which SQL function counts rows?', 'SUM()', 'COUNT()', 'TOTAL()', 'NUMBER()', 'B', 'COUNT returns the number of rows or non-NULL values depending on its argument.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which SQL function counts rows?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'DBMS', 'Medium', 'What is an index mainly used for?', 'Speeding up data retrieval', 'Encrypting records', 'Preventing all duplicates', 'Replacing tables', 'A', 'Indexes can improve lookup performance at the cost of storage and write overhead.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is an index mainly used for?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Operating Systems', 'Easy', 'Which is an example of an operating system?', 'Oracle', 'Linux', 'JDBC', 'HTML', 'B', 'Linux is an operating system.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which is an example of an operating system?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Operating Systems', 'Medium', 'What is a process?', 'A program in execution', 'A source-code comment', 'A disk partition', 'A compiler warning', 'A', 'A process is an executing instance of a program.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is a process?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Operating Systems', 'Easy', 'Which memory is volatile?', 'SSD', 'ROM', 'RAM', 'Blu-ray', 'C', 'RAM generally loses its contents when power is removed.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which memory is volatile?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Operating Systems', 'Medium', 'What is deadlock?', 'A process finishing normally', 'Processes waiting indefinitely for resources held by each other', 'A fast context switch', 'A disk cleanup operation', 'B', 'Deadlock can occur when processes wait in a cycle for resources.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is deadlock?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Operating Systems', 'Easy', 'Which scheduling algorithm uses a fixed time quantum?', 'FCFS', 'Round Robin', 'Shortest Job First only', 'Priority without preemption', 'B', 'Round Robin gives each ready process a time slice.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which scheduling algorithm uses a fixed time quantum?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Operating Systems', 'Medium', 'What does virtual memory allow?', 'Only one process to run', 'Programs to use an address space larger than physical RAM', 'RAM to become permanent', 'CPU clock to increase', 'B', 'Virtual memory maps virtual addresses to physical memory and may use disk-backed pages.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What does virtual memory allow?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Operating Systems', 'Easy', 'What is a context switch?', 'Changing source language', 'Switching CPU execution between processes or threads', 'Deleting a process', 'Formatting memory', 'B', 'A context switch saves and restores execution state.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is a context switch?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Computer Networks', 'Easy', 'How many layers are in the OSI model?', '4', '5', '7', '8', 'C', 'The OSI reference model has seven layers.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='How many layers are in the OSI model?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Computer Networks', 'Easy', 'Which device forwards packets between networks?', 'Hub', 'Router', 'Repeater', 'Keyboard', 'B', 'Routers forward packets between IP networks.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which device forwards packets between networks?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Computer Networks', 'Medium', 'What does DNS do?', 'Encrypts all traffic', 'Maps domain names to IP addresses', 'Assigns CPU processes', 'Compresses images', 'B', 'DNS resolves names such as example.com to records including IP addresses.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What does DNS do?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Computer Networks', 'Easy', 'Which protocol assigns IP configuration automatically?', 'DHCP', 'SMTP', 'SSH', 'IMAP', 'A', 'DHCP can automatically provide IP addresses and related network settings.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which protocol assigns IP configuration automatically?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Computer Networks', 'Medium', 'Which transport protocol is connection-oriented?', 'UDP', 'IP', 'TCP', 'ARP', 'C', 'TCP establishes a connection and provides reliable ordered byte-stream delivery.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='Which transport protocol is connection-oriented?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Computer Networks', 'Easy', 'What is the default port for HTTPS?', '21', '25', '80', '443', 'D', 'HTTPS commonly uses TCP port 443.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What is the default port for HTTPS?');
INSERT INTO questions (category,difficulty,question_text,option_a,option_b,option_c,option_d,correct_option,explanation) SELECT 'Computer Networks', 'Medium', 'What does LAN stand for?', 'Large Access Node', 'Local Area Network', 'Linked Analog Network', 'Long Area Number', 'B', 'A LAN connects devices across a limited geographic area.' WHERE NOT EXISTS (SELECT 1 FROM questions WHERE question_text='What does LAN stand for?');
