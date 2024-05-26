-- Creation of a database
CREATE DATABASE IF NOT EXISTS Library;
USE Library;

-- Creation of a table `users`
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    member_in_good_standing BOOLEAN DEFAULT TRUE,
    email VARCHAR(255) UNIQUE NOT NULL,
    number_borrowing INT DEFAULT 0,
	CHECK (number_borrowing BETWEEN 0 AND 3),
    birth_date DATE NOT NULL
);

-- Création de la table `livres_empruntes`
CREATE TABLE IF NOT EXISTS books (
    isbn VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id INT NOT NULL,
    loan_date DATE NOT NULL,
    return_date DATE NOT NULL,
    quantity_available INT,
	total_quantity INT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insertion of members in table users
INSERT INTO users (id,firstname, lastname, member_in_good_standing, email, birth_date, number_borrowing) VALUES
(1,'rithan', 'juli', TRUE,'julirithan@cy-tech.fr','2004-06-29',0),
(2,'sekou', 'bah', TRUE,'bahsekou@cy-tech.fr','2003-09-21',0),
(3,'anis', 'melaimi', TRUE,'melaimiani@cy-tech.fr','2003-07-15',0),
(4,'yassine', 'lazizi', TRUE,'laziziyass@cy-tech.fr','2002-04-20',0),
(5,'betsaleel', 'clovis', TRUE,'clovisbets@cy-tech.fr','2003-04-20',0);



CREATE TABLE IF NOT EXISTS historic (
	id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(255) NOT NULL,
    loan_date DATE NOT NULL
);



