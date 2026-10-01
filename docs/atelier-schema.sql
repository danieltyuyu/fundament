-- =================================================================================================
-- ATELIER DATABASE - SCHEMA (PostgreSQL)
-- =================================================================================================

-- -------------------------------------------------------------------------------------------------
-- 1. CREACIÓN DE TABLAS
-- -------------------------------------------------------------------------------------------------

CREATE TABLE appointments
(
    id              uuid        NOT NULL UNIQUE,
    branch_id       uuid        NOT NULL,
    customer_id     uuid        NOT NULL,
    vehicle_id      uuid        NOT NULL,
    status          varchar(20) NOT NULL DEFAULT 'PENDING',
    scheduled_start timestamp   NOT NULL,
    scheduled_end   timestamp   NOT NULL,
    notes           text       ,
    created_at      timestamp   NOT NULL,
    updated_at      timestamp   NOT NULL,
    deleted_at      timestamp  ,
    created_by      uuid        NOT NULL,
    updated_by      uuid       ,
    version         bigint      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN appointments.status IS 'enum: PENDING, COMPLETED, CANCELED';

CREATE TABLE branch_subscriptions
(
    id            uuid        NOT NULL UNIQUE,
    branch_id     uuid        NOT NULL,
    plan_id       uuid        NOT NULL,
    status        varchar(20) NOT NULL DEFAULT 'ACTIVE',
    billing_cycle varchar(20) NOT NULL,
    start_date    timestamp   NOT NULL,
    end_date      timestamp   NOT NULL,
    canceled_at   timestamp  ,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN branch_subscriptions.status IS 'enum: ACTIVE, CANCELED, EXPIRED';
COMMENT ON COLUMN branch_subscriptions.billing_cycle IS 'enum: MONTHLY, ANNUAL';

CREATE TABLE branches
(
    id          uuid         NOT NULL UNIQUE,
    workshop_id uuid         NOT NULL,
    code        varchar(20)  NOT NULL UNIQUE,
    name        varchar(100) NOT NULL,
    address     varchar(100),
    phone       char(9)      UNIQUE,
    created_at  timestamp    NOT NULL,
    updated_at  timestamp    NOT NULL,
    deleted_at  timestamp   ,
    created_by  uuid         NOT NULL,
    updated_by  uuid        ,
    version     bigint      ,
    PRIMARY KEY (id)
);

CREATE TABLE customer_registrations
(
    id          uuid        NOT NULL UNIQUE,
    customer_id uuid        NOT NULL,
    branch_id   uuid        NOT NULL,
    created_at  timestamp   NOT NULL,
    status      varchar(20) NOT NULL DEFAULT 'ACTIVE',
    updated_at  timestamp   NOT NULL,
    deleted_at  timestamp  ,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN customer_registrations.status IS 'enum: ACTIVE, INACTIVE';

CREATE TABLE customers
(
    id            uuid         NOT NULL UNIQUE,
    user_id       uuid         NOT NULL,
    first_name    varchar(100),
    last_name     varchar(100),
    is_corporate  boolean      NOT NULL,
    business_name varchar(100),
    document_type   varchar(20)  NOT NULL,
    document_number varchar(50)  NOT NULL,
    phone           char(9)      NOT NULL,
    created_at      timestamp    NOT NULL,
    updated_at      timestamp    NOT NULL,
    deleted_at      timestamp   ,
    version         bigint       NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN customers.document_type IS 'enum: DNI, RUC, CE, PASSPORT';

CREATE TABLE dtc_alerts
(
    id                    uuid        NOT NULL,
    telemetry_snapshot_id uuid        NOT NULL,
    branch_id             uuid        NOT NULL,
    dtc_code              varchar(10) NOT NULL,
    description           text        NOT NULL,
    severity              varchar(20) NOT NULL,
    created_at            timestamp   NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN dtc_alerts.severity IS 'enum: LOW, MEDIUM, HIGH, CRITICAL';

CREATE TABLE employee_registrations
(
    id              uuid          NOT NULL UNIQUE,
    employee_id     uuid          NOT NULL,
    branch_id       uuid          NOT NULL,
    speciality      varchar(50)   NOT NULL,
    speciality_name varchar(50)  ,
    salary          decimal(10,2) NOT NULL,
    status          varchar(20)   NOT NULL DEFAULT 'ACTIVE',
    created_at      timestamp     NOT NULL,
    updated_at      timestamp     NOT NULL,
    deleted_at      timestamp    ,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN employee_registrations.speciality IS 'enum: ADMINISTRATOR, RECEPTIONIST, GENERAL_MECHANIC, PAINTER, ELECTRICIAN_MECHANIC, OTHER';
COMMENT ON COLUMN employee_registrations.status IS 'enum: ACTIVE, INACTIVE';

CREATE TABLE employees
(
    id         uuid         NOT NULL UNIQUE,
    user_id    uuid         NOT NULL,
    first_name varchar(100) NOT NULL,
    last_name  varchar(100) NOT NULL,
    document_type   varchar(20)  NOT NULL,
    document_number varchar(50)  NOT NULL,
    phone           char(9)      NOT NULL,
    created_at      timestamp    NOT NULL,
    updated_at      timestamp    NOT NULL,
    deleted_at      timestamp   ,
    version         bigint       NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN employees.document_type IS 'enum: DNI, RUC, CE, PASSPORT';

CREATE TABLE obd2_device_registrations
(
    id             uuid        NOT NULL UNIQUE,
    obd2_device_id uuid        NOT NULL,
    branch_id      uuid        NOT NULL,
    vehicle_id     uuid        NOT NULL,
    status         varchar(20) NOT NULL DEFAULT 'ACTIVE',
    created_at     timestamp   NOT NULL,
    deleted_at     timestamp  ,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN obd2_device_registrations.status IS 'enum: ACTIVE, INACTIVE';

CREATE TABLE obd2_devices
(
    id          uuid        NOT NULL UNIQUE,
    branch_id   uuid        NOT NULL,
    mac_address varchar(17) NOT NULL UNIQUE,
    last_ping   timestamp  ,
    status      varchar(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at  timestamp   NOT NULL,
    updated_at  timestamp   NOT NULL,
    deleted_at  timestamp  ,
    version     bigint      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN obd2_devices.status IS 'enum: AVAILABLE, LINKED, NOT_AVAILABLE';

CREATE TABLE owners
(
    id         uuid         NOT NULL UNIQUE,
    user_id    uuid         NOT NULL,
    first_name varchar(100) NOT NULL,
    last_name  varchar(100) NOT NULL,
    document_type   varchar(20)  NOT NULL,
    document_number varchar(50)  NOT NULL,
    phone           char(9)      NOT NULL,
    created_at      timestamp    NOT NULL,
    updated_at      timestamp    NOT NULL,
    deleted_at      timestamp   ,
    version         bigint       NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN owners.document_type IS 'enum: DNI, RUC, CE, PASSPORT';

CREATE TABLE password_recovery_tokens
(
    id         uuid         NOT NULL UNIQUE,
    token_hash varchar(255) NOT NULL,
    created_at timestamp    NOT NULL,
    expires_at timestamp    NOT NULL,
    is_used    boolean      NOT NULL DEFAULT false,
    user_id    uuid         NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE payments
(
    id         uuid          NOT NULL UNIQUE,
    voucher_id uuid          NOT NULL,
    branch_id  uuid          NOT NULL,
    amount     decimal(10,2) NOT NULL,
    currency   char(3)       NOT NULL,
    method     varchar(20)   NOT NULL,
    paid_at    timestamp     NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN payments.method IS 'enum: CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER';

CREATE TABLE product_batches
(
    id                 uuid          NOT NULL UNIQUE,
    product_id         uuid          NOT NULL,
    branch_id          uuid          NOT NULL,
    initial_quantity   int           NOT NULL,
    available_quantity int           NOT NULL,
    acquisition_cost   decimal(10,2) NOT NULL,
    created_at         timestamp     NOT NULL,
    updated_at         timestamp     NOT NULL,
    deleted_at         timestamp    ,
    version            bigint        NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE products
(
    id                    uuid          NOT NULL UNIQUE,
    branch_id             uuid          NOT NULL,
    category              varchar(50)   NOT NULL,
    name                  varchar(50)   NOT NULL,
    sku                   varchar(50)   NOT NULL,
    description           text         ,
    current_selling_price decimal(10,2) NOT NULL,
    current_stock         int           NOT NULL DEFAULT 0,
    minimum_stock         int           NOT NULL,
    created_at            timestamp     NOT NULL,
    updated_at            timestamp     NOT NULL,
    deleted_at            timestamp    ,
    created_by            uuid          NOT NULL,
    updated_by            uuid         ,
    version               bigint        NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN products.category IS 'enum: FLUID, PART, MOTOR, OTHER';

CREATE TABLE quotes
(
    id                  uuid          NOT NULL UNIQUE,
    work_order_id       uuid          NOT NULL,
    branch_id           uuid          NOT NULL,
    subtotal_amount     decimal(10,2) NOT NULL,
    discount_percentage decimal(5,2)  NOT NULL,
    total_amount        decimal(10,2) NOT NULL,
    status              varchar(20)   NOT NULL DEFAULT 'DRAFT',
    created_at          timestamp     NOT NULL,
    updated_at          timestamp     NOT NULL,
    deleted_at          timestamp    ,
    created_by          uuid          NOT NULL,
    updated_by          uuid         ,
    version             bigint        NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN quotes.status IS 'enum: DRAFT, APPROVED, CANCELED';

CREATE TABLE services
(
    id         uuid          NOT NULL UNIQUE,
    branch_id  uuid          NOT NULL,
    name       varchar(50)   NOT NULL,
    price      decimal(10,2) NOT NULL,
    created_at timestamp     NOT NULL,
    updated_at timestamp     NOT NULL,
    deleted_at timestamp    ,
    version    bigint        NOT NULL DEFAULT 0,
    created_by uuid          NOT NULL,
    updated_by uuid         ,
    PRIMARY KEY (id)
);

CREATE TABLE subscription_plans
(
    id                                uuid           NOT NULL UNIQUE,
    name                              varchar(50)    NOT NULL,
    monthly_price                     decimal(10, 2) NOT NULL,
    max_obd2_devices                  int            NOT NULL,
    max_monthly_snapshots_per_vehicle int            NOT NULL,
    max_customers                     int            NOT NULL,
    max_staff_accounts                int            NOT NULL,
    is_active                         boolean        NOT NULL DEFAULT true,
    PRIMARY KEY (id)
);

CREATE TABLE telemetry_snapshots
(
    id                          uuid             NOT NULL UNIQUE,
    obd2_device_registration_id uuid             NOT NULL,
    branch_id                   uuid             NOT NULL,
    rpm                         int              NOT NULL,
    temperature                 int              NOT NULL,
    speed_kmh                   double precision,
    odometer_km                 int             ,
    fuel_level_percent          double precision NOT NULL,
    created_at                  timestamp        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              uuid         NOT NULL UNIQUE,
    email           varchar(100) NOT NULL UNIQUE,
    password_hash   varchar(255) NOT NULL UNIQUE,
    google_id       varchar(255) UNIQUE,
    status          varchar(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at      timestamp    NOT NULL,
    updated_at      timestamp    NOT NULL,
    deleted_at      timestamp   ,
    version         bigint       NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN users.status IS 'enum: ACTIVE, INACTIVE';

CREATE TABLE vehicle_registrations
(
    id         uuid        NOT NULL UNIQUE,
    user_id    uuid        NOT NULL,
    vehicle_id uuid        NOT NULL,
    status     varchar(20) NOT NULL DEFAULT 'ACTIVE',
    created_at timestamp   NOT NULL,
    deleted_at timestamp  ,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN vehicle_registrations.status IS 'enum: ACTIVE, PREVIOUS';

CREATE TABLE vehicles
(
    id           uuid        NOT NULL UNIQUE,
    plate_number varchar(20) NOT NULL,
    vin          varchar(50) NOT NULL UNIQUE,
    year         int         NOT NULL,
    brand        varchar(50) NOT NULL,
    model        varchar(50) NOT NULL,
    created_at   timestamp   NOT NULL,
    updated_at   timestamp   NOT NULL,
    deleted_at   timestamp  ,
    version      bigint      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE vouchers
(
    id              uuid          NOT NULL UNIQUE,
    quote_id        uuid          NOT NULL,
    branch_id       uuid          NOT NULL,
    voucher_number  int           NOT NULL,
    subtotal_amount decimal(10,2) NOT NULL,
    total_amount    decimal(10,2) NOT NULL,
    type            varchar(20)   NOT NULL,
    status          varchar(20)   NOT NULL DEFAULT 'PENDING',
    currency        char(3)       NOT NULL,
    created_at      timestamp     NOT NULL,
    updated_at      timestamp     NOT NULL,
    deleted_at      timestamp    ,
    created_by      uuid          NOT NULL,
    updated_by      uuid         ,
    version         bigint        DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN vouchers.total_amount IS 'subtotal_amount + 18%*subtotal_amount';
COMMENT ON COLUMN vouchers.type IS 'enum: INVOICE, RECEIPT, CREDIT_NOTE';
COMMENT ON COLUMN vouchers.status IS 'enum: PENDING, PAID';
COMMENT ON COLUMN vouchers.currency IS 'enum: PEN, USD';

CREATE TABLE work_order_task_products
(
    id                 uuid          NOT NULL UNIQUE,
    product_id         uuid          NOT NULL,
    work_order_task_id uuid          NOT NULL,
    branch_id          uuid          NOT NULL,
    quantity           int           NOT NULL,
    unit_price         decimal(10,2) NOT NULL,
    total_amount       decimal(10,2) NOT NULL,
    created_at         timestamp     NOT NULL,
    updated_at         timestamp     NOT NULL,
    deleted_at         timestamp    ,
    version            bigint        NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE work_order_tasks
(
    id                   uuid          NOT NULL UNIQUE,
    work_order_id        uuid          NOT NULL,
    service_id           uuid          NOT NULL,
    branch_id            uuid          NOT NULL,
    assigned_mechanic_id uuid          NOT NULL,
    status               varchar(20)   NOT NULL DEFAULT 'PENDING',
    description          text          NOT NULL,
    started_at           timestamp    ,
    completed_at         timestamp    ,
    price                decimal(10,2) NOT NULL DEFAULT 0,
    created_at           timestamp     NOT NULL,
    updated_at           timestamp     NOT NULL,
    deleted_at           timestamp    ,
    created_by           uuid          NOT NULL,
    updated_by           uuid         ,
    version              bigint        NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN work_order_tasks.status IS 'enum: PENDING, DOING, COMPLETED';

CREATE TABLE work_orders
(
    id                 uuid          NOT NULL UNIQUE,
    appointment_id     uuid          NOT NULL,
    branch_id          uuid          NOT NULL,
    vehicle_id         uuid          NOT NULL,
    customer_id        uuid          NOT NULL,
    internal_number    int           NOT NULL,
    status             varchar(20)   DEFAULT 'PENDING',
    diagnostic_summary text          NOT NULL,
    mileage_in         int           NOT NULL,
    total_amount       decimal(10,2) NOT NULL DEFAULT 0,
    created_at         timestamp     NOT NULL,
    updated_at         timestamp     NOT NULL,
    deleted_at         timestamp    ,
    created_by         uuid          NOT NULL,
    updated_by         uuid         ,
    version            bigint        NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN work_orders.status IS 'enum: PENDING, IN_PROGRESS, COMPLETED, PAID';

CREATE TABLE workshops
(
    id                      uuid         NOT NULL UNIQUE,
    owner_id                uuid         NOT NULL,
    business_name           varchar(100) NOT NULL,
    brand_name              varchar(100) NOT NULL,
    tax_id                  varchar(50)  NOT NULL,
    mileage_interval_config int          DEFAULT 1,
    created_at              timestamp    NOT NULL,
    updated_at              timestamp    NOT NULL,
    deleted_at              timestamp   ,
    version                 bigint       NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

COMMENT ON COLUMN workshops.tax_id IS 'RUC';

-- -------------------------------------------------------------------------------------------------
-- 2. LLAVES FORÁNEAS (CONSTRAINTS)
-- -------------------------------------------------------------------------------------------------

ALTER TABLE owners ADD CONSTRAINT FK_users_TO_owners FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE workshops ADD CONSTRAINT FK_owners_TO_workshops FOREIGN KEY (owner_id) REFERENCES owners (id);
ALTER TABLE branches ADD CONSTRAINT FK_workshops_TO_branches FOREIGN KEY (workshop_id) REFERENCES workshops (id);
ALTER TABLE branch_subscriptions ADD CONSTRAINT FK_branches_TO_branch_subscriptions FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE branch_subscriptions ADD CONSTRAINT FK_subscription_plans_TO_branch_subscriptions FOREIGN KEY (plan_id) REFERENCES subscription_plans (id);
ALTER TABLE customers ADD CONSTRAINT FK_users_TO_customers FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE employees ADD CONSTRAINT FK_users_TO_employees FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE vehicle_registrations ADD CONSTRAINT FK_users_TO_vehicle_registrations FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE vehicle_registrations ADD CONSTRAINT FK_vehicles_TO_vehicle_registrations FOREIGN KEY (vehicle_id) REFERENCES vehicles (id);
ALTER TABLE password_recovery_tokens ADD CONSTRAINT FK_users_TO_password_recovery_tokens FOREIGN KEY (user_id) REFERENCES users (id);
ALTER TABLE employee_registrations ADD CONSTRAINT FK_employees_TO_employee_registrations FOREIGN KEY (employee_id) REFERENCES employees (id);
ALTER TABLE employee_registrations ADD CONSTRAINT FK_branches_TO_employee_registrations FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE customer_registrations ADD CONSTRAINT FK_customers_TO_customer_registrations FOREIGN KEY (customer_id) REFERENCES customers (id);
ALTER TABLE customer_registrations ADD CONSTRAINT FK_branches_TO_customer_registrations FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE appointments ADD CONSTRAINT FK_branches_TO_appointments FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE work_orders ADD CONSTRAINT FK_appointments_TO_work_orders FOREIGN KEY (appointment_id) REFERENCES appointments (id);
ALTER TABLE work_order_tasks ADD CONSTRAINT FK_work_orders_TO_work_order_tasks FOREIGN KEY (work_order_id) REFERENCES work_orders (id);
ALTER TABLE work_order_tasks ADD CONSTRAINT FK_services_TO_work_order_tasks FOREIGN KEY (service_id) REFERENCES services (id);
ALTER TABLE products ADD CONSTRAINT FK_branches_TO_products FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE product_batches ADD CONSTRAINT FK_products_TO_product_batches FOREIGN KEY (product_id) REFERENCES products (id);
ALTER TABLE work_order_task_products ADD CONSTRAINT FK_work_order_tasks_TO_work_order_task_products FOREIGN KEY (work_order_task_id) REFERENCES work_order_tasks (id);
ALTER TABLE work_order_task_products ADD CONSTRAINT FK_products_TO_work_order_task_products FOREIGN KEY (product_id) REFERENCES products (id);
ALTER TABLE obd2_devices ADD CONSTRAINT FK_branches_TO_obd2_devices FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE obd2_device_registrations ADD CONSTRAINT FK_obd2_devices_TO_obd2_device_registrations FOREIGN KEY (obd2_device_id) REFERENCES obd2_devices (id);
ALTER TABLE telemetry_snapshots ADD CONSTRAINT FK_obd2_device_registrations_TO_telemetry_snapshots FOREIGN KEY (obd2_device_registration_id) REFERENCES obd2_device_registrations (id);
ALTER TABLE dtc_alerts ADD CONSTRAINT FK_telemetry_snapshots_TO_dtc_alerts FOREIGN KEY (telemetry_snapshot_id) REFERENCES telemetry_snapshots (id);
ALTER TABLE quotes ADD CONSTRAINT FK_work_orders_TO_quotes FOREIGN KEY (work_order_id) REFERENCES work_orders (id);
ALTER TABLE vouchers ADD CONSTRAINT FK_quotes_TO_vouchers FOREIGN KEY (quote_id) REFERENCES quotes (id);
ALTER TABLE payments ADD CONSTRAINT FK_vouchers_TO_payments FOREIGN KEY (voucher_id) REFERENCES vouchers (id);
ALTER TABLE appointments ADD CONSTRAINT FK_customers_TO_appointments FOREIGN KEY (customer_id) REFERENCES customers (id);
ALTER TABLE appointments ADD CONSTRAINT FK_vehicles_TO_appointments FOREIGN KEY (vehicle_id) REFERENCES vehicles (id);
ALTER TABLE dtc_alerts ADD CONSTRAINT FK_branches_TO_dtc_alerts FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE obd2_device_registrations ADD CONSTRAINT FK_branches_TO_obd2_device_registrations FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE obd2_device_registrations ADD CONSTRAINT FK_vehicles_TO_obd2_device_registrations FOREIGN KEY (vehicle_id) REFERENCES vehicles (id);
ALTER TABLE payments ADD CONSTRAINT FK_branches_TO_payments FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE product_batches ADD CONSTRAINT FK_branches_TO_product_batches FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE quotes ADD CONSTRAINT FK_branches_TO_quotes FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE services ADD CONSTRAINT FK_branches_TO_services FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE telemetry_snapshots ADD CONSTRAINT FK_branches_TO_telemetry_snapshots FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE vouchers ADD CONSTRAINT FK_branches_TO_vouchers FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE work_order_task_products ADD CONSTRAINT FK_branches_TO_work_order_task_products FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE work_order_tasks ADD CONSTRAINT FK_branches_TO_work_order_tasks FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE work_order_tasks ADD CONSTRAINT FK_employee_registrations_TO_work_order_tasks FOREIGN KEY (assigned_mechanic_id) REFERENCES employee_registrations (id);
ALTER TABLE work_orders ADD CONSTRAINT FK_branches_TO_work_orders FOREIGN KEY (branch_id) REFERENCES branches (id);
ALTER TABLE work_orders ADD CONSTRAINT FK_vehicles_TO_work_orders FOREIGN KEY (vehicle_id) REFERENCES vehicles (id);
ALTER TABLE work_orders ADD CONSTRAINT FK_customers_TO_work_orders FOREIGN KEY (customer_id) REFERENCES customers (id);

-- -------------------------------------------------------------------------------------------------
-- 3. ÍNDICES DE RENDIMIENTO Y MULTI-TENANT
-- -------------------------------------------------------------------------------------------------

CREATE INDEX idx_appointments_branch_id ON appointments (branch_id);
CREATE INDEX idx_appointments_customer_id ON appointments (customer_id);
CREATE INDEX idx_appointments_vehicle_id ON appointments (vehicle_id);
CREATE INDEX idx_appointments_status ON appointments (status);
CREATE INDEX idx_appointments_scheduled_start ON appointments (scheduled_start);
CREATE INDEX idx_branch_subs_branch_id ON branch_subscriptions (branch_id);
CREATE INDEX idx_branch_subs_plan_id ON branch_subscriptions (plan_id);
CREATE INDEX idx_branches_workshop_id ON branches (workshop_id);
CREATE INDEX idx_customer_regs_customer_id ON customer_registrations (customer_id);
CREATE INDEX idx_customer_regs_branch_id ON customer_registrations (branch_id);
CREATE INDEX idx_customers_user_id ON customers (user_id);
CREATE INDEX idx_customers_business_name ON customers (business_name);
CREATE INDEX idx_dtc_alerts_telemetry_id ON dtc_alerts (telemetry_snapshot_id);
CREATE INDEX idx_dtc_alerts_branch_id ON dtc_alerts (branch_id);
CREATE INDEX idx_employee_regs_employee_id ON employee_registrations (employee_id);
CREATE INDEX idx_employee_regs_branch_id ON employee_registrations (branch_id);
CREATE INDEX idx_employees_user_id ON employees (user_id);
CREATE INDEX idx_obd2_dev_regs_device_id ON obd2_device_registrations (obd2_device_id);
CREATE INDEX idx_obd2_dev_regs_branch_id ON obd2_device_registrations (branch_id);
CREATE INDEX idx_obd2_dev_regs_vehicle_id ON obd2_device_registrations (vehicle_id);
CREATE INDEX idx_obd2_devices_branch_id ON obd2_devices (branch_id);
CREATE INDEX idx_owners_user_id ON owners (user_id);
CREATE INDEX idx_pwd_tokens_user_id ON password_recovery_tokens (user_id);
CREATE INDEX idx_payments_voucher_id ON payments (voucher_id);
CREATE INDEX idx_payments_branch_id ON payments (branch_id);
CREATE INDEX idx_product_batches_product_id ON product_batches (product_id);
CREATE INDEX idx_product_batches_branch_id ON product_batches (branch_id);
CREATE INDEX idx_products_branch_id ON products (branch_id);
CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_quotes_work_order_id ON quotes (work_order_id);
CREATE INDEX idx_quotes_branch_id ON quotes (branch_id);
CREATE INDEX idx_services_branch_id ON services (branch_id);
CREATE INDEX idx_telemetry_registration_id ON telemetry_snapshots (obd2_device_registration_id);
CREATE INDEX idx_telemetry_branch_id ON telemetry_snapshots (branch_id);
CREATE INDEX idx_telemetry_created_at ON telemetry_snapshots (created_at DESC);
CREATE INDEX idx_vehicle_regs_user_id ON vehicle_registrations (user_id);
CREATE INDEX idx_vehicle_regs_vehicle_id ON vehicle_registrations (vehicle_id);
CREATE INDEX idx_vehicles_plate_number ON vehicles (plate_number);
CREATE INDEX idx_vouchers_quote_id ON vouchers (quote_id);
CREATE INDEX idx_vouchers_branch_id ON vouchers (branch_id);
CREATE INDEX idx_wot_products_task_id ON work_order_task_products (work_order_task_id);
CREATE INDEX idx_wot_products_product_id ON work_order_task_products (product_id);
CREATE INDEX idx_wot_products_branch_id ON work_order_task_products (branch_id);
CREATE INDEX idx_wo_tasks_work_order_id ON work_order_tasks (work_order_id);
CREATE INDEX idx_wo_tasks_service_id ON work_order_tasks (service_id);
CREATE INDEX idx_wo_tasks_branch_id ON work_order_tasks (branch_id);
CREATE INDEX idx_wo_tasks_mechanic_id ON work_order_tasks (assigned_mechanic_id);
CREATE INDEX idx_work_orders_appointment_id ON work_orders (appointment_id);
CREATE INDEX idx_work_orders_branch_id ON work_orders (branch_id);
CREATE INDEX idx_work_orders_vehicle_id ON work_orders (vehicle_id);
CREATE INDEX idx_work_orders_customer_id ON work_orders (customer_id);
CREATE INDEX idx_work_orders_status ON work_orders (status);
CREATE INDEX idx_workshops_owner_id ON workshops (owner_id);

-- -------------------------------------------------------------------------------------------------
-- 4. FUNCIONES Y TRIGGERS DE AUDITORÍA (`updated_at`)
-- -------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_appointments_updated_at BEFORE UPDATE ON appointments FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_branches_updated_at BEFORE UPDATE ON branches FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_customer_registrations_updated_at BEFORE UPDATE ON customer_registrations FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_customers_updated_at BEFORE UPDATE ON customers FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_employee_registrations_updated_at BEFORE UPDATE ON employee_registrations FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_employees_updated_at BEFORE UPDATE ON employees FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_obd2_devices_updated_at BEFORE UPDATE ON obd2_devices FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_owners_updated_at BEFORE UPDATE ON owners FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_product_batches_updated_at BEFORE UPDATE ON product_batches FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_products_updated_at BEFORE UPDATE ON products FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_quotes_updated_at BEFORE UPDATE ON quotes FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_services_updated_at BEFORE UPDATE ON services FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_vehicles_updated_at BEFORE UPDATE ON vehicles FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_vouchers_updated_at BEFORE UPDATE ON vouchers FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_work_order_task_products_updated_at BEFORE UPDATE ON work_order_task_products FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_work_order_tasks_updated_at BEFORE UPDATE ON work_order_tasks FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_work_orders_updated_at BEFORE UPDATE ON work_orders FOR EACH ROW EXECUTE FUNCTION update_modified_column();
CREATE TRIGGER trg_workshops_updated_at BEFORE UPDATE ON workshops FOR EACH ROW EXECUTE FUNCTION update_modified_column();

-- -------------------------------------------------------------------------------------------------
-- 5. FUNCIONES Y TRIGGERS DE INVENTARIO
-- -------------------------------------------------------------------------------------------------

CREATE OR REPLACE FUNCTION sync_product_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF (TG_OP = 'DELETE') THEN
UPDATE products
SET current_stock = (SELECT COALESCE(SUM(available_quantity), 0) FROM product_batches WHERE product_id = OLD.product_id)
WHERE id = OLD.product_id;
RETURN OLD;
ELSE
UPDATE products
SET current_stock = (SELECT COALESCE(SUM(available_quantity), 0) FROM product_batches WHERE product_id = NEW.product_id)
WHERE id = NEW.product_id;
RETURN NEW;
END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_sync_product_stock
    AFTER INSERT OR UPDATE OR DELETE ON product_batches
    FOR EACH ROW EXECUTE FUNCTION sync_product_stock();
