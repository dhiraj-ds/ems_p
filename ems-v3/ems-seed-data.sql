-- =============================================================================
--  EMS Seed Data — 30+ entries per table
--  Run this AFTER spring-boot starts (ddl-auto=update creates the schema first)
--  Passwords are stored as plain text (no hashing)
--  Run order: departments → users → employees → leaves → payrolls
--             → attendances → announcements
-- =============================================================================

USE ems_db;   -- ← select the database before any other statement

-- ─────────────────────────────────────────────────────────────────────────────
-- 0. Clean slate (optional — comment out if you want to keep existing data)
-- ─────────────────────────────────────────────────────────────────────────────
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE refresh_tokens;
TRUNCATE TABLE attendances;
TRUNCATE TABLE leave_requests;
TRUNCATE TABLE payrolls;
TRUNCATE TABLE employees;
TRUNCATE TABLE users;
TRUNCATE TABLE departments;
TRUNCATE TABLE announcements;
SET FOREIGN_KEY_CHECKS = 1;

-- ─────────────────────────────────────────────────────────────────────────────
-- 1. DEPARTMENTS  (8 rows)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO departments (id, name, head, email, description, number_of_positions, status) VALUES
(1,  'Engineering',       'Ravi Kumar',     'engineering@company.com',  'Software development and infrastructure team',          25, 'ACTIVE'),
(2,  'Human Resources',   'Priya Sharma',   'hr@company.com',           'Recruitment, onboarding and employee welfare',           10, 'ACTIVE'),
(3,  'Finance',           'Amit Patel',     'finance@company.com',      'Accounting, budgeting and financial reporting',          8,  'ACTIVE'),
(4,  'Marketing',         'Sunita Rao',     'marketing@company.com',    'Brand management and digital campaigns',                 12, 'ACTIVE'),
(5,  'Operations',        'Kiran Desai',    'operations@company.com',   'Supply chain and daily business operations',             15, 'ACTIVE'),
(6,  'Design',            'Neha Gupta',     'design@company.com',       'UI/UX and graphic design',                              8,  'ACTIVE'),
(7,  'Sales',             'Rohit Verma',    'sales@company.com',        'Inside and field sales team',                           20, 'EXPANDING'),
(8,  'Legal',             'Anjali Mehta',   'legal@company.com',        'Compliance, contracts and intellectual property',        5,  'ACTIVE');

