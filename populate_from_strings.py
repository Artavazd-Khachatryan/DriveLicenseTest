#!/usr/bin/env python3
"""
Script to populate database from QuestionGroup classes and strings.xml
This reads the strings.xml file to get actual question text and generates SQL insert statements.
"""

import xml.etree.ElementTree as ET
import re
import json
from typing import Dict, List, Tuple

def parse_strings_xml(filepath: str) -> Dict[str, str]:
    """Parse strings.xml and return a dictionary of string name -> value"""
    tree = ET.parse(filepath)
    root = tree.getroot()
    
    strings = {}
    for string_element in root.findall('string'):
        name = string_element.get('name')
        text = string_element.text or ''
        # Clean up the text (remove extra whitespace)
        text = ' '.join(text.split())
        strings[name] = text
    
    return strings

def extract_question_info_from_kotlin(filepath: str) -> List[Tuple[str, str, List[str]]]:
    """
    Extract question info from QuestionGroup Kotlin files.
    Returns list of (book, image, categories) tuples in order.
    """
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    questions = []
    
    # Split into individual Question blocks
    question_blocks = re.split(r'Question\s*\(', content)[1:]  # Skip first empty part
    
    for block in question_blocks:
        try:
            # Extract question key
            question_match = re.search(r'question\s*=\s*Res\.string\.([^,\s]+)', block)
            if not question_match:
                continue
            question_key = question_match.group(1).strip()
            
            # Extract image
            image_match = re.search(r'image\s*=\s*([^,\n]+)', block)
            image = ""
            if image_match:
                image = image_match.group(1).strip().replace('"', '').replace('null', '')
            
            # Extract book
            book_match = re.search(r'book\s*=\s*Book\.([A-Z0-9_]+)', block)
            if not book_match:
                continue
            book = book_match.group(1).strip()
            
            # Extract categories
            categories_match = re.search(r'categories\s*=\s*listOf\(([^)]+)\)', block, re.DOTALL)
            categories = []
            if categories_match:
                categories_str = categories_match.group(1)
                category_pattern = r'QuestionCategory\.([A-Z_]+)'
                categories = re.findall(category_pattern, categories_str)
            
            questions.append((book, image if image != '' else None, categories))
            
        except Exception as e:
            print(f"Error parsing question block: {e}")
            continue
    
    return questions

def get_book_id(book_name: str) -> int:
    """Convert book name to ID"""
    book_mapping = {
        'BOOK_1': 1, 'BOOK_2': 2, 'BOOK_3': 3, 'BOOK_4': 4, 'BOOK_5': 5,
        'BOOK_6': 6, 'BOOK_7': 7, 'BOOK_8': 8, 'BOOK_9': 9, 'BOOK_10': 10
    }
    return book_mapping.get(book_name, 1)

def get_category_id(category_name: str) -> int:
    """Convert category name to ID"""
    category_mapping = {
        'TRAFFIC_SIGNS_AND_MARKINGS': 1,
        'LANE_USAGE_AND_POSITIONING': 2,
        'MANEUVERS_AND_TURNS': 3,
        'RIGHT_OF_WAY_AND_PRIORITY': 4,
        'PROHIBITED_ACTIONS': 5,
        'SPECIAL_VEHICLES_AND_SITUATIONS': 6,
        'INTERSECTIONS_AND_CROSSINGS': 7,
        'ROAD_CONDITIONS_AND_VISIBILITY': 8,
        'VEHICLE_TYPES_AND_CATEGORIES': 9,
        'GENERAL_TRAFFIC_RULES': 10
    }
    return category_mapping.get(category_name, 10)

def escape_sql_string(text: str) -> str:
    """Escape single quotes for SQL"""
    return text.replace("'", "''")

def main():
    # Parse strings.xml
    print("Parsing strings.xml...")
    strings = parse_strings_xml('composeApp/src/commonMain/composeResources/values/strings.xml')
    print(f"Found {len(strings)} strings")
    
    # Process all QuestionGroup files
    all_sql_statements = []
    question_id = 1
    
    for group_num in range(1, 11):
        group_file = f'composeApp/src/commonMain/kotlin/com/drive/license/test/models/QuestionGroup{group_num}.kt'
        
        try:
            print(f"Processing QuestionGroup{group_num}...")
            questions_info = extract_question_info_from_kotlin(group_file)
            
            for i, (book, image, categories) in enumerate(questions_info):
                question_key = f"question_{question_id}"
                
                # Get question text and answers
                if question_key in strings:
                    question_text = escape_sql_string(strings[question_key])
                    
                    # Get answers
                    answers = []
                    answer_num = 1
                    while f"{question_key}_answer_{answer_num}" in strings:
                        answers.append(strings[f"{question_key}_answer_{answer_num}"])
                        answer_num += 1
                    
                    # Get true answer
                    true_answer = strings.get(f"{question_key}_true_answer", "")
                    
                    # Create JSON for answers
                    answers_json = json.dumps(answers, ensure_ascii=False)
                    answers_json = escape_sql_string(answers_json)
                    
                    # Generate SQL
                    book_id = get_book_id(book)
                    image_sql = f"'{image}'" if image else "NULL"
                    
                    sql = f"""INSERT INTO Question (id, question, image, answers, true_answer, book_id) VALUES ({question_id}, '{question_text}', {image_sql}, '{answers_json}', '{escape_sql_string(true_answer)}', {book_id});"""
                    all_sql_statements.append(sql)
                    
                    # Add category associations
                    for category in categories:
                        category_id = get_category_id(category)
                        category_sql = f"INSERT INTO QuestionCategoryJunction (question_id, category_id) VALUES ({question_id}, {category_id});"
                        all_sql_statements.append(category_sql)
                
                question_id += 1
                
        except FileNotFoundError:
            print(f"File not found: {group_file}")
        except Exception as e:
            print(f"Error processing {group_file}: {e}")
    
    # Write SQL file
    print(f"Generating SQL file with {len(all_sql_statements)} statements...")
    with open('populate_questions.sql', 'w', encoding='utf-8') as f:
        f.write("-- Generated SQL script to populate questions from QuestionGroup classes\n")
        f.write("-- This script should be run after populate_database_script.sql\n\n")
        
        for sql in all_sql_statements:
            f.write(sql + '\n')
        
        f.write(f"\n-- Inserted {question_id - 1} questions total\n")
        f.write("SELECT 'Questions populated successfully' as status;\n")
    
    print(f"SQL script written to populate_questions.sql")
    print(f"Total questions: {question_id - 1}")

if __name__ == "__main__":
    main()