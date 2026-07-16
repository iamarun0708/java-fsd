CREATE TABLE country (
    code VARCHAR(2) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE stock (
    st_id INT AUTO_INCREMENT PRIMARY KEY,
    st_code VARCHAR(10) NOT NULL,
    st_date DATE NOT NULL,
    st_open NUMERIC(10,2) NOT NULL,
    st_close NUMERIC(10,2) NOT NULL,
    st_volume BIGINT NOT NULL
);

CREATE TABLE department (
    dp_id INT AUTO_INCREMENT PRIMARY KEY,
    dp_name VARCHAR(50) NOT NULL
);

CREATE TABLE employee (
    em_id INT AUTO_INCREMENT PRIMARY KEY,
    em_name VARCHAR(50) NOT NULL,
    em_salary NUMERIC(10,2) NOT NULL,
    em_permanent BOOLEAN NOT NULL,
    em_date_of_birth DATE NOT NULL,
    em_dp_id INT,
    FOREIGN KEY (em_dp_id) REFERENCES department(dp_id)
);

CREATE TABLE skill (
    sk_id INT AUTO_INCREMENT PRIMARY KEY,
    sk_name VARCHAR(50) NOT NULL
);

CREATE TABLE employee_skill (
    es_em_id INT NOT NULL,
    es_sk_id INT NOT NULL,
    PRIMARY KEY (es_em_id, es_sk_id),
    FOREIGN KEY (es_em_id) REFERENCES employee(em_id),
    FOREIGN KEY (es_sk_id) REFERENCES skill(sk_id)
);

-- Quiz Tables
CREATE TABLE user (
    us_id INT AUTO_INCREMENT PRIMARY KEY,
    us_name VARCHAR(50) NOT NULL
);

CREATE TABLE question (
    qt_id INT AUTO_INCREMENT PRIMARY KEY,
    qt_text VARCHAR(255) NOT NULL,
    qt_score NUMERIC(5,2) NOT NULL
);

CREATE TABLE options (
    op_id INT AUTO_INCREMENT PRIMARY KEY,
    op_qt_id INT NOT NULL,
    op_text VARCHAR(100) NOT NULL,
    op_is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (op_qt_id) REFERENCES question(qt_id)
);

CREATE TABLE attempt (
    at_id INT AUTO_INCREMENT PRIMARY KEY,
    at_us_id INT NOT NULL,
    at_date DATETIME NOT NULL,
    at_score NUMERIC(5,2) NOT NULL,
    FOREIGN KEY (at_us_id) REFERENCES user(us_id)
);

CREATE TABLE attempt_question (
    aq_id INT AUTO_INCREMENT PRIMARY KEY,
    aq_at_id INT NOT NULL,
    aq_qt_id INT NOT NULL,
    FOREIGN KEY (aq_at_id) REFERENCES attempt(at_id),
    FOREIGN KEY (aq_qt_id) REFERENCES question(qt_id)
);

CREATE TABLE attempt_option (
    ao_id INT AUTO_INCREMENT PRIMARY KEY,
    ao_aq_id INT NOT NULL,
    ao_op_id INT NOT NULL,
    FOREIGN KEY (ao_aq_id) REFERENCES attempt_question(aq_id),
    FOREIGN KEY (ao_op_id) REFERENCES options(op_id)
);
