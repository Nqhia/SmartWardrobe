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

app = Flask(__name__)
CORS(app)

logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

BASE_DIR = os.path.dirname(__file__)
UPLOAD_FOLDER = os.path.join(BASE_DIR, 'user_images')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

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
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        categories = UserCategories.get_categories(user_id)
        if category not in categories:
            return jsonify({'error': 'Invalid category'}), 400

        file_path = os.path.join(UPLOAD_FOLDER, str(user_id), category, filename)
        if not os.path.exists(file_path):
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
            
        success, message = UserCategories.add_category(user_id, category_name)
        if success:
            return jsonify({'message': message}), 200
        else:
            return jsonify({'error': message}), 400
            
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
            
        success, message = UserCategories.remove_category(user_id, category_name)
        if success:
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
            
        categories = UserCategories.get_categories(user_id)
        if new_category not in categories:
            return jsonify({'error': 'Invalid category'}), 400
            
        metadata = load_metadata(user_id)
        if not metadata or filename not in metadata['images']:
            return jsonify({'error': 'Image not found'}), 404
            
        old_category = metadata['images'][filename]['category']
        old_path = os.path.join(UPLOAD_FOLDER, str(user_id), old_category, filename)
        new_path = os.path.join(UPLOAD_FOLDER, str(user_id), new_category, filename)
        
        if not os.path.exists(old_path):
            return jsonify({'error': 'Image file not found'}), 404
            
        # Move file
        os.rename(old_path, new_path)
        
        # Update metadata
        metadata['categories'][old_category].remove(filename)
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

@app.route('/health', methods=['GET'])
def health_check():
    return jsonify({'status': 'healthy', 'timestamp': datetime.datetime.now().isoformat()})

if __name__ == '__main__':
    # Create upload folder if it doesn't exist
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    
    # Run the app
    app.run(host='0.0.0.0', port=5000, debug=True)