-- ─────────────────────────────────────────────────────────────────────────────
-- 2. USERS  (32 rows — 1 admin + 31 employees)
--    All employee passwords = "Pass@1234"  (plain text)
--    Admin password         = "admin123"   (plain text)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO users (id, username, password, role) VALUES
-- Admin
(1,  'admin',      'admin123', 'ROLE_ADMIN'),
-- Engineering (6)
(2,  'ravi.k',     'Pass@1234', 'ROLE_EMPLOYEE'),
(3,  'mohan.k',    'Pass@1234', 'ROLE_EMPLOYEE'),
(4,  'sneha.p',    'Pass@1234', 'ROLE_EMPLOYEE'),
(5,  'arjun.s',    'Pass@1234', 'ROLE_EMPLOYEE'),
(6,  'divya.m',    'Pass@1234', 'ROLE_EMPLOYEE'),
(7,  'rahul.g',    'Pass@1234', 'ROLE_EMPLOYEE'),
-- HR (3)
(8,  'priya.s',    'Pass@1234', 'ROLE_EMPLOYEE'),
(9,  'kavya.r',    'Pass@1234', 'ROLE_EMPLOYEE'),
(10, 'suresh.n',   'Pass@1234', 'ROLE_EMPLOYEE'),
-- Finance (4)
(11, 'amit.p',     'Pass@1234', 'ROLE_EMPLOYEE'),
(12, 'pooja.v',    'Pass@1234', 'ROLE_EMPLOYEE'),
(13, 'vikram.d',   'Pass@1234', 'ROLE_EMPLOYEE'),
(14, 'nisha.b',    'Pass@1234', 'ROLE_EMPLOYEE'),
-- Marketing (4)
(15, 'sunita.r',   'Pass@1234', 'ROLE_EMPLOYEE'),
(16, 'ankit.j',    'Pass@1234', 'ROLE_EMPLOYEE'),
(17, 'meera.t',    'Pass@1234', 'ROLE_EMPLOYEE'),
(18, 'deepak.c',   'Pass@1234', 'ROLE_EMPLOYEE'),
-- Operations (4)
(19, 'kiran.d',    'Pass@1234', 'ROLE_EMPLOYEE'),
(20, 'lakshmi.k',  'Pass@1234', 'ROLE_EMPLOYEE'),
(21, 'sunil.m',    'Pass@1234', 'ROLE_EMPLOYEE'),
(22, 'rekha.s',    'Pass@1234', 'ROLE_EMPLOYEE'),
-- Design (3)
(23, 'neha.g',     'Pass@1234', 'ROLE_EMPLOYEE'),
(24, 'arun.b',     'Pass@1234', 'ROLE_EMPLOYEE'),
(25, 'sonal.k',    'Pass@1234', 'ROLE_EMPLOYEE'),
-- Sales (4)
(26, 'rohit.v',    'Pass@1234', 'ROLE_EMPLOYEE'),
(27, 'preeti.a',   'Pass@1234', 'ROLE_EMPLOYEE'),
(28, 'manish.y',   'Pass@1234', 'ROLE_EMPLOYEE'),
(29, 'tanvi.p',    'Pass@1234', 'ROLE_EMPLOYEE'),
-- Legal (3)
(30, 'anjali.m',   'Pass@1234', 'ROLE_EMPLOYEE'),
(31, 'sanjay.l',   'Pass@1234', 'ROLE_EMPLOYEE'),
(32, 'geeta.n',    'Pass@1234', 'ROLE_EMPLOYEE');

-- NOTE: Plain text passwords: "Pass@1234" for all employee accounts.
-- Admin password is "admin123".
-- To generate your own hashes: https://bcrypt-generator.com  (rounds = 10)

-- ─────────────────────────────────────────────────────────────────────────────
-- 3. EMPLOYEES  (31 rows — one per employee user)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO employees
  (id, first_name, last_name, email, phone, position, gender, address,
   department_id, join_date, basic_salary, status, user_id)
