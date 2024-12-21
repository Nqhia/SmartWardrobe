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

app = Flask(__name__)
CORS(app)


logging.basicConfig(level=logging.DEBUG)
logger = logging.getLogger(__name__)

BASE_DIR = os.path.dirname(__file__)
UPLOAD_FOLDER = os.path.join(BASE_DIR, 'user_images')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER 
ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}

def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def is_valid_firebase_uid(uid):
    """Validate Firebase UID format"""
    return bool(re.match(r'^[a-zA-Z0-9]{28}$', uid))

def ensure_user_directory(user_id):
    """Create a directory for the user if it doesn't exist"""
    if not user_id or not is_valid_firebase_uid(user_id):
        raise ValueError("Invalid user ID")
        
    user_dir = os.path.join(UPLOAD_FOLDER, str(user_id))
    if not os.path.exists(user_dir):
        os.makedirs(user_dir)
    return user_dir

@app.route('/remove-background', methods=['POST'])
def remove_background():
    try:
        # Validate user ID
        user_id = request.form.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        # Validate image file
        if 'image' not in request.files:
            return jsonify({'error': 'No image file provided'}), 400
        
        file = request.files['image']
        if file.filename == '':
            return jsonify({'error': 'No selected file'}), 400
            
        if file and allowed_file(file.filename):
            try:
                user_dir = ensure_user_directory(user_id)
            except ValueError as e:
                logger.error(f"Invalid user directory: {str(e)}")
                return jsonify({'error': str(e)}), 400

            try:
                # Process image
                input_image = Image.open(file.stream)
                output_image = remove(input_image)
                
                # Generate safe filename and save
                timestamp = datetime.datetime.now().strftime("%Y%m%d_%H%M%S")
                safe_filename = secure_filename(f"processed_{timestamp}_{file.filename}")
                file_path = os.path.join(user_dir, safe_filename)
                
                # Save processed image
                output_image.save(file_path, 'PNG')
                
                # Return processed image
                return send_file(
                    file_path,
                    mimetype='image/png',
                    as_attachment=True,
                    download_name=safe_filename
                )
            except Exception as e:
                logger.error(f"Image processing error: {str(e)}")
                return jsonify({'error': 'Error processing image'}), 500
        else:
            return jsonify({'error': 'Invalid file type'}), 400
                
    except Exception as e:
        logger.error(f"Unexpected error in remove_background: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/get-user-images', methods=['GET'])
def get_user_images():
    try:
        user_id = request.args.get('user_id')
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400

        user_dir = os.path.join(UPLOAD_FOLDER, str(user_id))
        
        if not os.path.exists(user_dir):
            return jsonify([])
            
        image_files = []
        for filename in os.listdir(user_dir):
            if allowed_file(filename):
                image_url = f"/images/{user_id}/{filename}"
                image_files.append(image_url)
                
        return jsonify(image_files)
        
    except Exception as e:
        logger.error(f"Error in get_user_images: {str(e)}")
        return jsonify({'error': str(e)}), 500

@app.route('/images/<user_id>/<filename>')
def serve_image(user_id, filename):
    try:
        if not user_id or not is_valid_firebase_uid(user_id):
            return jsonify({'error': 'Invalid user ID'}), 400
            
        file_path = os.path.join(UPLOAD_FOLDER, str(user_id), filename)
        if not os.path.exists(file_path):
            return jsonify({'error': 'Image not found'}), 404
            
        return send_file(file_path, mimetype='image/png')
    except Exception as e:
        logger.error(f"Error serving image: {str(e)}")
        return jsonify({'error': str(e)}), 404

if __name__ == '__main__':
    if not os.path.exists(UPLOAD_FOLDER):
        os.makedirs(UPLOAD_FOLDER)
    
    app.run(host='0.0.0.0', port=5001, debug=True)