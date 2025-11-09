-- Clean up existing data
DELETE FROM categorized_transaction;

-- Test data for client 'client-001'
INSERT INTO categorized_transaction (transaction_id, client_id, account_id, amount, expense_category)
VALUES
    ('txn-001', 'client-001', 'acc-001', 15.50, 'GROCERIES'),
    ('txn-002', 'client-001', 'acc-001', 24.99, 'GROCERIES'),
    ('txn-003', 'client-001', 'acc-001', 45.00, 'TRANSPORTATION'),
    ('txn-004', 'client-001', 'acc-002', 12.00, 'RENT'),
    ('txn-005', 'client-001', 'acc-002', 75.25, 'UTILITIES'),
    ('txn-006', 'client-001', 'acc-001', 25.00, 'ENTERTAINMENT'),
    ('txn-007', 'client-001', 'acc-001', 12.00, 'DINING'),
    ('txn-008', 'client-001', 'acc-002', 50.00, 'HEALTHCARE');

-- Test data for client 'client-002'
INSERT INTO categorized_transaction (transaction_id, client_id, account_id, amount, expense_category)
VALUES
    ('txn-009', 'client-002', 'acc-003', 20.00, 'GROCERIES'),
    ('txn-010', 'client-002', 'acc-003', 50.00, 'TRANSPORTATION'),
    ('txn-011', 'client-002', 'acc-003', 15.00, 'RENT'),
    ('txn-012', 'client-002', 'acc-003', 10.00, 'UTILITIES');

-- Test data for client 'client-003' (single transaction)
INSERT INTO categorized_transaction (transaction_id, client_id, account_id, amount, expense_category)
VALUES
    ('txn-013', 'client-003', 'acc-004', 99, 'SHOPPING');
