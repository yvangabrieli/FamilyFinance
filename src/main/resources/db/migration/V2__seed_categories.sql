-- V2__seed_categories.sql
-- Default categories seeded on first startup

INSERT INTO categories (name, icon, type, is_default) VALUES
-- Expense categories
('Groceries',      'shopping-cart',  'EXPENSE', TRUE),
('Rent/Mortgage',  'home',           'EXPENSE', TRUE),
('Transport',      'car',            'EXPENSE', TRUE),
('Restaurants',    'utensils',       'EXPENSE', TRUE),
('Health',         'heart-pulse',    'EXPENSE', TRUE),
('Subscriptions',  'repeat',         'EXPENSE', TRUE),
('Entertainment',  'tv',             'EXPENSE', TRUE),
('Education',      'book-open',      'EXPENSE', TRUE),
('Clothing',       'shirt',          'EXPENSE', TRUE),
('Utilities',      'zap',            'EXPENSE', TRUE),
('Other Expense',  'circle-minus',   'EXPENSE', TRUE),

-- Income categories
('Salary',         'briefcase',      'INCOME',  TRUE),
('Freelance',      'laptop',         'INCOME',  TRUE),
('Gift',           'gift',           'INCOME',  TRUE),
('Investment',     'trending-up',    'INCOME',  TRUE),
('Other Income',   'circle-plus',    'INCOME',  TRUE);
