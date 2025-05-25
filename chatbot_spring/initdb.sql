INSERT INTO tbl_category (id, code, description, name, thumbnail_url) VALUES
                                                                          (1, 'CAT001', 'Electronics devices', 'Electronics', 'https://example.com/electronics.jpg'),
                                                                          (2, 'CAT002', 'Fashion and clothing', 'Fashion', 'https://example.com/fashion.jpg');

INSERT INTO tbl_product (id, category_id, price, description, name, thumbnail_url, stock, color, size) VALUES
                                                                                       (1, 1, 999.99, 'High-performance smartphone with 128GB storage', 'Smartphone X', 'https://example.com/smartphone_x.jpg', 10, 'white', 'L'),
                                                                                       (2, 1, 1299.99, '15-inch laptop with 16GB RAM', 'Laptop Pro', 'https://example.com/laptop_pro.jpg', 10, 'white', 'L'),
                                                                                       (3, 1, 199.99, 'Wireless earbuds with noise cancellation', 'Earbuds Z', 'https://example.com/earbuds_z.jpg', 10, 'white', 'L'),
                                                                                       (4, 1, 499.99, 'Smartwatch with health tracking', 'Smartwatch S', 'https://example.com/smartwatch_s.jpg', 10, 'white', 'L'),
                                                                                       (5, 1, 299.99, 'Portable Bluetooth speaker', 'Speaker V', 'https://example.com/speaker_v.jpg', 10, 'white', 'L'),
                                                                                       (6, 2, 49.99, 'Casual cotton t-shirt', 'T-Shirt Basic', 'https://example.com/tshirt_basic.jpg', 10, 'white', 'L'),
                                                                                       (7, 2, 89.99, 'Slim-fit jeans', 'Jeans Classic', 'https://example.com/jeans_classic.jpg', 10, 'white', 'L'),
                                                                                       (8, 2, 149.99, 'Leather jacket', 'Jacket Cool', 'https://example.com/jacket_cool.jpg', 10, 'white', 'L'),
                                                                                       (9, 2, 29.99, 'Running sneakers', 'Sneakers Run', 'https://example.com/sneakers_run.jpg', 10, 'white', 'L'),
                                                                                       (10, 2, 19.99, 'Woolen scarf', 'Scarf Warm', 'https://example.com/scarf_warm.jpg', 10, 'white', 'L');

INSERT INTO tbl_product_image (id, product_id, image_url) VALUES
                                                              (1, 1, 'https://example.com/smartphone_x_1.jpg'),
                                                              (2, 1, 'https://example.com/smartphone_x_2.jpg'),
                                                              (3, 2, 'https://example.com/laptop_pro_1.jpg'),
                                                              (4, 2, 'https://example.com/laptop_pro_2.jpg'),
                                                              (5, 3, 'https://example.com/earbuds_z_1.jpg'),
                                                              (6, 4, 'https://example.com/smartwatch_s_1.jpg'),
                                                              (7, 5, 'https://example.com/speaker_v_1.jpg'),
                                                              (8, 6, 'https://example.com/tshirt_basic_1.jpg'),
                                                              (9, 7, 'https://example.com/jeans_classic_1.jpg'),
                                                              (10, 8, 'https://example.com/jacket_cool_1.jpg'),
                                                              (11, 9, 'https://example.com/sneakers_run_1.jpg'),
                                                              (12, 10, 'https://example.com/scarf_warm_1.jpg');


INSERT INTO tbl_cart_item (id, cart_id, product_id, quantity) VALUES
                                                                  (1, 1, 1, 2),  -- 2 Smartphone X
                                                                  (2, 1, 2, 1),  -- 1 Laptop Pro
                                                                  (3, 1, 3, 1),  -- 1 Earbuds Z
                                                                  (4, 1, 4, 2),  -- 2 Smartwatch S
                                                                  (5, 1, 5, 1),  -- 1 Speaker V
                                                                  (6, 1, 6, 3),  -- 3 T-Shirt Basic
                                                                  (7, 1, 7, 2),  -- 2 Jeans Classic
                                                                  (8, 1, 8, 1),  -- 1 Jacket Cool
                                                                  (9, 1, 9, 2),  -- 2 Sneakers Run
                                                                  (10, 1, 10, 3); -- 3 Scarf Warm