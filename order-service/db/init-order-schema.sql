CREATE TABLE p_order (
    id UUID NOT NULL PRIMARY KEY,
    supply_company_id UUID NOT NULL,
    request_company_id UUID NOT NULL,
    user_id UUID NOT NULL,
    delivery_id UUID,
    price BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    desired_arrival TIMESTAMP NOT NULL,
    request_memo TEXT,
    created_by UUID NOT NULL,
    updated_by UUID,
    deleted_by UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_p_order_deleted_at ON p_order (deleted_at);

CREATE TABLE p_order_item (
    id UUID NOT NULL PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES p_order (id),
    product_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    created_by UUID NOT NULL,
    updated_by UUID,
    deleted_by UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_p_order_item_order_id ON p_order_item (order_id);
