import uuid

from flask import Flask, request, send_file, jsonify
from rembg import remove
from PIL import Image
import io
import os
import datetime
import re
from flask_cors import CORS
from werkzeug.utils import secure_filename
import logging
import json
from pathlib import Path

# Import for Machine Learning
import numpy as np
import pandas as pd
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.ensemble import RandomForestClassifier

app = Flask(__name__)
CORS(app)

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

BASE_DIR = os.path.dirname(__file__)
UPLOAD_FOLDER = os.path.join(BASE_DIR, 'user_images')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

# Machine Learning

class ClothingRecommender:
    def __init__(self):
        # Dữ liệu mở rộng với nhiều loại trang phục hơn
        self.training_data = pd.DataFrame({
            'temperature': [0, 5, 10, 15, 20, 25, 30, 35, 40]*4,
            'humidity': [20, 30, 40, 50, 60, 70, 80, 90, 100]*4,
            'wind_speed': [0, 5, 10, 15, 20, 25, 30, 35, 40]*4,
            'top_wear': [
                "Winter Coat", "Heavy Jacket", "Parka", "Trench Coat", "Peacoat", "Leather Jacket",
                "Fleece Jacket", "Turtleneck", "Wool Sweater", "Hoodie", "Pullover", "Cardigan",
                "T-Shirt", "Polo Shirt", "Tank Top", "Crop Top", "Sleeveless Shirt", "Hawaiian Shirt",
                "Denim Jacket", "Bomber Jacket", "Windbreaker", "Blazer", "Flannel Shirt", "Long Sleeve Shirt",
                "Athletic Shirt", "Compression Shirt", "Thermal Shirt", "Baseball Shirt", "Fishing Shirt", "Cycling Jersey",
                "Kimono", "Poncho", "Chambray Shirt", "Henley Shirt", "V-Neck Shirt", "Graphic Tee"
            ],
            'bottom_wear': [
                "Thermal Pants", "Fleece-Lined Pants", "Corduroy Pants", "Wool Pants", "Leather Pants", "Cargo Pants",
                "Shorts", "Denim Shorts", "Bermuda Shorts", "Board Shorts", "Capri Pants", "Linen Pants",
                "Jeans", "Slim-Fit Jeans", "Straight-Leg Jeans", "Distressed Jeans", "Chinos", "Khaki Pants",
                "Joggers", "Sweatpants", "Track Pants", "Yoga Pants", "Tights", "Cycling Shorts",
                "Skirt", "Mini Skirt", "Maxi Skirt", "Pleated Skirt", "A-Line Skirt", "Pencil Skirt",
                "Palazzo Pants", "Culottes", "Overalls", "Parachute Pants", "Bell-Bottoms", "Sarong"
            ]
        })
        
        self.model = None
        self.prepare_model()

    def prepare_model(self):
        """Chuẩn bị mô hình máy học"""
        X = self.training_data[['temperature', 'humidity', 'wind_speed']]
        y_top = self.training_data['top_wear']
        y_bottom = self.training_data['bottom_wear']

        # Chuẩn hóa dữ liệu
        self.scaler = StandardScaler()
        X_scaled = self.scaler.fit_transform(X)
        
        # Huấn luyện mô hình
        self.top_classifier = RandomForestClassifier(n_estimators=100, random_state=42)
        self.bottom_classifier = RandomForestClassifier(n_estimators=100, random_state=42)
        
        self.top_classifier.fit(X_scaled, y_top)
        self.bottom_classifier.fit(X_scaled, y_bottom)

    def recommend_clothing(self, temperature, humidity, wind_speed):
        """Dự đoán quần áo dựa trên nhiệt độ, độ ẩm và tốc độ gió"""
        input_data = [[temperature, humidity, wind_speed]]
        input_scaled = self.scaler.transform(input_data)
        
        top_wear = self.top_classifier.predict(input_scaled)[0]
        bottom_wear = self.bottom_classifier.predict(input_scaled)[0]

        return {
            'topWear': top_wear,
            'bottomWear': bottom_wear
        }

# Khởi tạo recommender toàn cục
global_recommender = ClothingRecommender()

