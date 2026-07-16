-- Countries Seed Data
INSERT INTO country (code, name) VALUES ('IN', 'India');
INSERT INTO country (code, name) VALUES ('US', 'United States of America');
INSERT INTO country (code, name) VALUES ('FR', 'France');
INSERT INTO country (code, name) VALUES ('DE', 'Germany');
INSERT INTO country (code, name) VALUES ('JP', 'Japan');
INSERT INTO country (code, name) VALUES ('BV', 'Bouvet Island');
INSERT INTO country (code, name) VALUES ('DJ', 'Djibouti');
INSERT INTO country (code, name) VALUES ('GP', 'Guadeloupe');
INSERT INTO country (code, name) VALUES ('GS', 'South Georgia and the South Sandwich Islands');
INSERT INTO country (code, name) VALUES ('LU', 'Luxembourg');
INSERT INTO country (code, name) VALUES ('SS', 'South Sudan');
INSERT INTO country (code, name) VALUES ('TF', 'French Southern Territories');
INSERT INTO country (code, name) VALUES ('UM', 'United States Minor Outlying Islands');
INSERT INTO country (code, name) VALUES ('ZA', 'South Africa');
INSERT INTO country (code, name) VALUES ('ZM', 'Zambia');
INSERT INTO country (code, name) VALUES ('ZW', 'Zimbabwe');

-- Stock Seed Data
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-03', 184.00, 182.39, 9779400);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-04', 184.65, 187.14, 11308000);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-05', 188.53, 190.90, 13876700);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-06', 190.21, 187.49, 15226800);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-09', 187.73, 188.76, 14722400);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-10', 187.44, 186.17, 15455900);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-11', 186.46, 188.49, 11761700);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-12', 189.86, 187.47, 11419800);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-13', 187.33, 187.19, 11441100);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-16', 186.93, 186.22, 8444800);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-17', 186.66, 188.08, 9671100);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-18', 188.09, 188.14, 9681900);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-19', 188.66, 190.14, 10392700);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-20', 190.66, 189.93, 19934200);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-23', 189.34, 186.82, 13327600);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-24', 187.98, 181.28, 18546600);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-25', 181.45, 182.80, 18068300);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-26', 181.33, 180.11, 16083300);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-09-27', 180.49, 177.10, 14656200);

INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('GOOGL', '2019-04-22', 1236.67, 1253.76, 954200);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('GOOGL', '2019-04-23', 1256.64, 1270.59, 1593400);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('GOOGL', '2019-04-24', 1270.59, 1260.05, 1169800);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('GOOGL', '2019-04-25', 1270.30, 1267.34, 1567200);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('GOOGL', '2019-04-26', 1273.38, 1277.42, 1361400);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('GOOGL', '2019-04-29', 1280.51, 1296.20, 3618400);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('GOOGL', '2019-10-17', 1251.40, 1252.80, 1047900);

INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2019-01-31', 165.60, 166.69, 77233600);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2018-10-31', 155.00, 151.79, 60101300);
INSERT INTO stock (st_code, st_date, st_open, st_close, st_volume) VALUES ('FB', '2018-12-19', 141.21, 133.24, 57404900);

-- Department Seed Data
INSERT INTO department (dp_id, dp_name) VALUES (1, 'IT');
INSERT INTO department (dp_id, dp_name) VALUES (2, 'HR');
INSERT INTO department (dp_id, dp_name) VALUES (3, 'Finance');

-- Employee Seed Data
INSERT INTO employee (em_id, em_name, em_salary, em_permanent, em_date_of_birth, em_dp_id) VALUES (1, 'John Doe', 5500.00, true, '1990-05-15', 1);
INSERT INTO employee (em_id, em_name, em_salary, em_permanent, em_date_of_birth, em_dp_id) VALUES (2, 'Jane Smith', 6200.00, true, '1992-08-20', 2);
INSERT INTO employee (em_id, em_name, em_salary, em_permanent, em_date_of_birth, em_dp_id) VALUES (3, 'Bob Johnson', 4800.00, false, '1995-12-10', 1);

-- Skill Seed Data
INSERT INTO skill (sk_id, sk_name) VALUES (1, 'Java');
INSERT INTO skill (sk_id, sk_name) VALUES (2, 'Spring Boot');
INSERT INTO skill (sk_id, sk_name) VALUES (3, 'Hibernate');
INSERT INTO skill (sk_id, sk_name) VALUES (4, 'SQL');

-- Employee Skill Association
INSERT INTO employee_skill (es_em_id, es_sk_id) VALUES (1, 1);
INSERT INTO employee_skill (es_em_id, es_sk_id) VALUES (1, 2);
INSERT INTO employee_skill (es_em_id, es_sk_id) VALUES (2, 4);
INSERT INTO employee_skill (es_em_id, es_sk_id) VALUES (3, 1);
INSERT INTO employee_skill (es_em_id, es_sk_id) VALUES (3, 3);

-- Quiz Users Seed Data
INSERT INTO user (us_id, us_name) VALUES (1, 'Arun Kumar');

-- Quiz Questions Seed Data
INSERT INTO question (qt_id, qt_text, qt_score) VALUES (1, 'What is the extension of the hyper text markup language file?', 1.0);
INSERT INTO question (qt_id, qt_text, qt_score) VALUES (2, 'What is the maximum level of heading tag can be used in a HTML page?', 1.0);
INSERT INTO question (qt_id, qt_text, qt_score) VALUES (3, 'The HTML document itself begins with <html> and ends </html>. State True of False', 1.0);
INSERT INTO question (qt_id, qt_text, qt_score) VALUES (4, 'Choose the right option to store text value value in a variable', 1.0);

-- Quiz Options Seed Data
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (1, 1, '.xhtm', false);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (2, 1, '.ht', false);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (3, 1, '.html', true);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (4, 1, '.htmx', false);

INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (5, 2, '5', false);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (6, 2, '3', false);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (7, 2, '4', false);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (8, 2, '6', true);

INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (9, 3, 'false', false);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (10, 3, 'true', true);

INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (11, 4, '''John''', true);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (12, 4, 'John', false);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (13, 4, '"John"', false);
INSERT INTO options (op_id, op_qt_id, op_text, op_is_correct) VALUES (14, 4, '/John/', false);

-- Quiz Attempt Seed Data
INSERT INTO attempt (at_id, at_us_id, at_date, at_score) VALUES (1, 1, '2026-07-14 10:00:00', 3.0);

-- Quiz Attempt Question mappings
INSERT INTO attempt_question (aq_id, aq_at_id, aq_qt_id) VALUES (1, 1, 1);
INSERT INTO attempt_question (aq_id, aq_at_id, aq_qt_id) VALUES (2, 1, 2);
INSERT INTO attempt_question (aq_id, aq_at_id, aq_qt_id) VALUES (3, 1, 3);
INSERT INTO attempt_question (aq_id, aq_at_id, aq_qt_id) VALUES (4, 1, 4);

-- Quiz Attempt Options selected by User (Arun chose option 3 (.html - true), option 6 (3 - false), option 10 (true - true), option 11 ('John' - true))
INSERT INTO attempt_option (ao_aq_id, ao_op_id) VALUES (1, 3);
INSERT INTO attempt_option (ao_aq_id, ao_op_id) VALUES (2, 6);
INSERT INTO attempt_option (ao_aq_id, ao_op_id) VALUES (3, 10);
INSERT INTO attempt_option (ao_aq_id, ao_op_id) VALUES (4, 11);