VALUES
-- Engineering
(1,  'Ravi',     'Kumar',    'ravi.kumar@company.com',     '9876501001', 'Engineering Manager',     'Male',   '12 MG Road, Bengaluru',        1, '2021-03-15', 95000.00,  'ACTIVE',   2),
(2,  'Mohan',    'Kumar',    'mohan.kumar@company.com',    '9876501002', 'Senior Software Engineer', 'Male',   '45 Brigade Road, Bengaluru',   1, '2022-01-10', 75000.00,  'ACTIVE',   3),
(3,  'Sneha',    'Patil',    'sneha.patil@company.com',    '9876501003', 'Software Engineer',        'Female', '8 Koramangala, Bengaluru',     1, '2022-06-20', 62000.00,  'ACTIVE',   4),
(4,  'Arjun',    'Singh',    'arjun.singh@company.com',   '9876501004', 'Backend Developer',        'Male',   '22 Indiranagar, Bengaluru',    1, '2023-02-14', 58000.00,  'ACTIVE',   5),
(5,  'Divya',    'Menon',    'divya.menon@company.com',    '9876501005', 'Frontend Developer',       'Female', '3 HSR Layout, Bengaluru',      1, '2023-07-01', 55000.00,  'ON_LEAVE', 6),
(6,  'Rahul',    'Gupta',    'rahul.gupta@company.com',   '9876501006', 'DevOps Engineer',          'Male',   '67 Whitefield, Bengaluru',     1, '2021-11-05', 72000.00,  'ACTIVE',   7),
-- HR
(7,  'Priya',    'Sharma',   'priya.sharma@company.com',  '9876501007', 'HR Manager',               'Female', '15 Jayanagar, Bengaluru',      2, '2020-08-01', 80000.00,  'ACTIVE',   8),
(8,  'Kavya',    'Reddy',    'kavya.reddy@company.com',   '9876501008', 'HR Executive',             'Female', '90 BTM Layout, Bengaluru',     2, '2022-03-22', 48000.00,  'ACTIVE',   9),
(9,  'Suresh',   'Nair',     'suresh.nair@company.com',   '9876501009', 'Recruiter',                'Male',   '5 Malleswaram, Bengaluru',     2, '2023-01-18', 45000.00,  'ACTIVE',   10),
-- Finance
(10, 'Amit',     'Patel',    'amit.patel@company.com',    '9876501010', 'Finance Manager',          'Male',   '34 Sadashivanagar, Bengaluru', 3, '2019-05-12', 90000.00,  'ACTIVE',   11),
(11, 'Pooja',    'Verma',    'pooja.verma@company.com',   '9876501011', 'Senior Accountant',        'Female', '11 Rajajinagar, Bengaluru',    3, '2021-09-08', 60000.00,  'ACTIVE',   12),
(12, 'Vikram',   'Das',      'vikram.das@company.com',    '9876501012', 'Financial Analyst',        'Male',   '28 Yeshwanthpur, Bengaluru',   3, '2022-05-30', 55000.00,  'ACTIVE',   13),
(13, 'Nisha',    'Bose',     'nisha.bose@company.com',    '9876501013', 'Accounts Executive',       'Female', '7 Vijayanagar, Bengaluru',     3, '2023-04-05', 42000.00,  'ACTIVE',   14),
-- Marketing
(14, 'Sunita',   'Rao',      'sunita.rao@company.com',    '9876501014', 'Marketing Head',           'Female', '19 Cunningham Road, Bengaluru',4, '2020-01-20', 88000.00,  'ACTIVE',   15),
(15, 'Ankit',    'Joshi',    'ankit.joshi@company.com',   '9876501015', 'Digital Marketing Lead',   'Male',   '52 Richmond Road, Bengaluru',  4, '2021-07-15', 65000.00,  'ACTIVE',   16),
(16, 'Meera',    'Thomas',   'meera.thomas@company.com',  '9876501016', 'Content Strategist',       'Female', '33 Lavelle Road, Bengaluru',   4, '2022-09-01', 52000.00,  'ACTIVE',   17),
(17, 'Deepak',   'Chandra',  'deepak.chandra@company.com','9876501017', 'SEO Specialist',           'Male',   '6 Museum Road, Bengaluru',     4, '2023-03-12', 48000.00,  'ACTIVE',   18),
-- Operations
(18, 'Kiran',    'Desai',    'kiran.desai@company.com',   '9876501018', 'Operations Manager',       'Female', '41 Residency Road, Bengaluru', 5, '2019-11-01', 85000.00,  'ACTIVE',   19),
(19, 'Lakshmi',  'Krishnan', 'lakshmi.k@company.com',     '9876501019', 'Operations Executive',     'Female', '14 St. Marks Road, Bengaluru', 5, '2021-04-22', 52000.00,  'ACTIVE',   20),
(20, 'Sunil',    'Menon',    'sunil.menon@company.com',   '9876501020', 'Logistics Coordinator',    'Male',   '77 Infantry Road, Bengaluru',  5, '2022-08-17', 46000.00,  'ACTIVE',   21),
(21, 'Rekha',    'Shetty',   'rekha.shetty@company.com',  '9876501021', 'Supply Chain Analyst',     'Female', '23 Dickenson Road, Bengaluru', 5, '2023-06-10', 44000.00,  'ON_LEAVE', 22),
-- Design
(22, 'Neha',     'Gupta',    'neha.gupta@company.com',    '9876501022', 'Design Lead',              'Female', '9 Church Street, Bengaluru',   6, '2020-06-08', 82000.00,  'ACTIVE',   23),
(23, 'Arun',     'Bhat',     'arun.bhat@company.com',     '9876501023', 'UI/UX Designer',           'Male',   '56 Queens Road, Bengaluru',    6, '2022-02-28', 60000.00,  'ACTIVE',   24),
(24, 'Sonal',    'Kapoor',   'sonal.kapoor@company.com',  '9876501024', 'Graphic Designer',         'Female', '31 Kasturba Road, Bengaluru',  6, '2023-05-20', 50000.00,  'ACTIVE',   25),
-- Sales
(25, 'Rohit',    'Verma',    'rohit.verma@company.com',   '9876501025', 'Sales Manager',            'Male',   '18 Nrupathunga Road, Bengaluru',7,'2019-09-15', 92000.00,  'ACTIVE',   26),
(26, 'Preeti',   'Agarwal',  'preeti.agarwal@company.com','9876501026', 'Senior Sales Executive',   'Female', '44 Vittal Mallya Road, Bengaluru',7,'2021-06-05',65000.00, 'ACTIVE',   27),
(27, 'Manish',   'Yadav',    'manish.yadav@company.com',  '9876501027', 'Sales Executive',          'Male',   '2 Palace Road, Bengaluru',     7, '2022-11-14', 50000.00,  'ACTIVE',   28),
(28, 'Tanvi',    'Parekh',   'tanvi.parekh@company.com',  '9876501028', 'Business Dev Executive',   'Female', '88 Cubbon Road, Bengaluru',    7, '2023-08-01', 47000.00,  'ACTIVE',   29),
-- Legal
(29, 'Anjali',   'Mehta',    'anjali.mehta@company.com',  '9876501029', 'Legal Head',               'Female', '5 Lavelle Road, Bengaluru',    8, '2018-04-01', 105000.00, 'ACTIVE',   30),
(30, 'Sanjay',   'Lal',      'sanjay.lal@company.com',    '9876501030', 'Legal Counsel',            'Male',   '72 Airport Road, Bengaluru',   8, '2021-10-10', 78000.00,  'ACTIVE',   31),
(31, 'Geeta',    'Nambiar',  'geeta.nambiar@company.com', '9876501031', 'Compliance Officer',       'Female', '16 Old Madras Road, Bengaluru',8, '2022-07-25', 62000.00,  'ACTIVE',   32);