class UserCategories:
    DEFAULT_CATEGORIES = [
        'uncategorized',
        'long sleeves',
        'short sleeves', 
        'long leggings',
        'short leggings'
    ]
    
    @staticmethod
    def get_user_categories_file(user_id):
        return os.path.join(UPLOAD_FOLDER, str(user_id), 'categories.json')
    
    @staticmethod
    def init_categories(user_id):
        user_dir = os.path.join(UPLOAD_FOLDER, str(user_id))
        os.makedirs(user_dir, exist_ok=True)
    
        categories_file = UserCategories.get_user_categories_file(user_id)
        if not os.path.exists(categories_file):
            categories_data = {
                'categories': UserCategories.DEFAULT_CATEGORIES,
                'custom_categories': []
            }
            # Ensure directory exists before writing file
            os.makedirs(os.path.dirname(categories_file), exist_ok=True)
            with open(categories_file, 'w') as f:
                json.dump(categories_data, f, indent=4)
        
        return UserCategories.get_categories(user_id)

    @staticmethod
    def get_categories(user_id):
        """Get all categories for a user"""
        categories_file = UserCategories.get_user_categories_file(user_id)
        if os.path.exists(categories_file):
            with open(categories_file, 'r') as f:
                data = json.load(f)
                return data['categories'] + data['custom_categories']
        return UserCategories.init_categories(user_id)
    
    @staticmethod
    def add_category(user_id, category_name):
        """Add a new custom category"""
        categories_file = UserCategories.get_user_categories_file(user_id)
        with open(categories_file, 'r') as f:
            data = json.load(f)
        
        category_name = category_name.lower().strip()
        all_categories = data['categories'] + data['custom_categories']
        
        if category_name in all_categories:
            return False, "Category already exists"
            
        data['custom_categories'].append(category_name)
        
        with open(categories_file, 'w') as f:
            json.dump(data, f, indent=4)
            
        category_dir = os.path.join(UPLOAD_FOLDER, str(user_id), category_name)
        if not os.path.exists(category_dir):
            os.makedirs(category_dir)
            
        return True, "Category added successfully"
    
    @staticmethod
    def remove_category(user_id, category_name):
        """Remove a custom category"""
        categories_file = UserCategories.get_user_categories_file(user_id)
        with open(categories_file, 'r') as f:
            data = json.load(f)
            
        if category_name in data['categories']:
            return False, "Cannot remove default category"
            
        if category_name not in data['custom_categories']:
            return False, "Category not found"
            
        old_dir = os.path.join(UPLOAD_FOLDER, str(user_id), category_name)
        new_dir = os.path.join(UPLOAD_FOLDER, str(user_id), 'uncategorized')
        
        if os.path.exists(old_dir):
            for filename in os.listdir(old_dir):
                old_path = os.path.join(old_dir, filename)
                new_path = os.path.join(new_dir, filename)
                os.rename(old_path, new_path)
            os.rmdir(old_dir)
        
        metadata = load_metadata(user_id)
        if metadata:
            for filename, info in metadata['images'].items():
                if info['category'] == category_name:
                    info['category'] = 'uncategorized'
                    metadata['categories']['uncategorized'].append(filename)
            save_metadata(user_id, metadata)
        
        data['custom_categories'].remove(category_name)
        
        with open(categories_file, 'w') as f:
            json.dump(data, f, indent=4)
            
        return True, "Category removed successfully"

def verify_metadata_structure(user_id):
    """Verify and repair metadata structure if needed"""
    metadata = load_metadata(user_id)
    categories = UserCategories.get_categories(user_id)
    
    if metadata:
        # Ensure all categories exist in metadata
        for category in categories:
            if category not in metadata['categories']:
                metadata['categories'][category] = []
                
        # Verify all images are properly categorized
        for filename, info in metadata['images'].items():
            category = info['category']
            if category not in metadata['categories']:
                metadata['categories'][category] = []
            if filename not in metadata['categories'][category]:
                metadata['categories'][category].append(filename)
                
        save_metadata(user_id, metadata)
    
    return metadata

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def is_valid_firebase_uid(uid):
    """Validate Firebase UID format"""
    return bool(re.match(r'^[a-zA-Z0-9]{28}$', uid))

