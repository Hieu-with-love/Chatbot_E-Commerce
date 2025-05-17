create table tbl_category
(
    id            int auto_increment
        primary key,
    code          varchar(255) null,
    description   varchar(255) null,
    name          varchar(255) not null,
    thumbnail_url varchar(255) null
);

create table tbl_product
(
    category_id   int            null,
    id            int auto_increment
        primary key,
    price         decimal(10, 2) not null,
    description   varchar(255)   not null,
    name          varchar(255)   not null,
    thumbnail_url varchar(255)   not null,
    constraint FKfq7110lh85cseoy13cgni7pet
        foreign key (category_id) references tbl_category (id)
);

create table tbl_product_image
(
    id         int auto_increment
        primary key,
    product_id int          null,
    image_url  varchar(255) not null,
    constraint FKmoh9bwnhu81rx27vxwxbg80up
        foreign key (product_id) references tbl_product (id)
);

create table tbl_user
(
    id        int auto_increment
        primary key,
    email     varchar(255) not null,
    full_name varchar(255) not null,
    password  varchar(255) not null,
    phone     varchar(255) not null,
    role      varchar(255) not null,
    constraint UKnpn1wf1yu1g5rjohbek375pp1
        unique (email)
);

create table tbl_address
(
    id           int auto_increment
        primary key,
    user_id      int          null,
    address_line varchar(255) null,
    city         varchar(255) null,
    country      varchar(255) null,
    full_name    varchar(255) null,
    phone        varchar(255) null,
    postal_code  varchar(255) null,
    constraint FKlo13i087wmqhi0h7ffjxoljrb
        foreign key (user_id) references tbl_user (id)
);

create table tbl_cart
(
    id      int auto_increment
        primary key,
    user_id int null,
    constraint UKeymp5kbfgooc31l260hr63vox
        unique (user_id),
    constraint FKhv6grtjnmtoylt2yyt4wmqtf3
        foreign key (user_id) references tbl_user (id)
);

create table tbl_cart_item
(
    cart_id    int null,
    id         int auto_increment
        primary key,
    product_id int null,
    quantity   int not null,
    constraint FK84umwt3ihkiggf03us5gq116j
        foreign key (product_id) references tbl_product (id),
    constraint FKhaw0aw4g8s9icxekpl4oi715a
        foreign key (cart_id) references tbl_cart (id)
);

create table tbl_order
(
    id               int auto_increment
        primary key,
    total_price      decimal(10, 2) not null,
    user_id          int            null,
    order_date       datetime(6)    not null,
    shipping_address varchar(255)   not null,
    shipping_phone   varchar(255)   not null,
    status           varchar(255)   not null,
    constraint FKhyolniflkctr0p6bp4t8me9vj
        foreign key (user_id) references tbl_user (id)
);

create table tbl_order_item
(
    id         int auto_increment
        primary key,
    order_id   int            null,
    price      decimal(10, 2) not null,
    product_id int            null,
    quantity   int            not null,
    constraint FK1oy9x003q55eqmuiv0y8a15e
        foreign key (product_id) references tbl_product (id),
    constraint FKmkqpajkg6p2wq4owcv1v08pc5
        foreign key (order_id) references tbl_order (id)
);

