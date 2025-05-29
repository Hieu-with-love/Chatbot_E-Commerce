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
                                                                                       (10, 2, 19.99, 'Woolen scarf', 'Scarf Warm', 'https://example.com/scarf_warm.jpg', 10, 'white', 'L'),
                                                                                       (11, 1, 799.99, '4K Ultra HD Smart TV 55-inch', 'Smart TV Ultra', 'https://example.com/smart_tv_ultra.jpg', 8, 'black', 'XL'),
                                                                                       (12, 1, 149.99, 'Wireless gaming mouse', 'Gaming Mouse X', 'https://example.com/gaming_mouse_x.jpg', 15, 'black', 'M'),
                                                                                       (13, 1, 249.99, 'Mechanical keyboard with RGB lighting', 'Keyboard Pro', 'https://example.com/keyboard_pro.jpg', 12, 'silver', 'M'),
                                                                                       (14, 1, 89.99, 'USB-C charging cable 2m', 'Charger Fast', 'https://example.com/charger_fast.jpg', 20, 'white', 'S'),
                                                                                       (15, 1, 599.99, 'Mirrorless camera with 24MP sensor', 'Camera Snap', 'https://example.com/camera_snap.jpg', 5, 'black', 'L'),
                                                                                       (16, 1, 349.99, 'Noise-canceling headphones', 'Headphones Elite', 'https://example.com/headphones_elite.jpg', 10, 'blue', 'M'),
                                                                                       (17, 1, 199.99, 'Portable SSD 1TB', 'SSD Quick', 'https://example.com/ssd_quick.jpg', 18, 'silver', 'S'),
                                                                                       (18, 1, 1099.99, 'Gaming console with 1TB storage', 'Console Next', 'https://example.com/console_next.jpg', 7, 'black', 'L'),
                                                                                       (19, 1, 79.99, 'Smart home assistant speaker', 'Assistant Echo', 'https://example.com/assistant_echo.jpg', 25, 'grey', 'S'),
                                                                                       (20, 1, 399.99, 'Drone with 4K camera', 'Drone Fly', 'https://example.com/drone_fly.jpg', 6, 'white', 'M'),
                                                                                       (21, 2, 59.99, 'Polo shirt with breathable fabric', 'Polo Classic', 'https://example.com/polo_classic.jpg', 15, 'navy', 'M'),
                                                                                       (22, 2, 39.99, 'Athletic shorts with pockets', 'Shorts Active', 'https://example.com/shorts_active.jpg', 20, 'black', 'M'),
                                                                                       (23, 2, 199.99, 'Winter coat with insulation', 'Coat Warm', 'https://example.com/coat_warm.jpg', 8, 'grey', 'L'),
                                                                                       (24, 2, 24.99, 'Cotton beanie for cold weather', 'Beanie Cozy', 'https://example.com/beanie_cozy.jpg', 30, 'red', 'S'),
                                                                                       (25, 2, 69.99, 'Formal dress shirt', 'Shirt Elegant', 'https://example.com/shirt_elegant.jpg', 12, 'white', 'L'),
                                                                                       (26, 2, 99.99, 'Leather belt with classic buckle', 'Belt Premium', 'https://example.com/belt_premium.jpg', 18, 'brown', 'M'),
                                                                                       (27, 2, 129.99, 'Hiking boots with waterproofing', 'Boots Trek', 'https://example.com/boots_trek.jpg', 10, 'black', 'L'),
                                                                                       (28, 2, 34.99, 'Graphic hoodie with bold design', 'Hoodie Trendy', 'https://example.com/hoodie_trendy.jpg', 15, 'green', 'L'),
                                                                                       (29, 2, 49.99, 'Sunglasses with UV protection', 'Sunglasses Cool', 'https://example.com/sunglasses_cool.jpg', 20, 'black', 'M'),
                                                                                       (30, 2, 79.99, 'Denim skirt with casual style', 'Skirt Denim', 'https://example.com/skirt_denim.jpg', 12, 'blue', 'M');

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
                                                              (12, 10, 'https://example.com/scarf_warm_1.jpg'),
                                                              (13, 11, 'https://example.com/smart_tv_ultra_1.jpg'),
                                                              (14, 12, 'https://example.com/gaming_mouse_x_1.jpg'),
                                                              (15, 13, 'https://example.com/keyboard_pro_1.jpg'),
                                                              (16, 14, 'https://example.com/charger_fast_1.jpg'),
                                                              (17, 15, 'https://example.com/camera_snap_1.jpg'),
                                                              (18, 16, 'https://example.com/headphones_elite_1.jpg'),
                                                              (19, 17, 'https://example.com/ssd_quick_1.jpg'),
                                                              (20, 18, 'https://example.com/console_next_1.jpg'),
                                                              (21, 19, 'https://example.com/assistant_echo_1.jpg'),
                                                              (22, 20, 'https://example.com/drone_fly_1.jpg'),
                                                              (23, 21, 'https://example.com/polo_classic_1.jpg'),
                                                              (24, 22, 'https://example.com/shorts_active_1.jpg'),
                                                              (25, 23, 'https://example.com/coat_warm_1.jpg'),
                                                              (26, 24, 'https://example.com/beanie_cozy_1.jpg'),
                                                              (27, 25, 'https://example.com/shirt_elegant_1.jpg'),
                                                              (28, 26, 'https://example.com/belt_premium_1.jpg'),
                                                              (29, 27, 'https://example.com/boots_trek_1.jpg'),
                                                              (30, 28, 'https://example.com/hoodie_trendy_1.jpg'),
                                                              (31, 29, 'https://example.com/sunglasses_cool_1.jpg'),
                                                              (32, 30, 'https://example.com/skirt_denim_1.jpg');


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