def ensure_user_directory(user_id):
    """Create directory structure for new user"""
    if not user_id or not is_valid_firebase_uid(user_id):
        raise ValueError("Invalid user ID")

    # Create main user directory
    user_dir = os.path.join(UPLOAD_FOLDER, str(user_id))
    os.makedirs(user_dir, exist_ok=True)
    
    # Create categories.json if it doesn't exist
    categories_file = os.path.join(user_dir, 'categories.json')
    if not os.path.exists(categories_file):
        categories_data = {
            'categories': UserCategories.DEFAULT_CATEGORIES,
            'custom_categories': []
        }
        with open(categories_file, 'w') as f:
            json.dump(categories_data, f, indent=4)
    
    # Create category directories
    categories = UserCategories.get_categories(user_id)
    for category in categories:
        category_dir = os.path.join(user_dir, category)
        os.makedirs(category_dir, exist_ok=True)
            
    # Create/update metadata.json
    metadata_file = os.path.join(user_dir, 'metadata.json')
    if not os.path.exists(metadata_file):
        metadata = {
            'user_id': user_id,
            'images': {},
            'categories': {cat: [] for cat in categories}
        }
        save_metadata(user_id, metadata)
    
    return user_dir

def save_metadata(user_id, metadata):
    """Save metadata to JSON file"""
    metadata_file = os.path.join(UPLOAD_FOLDER, str(user_id), 'metadata.json')
    with open(metadata_file, 'w') as f:
        json.dump(metadata, f, indent=4)

def load_metadata(user_id):
    """Load metadata from JSON file"""
    metadata_file = os.path.join(UPLOAD_FOLDER, str(user_id), 'metadata.json')
    if os.path.exists(metadata_file):
        with open(metadata_file, 'r') as f:
            return json.load(f)
    return None