-- ─────────────────────────────────────────────────────────────────────────────
-- 4. LEAVE REQUESTS  (32 rows — mix of all statuses and types)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO leave_requests
  (id, employee_id, leave_type, from_date, to_date, reason, remarks, status)
VALUES
(1,  2,  'ANNUAL',    '2025-01-06', '2025-01-10', 'Family vacation to Goa',              NULL,                    'APPROVED'),
(2,  3,  'SICK',      '2025-01-15', '2025-01-16', 'Fever and cold',                      'Get well soon',         'APPROVED'),
(3,  4,  'CASUAL',    '2025-02-03', '2025-02-03', 'Personal errand',                     NULL,                    'APPROVED'),
(4,  5,  'MEDICAL',   '2025-02-10', '2025-02-20', 'Surgery and recovery',                'Rest as needed',        'APPROVED'),
(5,  6,  'ANNUAL',    '2025-03-01', '2025-03-07', 'Wedding anniversary trip',            NULL,                    'APPROVED'),
(6,  7,  'CASUAL',    '2025-03-14', '2025-03-14', 'Child school event',                  NULL,                    'APPROVED'),
(7,  8,  'SICK',      '2025-04-02', '2025-04-03', 'Migraine',                            'Approved with care',    'APPROVED'),
(8,  9,  'ANNUAL',    '2025-04-21', '2025-04-25', 'Holiday to Kerala',                   NULL,                    'APPROVED'),
(9,  10, 'PATERNITY', '2025-05-05', '2025-05-19', 'Newborn baby care',                   NULL,                    'APPROVED'),
(10, 11, 'CASUAL',    '2025-05-22', '2025-05-22', 'Home repair work',                    NULL,                    'APPROVED'),
(11, 12, 'ANNUAL',    '2025-06-09', '2025-06-13', 'Vacation with family',                NULL,                    'APPROVED'),
(12, 13, 'SICK',      '2025-06-25', '2025-06-26', 'Stomach infection',                   'Approved',              'APPROVED'),
(13, 14, 'MEDICAL',   '2025-07-01', '2025-07-05', 'Dental procedure',                    NULL,                    'APPROVED'),
(14, 15, 'CASUAL',    '2025-07-18', '2025-07-18', 'Passport renewal',                    NULL,                    'APPROVED'),
(15, 16, 'ANNUAL',    '2025-08-04', '2025-08-08', 'Summer vacation',                     NULL,                    'APPROVED'),
(16, 17, 'SICK',      '2025-08-19', '2025-08-20', 'Flu',                                 'Rest prescribed',       'APPROVED'),
(17, 18, 'MATERNITY', '2025-09-01', '2025-11-30', 'Maternity leave',                     NULL,                    'APPROVED'),
(18, 19, 'CASUAL',    '2025-09-12', '2025-09-12', 'Bank work',                           NULL,                    'APPROVED'),
(19, 20, 'ANNUAL',    '2025-10-06', '2025-10-10', 'Diwali holidays',                     NULL,                    'APPROVED'),
(20, 21, 'MEDICAL',   '2025-10-20', '2025-10-25', 'Knee surgery recovery',               'Approved — get well',   'APPROVED'),
(21, 22, 'ANNUAL',    '2025-11-03', '2025-11-07', 'Trip to Rajasthan',                   NULL,                    'APPROVED'),
(22, 23, 'SICK',      '2025-11-17', '2025-11-18', 'Viral fever',                         'Rest and fluids',       'APPROVED'),
(23, 24, 'CASUAL',    '2025-12-01', '2025-12-01', 'Personal commitment',                 NULL,                    'APPROVED'),
(24, 25, 'ANNUAL',    '2025-12-22', '2025-12-31', 'Year-end vacation',                   NULL,                    'APPROVED'),
-- Pending approvals
(25, 2,  'CASUAL',    '2026-03-10', '2026-03-10', 'Vehicle registration renewal',        NULL,                    'PENDING'),
(26, 4,  'ANNUAL',    '2026-03-17', '2026-03-21', 'Spring holiday with family',          NULL,                    'PENDING'),
(27, 9,  'SICK',      '2026-03-06', '2026-03-07', 'High fever and body ache',            NULL,                    'PENDING'),
(28, 13, 'CASUAL',    '2026-03-12', '2026-03-12', 'Doctor appointment',                  NULL,                    'PENDING'),
(29, 17, 'ANNUAL',    '2026-04-01', '2026-04-05', 'Ugadi festival travel',               NULL,                    'PENDING'),
-- Rejected
(30, 6,  'ANNUAL',    '2025-12-24', '2025-12-30', 'Extended Christmas break',            'Peak period — rejected', 'REJECTED'),
(31, 15, 'CASUAL',    '2026-02-14', '2026-02-14', 'Valentine Day plans',                 'Not a valid reason',    'REJECTED'),
(32, 27, 'ANNUAL',    '2026-01-26', '2026-01-30', 'Republic Day extended holiday',       'Short notice — rejected','REJECTED');

