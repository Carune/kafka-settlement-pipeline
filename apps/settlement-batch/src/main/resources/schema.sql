create table if not exists settlement_source_tx (
    id bigserial primary key,
    source_tx_id varchar(64) not null unique,
    merchant_id varchar(64) not null,
    business_date date not null,
    expected_amount numeric(19, 2) not null,
    actual_amount numeric(19, 2) not null,
    created_at timestamp not null default now()
);

create index if not exists idx_settlement_source_tx_business_date
    on settlement_source_tx (business_date, merchant_id);

create table if not exists settlement_ledger (
    id bigserial primary key,
    merchant_id varchar(64) not null,
    business_date date not null,
    expected_amount numeric(19, 2) not null,
    actual_amount numeric(19, 2) not null,
    difference_amount numeric(19, 2) not null,
    status varchar(32) not null,
    created_at timestamp not null default now()
);

create unique index if not exists uq_settlement_ledger_business_merchant
    on settlement_ledger (business_date, merchant_id);

create table if not exists outbox_event (
    id bigserial primary key,
    event_id varchar(64) not null unique,
    topic varchar(128) not null,
    event_key varchar(128) not null,
    payload_json text not null,
    status varchar(32) not null,
    retry_count integer not null default 0,
    created_at timestamp not null default now(),
    published_at timestamp
);

create index if not exists idx_outbox_event_status_created_at
    on outbox_event (status, created_at);

create table if not exists processed_event (
    id bigserial primary key,
    event_id varchar(64) not null unique,
    event_key varchar(128),
    source_topic varchar(128) not null,
    processed_at timestamp not null default now()
);

create index if not exists idx_processed_event_processed_at
    on processed_event (processed_at);