@app.route('/remove-background', methods=['POST'])
def remove_background():
    try:
        user_id = request.form.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        if 'image' not in request.files:
            return jsonify({'error': 'No image file provided'}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({'error': 'No selected file'}), 400

        if file and allowed_file(file.filename):
            try:
                input_image = Image.open(file.stream)
                output_image = remove(input_image)

                temp_buffer = io.BytesIO()
                output_image.save(temp_buffer, format='PNG')
                temp_buffer.seek(0)

                return send_file(
                    temp_buffer,
                    mimetype='image/png',
                    as_attachment=False
                )
            except Exception as e:
                logger.error(f"Image processing error: {str(e)}")
                return jsonify({'error': 'Error processing image'}), 500
        else:
            return jsonify({'error': 'Invalid file type'}), 400

    except Exception as e:
        logger.error(f"Unexpected error in remove_background: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/save-image', methods=['POST'])
def save_image():
    try:
        user_id = request.form.get('user_id')
        category = request.form.get('category', 'uncategorized').lower().strip()
        
        logger.debug(f"Saving image for user: {user_id}, category: {category}")
        
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400
            
        categories = [cat.lower().strip() for cat in UserCategories.get_categories(user_id)]
        if category not in categories:
            logger.error(f"Invalid category: {category}")
            logger.error(f"Available categories: {categories}")
            category = 'uncategorized'  # Fallback to uncategorized

        if 'image' not in request.files:
            return jsonify({'error': 'No image file provided'}), 400

        file = request.files['image']
        if file.filename == '':
            return jsonify({'error': 'No selected file'}), 400

        if file and allowed_file(file.filename):
            try:
                user_dir = ensure_user_directory(user_id)
                
                timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
                safe_filename = secure_filename(f"processed_{timestamp}_{file.filename}")
                
                category_dir = os.path.join(user_dir, category)
                file_path = os.path.join(category_dir, safe_filename)
                
                input_image = Image.open(file.stream)
                input_image.save(file_path, 'PNG')
                
                metadata = load_metadata(user_id)
                metadata['images'][safe_filename] = {
                    'category': category,
                    'timestamp': timestamp,
                    'original_filename': file.filename
                }
                metadata['categories'][category].append(safe_filename)
                save_metadata(user_id, metadata)

                return jsonify({
                    'message': 'Image saved successfully',
                    'filename': safe_filename,
                    'category': category
                }), 200
                
            except Exception as e:
                logger.error(f"Image saving error: {str(e)}")
                return jsonify({'error': 'Error saving image'}), 500
        else:
            return jsonify({'error': 'Invalid file type'}), 400

    except Exception as e:
        logger.error(f"Unexpected error in save_image: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/get-user-images', methods=['GET'])
def get_user_images():
    try:
        user_id = request.args.get('user_id')
        category = request.args.get('category')
        
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        # Log received parameters
        logger.debug(f"Getting images for user: {user_id}, category: {category}")

        metadata = load_metadata(user_id)
        if not metadata:
            logger.debug("No metadata found, returning empty list")
            return jsonify([])

        # Normalize category name
        if category:
            category = category.lower().strip()
            categories = UserCategories.get_categories(user_id)
            categories = [cat.lower().strip() for cat in categories]
            
            if category not in categories:
                logger.error(f"Invalid category: {category}")
                logger.error(f"Available categories: {categories}")
                return jsonify({'error': 'Invalid category'}), 400
            
            image_files = metadata['categories'].get(category, [])
        else:
            image_files = list(metadata['images'].keys())

        image_urls = []
        for filename in image_files:
            category = metadata['images'][filename]['category']
            image_urls.append({
                'url': f"/images/{user_id}/{category}/{filename}",
                'category': category,
                'filename': filename
            })

        logger.debug(f"Returning {len(image_urls)} images")
        return jsonify(image_urls)

    except Exception as e:
        logger.error(f"Error in get_user_images: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/images/<user_id>/<category>/<filename>')
def serve_image(user_id, category, filename):
    try:
        logger.debug(f"Attempting to serve image - User ID: {user_id}, Category: {category}, Filename: {filename}")
        
        if not user_id or not is_valid_firebase_uid(user_id):
            logger.error("Invalid user ID")
            return jsonify({'error': 'Invalid user ID'}), 400

        categories = UserCategories.get_categories(user_id)
        logger.debug(f"Available categories for user: {categories}")
        
        if category not in categories:
            logger.error(f"Category {category} not found in user categories")
            return jsonify({'error': 'Invalid category'}), 400

        file_path = os.path.join(UPLOAD_FOLDER, str(user_id), category, filename)
        logger.debug(f"Searching for file at path: {file_path}")

        if not os.path.exists(file_path):
            logger.error(f"File not found: {file_path}")
            return jsonify({'error': 'Image not found'}), 404

        return send_file(file_path, mimetype='image/png')
    except Exception as e:
        logger.error(f"Error serving image: {str(e)}")
        return jsonify({'error': str(e)}), 404

@app.route('/delete-images', methods=['POST'])
def delete_images():
    try:
        user_id = request.json.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        filenames = request.json.get('filenames')
        if not filenames or not isinstance(filenames, list):
            return jsonify({'error': 'Invalid filenames provided'}), 400

        metadata = load_metadata(user_id)
        if not metadata:
            return jsonify({'error': 'User metadata not found'}), 404

        deleted_files = []
        failed_files = []

        for filename in filenames:
            if filename not in metadata['images']:
                failed_files.append(filename)
                continue

            category = metadata['images'][filename]['category']
            file_path = os.path.join(UPLOAD_FOLDER, str(user_id), category, filename)

            try:
                if os.path.exists(file_path):
                    os.remove(file_path)
                    metadata['categories'][category].remove(filename)
                    del metadata['images'][filename]
                    deleted_files.append(filename)
                else:
                    failed_files.append(filename)
            except Exception as e:
                logger.error(f"Error deleting file {filename}: {str(e)}")
                failed_files.append(filename)

        save_metadata(user_id, metadata)

        return jsonify({
            'success': True,
            'deleted_files': deleted_files,
            'failed_files': failed_files
        })

    except Exception as e:
        logger.error(f"Error in delete_images: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/get-categories', methods=['GET'])
def get_categories():
    try:
        user_id = request.args.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400
            
        # Verify metadata structure before returning categories
        verify_metadata_structure(user_id)
        categories = UserCategories.get_categories(user_id)
        return jsonify({'categories': categories})
        
    except Exception as e:
        logger.error(f"Error getting categories: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/add-category', methods=['POST'])
def add_category():
    try:
        user_id = request.json.get('user_id')
        category_name = request.json.get('category_name')
        
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400
            
        if not category_name or not isinstance(category_name, str):
            return jsonify({'error': 'Invalid category name'}), 400
        
        # Normalize category name
        category_name = category_name.lower().strip()
        
        # First update categories.json
        success, message = UserCategories.add_category(user_id, category_name)
        if not success:
            return jsonify({'error': message}), 400
            
        # Then update metadata.json
        metadata = load_metadata(user_id)
        if metadata and category_name not in metadata['categories']:
            metadata['categories'][category_name] = []
            save_metadata(user_id, metadata)
            
        return jsonify({'message': message}), 200
            
    except Exception as e:
        logger.error(f"Error adding category: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/remove-categories', methods=['DELETE'])
def remove_category():
    try:
        user_id = request.json.get('user_id')
        category_name = request.json.get('category_name')
        
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400
            
        if not category_name or not isinstance(category_name, str):
            return jsonify({'error': 'Invalid category name'}), 400
            
        # Load metadata first to check for images
        metadata = load_metadata(user_id)
        if metadata and category_name in metadata['categories']:
            # Move all images to 'uncategorized' category
            if len(metadata['categories'][category_name]) > 0:
                for filename in metadata['categories'][category_name][:]:  # Create a copy of the list
                    try:
                        # Move file
                        old_path = os.path.join(UPLOAD_FOLDER, str(user_id), category_name, filename)
                        new_path = os.path.join(UPLOAD_FOLDER, str(user_id), 'uncategorized', filename)
                        if os.path.exists(old_path):
                            os.rename(old_path, new_path)
                        
                        # Update metadata
                        metadata['categories'][category_name].remove(filename)
                        if filename not in metadata['categories']['uncategorized']:
                            metadata['categories']['uncategorized'].append(filename)
                        metadata['images'][filename]['category'] = 'uncategorized'
                    except Exception as e:
                        logger.error(f"Error moving file {filename}: {str(e)}")
                
                save_metadata(user_id, metadata)
        
        # Then remove the category
        success, message = UserCategories.remove_category(user_id, category_name)
        if success:
            # Remove category from metadata if it exists
            if metadata and category_name in metadata['categories']:
                del metadata['categories'][category_name]
                save_metadata(user_id, metadata)
            return jsonify({'message': message}), 200
        else:
            return jsonify({'error': message}), 400
            
    except Exception as e:
        logger.error(f"Error removing category: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/move-image', methods=['POST'])
def move_image():
    try:
        user_id = request.json.get('user_id')
        filename = request.json.get('filename')
        new_category = request.json.get('new_category')
        
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400
            
        if not filename or not new_category:
            return jsonify({'error': 'Missing filename or new category'}), 400
            
        # Verify metadata structure
        metadata = verify_metadata_structure(user_id)
        if not metadata:
            return jsonify({'error': 'Metadata not found'}), 404
            
        categories = UserCategories.get_categories(user_id)
        if new_category not in categories:
            return jsonify({'error': 'Invalid category'}), 400
            
        if filename not in metadata['images']:
            return jsonify({'error': 'Image not found'}), 404
            
        old_category = metadata['images'][filename]['category']
        old_path = os.path.join(UPLOAD_FOLDER, str(user_id), old_category, filename)
        new_path = os.path.join(UPLOAD_FOLDER, str(user_id), new_category, filename)
        
        if not os.path.exists(old_path):
            return jsonify({'error': 'Image file not found'}), 404
            
        # Move file
        os.rename(old_path, new_path)
        
        # Update metadata
        if filename in metadata['categories'][old_category]:
            metadata['categories'][old_category].remove(filename)
        if filename not in metadata['categories'][new_category]:
            metadata['categories'][new_category].append(filename)
        metadata['images'][filename]['category'] = new_category
        save_metadata(user_id, metadata)
        
        return jsonify({
            'message': 'Image moved successfully',
            'new_category': new_category
        })
        
    except Exception as e:
        logger.error(f"Error moving image: {str(e)}")
        return jsonify({'error': str(e)}), 500
    
##################################################################33
@app.route('/recommend-clothing', methods=['GET'])
def recommend_clothing():
    try:
        temperature = float(request.args.get('temperature'))
        humidity = float(request.args.get('humidity', 50))
        wind_speed = float(request.args.get('wind_speed', 10))
        user_id = request.args.get('user_id')
        
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400
        
        # Dự đoán quần áo
        recommendation = global_recommender.recommend_clothing(temperature, humidity, wind_speed)
        
        # Kiểm tra nếu quần áo được đề xuất có trong danh mục người dùng
        categories = UserCategories.get_categories(user_id)
        if recommendation['topWear'] not in categories:
            recommendation['topWear'] = select_best_match(categories, 'top', temperature)
        if recommendation['bottomWear'] not in categories:
            recommendation['bottomWear'] = select_best_match(categories, 'bottom', temperature)
        
        # Lấy ảnh phù hợp nếu có
        top_images = get_user_images_by_categories(user_id, recommendation['topWear'], categories)
        bottom_images = get_user_images_by_categories(user_id, recommendation['bottomWear'], categories)
        
        return jsonify({
            'topWear': recommendation['topWear'],
            'bottomWear': recommendation['bottomWear'],
            'topImages': top_images,
            'bottomImages': bottom_images
        })
    
    except Exception as e:
        logger.error(f"Lỗi đề xuất quần áo: {str(e)}")
        return jsonify({'error': str(e)}), 500


def get_user_images_by_categories(user_id, clothing_type, categories):
    """
    Lấy danh sách ảnh phù hợp với loại quần áo và danh mục người dùng.
    """
    try:
        metadata = load_metadata(user_id)
        if not metadata:
            return []
        
        matching_images = [
            f"/images/{user_id}/{info['category']}/{filename}"
            for filename, info in metadata['images'].items()
            if info['category'] in categories and clothing_type.lower() in info['category'].lower()
        ]
        
        return matching_images
    except Exception as e:
        logger.error(f"Lỗi lấy ảnh: {str(e)}")
        return []


def select_best_match(categories, clothing_type, temperature):
    """
    Chọn category gần nhất nếu category được đề xuất không tồn tại.
    """
    temperature_ranges = {
        'top': {
            (float('-inf'), 25): ['long sleeves', 'sweater', 'hoodie', 'turtleneck'],
            (25, float('inf')): ['short sleeves', 't-shirt', 'shirt', 'top', 'blouse', 'tank top']
        },
        'bottom': {
            (float('-inf'), 25): ['long leggings', 'jeans', 'pants', 'trousers'],
            (25, float('inf')): ['short leggings', 'shorts', 'skirt']
        }
    }

    for temp_range, preferred_categories in temperature_ranges[clothing_type].items():
        if temp_range[0] <= temperature <= temp_range[1]:
            for category in preferred_categories:
                if category in categories:
                    return category

    return 'uncategorized'
    
###################################################3

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'healthy', 'timestamp': datetime.datetime.now().isoformat()})

@app.route('/random-outfit', methods=['GET'])
def random_outfit():
    try:
        user_id = request.args.get('user_id')
        
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400
            
        # Get user's metadata
        metadata = load_metadata(user_id)
        
        if not metadata or not metadata['images']:
            return jsonify({'error': 'No images found for the user'}), 404
        
        # Riêng với top, mở rộng keywords để bao gồm nhiều loại áo hơn
        top_keywords = [
            'sleeve', 'shirt', 'top', 'jacket', 'sweater', 
            't-shirt', 'long sleeve', 'short sleeve', 
            'polo', 'blouse', 'tank top','short'
        ]
        bottom_keywords = ['legging', 'pant', 'trouser', 'jean', 'short', 'skirt']
        
        # Lọc categories top wear
        top_categories = [
            cat for cat in metadata['categories'].keys() 
            if any(keyword in cat.lower() for keyword in top_keywords) 
            and not any(keyword in cat.lower() for keyword in bottom_keywords)
        ]
        
        bottom_categories = [
            cat for cat in metadata['categories'].keys() 
            if any(word in cat.lower() for word in bottom_keywords) 
            and not any(word in cat.lower() for word in top_keywords)
        ]
        
        # Filter categories with actual images
        valid_top_categories = [cat for cat in top_categories if metadata['categories'][cat]]
        valid_bottom_categories = [cat for cat in bottom_categories if metadata['categories'][cat]]
        
        if not valid_top_categories or not valid_bottom_categories:
            return jsonify({'error': 'Not enough categories with images'}), 404
        
        import random
        
        # Randomly select a top category and an image from that category
        random_top_category = random.choice(valid_top_categories)
        top_images = metadata['categories'][random_top_category]
        random_top_image = random.choice(top_images)
        
        # Randomly select a bottom category and an image from that category
        random_bottom_category = random.choice(valid_bottom_categories)
        bottom_images = metadata['categories'][random_bottom_category]
        random_bottom_image = random.choice(bottom_images)
        
        # Return image URLs and categories with full URL
        base_url = request.host_url.rstrip('/')  # Get the base URL of the server
        return jsonify({
            'top': {
                'category': random_top_category,
                'filename': random_top_image,
                'url': f"{base_url}/images/{user_id}/{random_top_category}/{random_top_image}"
            },
            'bottom': {
                'category': random_bottom_category,
                'filename': random_bottom_image,
                'url': f"{base_url}/images/{user_id}/{random_bottom_category}/{random_bottom_image}"
            }
        })
    except Exception as e:
        logger.error(f"Error generating random outfit: {str(e)}")
        return jsonify({'error': str(e)}), 500

# Thêm endpoint /getAllUserClothes
@app.route('/getAllUserClothes', methods=['GET'])
def get_all_user_clothes():
    try:
        user_id = request.args.get('user_id')
        
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        # Log received parameters
        logger.debug(f"Getting all clothes for user: {user_id}")

        metadata = load_metadata(user_id)
        if not metadata:
            logger.debug("No metadata found, returning empty list")
            return jsonify([])

        categories = UserCategories.get_categories(user_id)
        result = []

        for category in categories:
            image_files = metadata['categories'].get(category, [])
            image_urls = [f"/images/{user_id}/{category}/{filename}" for filename in image_files]
            result.append({
                'category': category,
                'images': image_urls
            })

        logger.debug(f"Returning {len(result)} categories with images")
        return jsonify(result)

    except Exception as e:
        logger.error(f"Error in get_all_user_clothes: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/add-to-favorite', methods=['POST'])
def check_favorite_status():
        try:
            # Lấy dữ liệu từ JSON hoặc form-data
            if request.is_json:
                data = request.get_json()
            else:
                data = request.form

            user_id = data.get('user_id')
            filename = data.get('filename')

            # Kiểm tra thông tin bắt buộc
            if not user_id or not filename:
                return jsonify({'error': 'Missing user_id or filename'}), 400

            if not is_valid_firebase_uid(user_id):
                return jsonify({'error': 'Invalid user ID'}), 400

            metadata = load_metadata(user_id)
            if not metadata:
                return jsonify({'error': 'User metadata not found'}), 404

            # Kiểm tra xem hình ảnh có tồn tại trong metadata hay không
            if filename not in metadata.get('images', {}):
                return jsonify({'error': 'Image not found'}), 404

            # Lấy giá trị is_favorite, nếu chưa có thì mặc định False
            image_info = metadata['images'][filename]
            is_fav = image_info.get('is_favorite', False)

            # Trả về kết quả
            return jsonify({
                'filename': filename,
                'is_favorite': is_fav
            }), 200

        except Exception as e:
            logger.error(f"Error in check_favorite_status: {str(e)}")
            return jsonify({'error': str(e)}), 500

from flask import url_for

@app.route('/create-favorite-set', methods=['POST'])
def create_favorite_set():
        try:
            # Nhận dữ liệu từ JSON hoặc form-data
            data = request.get_json() if request.is_json else request.form
            user_id = data.get('user_id')
            set_name = data.get('set_name')
            shirt_filenames = data.get('shirt_images')
            pant_filenames = data.get('pant_images')

            # Kiểm tra thông tin bắt buộc
            if not user_id or not set_name or shirt_filenames is None or pant_filenames is None:
                return jsonify({'error': 'Missing required parameters'}), 400

            if not is_valid_firebase_uid(user_id):
                return jsonify({'error': 'Invalid user ID'}), 400

            # Nếu nhận được chuỗi, chuyển thành list (giả sử phân cách bởi dấu phẩy)
            if isinstance(shirt_filenames, str):
                shirt_filenames = [s.strip() for s in shirt_filenames.split(',')]
            if isinstance(pant_filenames, str):
                pant_filenames = [s.strip() for s in pant_filenames.split(',')]

            metadata = load_metadata(user_id)
            if not metadata:
                return jsonify({'error': 'User metadata not found'}), 404

            # Kiểm tra xem favorite set với tên này đã tồn tại chưa
            if 'favorite_sets' not in metadata:
                metadata['favorite_sets'] = []
            for fav_set in metadata['favorite_sets']:
                if fav_set.get('set_name') == set_name:
                    return jsonify({'error': 'Favorite set with this name already exists'}), 400

            # Tạo danh sách ảnh chi tiết cho áo
            shirt_images = []
            for filename in shirt_filenames:
                if filename in metadata.get('images', {}):
                    info = metadata['images'][filename]
                    category = info.get('category', 'uncategorized')
                    image_url = url_for('serve_image', user_id=user_id, category=category, filename=filename,
                                        _external=True)
                    shirt_images.append({
                        "filename": filename,
                        "category": category,
                        "url": image_url
                    })
                else:
                    shirt_images.append({
                        "filename": filename,
                        "category": "uncategorized",
                        "url": ""
                    })

            # Tạo danh sách ảnh chi tiết cho quần
            pant_images = []
            for filename in pant_filenames:
                if filename in metadata.get('images', {}):
                    info = metadata['images'][filename]
                    category = info.get('category', 'uncategorized')
                    image_url = url_for('serve_image', user_id=user_id, category=category, filename=filename,
                                        _external=True)
                    pant_images.append({
                        "filename": filename,
                        "category": category,
                        "url": image_url
                    })
                else:
                    pant_images.append({
                        "filename": filename,
                        "category": "uncategorized",
                        "url": ""
                    })

            new_set = {
                "id": str(uuid.uuid4()),  # Tạo id duy nhất cho favorite set
                "set_name": set_name,
                "shirt_images": shirt_images,
                "pant_images": pant_images,
                "timestamp": datetime.datetime.now().isoformat()
            }

            metadata['favorite_sets'].append(new_set)
            save_metadata(user_id, metadata)
            return jsonify({'message': 'Favorite set created successfully'}), 200
        except Exception as e:
            logger.error(f"Error creating favorite set: {str(e)}")
            return jsonify({'error': str(e)}), 500

@app.route('/edit-favorite-set', methods=['POST'])
def edit_favorite_set():
        try:
            data = request.get_json() if request.is_json else request.form
            user_id = data.get('user_id')
            set_id = data.get('set_id')  # Lấy set_id từ client
            new_set_name = data.get('set_name')
            shirt_filenames = data.get('shirt_images')
            pant_filenames = data.get('pant_images')

            # Kiểm tra thông tin bắt buộc
            if not user_id or not set_id or new_set_name is None or shirt_filenames is None or pant_filenames is None:
                return jsonify({'error': 'Missing required parameters'}), 400

            # Nếu nhận được chuỗi, chuyển thành list
            if isinstance(shirt_filenames, str):
                shirt_filenames = [s.strip() for s in shirt_filenames.split(',')]
            if isinstance(pant_filenames, str):
                pant_filenames = [s.strip() for s in pant_filenames.split(',')]

            metadata = load_metadata(user_id)
            if not metadata:
                return jsonify({'error': 'User metadata not found'}), 404

            if 'favorite_sets' not in metadata:
                return jsonify({'error': 'No favorite sets found'}), 404

            found = False
            for fav_set in metadata['favorite_sets']:
                # Tìm bộ sưu tập theo set_id
                if fav_set.get('id') == set_id:
                    # Cập nhật danh sách ảnh áo
                    new_shirt_images = []
                    for filename in shirt_filenames:
                        if filename in metadata.get('images', {}):
                            info = metadata['images'][filename]
                            category = info.get('category', 'uncategorized')
                            image_url = url_for('serve_image', user_id=user_id, category=category, filename=filename,
                                                _external=True)
                            new_shirt_images.append({
                                "filename": filename,
                                "category": category,
                                "url": image_url
                            })
                        else:
                            new_shirt_images.append({
                                "filename": filename,
                                "category": "uncategorized",
                                "url": ""
                            })
                    # Cập nhật danh sách ảnh quần
                    new_pant_images = []
                    for filename in pant_filenames:
                        if filename in metadata.get('images', {}):
                            info = metadata['images'][filename]
                            category = info.get('category', 'uncategorized')
                            image_url = url_for('serve_image', user_id=user_id, category=category, filename=filename,
                                                _external=True)
                            new_pant_images.append({
                                "filename": filename,
                                "category": category,
                                "url": image_url
                            })
                        else:
                            new_pant_images.append({
                                "filename": filename,
                                "category": "uncategorized",
                                "url": ""
                            })
                    # Cập nhật favorite set
                    fav_set['shirt_images'] = new_shirt_images
                    fav_set['pant_images'] = new_pant_images
                    fav_set['set_name'] = new_set_name  # Cập nhật tên mới nếu cần
                    fav_set['timestamp'] = datetime.datetime.now().isoformat()
                    found = True
                    break

            if not found:
                return jsonify({'error': 'Favorite set not found'}), 404

            save_metadata(user_id, metadata)
            return jsonify({'message': 'Favorite set updated successfully'}), 200
        except Exception as e:
            logger.error(f"Error editing favorite set: {str(e)}")
            return jsonify({'error': str(e)}), 500

@app.route('/get-favorite-sets', methods=['GET'])
def get_favorite_sets():
        try:
            user_id = request.args.get('user_id')
            if not user_id or not is_valid_firebase_uid(user_id):
                return jsonify({'error': 'Invalid user ID'}), 400

            metadata = load_metadata(user_id)
            if not metadata:
                return jsonify({'error': 'User metadata not found'}), 404

            # Nếu chưa có favorite_sets, trả về danh sách rỗng
            favorite_sets = metadata.get('favorite_sets', [])
            return jsonify(favorite_sets), 200

        except Exception as e:
            logger.error(f"Error getting favorite sets: {str(e)}")
            return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    # Create upload folder if it doesn't exist
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    
    # Run the app
    app.run(host='0.0.0.0', port=5000, debug=True)