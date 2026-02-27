insert into settlement_source_tx (source_tx_id, merchant_id, business_date, expected_amount, actual_amount)
values
    ('SRC-10001', 'merchant-a', (current_date - interval '1 day')::date, 100000.00, 97000.00),
    ('SRC-10002', 'merchant-a', (current_date - interval '1 day')::date, 50000.00, 50000.00),
    ('SRC-10003', 'merchant-b', (current_date - interval '1 day')::date, 90000.00, 91000.00),
    ('SRC-10004', 'merchant-c', (current_date - interval '1 day')::date, 120000.00, 120000.00)
on conflict (source_tx_id) do nothing;