-- ─────────────────────────────────────────────────────────────────────────────
-- 5. PAYROLLS  (32 rows — Jan & Feb 2025 cycles for various employees)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO payrolls
  (id, employee_id, payroll_month, payroll_year,
   basic_salary, allowances, deductions, net_pay,
   payment_method, payment_date, status)
VALUES
-- January 2025 — 16 records
(1,  1,  1, 2025, 95000.00,  8000.00, 5000.00,  98000.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(2,  2,  1, 2025, 75000.00,  6000.00, 4000.00,  77000.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(3,  3,  1, 2025, 62000.00,  5000.00, 3200.00,  63800.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(4,  4,  1, 2025, 58000.00,  4500.00, 3000.00,  59500.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(5,  7,  1, 2025, 80000.00,  7000.00, 4500.00,  82500.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(6,  10, 1, 2025, 90000.00,  8000.00, 5000.00,  93000.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(7,  11, 1, 2025, 60000.00,  4800.00, 3200.00,  61600.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(8,  14, 1, 2025, 88000.00,  7500.00, 4800.00,  90700.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(9,  18, 1, 2025, 85000.00,  7200.00, 4600.00,  87600.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(10, 22, 1, 2025, 82000.00,  7000.00, 4400.00,  84600.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(11, 25, 1, 2025, 92000.00,  8200.00, 5200.00,  95000.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(12, 29, 1, 2025, 105000.00, 9000.00, 6000.00, 108000.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(13, 5,  1, 2025, 55000.00,  4000.00, 2800.00,  56200.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(14, 15, 1, 2025, 65000.00,  5500.00, 3500.00,  67000.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(15, 19, 1, 2025, 52000.00,  3800.00, 2600.00,  53200.00,  'Bank Transfer', '2025-01-31', 'PAID'),
(16, 23, 1, 2025, 60000.00,  4800.00, 3100.00,  61700.00,  'Bank Transfer', '2025-01-31', 'PAID'),
-- February 2025 — 16 records (mix of PAID and PENDING)
(17, 1,  2, 2025, 95000.00,  8000.00, 5000.00,  98000.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(18, 2,  2, 2025, 75000.00,  6000.00, 4000.00,  77000.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(19, 3,  2, 2025, 62000.00,  5000.00, 3200.00,  63800.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(20, 4,  2, 2025, 58000.00,  4500.00, 3000.00,  59500.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(21, 7,  2, 2025, 80000.00,  7000.00, 4500.00,  82500.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(22, 10, 2, 2025, 90000.00,  8000.00, 5000.00,  93000.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(23, 14, 2, 2025, 88000.00,  7500.00, 4800.00,  90700.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(24, 18, 2, 2025, 85000.00,  7200.00, 4600.00,  87600.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(25, 22, 2, 2025, 82000.00,  7000.00, 4400.00,  84600.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(26, 25, 2, 2025, 92000.00,  8200.00, 5200.00,  95000.00,  'Bank Transfer', '2025-02-28', 'PAID'),
(27, 11, 2, 2025, 60000.00,  4800.00, 3200.00,  61600.00,  'Bank Transfer', NULL,          'PENDING'),
(28, 15, 2, 2025, 65000.00,  5500.00, 3500.00,  67000.00,  'Bank Transfer', NULL,          'PENDING'),
(29, 19, 2, 2025, 52000.00,  3800.00, 2600.00,  53200.00,  'Bank Transfer', NULL,          'PENDING'),
(30, 23, 2, 2025, 60000.00,  4800.00, 3100.00,  61700.00,  'Bank Transfer', NULL,          'PENDING'),
(31, 29, 2, 2025, 105000.00, 9000.00, 6000.00, 108000.00,  'Bank Transfer', NULL,          'PENDING'),
(32, 5,  2, 2025, 55000.00,  4000.00, 2800.00,  56200.00,  'Bank Transfer', NULL,          'PENDING');

-- ─────────────────────────────────────────────────────────────────────────────
-- 6. ATTENDANCES  (35 rows — today + past dates for several employees)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO attendances
  (id, employee_id, date, check_in, check_out, status, remarks)
VALUES
-- Today's attendance (2026-03-05)
(1,  1,  '2026-03-05', '09:05:00', '18:10:00', 'PRESENT',  NULL),
(2,  2,  '2026-03-05', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(3,  3,  '2026-03-05', '09:15:00', '18:05:00', 'PRESENT',  NULL),
(4,  4,  '2026-03-05', NULL,        NULL,       'ABSENT',   'No show — not informed'),
(5,  6,  '2026-03-05', '10:20:00', '18:00:00', 'LATE',     'Traffic issue'),
(6,  7,  '2026-03-05', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(7,  8,  '2026-03-05', '09:10:00', '17:55:00', 'PRESENT',  NULL),
(8,  10, '2026-03-05', '08:55:00', '18:00:00', 'PRESENT',  NULL),
(9,  11, '2026-03-05', '09:30:00', '18:00:00', 'PRESENT',  NULL),
(10, 14, '2026-03-05', '09:00:00', '18:10:00', 'PRESENT',  NULL),
(11, 18, '2026-03-05', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(12, 22, '2026-03-05', '09:05:00', '17:50:00', 'PRESENT',  NULL),
(13, 25, '2026-03-05', '09:00:00', '18:30:00', 'PRESENT',  NULL),
(14, 29, '2026-03-05', '10:45:00', '18:00:00', 'LATE',     'Meeting ran over'),
(15, 5,  '2026-03-05', NULL,        NULL,       'ON_LEAVE', 'Approved medical leave'),
(16, 21, '2026-03-05', NULL,        NULL,       'ON_LEAVE', 'Approved surgery leave'),
-- Previous week (2026-02-28)
(17, 1,  '2026-02-28', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(18, 2,  '2026-02-28', '09:05:00', '17:50:00', 'PRESENT',  NULL),
(19, 3,  '2026-02-28', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(20, 6,  '2026-02-28', '09:00:00', '18:10:00', 'PRESENT',  NULL),
(21, 7,  '2026-02-28', '09:15:00', '18:00:00', 'PRESENT',  NULL),
(22, 10, '2026-02-28', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(23, 14, '2026-02-28', '09:00:00', '18:05:00', 'PRESENT',  NULL),
(24, 18, '2026-02-28', '09:00:00', '18:00:00', 'PRESENT',  NULL),
-- 2026-02-27
(25, 2,  '2026-02-27', '11:10:00', '18:00:00', 'LATE',     'Metro delay'),
(26, 4,  '2026-02-27', '09:00:00', '13:00:00', 'HALF_DAY', 'Medical appointment in afternoon'),
(27, 9,  '2026-02-27', '09:05:00', '18:00:00', 'PRESENT',  NULL),
(28, 12, '2026-02-27', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(29, 15, '2026-02-27', '09:00:00', '18:10:00', 'PRESENT',  NULL),
-- 2026-02-26
(30, 1,  '2026-02-26', '09:00:00', '18:30:00', 'PRESENT',  'Stayed late for release'),
(31, 3,  '2026-02-26', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(32, 7,  '2026-02-26', NULL,        NULL,       'ABSENT',   'Sick — called in'),
(33, 22, '2026-02-26', '09:00:00', '18:00:00', 'PRESENT',  NULL),
(34, 25, '2026-02-26', '09:10:00', '18:00:00', 'PRESENT',  NULL),
(35, 29, '2026-02-26', '09:00:00', '18:00:00', 'PRESENT',  NULL);

-- ─────────────────────────────────────────────────────────────────────────────
-- 7. ANNOUNCEMENTS  (10 rows)
-- ─────────────────────────────────────────────────────────────────────────────
INSERT INTO announcements
  (id, title, audience, priority, message, post_date, posted_by)
VALUES
(1,  'Q1 2025 All-Hands Meeting',
     'ALL_STAFF',   'IMPORTANT',
     'Our Q1 all-hands meeting is scheduled for January 15, 2025 at 3:00 PM in the main conference hall. All employees are required to attend. The agenda includes company performance review, OKRs for 2025, and team recognitions.',
     '2025-01-08', 'admin'),

(2,  'New Work From Home Policy',
     'ALL_STAFF',   'NORMAL',
     'Effective February 1, 2025, employees are permitted to work from home up to 2 days per week. Prior approval from your reporting manager is required. Please read the full policy document shared on the intranet.',
     '2025-01-20', 'priya.s'),

(3,  'Office Closed — Republic Day',
     'ALL_STAFF',   'NORMAL',
     'The office will remain closed on January 26, 2025 in observance of Republic Day. Employees on critical on-call duty should coordinate with their managers in advance.',
     '2025-01-23', 'admin'),

(4,  'Mandatory Cybersecurity Training',
     'ALL_STAFF',   'URGENT',
     'All employees must complete the annual cybersecurity awareness training by February 28, 2025. Log in to the Learning Portal at learn.company.com. Failure to complete will result in system access restrictions.',
     '2025-02-05', 'admin'),

(5,  'Engineering Team Sprint Planning',
     'ENGINEERING', 'IMPORTANT',
     'Sprint 14 planning session is scheduled for February 10, 2025 at 10:00 AM. Please update your task estimates in Jira before the session. Attendees: all engineers, QA leads, and product managers.',
     '2025-02-07', 'ravi.k'),

(6,  'Salary Increment Letters — March 2025',
     'ALL_STAFF',   'IMPORTANT',
     'Annual performance-based salary increments will be effective from March 1, 2025. Revised offer letters will be shared via email by February 20. For queries contact HR at hr@company.com.',
     '2025-02-15', 'priya.s'),

(7,  'Health Insurance Enrollment Open',
     'ALL_STAFF',   'HR_UPDATE',
     'The annual health insurance enrollment window is open from March 1 to March 20, 2025. You can add or remove dependents, upgrade plans, or opt into dental/vision coverage. Login to the HR portal to make your selections.',
     '2025-02-28', 'priya.s'),

(8,  'Office Expansion — New Floor Inauguration',
     'ALL_STAFF',   'NORMAL',
     'We are pleased to announce that the 5th floor expansion is complete. The new floor houses 80 additional workstations and two large meeting rooms. A brief inauguration ceremony will be held on March 10, 2025 at 11:00 AM.',
     '2025-03-01', 'admin'),

(9,  'Finance Team — Quarterly Closing Reminder',
     'FINANCE',     'URGENT',
     'Q1 2025 financial closing is on March 31. All expense reports, vendor invoices, and reimbursement claims must be submitted by March 25. Late submissions will be processed in Q2. Contact Amit for any queries.',
     '2025-03-03', 'amit.p'),

(10, 'Welcome New Joiners — March 2025 Batch',
     'ALL_STAFF',   'NORMAL',
     'Please join us in welcoming our new team members who joined this month across Engineering, Marketing, and Sales. An introduction session will be held on March 7, 2025 at 2:00 PM. A warm welcome to all our new colleagues!',
     '2026-03-05', 'admin');

-- ─────────────────────────────────────────────────────────────────────────────
-- Done! Summary:
--   departments   :  8 rows
--   users         : 32 rows  (1 admin + 31 employees)
--   employees     : 31 rows
--   leave_requests: 32 rows  (24 approved, 5 pending, 3 rejected)
--   payrolls      : 32 rows  (16 Jan 2025 PAID, 10 Feb PAID, 6 Feb PENDING)
--   attendances   : 35 rows  (today + 3 previous days)
--   announcements : 10 rows
--
-- Login credentials:
--   Admin    → username: admin       password: admin123
--   Employee → username: <any above> password: Pass@1234
--   Example  → username: mohan.k     password: Pass@1234
-- =============================================================================
