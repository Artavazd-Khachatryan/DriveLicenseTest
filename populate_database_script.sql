-- Step 1: Delete all existing questions and relationships
DELETE FROM QuestionCategoryJunction;
DELETE FROM Question;

-- Step 2: Clear and populate Books (if needed)
-- The books should already be populated, but let's ensure they're correct
DELETE FROM Book;
INSERT INTO Book (id, name) VALUES (1, 'BOOK_1');
INSERT INTO Book (id, name) VALUES (2, 'BOOK_2');
INSERT INTO Book (id, name) VALUES (3, 'BOOK_3');
INSERT INTO Book (id, name) VALUES (4, 'BOOK_4');
INSERT INTO Book (id, name) VALUES (5, 'BOOK_5');
INSERT INTO Book (id, name) VALUES (6, 'BOOK_6');
INSERT INTO Book (id, name) VALUES (7, 'BOOK_7');
INSERT INTO Book (id, name) VALUES (8, 'BOOK_8');
INSERT INTO Book (id, name) VALUES (9, 'BOOK_9');
INSERT INTO Book (id, name) VALUES (10, 'BOOK_10');

-- Step 3: Clear and populate Categories (if needed)
-- The categories should already be populated, but let's ensure they're correct
DELETE FROM QuestionCategory;
INSERT INTO QuestionCategory (id, name) VALUES (1, 'TRAFFIC_SIGNS_AND_MARKINGS');
INSERT INTO QuestionCategory (id, name) VALUES (2, 'LANE_USAGE_AND_POSITIONING');
INSERT INTO QuestionCategory (id, name) VALUES (3, 'MANEUVERS_AND_TURNS');
INSERT INTO QuestionCategory (id, name) VALUES (4, 'RIGHT_OF_WAY_AND_PRIORITY');
INSERT INTO QuestionCategory (id, name) VALUES (5, 'PROHIBITED_ACTIONS');
INSERT INTO QuestionCategory (id, name) VALUES (6, 'SPECIAL_VEHICLES_AND_SITUATIONS');
INSERT INTO QuestionCategory (id, name) VALUES (7, 'INTERSECTIONS_AND_CROSSINGS');
INSERT INTO QuestionCategory (id, name) VALUES (8, 'ROAD_CONDITIONS_AND_VISIBILITY');
INSERT INTO QuestionCategory (id, name) VALUES (9, 'VEHICLE_TYPES_AND_CATEGORIES');
INSERT INTO QuestionCategory (id, name) VALUES (10, 'GENERAL_TRAFFIC_RULES');

-- Step 4: Questions will be populated by the Kotlin script
-- This SQL script just prepares the tables

SELECT 'Database cleared and reference tables populated' as status;